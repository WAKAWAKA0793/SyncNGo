package com.example.tripshare.ui.trip

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.CategoryWithItems
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.SavedChecklistEntity
import com.example.tripshare.data.model.SharedCategory
import com.example.tripshare.data.model.SharedChecklist
import com.example.tripshare.data.model.SharedItem
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.repo.ChatRepository
import com.example.tripshare.data.repo.ChecklistRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.util.ChecklistPdfGenerator
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate

data class CategoryUi(
    val categoryId: Long,
    val title: String,
    val done: Int,
    val total: Int,
    val items: List<ItemUi> = emptyList()
)

data class ItemUi(
    val id: Long,
    val title: String,
    val completed: Boolean,
    val dateText: String? = null,
    val note: String? = null,
    val quantity: Int = 1
)



class ChecklistVMFactory(
    private val repo: ChecklistRepository,
    private val tripRepository: TripRepository,
    private val chatRepository: ChatRepository,
    private val userDao: UserDao,
    private val tripId: Long,
    private val userId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChecklistViewModel(repo, tripRepository, chatRepository, userDao, tripId, userId) as T
    }
}
/** One-off UI events your screen can observe (optional). */
sealed interface ChecklistUiEvent {
    data object OpenAddSheet : ChecklistUiEvent
    data object SavedToMyLists : ChecklistUiEvent
}

class ChecklistViewModel(

private val repo: ChecklistRepository,
private val tripRepository: TripRepository,
private val chatRepository: ChatRepository,
private val userDao: UserDao,
private val tripId: Long,
private val userId: Long,
tripNameShown: String? = null
) : ViewModel() {
    init {
        // ✅ START SYNC
        repo.startSync(tripId, viewModelScope)
    }
    // Optional trip name exposure, if you want to show it in the top bar later.
    private val _tripName = MutableStateFlow(tripNameShown ?: "")
    val tripName: StateFlow<String> = _tripName.asStateFlow()
    val joinedTrips: StateFlow<List<TripEntity>> = tripRepository.observeJoinedTripIds(userId)
        .flatMapLatest { ids ->
            // You might need a method in repo to get trips by IDs,
            // or just observe all and filter (simpler for now if list is small)
            tripRepository.observeTrips().map { allTrips ->
                allTrips.filter { it.id in ids }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Categories stream for the screen.
    val categories: StateFlow<List<CategoryUi>> =
        repo.observeCategoriesWithItems(tripId)
            .map { it.map(::toUi) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // One-shot UI events (FAB etc.). Screen may collect this to show the Add sheet.
    private val _uiEvents = MutableSharedFlow<ChecklistUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvents: SharedFlow<ChecklistUiEvent> = _uiEvents.asSharedFlow()

    fun deleteItem(itemId: Long) = viewModelScope.launch {
        repo.deleteItem(itemId)
    }

    fun shareChecklistToChat(context: Context) = viewModelScope.launch {
        val user = userDao.getUserById(userId) ?: return@launch

        // Use IO thread for file operations
        val pdfFile = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            ChecklistPdfGenerator.generatePdf(
                context = context,
                tripName = tripName.value.ifBlank { "Trip" },
                categories = categories.value // Ensure this has data!
            )
        }

        if (pdfFile != null) {
            chatRepository.sendGroupMessage(
                tripId = tripId,
                senderId = userId,
                senderName = user.name,
                senderAvatar = user.profilePhoto,
                // The format here MUST match what we look for in GroupChatScreen
                content = "📄 Shared a checklist PDF: ${pdfFile.name}"
            )
        }
    }

    /** Called when the user taps a “template” chip. For now we create a category with that name. */
    fun onAddTemplate(templateName: String) = viewModelScope.launch {
        if (templateName.isNotBlank()) {
            repo.addCategory(tripId, templateName.trim())
        }
    }
    fun deleteCategory(categoryId: Long) = viewModelScope.launch {
        repo.deleteCategory(categoryId)
    }
    fun deleteChecklist() = viewModelScope.launch {
        // Since we don't have a 'deleteAll' DAO method yet, we can iterate
        val currentCats = categories.value
        currentCats.forEach { cat ->
            repo.deleteCategory(cat.categoryId)
        }
    }
    val savedLists: StateFlow<List<SavedChecklistEntity>> = repo.observeSavedLists(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Save a specific category as a template
    // 2. Save a specific category as a template
    fun saveCategoryToMyLists(categoryId: Long) = viewModelScope.launch {
        // LOG 1: Check if function starts
        android.util.Log.d("ChecklistVM", "Saving Category ID: $categoryId")

        val category = categories.value.find { it.categoryId == categoryId }

        // LOG 2: Check if category was found
        if (category == null) {
            android.util.Log.e("ChecklistVM", "Category not found in current list!")
            return@launch
        }

        try {
            // LOG 3: Attempt save
            repo.saveTemplate(userId, category.title, category.items)
            android.util.Log.d("ChecklistVM", "Save command sent to Repo")

            _uiEvents.tryEmit(ChecklistUiEvent.SavedToMyLists)
        } catch (e: Exception) {
            android.util.Log.e("ChecklistVM", "Error saving template", e)
        }
    }

    // 3. Import a saved list into the current trip
    fun addSavedListToTrip(savedList: SavedChecklistEntity) = viewModelScope.launch {
        repo.importTemplate(tripId, savedList.id, savedList.name)
    }

    /** Create a new category. */
    fun addCategory(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) repo.addCategory(tripId, name.trim())
    }
    fun deleteSavedList(savedList: SavedChecklistEntity) = viewModelScope.launch {
        repo.deleteSavedList(savedList.id)
    }
    /** Add a new item under a category. */
    fun addItem(categoryId: Long, title: String, dueDate: LocalDate?, note: String?) =
        viewModelScope.launch {
            if (title.isNotBlank()) {
                repo.addItem(
                    categoryId = categoryId,
                    title = title.trim(),
                    dueDate = dueDate,
                    note = note?.trim().takeUnless { it.isNullOrEmpty() }
                )
            }
        }

    /** Toggle completion for a single item (keep if you add item rows later). */
    fun onToggle(item: ItemUi) = viewModelScope.launch {
        // send the NEW state (flip it)
        repo.toggle(item.id, !item.completed)
    }

    fun setQuantity(item: ItemUi, newQty: Int) = viewModelScope.launch {
        // clamp if you want: val q = newQty.coerceIn(0, 99)
        repo.setQuantity(item.id, newQty)
    }

    /* ------------ internal mapping ------------ */
    private fun toUi(cwi: CategoryWithItems): CategoryUi {
        val items = cwi.items
            .sortedWith(compareBy({ it.completed }, { it.sort }, { it.itemId }))
            .map {
                ItemUi(
                    id = it.itemId,
                    title = it.title,
                    dateText = it.dueDate?.toString(),
                    note = it.note,
                    completed = it.completed,
                    quantity = it.quantity ?: 1          // ← if your entity has "quantity"; else use 1
                )
            }
        val done = items.count { it.completed }
        return CategoryUi(
            categoryId = cwi.category.categoryId,
            title = cwi.category.categoryName,
            total = items.size,
            done = done,
            items = items
        )
    }
    // In ChecklistViewModel.kt

    fun shareChecklistJsonToChat() = viewModelScope.launch {
        val user = userDao.getUserById(userId) ?: return@launch

        // 1. Get the JSON string
        val jsonString = shareChecklistAsJson()

        // 2. Get the current trip name (handle if it's empty)
        val currentTripName = tripName.value.ifBlank { "Trip" }

        // 3. Send message with ALL required parameters
        chatRepository.sendGroupMessage(
            tripId = tripId,
            tripName = currentTripName,     // ✅ NEW: Required by your repo
            currentUserId = userId,         // ✅ NEW: Required by your repo
            senderId = userId,
            senderName = user.name,
            senderAvatar = user.profilePhoto,
            content = jsonString,
            type = "CHECKLIST_SHARE"
        )
    }

    // Optional helper if the trip name changes later
    fun setTripName(newName: String) { _tripName.value = newName }
    fun shareChecklistAsJson(): String {
        val currentCategories = categories.value
        val currentTripName = tripName.value.ifBlank { "Trip Checklist" }

        // 1. Map UI data to Serializable Transfer data
        // We exclude IDs and 'completed' status so the receiver gets a fresh list
        val transferData = SharedChecklist(
            tripName = currentTripName,
            categories = currentCategories.map { cat ->
                SharedCategory(
                    title = cat.title,
                    items = cat.items.map { item ->
                        SharedItem(
                            title = item.title,
                            quantity = item.quantity,
                            note = item.note
                        )
                    }
                )
            }
        )

        // 2. Encode to JSON String
        // This explicitly tells the compiler which serializer to use
        return Json.encodeToString(SharedChecklist.serializer(), transferData)
    }

    /**
     * IMPORT: Takes the JSON string (from chat) and adds it to the current trip.
     */
    fun importChecklistFromJson(jsonString: String) = viewModelScope.launch {
        try {
            // 1. Decode JSON
            val data = Json.decodeFromString<SharedChecklist>(jsonString)

            // 2. Insert into Database
            data.categories.forEach { sharedCat ->
                // A. Create the category
                val categoryId = repo.addCategoryReturnId(tripId, sharedCat.title)

                // B. Add all items to this new category
                sharedCat.items.forEach { sharedItem ->
                    repo.addItem(
                        categoryId = categoryId,
                        title = sharedItem.title,
                        dueDate = null,
                        note = sharedItem.note
                    )
                    // If you need quantity, ensure repo.addItem supports it or update immediately after
                    // repo.setQuantity(itemId, sharedItem.quantity)
                }
            }

            // Optional: Notify user of success via UI Event
            android.util.Log.d("ChecklistVM", "Imported ${data.categories.size} categories.")

        } catch (e: Exception) {
            android.util.Log.e("ChecklistVM", "Failed to import checklist", e)
        }
    }

}

@Composable
fun AddListScreenWithVM(
    vm: ChecklistViewModel,
    onClose: () -> Unit
) {
    // 1. Observe the live list from the Database (Room)
    val savedLists by vm.savedLists.collectAsStateWithLifecycle()

    // 2. Observe existing categories (to prevent duplicates if needed)
    val existingCategories by vm.categories.collectAsStateWithLifecycle()
    val existingNames = remember(existingCategories) {
        existingCategories.map { it.title.trim().lowercase() }.toSet()
    }

    AddListScreen(
        onClose = onClose,
        // Pass the real entities here
        yourLists = savedLists,

        // Action: User clicked "Select" on a saved list
        onSelectSavedList = { savedList ->
            vm.addSavedListToTrip(savedList)
            onClose()
        },
        onDeleteSavedList = { listToDelete ->
            vm.deleteSavedList(listToDelete)
        },
        // Action: User selected default templates (Hiking, Gym, etc.)
        onAddCategories = { selectedNames ->
            selectedNames.forEach { raw ->
                val name = raw.trim()
                if (name.isNotEmpty() && name.lowercase() !in existingNames) {
                    vm.addCategory(name)
                }
            }
            onClose()
        }
    )
}

