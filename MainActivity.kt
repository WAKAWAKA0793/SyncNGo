package com.example.tripshare

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.remote.RemoteChatStub
import com.example.tripshare.data.repo.ChatRepository
import com.example.tripshare.data.repo.ChecklistRepository
import com.example.tripshare.data.repo.EmergencyRepository
import com.example.tripshare.data.repo.ExpenseRepository
import com.example.tripshare.data.repo.ItineraryRepository
import com.example.tripshare.data.repo.PostRepository
import com.example.tripshare.data.repo.ReportRepository
import com.example.tripshare.data.repo.ReviewRepository
import com.example.tripshare.data.repo.RouteRepository
import com.example.tripshare.data.repo.TripCalendarRepository
import com.example.tripshare.data.repo.TripHistoryRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.data.repo.UserRepository
import com.example.tripshare.data.repo.VoteRepository
import com.example.tripshare.data.repo.WaitlistRepository
import com.example.tripshare.ui.MyKadScanner
import com.example.tripshare.ui.account.CreateAccountScreen
import com.example.tripshare.ui.account.CreateAccountViewModel
import com.example.tripshare.ui.account.EditProfileScreen
import com.example.tripshare.ui.account.EditProfileViewModel
import com.example.tripshare.ui.account.LoginScreen
import com.example.tripshare.ui.account.LoginViewModel
import com.example.tripshare.ui.account.ProfileScreen
import com.example.tripshare.ui.account.ProfileViewModel
import com.example.tripshare.ui.account.PublicProfileScreen
import com.example.tripshare.ui.account.ReportUserScreen
import com.example.tripshare.ui.account.ReportViewModel
import com.example.tripshare.ui.community.CommunityFeedScreen
import com.example.tripshare.ui.community.CreatePostScreen
import com.example.tripshare.ui.community.PostDetailScreen
import com.example.tripshare.ui.community.PostViewModel
import com.example.tripshare.ui.emergency.EmergencyScreen
import com.example.tripshare.ui.emergency.EmergencyViewModel
import com.example.tripshare.ui.expense.AddExpenseFlow
import com.example.tripshare.ui.expense.BalanceDetailScreen
import com.example.tripshare.ui.expense.BalanceScreen
import com.example.tripshare.ui.expense.BudgetScreen
import com.example.tripshare.ui.expense.EditExpenseScreen
import com.example.tripshare.ui.expense.ExpenseViewModel
import com.example.tripshare.ui.expense.SplitBillScreen
import com.example.tripshare.ui.group.CreateVoteScreen
import com.example.tripshare.ui.group.EditTripScreen
import com.example.tripshare.ui.group.ReviewViewModel
import com.example.tripshare.ui.group.TripGroupScreen
import com.example.tripshare.ui.group.TripVoteScreen
import com.example.tripshare.ui.group.VoteViewModel
import com.example.tripshare.ui.group.VoteViewModelFactory
import com.example.tripshare.ui.home.HomeScreen
import com.example.tripshare.ui.home.HomeViewModel
import com.example.tripshare.ui.messages.ChatListScreen
import com.example.tripshare.ui.messages.ChatListViewModel
import com.example.tripshare.ui.messages.ChatScreen
import com.example.tripshare.ui.messages.ChatViewModel
import com.example.tripshare.ui.messages.GroupChatScreen
import com.example.tripshare.ui.messages.GroupChatViewModel
import com.example.tripshare.ui.messages.rememberChatRepository
import com.example.tripshare.ui.notifications.NotificationCenterViewModel
import com.example.tripshare.ui.notifications.NotificationHelper
import com.example.tripshare.ui.notifications.NotificationScreen
import com.example.tripshare.ui.notifications.NotificationViewModel
import com.example.tripshare.ui.notifications.NotificationViewModelFactory
import com.example.tripshare.ui.notifications.RequestNotificationPermissionIfNeeded
import com.example.tripshare.ui.search.SearchScreen
import com.example.tripshare.ui.theme.TripShareTheme
import com.example.tripshare.ui.trip.AddActivityScreen
import com.example.tripshare.ui.trip.AddCarRentalScreen
import com.example.tripshare.ui.trip.AddCruiseScreen
import com.example.tripshare.ui.trip.AddFlightScreen
import com.example.tripshare.ui.trip.AddListScreenWithVM
import com.example.tripshare.ui.trip.AddLodgingScreen
import com.example.tripshare.ui.trip.AddRailScreen
import com.example.tripshare.ui.trip.AddRestaurantScreen
import com.example.tripshare.ui.trip.CategoryDetailScreen
import com.example.tripshare.ui.trip.ChecklistScreen
import com.example.tripshare.ui.trip.ChecklistVMFactory
import com.example.tripshare.ui.trip.ChecklistViewModel
import com.example.tripshare.ui.trip.CreateTripScreen
import com.example.tripshare.ui.trip.CreateTripViewModel
import com.example.tripshare.ui.trip.ExpensePreviewDialog
import com.example.tripshare.ui.trip.HomeVmFactory
import com.example.tripshare.ui.trip.ItineraryEditScreen
import com.example.tripshare.ui.trip.ItineraryPlannerScreen
import com.example.tripshare.ui.trip.ItineraryPlannerViewModel
import com.example.tripshare.ui.trip.JoinRequestsScreen
import com.example.tripshare.ui.trip.JoinRequestsViewModel
import com.example.tripshare.ui.trip.PlanType
import com.example.tripshare.ui.trip.RoutePlannerViewModel
import com.example.tripshare.ui.trip.TripCalendarScreen
import com.example.tripshare.ui.trip.TripCalendarViewModel
import com.example.tripshare.ui.trip.TripDetailsScreen
import com.example.tripshare.ui.trip.TripDetailsViewModel
import com.example.tripshare.ui.trip.TripHeaderViewModel
import com.example.tripshare.ui.trip.TripHistoryScreen
import com.example.tripshare.ui.trip.TripHistoryViewModel
import com.example.tripshare.ui.trip.WaitlistScreen
import com.example.tripshare.ui.trip.WaitlistViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    var onIcScannedCallback: ((String) -> Unit)? = null
    lateinit var fbCallbackManager: com.facebook.CallbackManager
    private val mainViewModel: MainViewModel by viewModels()
    private var userProfileListener: ListenerRegistration? = null
    private var myTripsListener: ListenerRegistration? = null
    private var publicTripsListener: ListenerRegistration? = null
    private var postListener: ListenerRegistration? = null
    private var chatListListeners: List<ListenerRegistration> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {

        val db = AppDatabase.get(applicationContext)
        FirebaseApp.initializeApp(this)
        val userRepoForSync = UserRepository(db.userDao())
        val tripRepoForSync = TripRepository(db.tripDao(), db.participantDao(), db.routeDao(),db.userDao())

// 2. Observe User Session to Start/Stop Sync
        lifecycleScope.launch {
            AuthPrefs.getUserId(applicationContext).collect { userId ->
                if (userId != null && userId != -1L) {
                    // User is logged in -> Get details and start sync
                    val user = kotlinx.coroutines.withContext(Dispatchers.IO) {
                        db.userDao().getUserById(userId)
                    }
                    if (user != null) {
                        setupRealtimeSync(user, userRepoForSync, tripRepoForSync)
                        saveUserToken(userId)
                    }
                } else {
                    // User logged out -> Stop sync
                    stopRealtimeSync()
                }
            }
        }
        super.onCreate(savedInstanceState)
        fbCallbackManager = com.facebook.CallbackManager.Factory.create()
      NotificationHelper.createChannels(this)
        // 1️⃣ Capture the navigation target from the intent
        val navigationTarget = intent.getStringExtra("navigate_to")
        val tripIdFromIntent = intent.getLongExtra("openGroupId", -1L)
        val tripIdFromNotif = intent.getLongExtra("tripId", -1L)
        // 2️⃣ Create notification channels once at app start
        NotificationHelper.createChannels(this)

        // 3️⃣ Handle other legacy intent logic (like opening specific groups)
        intent?.let { handleIntent(it) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            TripShareTheme {
                // Remove the extra RequestNotificationPermissionIfNeeded() here

                val navController = rememberNavController()

                // 4️⃣ Request permission properly with callbacks
                RequestNotificationPermissionIfNeeded(
                    onGranted = {
                        // Log or track permission granted if needed
                    },
                    onDenied = {
                        // Optional: handle denial
                    }

                )

                // 5️⃣ Handle navigation if the specific extra exists
                LaunchedEffect(navigationTarget) {
                    if (navigationTarget == "join_requests" && tripIdFromIntent != -1L) {
                        // 👇 Navigate to the specific route with ID
                        navController.navigate("joinRequests/$tripIdFromIntent")
                    }
                }
                LaunchedEffect(navigationTarget) {
                    if (navigationTarget == "group_chat" && tripIdFromNotif != -1L) {
                        // Navigate to your group chat route
                        // You might need to fetch the tripName or pass a placeholder
                        navController.navigate("groupChat/$tripIdFromNotif/Group Chat")
                    }
                }
                LaunchedEffect(navigationTarget, tripIdFromNotif) {
                    // Handle navigation to Budget screen from a "Bill Split" notification
                    if (navigationTarget == "budget" && tripIdFromNotif != -1L) {
                        navController.navigate("budget/$tripIdFromNotif")
                    }
                }
                LaunchedEffect(navigationTarget, tripIdFromNotif) {
                    if (navigationTarget == "trip_details" && tripIdFromNotif != -1L) {
                        navController.navigate("tripDetails/$tripIdFromNotif")
                    }
                }

                AppNavHost(
                    navController = navController,
                    // mainViewModel = mainViewModel
                )
            }
        }
    }
    private val myKadScanner = MyKadScanner()

    private var photoUri: Uri? = null
    // 1. Permission Launcher: Handles the user's response (Allow/Deny)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permissions", "Notification permission granted")
        } else {
            Log.d("Permissions", "Notification permission denied")
        }
    }

    // 2. Launcher specifically for Camera (keep your existing logic here)
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show()
        }
    }
    // 1. Initialize the Camera Launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && photoUri != null) {
            val bitmap = uriToBitmap(photoUri!!)

            myKadScanner.scanMyKad(bitmap) { icNumber ->
                if (icNumber != null) {
                    // SUCCESS!
                    Toast.makeText(this, "IC Found: $icNumber", Toast.LENGTH_SHORT).show()

                    // 👇 THIS IS THE MISSING PART: Send it to the screen!
                    onIcScannedCallback?.invoke(icNumber)

                } else {
                    Toast.makeText(this, "Please retake clearly", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // Add this inside MainActivity class
    private fun saveUserToken(userId: Long) {
        // 1. Get the current token from Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // 2. The token result
            val token = task.result
            Log.d("FCM", "Token retrieved: $token")

            // 3. Save it to Firestore under the user's ID
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId.toString())
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("current_user_id", userId).apply()
            userRef.update("fcmToken", token)
                .addOnFailureListener {
                    // If the document doesn't exist, create it
                    val data = hashMapOf("fcmToken" to token)
                    userRef.set(data)
                }
        }
    }
    // 2. Call this function when user clicks "Scan Button"
    // 2. Updated launchCamera function
    fun launchCamera() {
        // Check if we HAVE the permission already
        val permission = android.Manifest.permission.CAMERA

        if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            // YES: We have permission -> Open Camera
            try {
                val photoFile = createImageFile()
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    photoFile
                )
                takePictureLauncher.launch(photoUri!!) // Force non-null
            } catch (e: Exception) {
                Log.e("Camera", "Error launching camera", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }

        } else {
            // NO: We don't have permission -> Ask for it
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    // FIX 1: These methods must be INSIDE the class bracket
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val type = intent.getStringExtra("deep_link_type")
        val id = intent.getLongExtra("deep_link_id", -1L)
        if (!type.isNullOrBlank() && id != -1L) {
            mainViewModel.setDeepLink(DeepLink(type, id))
        }
    }

    private fun setupRealtimeSync(
        user: UserEntity,
        userRepo: UserRepository,
        tripRepo: TripRepository
    ) {
        // A. Sync User Profile (Name, Photo updates)
        if (userProfileListener == null) {
            userProfileListener = userRepo.startUserDocSync(
                scope = lifecycleScope,
                email = user.email
            )
        }

        // B. Sync "My Trips" (Trips I am a member of)
        if (myTripsListener == null) {
            // We use one listener for everything now
            myTripsListener = tripRepo.startGlobalSync(
                currentUserId = user.id.toString(),
                scope = lifecycleScope
            )
        }
        if (postListener == null) {
            val db = AppDatabase.get(applicationContext)
            val syncPostRepo = PostRepository(db.postDao(), db.userDao(), db.commentDao())
            syncPostRepo.startPostSync(lifecycleScope)
        }

        if (chatListListeners.isEmpty()) {
            // Initialize ChatRepository locally if not already available
            val db = AppDatabase.get(applicationContext)
            val chatRepo = ChatRepository(
                chatRoomDao = db.chatRoomDao(),
                messageDao = db.messageDao(),
                groupChatDao = db.groupChatDao(),
                remote = ChatRepository.remoteStub, // or your actual remote impl
                tripDao = db.tripDao()
            )

            // Start syncing rooms where I am User A or User B
            chatListListeners = chatRepo.startChatListSync(
                currentUserId = user.id.toString(),
                scope = lifecycleScope
            )
        }
    }

    private fun stopRealtimeSync() {
        userProfileListener?.remove()
        myTripsListener?.remove()
        publicTripsListener?.remove()

        userProfileListener = null
        myTripsListener = null
        publicTripsListener = null

        chatListListeners.forEach { it.remove() }
        chatListListeners = emptyList()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRealtimeSync()
    }

    @Deprecated("Deprecated in Activity Result API, required by Facebook SDK")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::fbCallbackManager.isInitialized) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}

@Composable
fun rememberCurrentUserId(): Long? {
    val context = LocalContext.current
    return AuthPrefs.getUserId(context)
        .collectAsState(initial = null)
        .value
}


@Composable
fun AppNavHost(navController: NavHostController) {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val tripRepo = remember(db) {
        TripRepository(
            db.tripDao(),
            db.participantDao(),
            db.routeDao(),
            db.userDao()
        )
    }

    val userRepo = remember { UserRepository(db.userDao()) }
    val postRepo = remember {
        PostRepository(
            dao = db.postDao(),
            commentDao = db.commentDao(),
            userDao = db.userDao() // 👈 Add this
        )
    }
    val emergencyRepo = remember {
        EmergencyRepository(
            dao = db.emergencyContactDao(),
            insuranceDao = db.insuranceDao(),
            userDao=db.userDao()
        )
    }

    val expenseRepo = remember {
        ExpenseRepository(
            db.expensePaymentDao(),
            db.costSplitDao(),
            db.tripDao(),
            db.userDao(),
            db.settlementDao()
        )
    }
    val itineraryRepo = remember { ItineraryRepository(db.itineraryDao(),db.tripDao()) }
// Checklist repo (Room DAO must exist in AppDatabase)
    val checklistRepo = remember {
        ChecklistRepository(
            dao = db.checklistDao(),
            savedListDao = db.savedListDao(),
            tripDao=db.tripDao(),// <--- Added missing parameter
        )
    }

    val homeVm: HomeViewModel = viewModel(factory = HomeVmFactory(tripRepo,userRepo))
    val reviewRepo = ReviewRepository(db.reviewDao(),db.userDao())
    val remoteStub = remember { RemoteChatStub() }
    val userDao = remember { db.userDao() }
    val chatRepo = remember(db) {
        ChatRepository(
            chatRoomDao = db.chatRoomDao(),
            messageDao = db.messageDao(),
            groupChatDao = db.groupChatDao(),
            remote = remoteStub,
            tripDao = db.tripDao(),
            ioDispatcher = Dispatchers.IO
        )
    }


    val createVm: CreateAccountViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CreateAccountViewModel(
                    ctx.applicationContext as Application,
                    userRepo,
                    emergencyRepo = emergencyRepo
                ) as T
            }
        }
    )


    NavHost(
        navController = navController,
        startDestination = "splash" // 👈 always start here
    ) {
        // 🚀 Splash/Auth gate: decide where to go after DataStore loads
        composable("splash") {
            AuthGate(navController)
        }

        // 🔐 Register (Sign-up)
        composable("register") {
            val scope = rememberCoroutineScope()
            val ctx = LocalContext.current
            val db = remember { AppDatabase.get(ctx) }
            androidx.compose.runtime.DisposableEffect(Unit) {
                val activity = ctx as? MainActivity
                activity?.onIcScannedCallback = { scannedIc ->
                    // 1. Pass data to ViewModel
                    createVm.setScannedIc(scannedIc)

                    // 2. Debug Toast (Optional: Remove later)
                    android.widget.Toast.makeText(ctx, "Auto-filling IC...", android.widget.Toast.LENGTH_SHORT).show()
                }
                onDispose {
                    activity?.onIcScannedCallback = null
                }
            }
            CreateAccountScreen(
                vm = createVm,
                onSuccess = { email: String, password: String ->
                    scope.launch {
                        // Get the newly created user to obtain its Long id
                        val user = db.userDao().findByEmail(email)
                        val userId = user?.id ?: -1L

                        AuthPrefs.saveRegistration(
                            context = ctx,
                            userId = userId,  // <-- Long, derived from DB
                            email = email,
                            password = password
                        )
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                        onScanIc = {
                    // 2. Trigger the camera in Activity
                    (ctx as? MainActivity)?.launchCamera()
                }
            )
        }

        // ✅ MOVE IT HERE:
        composable("emergency") {
            // 1. Get User ID safely
            val context = LocalContext.current
            val userIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
            val currentUserId = userIdState.value ?: -1L

            if (currentUserId != -1L) {
                // 2. Create ViewModel here
                val emergencyVm: EmergencyViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return EmergencyViewModel(emergencyRepo, currentUserId) as T
                        }
                    }
                )

                EmergencyScreen(
                    vm = emergencyVm,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Show loading if ID isn't ready
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // 🔐 Login
        // In MainActivity.kt inside AppNavHost

        composable("login") {
            val ctx = LocalContext.current
            val nav = navController

            // 1. Initialize ViewModel
            // 'userRepo' is already defined in your AppNavHost scope
            val loginVm = remember {
                LoginViewModel(ctx.applicationContext as Application, userRepo)
            }
            val loginState by loginVm.uiState.collectAsState()

            // 2. Handle Navigation Events based on ViewModel State
            LaunchedEffect(loginState) {
                // Success -> Go Home
                if (loginState.isSuccess) {
                    nav.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                    loginVm.resetState()
                }

                // New User -> Go to Register (Prefill Data)
                loginState.googleUserToRegister?.let { (email, name) ->
                    // Use your existing 'createVm' to pre-fill the form
                    createVm.initFromGoogle(name = name, email = email)
                    nav.navigate("register")

                    // Reset the flag so we don't navigate twice
                    loginVm.onRegisterNavigationHandled()
                }
            }

            /* ---------------- Google One Tap ---------------- */
            val credentialManager = remember { CredentialManager.create(ctx) }
            val firebaseAuth = remember { FirebaseAuth.getInstance() }
            val serverClientId = ctx.getString(R.string.default_web_client_id)

            suspend fun performOneTapSignIn() {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(serverClientId)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context = ctx, request = request)
                    val cred = result.credential

                    if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleCred = GoogleIdTokenCredential.createFrom(cred.data)

                        // Auth with Firebase
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleCred.idToken, null)
                        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

                        val email = authResult.user?.email ?: googleCred.id
                        val name = authResult.user?.displayName ?: "Traveler"

                        // 🛑 DELEGATE TO VIEWMODEL: Sync check logic
                        loginVm.handleSocialLogin(email, name)
                    }
                } catch (e: Exception) {
                    Log.e("LOGIN", "One Tap failed", e)
                }
            }

            /* ---------------- Facebook Login ---------------- */
            val activity = androidx.activity.compose.LocalActivityResultRegistryOwner.current?.let { it as? ComponentActivity }
            val fbCallbackManager = remember { com.facebook.CallbackManager.Factory.create() }

            androidx.compose.runtime.DisposableEffect(Unit) {
                val loginManager = com.facebook.login.LoginManager.getInstance()
                val callback = object : com.facebook.FacebookCallback<com.facebook.login.LoginResult> {
                    override fun onSuccess(result: com.facebook.login.LoginResult) {
                        // Placeholder logic: Facebook GraphRequest needed for real email
                        val fakeEmail = "${result.accessToken.userId}@facebook.local"
                        val fakeName = "Facebook User"

                        // 🛑 DELEGATE TO VIEWMODEL: Sync check logic
                        loginVm.handleSocialLogin(fakeEmail, fakeName)
                    }
                    override fun onCancel() {}
                    override fun onError(error: com.facebook.FacebookException) {}
                }

                loginManager.registerCallback(fbCallbackManager, callback)
                // Also register with the activity's callback manager if needed
                (activity as? MainActivity)?.fbCallbackManager?.let {
                    loginManager.registerCallback(it, callback)
                }

                onDispose {
                    // Cleanup if necessary
                }
            }

            // 3. Render Screen
            LoginScreen(
                onLogin = { email, password ->
                    loginVm.login(email, password)
                },
                onGoRegister = { nav.navigate("register") },
                onForgotPassword = { loginVm.resetPassword(loginState.error ?: "") },
                onGoogleLogin = {
                    // Launch coroutine for One Tap
                    (ctx as? ComponentActivity)?.lifecycleScope?.launch { performOneTapSignIn() }
                },
                onAppleLogin = { },
                onEmailLogin = { },
                onFacebookLogin = {
                    activity?.let {
                        com.facebook.login.LoginManager.getInstance()
                            .logInWithReadPermissions(it, listOf("email", "public_profile"))
                    }
                },
                onContinueAsGuest = {
                    loginVm.viewModelScope.launch {
                        AuthPrefs.setGuest(ctx)
                        nav.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                isLoading = loginState.isLoading,
                socialError = loginState.error
            )
        }

        composable("home") {
            val ctx = LocalContext.current
            val db = remember { AppDatabase.get(ctx) }
            val scope = rememberCoroutineScope()

            // 1) Current user id from DataStore
            val userId by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

            // 2) Push the user id into HomeViewModel so it can emit uiState
            LaunchedEffect(userId) {
                userId?.takeIf { it != -1L }?.let { homeVm.setCurrentUserId(it) }
            }

            // 3) Optional: show the name from DB
            val userDisplayNameFlow = remember(userId) {
                userId?.takeIf { it != -1L }?.let { uid ->
                    db.userDao().observeUserName(uid)
                } ?: kotlinx.coroutines.flow.flowOf<String?>(null)
            }
            val userDisplayName by userDisplayNameFlow.collectAsState(initial = null)

            HomeScreen(
                vm = homeVm,
                userDisplayName = userDisplayName,
                onJoinNow = { navController.navigate("createTrip") },
                onTripClick = { tripId ->
                    scope.launch {
                        val uid = AuthPrefs.getUserId(ctx).firstOrNull() ?: return@launch
                        val alreadyJoined = db.tripDao().isUserParticipant(tripId, uid) > 0
                        if (alreadyJoined) navController.navigate("tripGroup/$tripId")
                        else navController.navigate("tripDetails/$tripId")
                    }
                },
                onNotificationsClick = { navController.navigate("notification") },
                onMessagesClick = { navController.navigate("messages") },
                onSearchClick = { navController.navigate("search") },
                onNavClick = { route ->
                    when (route) {
                        "home" -> navController.navigate("home") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
                        }
                        "search" -> navController.navigate("search")
                        "createTrip" -> navController.navigate("createTrip")
                        "community" -> navController.navigate("community")
                        "emergency" -> navController.navigate("emergency")

                        "profile" -> {
                            when (userId) {
                                null -> return@HomeScreen
                                -1L -> navController.navigate("login") { launchSingleTop = true }
                                else -> navController.navigate("profile") { launchSingleTop = true }
                            }
                        }
                    }
                }

            )
        }

// 👤 Profile (guard: wait for DataStore, then decide)
        composable("profile") {
            val registered: Boolean? by AuthPrefs.isRegistered(ctx).collectAsState(initial = null)
            val uid: Long? by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

            when {
                // 1) Still loading DataStore
                registered == null || uid == null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                // 2) Logged in -> show profile
                registered == true && uid != -1L -> {
                    val tripHistoryDao = db.tripHistoryDao()
                    val profileVm: ProfileViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                                    @Suppress("UNCHECKED_CAST")
                                    return ProfileViewModel(
                                        repo = userRepo,   // make sure you have this variable in scope
                                        postRepo = postRepo,
                                        tripHistoryDao = tripHistoryDao,
                                        reviewRepo = reviewRepo
                                    ) as T
                                }
                                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
                            }
                        }
                    )

                    ProfileScreen(
                        vm = profileVm,
                        currentUserId = uid!!,
                        onBack = { navController.popBackStack() },
                        onEditProfile = { navController.navigate("editProfile") },
                        onNavClick = { route -> navController.navigate(route) }
                    )
                }
            }
        }

        // ✏️ Edit Profile
        composable("editProfile") {
            val editVm: EditProfileViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return EditProfileViewModel(userRepo, emergencyRepo) as T
                    }
                }
            )
            EditProfileScreen(
                vm = editVm,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // ➕ Create Trip
        composable("createTrip") {
            val routeRepo = remember { RouteRepository(db.routeDao()) }
            val context = LocalContext.current

            // 1. Get Current User ID safely
            val userIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
            val currentUserId = userIdState.value ?: -1L

            // 2. Create NotificationViewModel
            val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }
            val notifVm: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(
                    tripDao = db.tripDao(),
                    waitlistDao = db.waitlistDao(),
                    waitlistRepo = waitlistRepo,
                    localNotificationDao = db.localNotificationDao(),
                    appContext = context.applicationContext
                )
            )

            // 3. Only show screen if User ID is valid
            if (currentUserId != -1L) {
                val createVm: CreateTripViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CreateTripViewModel(
                                repo = tripRepo,
                                routeRepo = routeRepo,
                                notifVm = notifVm,        // 👈 Added
                                currentUserId = currentUserId // 👈 Added
                            ) as T
                        }
                    }
                )

                CreateTripScreen(
                    vm = createVm,
                    onCancel = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("createTrip") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                // Show loading while ID is fetched
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
// In your NavHost setup:

        composable(
            route = "editTrip/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId") ?: return@composable
            val routeRepo = remember { RouteRepository(db.routeDao()) }
            val context = LocalContext.current

            // 1. Get Current User ID safely
            val userIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
            val currentUserId = userIdState.value ?: -1L

            // 2. Create NotificationViewModel
            val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }
            val notifVm: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(
                    tripDao = db.tripDao(),
                    waitlistDao = db.waitlistDao(),
                    waitlistRepo = waitlistRepo,
                    localNotificationDao = db.localNotificationDao(),
                    appContext = context.applicationContext
                )
            )

            // 3. Only show screen if User ID is valid
            if (currentUserId != -1L) {
                val createVm: CreateTripViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CreateTripViewModel(
                                repo = tripRepo,
                                routeRepo = routeRepo,
                                notifVm = notifVm,        // 👈 Added
                                currentUserId = currentUserId // 👈 Added
                            ) as T
                        }
                    }
                )
                EditTripScreen(
                    tripId = tripId,
                    vm = createVm,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        // When saved, simply pop back.
                        // The DB update will auto-refresh the previous screen.
                        navController.popBackStack()
                    }
                )
            }
        }

        // 🔍 Search
        composable("search") {
            SearchScreen(
                vm = homeVm,
                onBack = { navController.popBackStack() },
                onAvatarClick = { /* profile */ },
                onTripClick = { id -> navController.navigate("tripDetails/$id") }
            )
        }

        // 📄 Trip Details
        composable("tripDetails/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable
            val context = LocalContext.current
            val db = AppDatabase.get(context)
            // 1. Get Current User ID (Safely)
            val currentUserIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
            val currentUserId = currentUserIdState.value ?: -1L

            // 2. Show loading if User ID isn't ready (prevents -1 ID issues)
            if (currentUserId == -1L) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            // 3. Create NotificationViewModel dependency
            val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }
            val notifVm: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(
                    tripDao = db.tripDao(),
                    waitlistDao = db.waitlistDao(),
                    waitlistRepo = waitlistRepo,
                    localNotificationDao = db.localNotificationDao(),
                    appContext = context.applicationContext
                )
            )

            // 4. Pass everything to the updated Factory
            val vmFactory = TripDetailsViewModel.TripDetailsVmFactory(
                tripDao = db.tripDao(),
                userDao = db.userDao(),
                waitlistDao = db.waitlistDao(),
                joinRequestDao = db.joinRequestDao(),
                localNotificationDao = db.localNotificationDao(),
                tripId = tripId,
                repo=tripRepo,
                context = context.applicationContext,
                reviewRepo = reviewRepo,
                notifVm = notifVm,
                currentUserId = currentUserId
            )

            val vm: TripDetailsViewModel = viewModel(factory = vmFactory)

            TripDetailsScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onJoined = { trip ->
                    navController.navigate("tripGroup/${trip.id}") {
                        popUpTo("tripDetails/$tripId") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onWaitlist = { navController.navigate("waitlist") },
                onEditTrip = { tripId ->
                    navController.navigate("editTrip/$tripId")
                },
                onOpenProfile = { userId -> navController.navigate("userProfile/$userId") },
                onCopyItinerary = { itineraryItems ->
                    navController.navigate("createTrip?templateId=$tripId")
                }
            )
        }

        // 👥 Trip Group


        // 🗳️ Create Poll
        composable("createPoll/{tripId}") { backStack ->
            val tripId =
                backStack.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable
            val voteVm: VoteViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return VoteViewModel(VoteRepository(db.voteDao(), db.tripDao(), db.userDao()), tripId) as T
                    }
                }
            )
            CreateVoteScreen(
                tripId = tripId,
                voteVm = voteVm,
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }

        // 2. ADD RAIL
        composable(
            route = "addRail/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull()

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddRailScreen(
                    tripId = tripId,
                    vm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }
        // 3. ADD CRUISE
        composable(
            route = "addCruise/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // empty = add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull() // ✅ null if empty

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddCruiseScreen(
                    tripId = tripId,
                    vm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }
        composable(
            route = "addFlight/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull()

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddFlightScreen(
                    tripId = tripId,
                    plannerVm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }

        // 5. ADD LODGING
        composable(
            route = "addLodging/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // empty = add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull()

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddLodgingScreen(
                    tripId = tripId,
                    plannerVm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }

        // 6. ADD RESTAURANT
        composable(
            route = "addRestaurant/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull()

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddRestaurantScreen(
                    tripId = tripId,
                    plannerVm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }
            // 7. ADD CAR RENTAL
        composable(
            route = "addCarRental/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = "" // empty = add mode
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L
            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId = planIdStr.toLongOrNull() // ✅ null if empty

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddCarRentalScreen(
                    tripId = tripId,
                    vm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }

        // 8. ADD ACTIVITY
        composable(
            route = "addActivity/{tripId}?planId={planId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("planId") {
                    type = NavType.StringType
                    defaultValue = ""   // ✅ empty means “not editing”
                }
            )
        ) { backStack ->
            val tripId = backStack.arguments?.getLong("tripId") ?: 0L

            val planIdStr = backStack.arguments?.getString("planId").orEmpty()
            val planId: Long? = planIdStr.toLongOrNull() // ✅ null if empty / invalid

            val plannerVm = rememberItineraryViewModel(tripId, db)

            if (plannerVm == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AddActivityScreen(
                    tripId = tripId,
                    plannerVm = plannerVm,
                    onClose = { navController.popBackStack() },
                    editingPlanId = planId
                )
            }
        }
// ➤ Single-item editor (add/update)
            composable("itineraryEdit/{tripId}/{itemId}") { backStack ->
                val tripId = backStack.arguments?.getString("tripId")!!.toLong()
                val itemId = backStack.arguments?.getString("itemId")!!.toLong()

                ItineraryEditScreen(
                    tripId = tripId,
                    itemId = itemId,
                    repo = itineraryRepo,
                    tripRepo = tripRepo,
                    onBack = { navController.popBackStack() }
                )
            }


            composable("joinRequests/{tripId}") { backStack ->
                val tripId = backStack.arguments?.getString("tripId")!!.toLong()
                val ctx = LocalContext.current
                val db = remember { AppDatabase.get(ctx) }

                val vm: JoinRequestsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = JoinRequestsViewModel.Factory(
                        tripDao = db.tripDao(),
                        userDao = db.userDao(),
                        joinRequestDao = db.joinRequestDao(),
                        tripId = tripId
                    )
                )

                JoinRequestsScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }


            composable("community") {
                val context = LocalContext.current

                // Keep the DB/repo/factory stable across recompositions:
                val db = remember { AppDatabase.get(ctx) }
                val postRepo = remember {
                    PostRepository(
                        dao = db.postDao(),
                        commentDao = db.commentDao(),
                        userDao = db.userDao() // 👈 Add this
                    )
                }

                val postVm: PostViewModel = viewModel(
                    factory = remember {
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return PostViewModel(postRepo) as T
                            }
                        }
                    }
                )

                // Collect posts from the VM (preferred to using produceState)
                val posts by postVm.posts.collectAsState(initial = emptyList())

                // Get current user id — use a Flow/State if AuthPrefs exposes one.
                // Example: if AuthPrefs.getUserId(context) returns Flow<Long?>, collect it:
                val currentUserId by produceState(initialValue = 0L, key1 = context) {
                    // safe to run suspend here
                    value = AuthPrefs.getUserId(context).firstOrNull() ?: 0L
                }

                CommunityFeedScreen(
                    posts = posts,
                    currentUserId = currentUserId,
                    onBack = { navController.popBackStack() },
                    onProfileClick = { userId -> navController.navigate("profile/$userId") },
                    onCreatePost = { navController.navigate("createPost") },
                    onPostClick = { postId -> navController.navigate("postDetail/$postId") },
                    onLikeClick = { postId, isLiked ->
                        postVm.onLikePost(postId, isLiked, currentUserId)   // ✅ pass userId
                    },
                    onSaveClick = { postId, isSaved -> postVm.onSavePost(postId, isSaved) },
                    onDeletePost = { postId -> postVm.onDeletePost(postId) },
                    onCommentSubmit = { postId, text ->
                        postVm.addComment(postId, currentUserId, text)
                    },
                    observeComments = postVm::observeComments
                )

            }

            composable("tripVotes/{tripId}") { backStack ->
                val tripId = backStack.arguments?.getString("tripId")!!.toLong()
                val voteRepo = VoteRepository(db.voteDao(), db.tripDao(), db.userDao())

                // ✅ Create ViewModel with factory
                val voteVm: VoteViewModel = viewModel(
                    factory = VoteViewModelFactory(voteRepo, tripId)
                )

                // ✅ Render vote screen
                TripVoteScreen(
                    voteVm = voteVm,
                    onBack = { navController.popBackStack() }
                )
            }


            // 📌 Post Details
            composable("postDetail/{postId}") { backStackEntry ->
                val postId =
                    backStackEntry.arguments?.getString("postId")?.toLongOrNull()
                        ?: return@composable

                val postVm: PostViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PostViewModel(PostRepository(db.postDao(), db.userDao(), db.commentDao())) as T
                        }
                    }
                )

                val posts by postVm.posts.collectAsState(initial = emptyList())
                val postWithUser = posts.find { it.post.id == postId }

                val ctxLocal = LocalContext.current
                val currentUserId: Long? by AuthPrefs.getUserId(ctxLocal)
                    .collectAsState(initial = null)

                if (postWithUser != null) {
                    PostDetailScreen(
                        postWithUser = postWithUser,
                        vm = postVm,
                        currentUserId = currentUserId ?: -1L,  // <-- coalesce
                        onBack = { navController.popBackStack() },
                        onProfileClick = { userId ->
                            // TODO: use your real route here
                            navController.navigate("userProfile/$userId")
                        }
                    )
                }
            }


            // ✍️ Create Post
            composable("createPost") {
                CreatePostScreen(
                    repo = PostRepository(db.postDao(), db.userDao(), db.commentDao()),
                    onBack = { navController.popBackStack() }
                )
            }
        composable("messages") {
            val ctx = LocalContext.current
            val db = remember { AppDatabase.get(ctx) }

            val currentUserId: Long? by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

            if (currentUserId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                return@composable
            }

            val uid = currentUserId!!
            val chatRepo = rememberChatRepository(db)

            val chatListVm: ChatListViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ChatListViewModel(
                            chatRepo = chatRepo,
                            userDao = db.userDao(),
                            messageDao = db.messageDao(),
                            currentUserId = uid
                        ) as T
                    }
                }
            )

            ChatListScreen(
                viewModel = chatListVm,

                onOpenChat = { chatId, title ->
                    if (chatId.startsWith("group:")) {
                        val tripId = chatId.removePrefix("group:").toLongOrNull() ?: return@ChatListScreen
                        val encodedTitle = Uri.encode(title)
                        navController.navigate("groupChat/$tripId/$encodedTitle")
                    } else {
                        // ✅ encode dm id before navigating
                        val encodedChatId = Uri.encode(chatId)
                        navController.navigate("directChat/$encodedChatId")
                    }
                },

                onDeleteChat = { chatId ->
                    chatListVm.deleteChat(chatId)
                },

                onBack = { navController.popBackStack() }
            )
        }


        composable(
            route = "directChat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { entry ->
            val ctx = LocalContext.current
            val db = remember { AppDatabase.get(ctx) }

            val loggedInUserId: Long? by AuthPrefs.getUserId(ctx).collectAsState(initial = null)
            if (loggedInUserId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            val currentUserIdStr = loggedInUserId!!.toString()

            // decode nav arg
            val encodedChatId = entry.arguments?.getString("chatId") ?: return@composable
            val chatId = Uri.decode(encodedChatId)

            val chatRepo = rememberChatRepository(db)
            val userDao = remember { db.userDao() }

            class ChatVmFactory(
                private val initialChatId: String,
                private val currentUserId: String
            ) : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(
                        initialChatId = initialChatId,
                        currentUserId = currentUserId,
                        repository = chatRepo,
                        userDao = userDao,
                        appContext = ctx.applicationContext,
                        ioDispatcher = Dispatchers.IO
                    ) as T
                }
            }

            val chatVm: ChatViewModel = viewModel(
                viewModelStoreOwner = entry,
                factory = remember(chatId, currentUserIdStr) { ChatVmFactory(chatId, currentUserIdStr) }
            )

            ChatScreen(
                viewModel = chatVm,
                defaultChatTitle = "Chat",
                onBack = { navController.popBackStack() },

                onMyAvatarClick = { navController.navigate("profile") },

                // ✅ FIX: explicitly type the lambda param
                onOtherAvatarClick = { userId: String ->
                    navController.navigate("userProfile/${Uri.encode(userId)}")
                }
            )
        }


        composable(
                route = "groupChat/{tripId}/{tripName}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.LongType },
                    navArgument("tripName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val context = LocalContext.current
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: 0L
                val tripNameRaw = backStackEntry.arguments?.getString("tripName") ?: "Group"
                val tripName = URLDecoder.decode(tripNameRaw, StandardCharsets.UTF_8.name())

                // 3. Get Current User ID
                val userIdState by AuthPrefs.getUserId(context).collectAsState(initial = null)
                val myUserId = userIdState

                // 4. Fetch User Details (Name/Avatar) asynchronously
                val currentUser by produceState<UserEntity?>(initialValue = null, key1 = myUserId) {
                    if (myUserId != null) {
                        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            // Ensure 'db' is accessible here
                            db.userDao().getUserById(myUserId!!)
                        }
                    }
                }

                // 5. Show Screen
                if (myUserId != null) {
                    val vm = remember(myUserId, currentUser, tripId, tripName) {
                        GroupChatViewModel(
                            repo = chatRepo,
                            tripId = tripId,
                            tripName = tripName,
                            currentUserId = myUserId,
                            currentUserName = currentUser?.name ?: "Me",
                            currentUserAvatar = currentUser?.profilePhoto,
                            context = context
                        )
                    }

                GroupChatScreen(
                        tripId = tripId,
                        tripName = tripName,
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    // Loading state while fetching User ID
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable("notification") {
                val context = LocalContext.current
                val db = AppDatabase.get(context)
                val notifDao = db.localNotificationDao()

                // Get current User ID safely
                val currentUserIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
                val currentUserId = currentUserIdState.value ?: -1L

                if (currentUserId != -1L) {
                    val vm: NotificationCenterViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return NotificationCenterViewModel(notifDao, currentUserId) as T
                            }
                        }
                    )
                    // 3️⃣ Pass VM into screen
                    NotificationScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onOpenJoinRequests = { id ->
                            navController.navigate("joinRequests/$id")
                        },
                        onOpenTrip = { tripId ->
                            navController.navigate("tripDetail/$tripId")
                        },
                        onOpenTripDashboard = { tripId ->
                            // For Upcoming Trip -> Go straight to the group dashboard
                            navController.navigate("tripGroup/$tripId")
                        },
                        onOpenPayment = { tripId ->

                            navController.navigate("budget/$tripId")
                        },
                        onOpenItinerary = { tripId ->
                            navController.navigate("itineraryPlanner/$tripId")
                        }
                    )

                }
            }

            // 🕰️ Past trips
        composable("pastTrips") {
            val ctx = LocalContext.current

            val historyVm: TripHistoryViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val appContext = ctx.applicationContext
                        val dbLocal = AppDatabase.get(appContext)

                        val repo = TripHistoryRepository(
                            tripDao = dbLocal.tripDao(),
                            appContext = appContext
                        )

                        return TripHistoryViewModel(repo) as T
                    }
                }
            )

            TripHistoryScreen(
                vm = historyVm,
                onBack = { navController.popBackStack() },
                onTripClick = { tripId ->
                    // Navigate to your main trip screen
                    navController.navigate("tripGroup/$tripId")
                }
            )
        }


            // 🧾 Waitlist
            composable("waitlist") {
                val waitlistVm: WaitlistViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val db = AppDatabase.get(ctx)
                            return WaitlistViewModel(WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao())) as T
                        }
                    }
                )
                WaitlistScreen(
                    vm = waitlistVm,
                    onBack = { navController.popBackStack() }
                )
            }

        /* ... inside your NavHost ... */

// 1. Fixed Calendar Route
        composable(
            route = "calendar/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStack ->

            // Explicitly handle Long? -> Long conversion to avoid type mismatches
            val tripIdStr = backStack.arguments?.getString("tripId")
            val tripId = tripIdStr?.toLongOrNull() ?: return@composable

            val context = LocalContext.current
            val db = remember { AppDatabase.get(context) }

            val expenseRepo = remember { ExpenseRepository(db.expensePaymentDao(), db.costSplitDao(), db.tripDao(), db.userDao(), db.settlementDao()) }

            // User ID check
            val currentUserIdState by AuthPrefs.getUserId(context).collectAsState(initial = null)
            val currentUserId = currentUserIdState ?: return@composable

            // ViewModels
            val calendarVm: TripCalendarViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        // 1. Initialize the new Repository
                        // Note: db.tripDao() must be available here
                        val calendarRepo = TripCalendarRepository(db.tripCalendarDao())

                        // 2. Create ViewModel with new dependencies
                        return TripCalendarViewModel(
                            tripRepository = tripRepo,
                            calendarDao = db.tripCalendarDao(),
                            calendarRepo = calendarRepo,
                            currentUserId = currentUserId,
                            tripId = tripId // ⚠️ Ensure 'tripId' is passed from your Composable arguments
                        ) as T
                    }
                }
            )

            val expenseVm: ExpenseViewModel = viewModel(
                key = "calendar_expense_${tripId}_${currentUserId}",
                factory = ExpenseViewModel.provideFactory(expenseRepo, tripId, currentUserId)
            )
            val localNotifDao = db.localNotificationDao()
            val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }

            val notifVm: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(
                    tripDao = db.tripDao(),
                    waitlistDao = db.waitlistDao(),
                    waitlistRepo = waitlistRepo,
                    localNotificationDao = localNotifDao,
                    appContext = context.applicationContext
                )
            )

            TripCalendarScreen(
                calendarVm = calendarVm,
                expenseVm = expenseVm,
                notifVm = notifVm, // <--- Pass the missing parameter
                tripId = tripId,
                currentUserId = currentUserId,
                onBack = { navController.popBackStack() },
                onGoToTrip = { targetTripId ->
                    navController.navigate("tripGroup/$targetTripId")
                },
                onOpenExpense = { tId, expenseId ->
                    // 1) Ensure budget/{tripId} exists in back stack for sharedExpenseViewModel()
                    navController.navigate("budget/$tId") {
                        launchSingleTop = true
                    }

                    // 2) Then open edit screen
                    navController.navigate("editExpense/$tId/$expenseId") {
                        launchSingleTop = true
                    }
                }

            )
        }

// 2. Fixed Split Bill Route
        navigation(
            startDestination = "tripGroup/{tripId}",
            route = "trip_flow/{tripId}" // <--- The shared VM attaches to THIS route
        ) {
            composable("tripGroup/{tripId}") { backStackEntry ->
                val tripId =
                    backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable

                // TODO: replace with real logged-in user id from your auth/session
                val currentUserId: Long = 123L

                val context = LocalContext.current
                val participantDao = remember { db.participantDao() }
                val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }
                val remoteChatStub = remember { RemoteChatStub() }

                // --- Shared chat repo ---
                val chatRepo = remember {
                    ChatRepository(
                        chatRoomDao = db.chatRoomDao(),
                        messageDao = db.messageDao(),
                        groupChatDao = db.groupChatDao(),
                        remote = remoteChatStub,
                        tripDao = db.tripDao(),
                        ioDispatcher = Dispatchers.IO
                    )
                }

                // --- Vote VM ---
                val voteVm: VoteViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return VoteViewModel(VoteRepository(db.voteDao(), db.tripDao(), db.userDao()), tripId) as T
                        }
                    }
                )

                // --- Expense VM ---
                val expenseVm: ExpenseViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val repo = ExpenseRepository(
                                db.expensePaymentDao(),
                                db.costSplitDao(),
                                db.tripDao(),
                                db.userDao(),
                                db.settlementDao()
                            )
                            @Suppress("UNCHECKED_CAST")
                            return ExpenseViewModel(
                                repo = repo,
                                tripId = tripId,
                                currentUserId = currentUserId
                            ) as T
                        }
                    }
                )

                // --- ChatViewModel factory (Long ids) ---
                val userDao = remember { db.userDao() }

                class ChatViewModelFactoryLongIds(
                    private val initialChatIdLong: Long,
                    private val currentUserIdLong: Long,
                    private val repository: ChatRepository,
                    private val userDao: UserDao,
                    private val appContext: Context,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
                ) : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (!modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                        }

                        val initialChatId =
                            if (initialChatIdLong == 0L) "" else initialChatIdLong.toString()
                        val currentUserId = currentUserIdLong.toString()

                        @Suppress("UNCHECKED_CAST")
                        return ChatViewModel(
                            initialChatId = initialChatId,
                            currentUserId = currentUserId,
                            repository = repository,
                            userDao = userDao,
                            appContext = appContext,
                            ioDispatcher = ioDispatcher
                        ) as T
                    }
                }

                val chatFactory = remember {
                    ChatViewModelFactoryLongIds(
                        initialChatIdLong = 0L,
                        currentUserIdLong = currentUserId,
                        repository = chatRepo,
                        appContext = context,
                        userDao = userDao
                    )
                }

                val chatVm: ChatViewModel = viewModel(factory = chatFactory)
                val localNotifDao = db.localNotificationDao()
                val notifVm: NotificationViewModel = viewModel(
                    factory = NotificationViewModelFactory(
                        tripDao = db.tripDao(),
                        waitlistDao = db.waitlistDao(),
                        waitlistRepo = waitlistRepo,
                        localNotificationDao = localNotifDao,
                        appContext = context.applicationContext
                    )
                )

                // ---------- NEW: load current user + create ReviewViewModel ----------

                // load the current UserEntity from DB (if you have such a DAO method)
                val currentUserState = produceState<UserEntity?>(initialValue = null, currentUserId) {
                    value = userDao.findById(currentUserId)
                }

                val reviewVm: ReviewViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ReviewViewModel(
                                repo = reviewRepo,
                                userDao = userDao
                            ) as T
                        }
                    }
                )

                // ---------- Load trip entity and show TripGroupScreen ----------

                val tripState = produceState<TripEntity?>(initialValue = null, tripId) {
                    value = tripRepo.getTripById(tripId)
                }

                tripState.value?.let { trip ->
                    TripGroupScreen(
                        trip = trip,
                        repo = tripRepo,
                        participantDao = participantDao,
                        chatRepo = chatRepo,
                        voteVm = voteVm,
                        expenseVm = expenseVm,
                        notifVm = notifVm,
                        reviewVm = reviewVm,   // ✅ now passed
                        navController = navController,
                        onBack = { navController.popBackStack() },
                        onOpenExpense = { navController.navigate("budget/$tripId") },
                        onEditTrip = { tripId ->
                            navController.navigate("editTrip/$tripId")
                        }
                    )
                }
            }

            // 1. Itinerary Screen
            composable(
                route = "itineraryPlanner/{tripId}",
                arguments = listOf(navArgument("tripId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: 0L

                // Helper for Itinerary VM
                val plannerVm = rememberItineraryViewModel(tripId, db)

                val tripVm: TripHeaderViewModel = viewModel(
                    factory = TripHeaderViewModel.provideFactory(db.tripDao(), tripId)
                )
                val routeVm: RoutePlannerViewModel = viewModel(
                    factory = RoutePlannerViewModel.provideFactory(RouteRepository(db.routeDao()), tripId)
                )

                val trip by tripVm.trip.collectAsState()
                val stops by routeVm.stops.collectAsState()

                if (plannerVm == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val plans by plannerVm.plans.collectAsState()
                    val planExpenseMap by plannerVm.planExpenseMap.collectAsState()
                    val availableTrips by plannerVm.availableTargetTrips.collectAsState()

                    // 1. ✅ Collect the selected expense state for the dialog
                    val selectedExpense by plannerVm.selectedExpense.collectAsState()
                    val comments by plannerVm.currentPlanComments.collectAsState()
                    val tripStart = trip?.startDate ?: java.time.LocalDate.now()
                    val tripEnd = trip?.endDate ?: tripStart
                    val dateRangeLabel = remember(trip?.startDate, trip?.endDate) {
                        "${trip?.startDate} - ${trip?.endDate}"
                    }

                    // 2. ✅ Show the Dialog overlay when an expense is selected
                    selectedExpense?.let { details ->
                        ExpensePreviewDialog(
                            details = details,
                            onDismiss = { plannerVm.clearSelectedExpense() },
                            onEdit = { id ->
                                // Close dialog first, then navigate to the actual Edit Screen
                                plannerVm.clearSelectedExpense()
                                navController.navigate("editExpense/$tripId/$id")
                            }
                        )
                    }

                    ItineraryPlannerScreen(
                        tripTitle = trip?.name ?: "Trip",
                        dateRangeLabel = dateRangeLabel,
                        stops = stops,
                        plans = plans,
                        planExpenseMap = planExpenseMap,
                        tripStart = tripStart,
                        tripEnd = tripEnd,
                        onBack = { navController.popBackStack() },
                        onAdd = { /* ... */ },
                        onOpenStop = { /* ... */ },
                        onAddPlanType = { type ->
                            when (type) {
                                PlanType.Activity -> navController.navigate("addActivity/$tripId")
                                PlanType.Flight -> navController.navigate("addFlight/$tripId")
                                PlanType.Lodging -> navController.navigate("addLodging/$tripId")
                                PlanType.Restaurant -> navController.navigate("addRestaurant/$tripId")
                                PlanType.CarRental -> navController.navigate("addCarRental/$tripId")
                                PlanType.Rail -> navController.navigate("addRail/$tripId")
                                PlanType.Cruise -> navController.navigate("addCruise/$tripId")
                            }
                        },
                        onDeletePlan = { plannerVm.deletePlan(it) },
                        onAddExpenseFromPlan = { plan ->
                            navController.navigate(
                                "addExpense/$tripId?source=itinerary&linkedPlanId=${plan.id}&desc=${Uri.encode(plan.title)}&date=${plan.date}"
                            )

                        },
                        currentComments = comments,
                        onSelectPlan = { planId -> plannerVm.loadComments(planId) },
                        // 3. ✅ CHANGED: Call VM to show dialog instead of navigating immediately
                        onViewExpense = { expenseId ->
                            plannerVm.selectExpenseForPreview(expenseId)
                        },
                        onPostComment = { planId, text -> plannerVm.postComment(planId, text) },
                        availableTrips = availableTrips,
                        onCopyToTrip = { targetId -> plannerVm.copyItineraryToTrip(targetId) },
                        onEditPlan = { planId ->
                            val planToEdit = plans.find { it.id == planId }
                            planToEdit?.let { plan ->
                                val route = when (plan.type) {
                                    PlanType.Activity -> "addActivity/$tripId?planId=$planId"
                                    PlanType.Flight -> "addFlight/$tripId?planId=$planId"
                                    PlanType.Lodging -> "addLodging/$tripId?planId=$planId"
                                    PlanType.Restaurant -> "addRestaurant/$tripId?planId=$planId"
                                    PlanType.CarRental -> "addCarRental/$tripId?planId=$planId"
                                    PlanType.Rail -> "addRail/$tripId?planId=$planId"
                                    PlanType.Cruise -> "addCruise/$tripId?planId=$planId"
                                }
                                navController.navigate(route)
                            }
                        }
                    )
                }
            }
            // 2. Add Expense
            composable(
                route = "addExpense/{tripId}?linkedPlanId={linkedPlanId}&desc={desc}&date={date}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.LongType },
                    navArgument("source") { nullable = true; defaultValue = "budget" },
                    navArgument("linkedPlanId") { nullable = true; defaultValue = null },
                    navArgument("desc") { nullable = true; defaultValue = null },
                    navArgument("date") { nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: 0L
                val currentUserId = rememberCurrentUserId() ?: return@composable
                val source = backStackEntry.arguments?.getString("source") ?: "budget"
                // Arguments
                val linkedPlanId = backStackEntry.arguments?.getString("linkedPlanId")?.toLongOrNull()
                val desc = backStackEntry.arguments?.getString("desc") ?: ""
                val dateStr = backStackEntry.arguments?.getString("date")
                val initialDate = if (dateStr != null) LocalDate.parse(dateStr) else LocalDate.now()

                // Shared VM
                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)

                // Notification VM setup...
                val waitlistRepo = remember { WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()) }
                val notifVm: NotificationViewModel = viewModel(
                    factory = NotificationViewModelFactory(
                        tripDao = db.tripDao(),
                        waitlistDao = db.waitlistDao(),
                        waitlistRepo = waitlistRepo,
                        localNotificationDao = db.localNotificationDao(),
                        appContext = ctx.applicationContext
                    )
                )

                AddExpenseFlow(
                    tripId = tripId,
                    vm = vm,
                    notifVm = notifVm,
                    linkedPlanId = linkedPlanId,
                    initialDescription = desc,
                    initialDate = initialDate,
                    onBack = { navController.popBackStack() },

                    // ✅ IMPORTANT: do NOT navigate to budget here
                    onAfterSave = {
                        // remove addExpense from stack and return to where we came from
                        navController.popBackStack()
                    }
                )
            }


            // 3. Edit Expense
            composable(
                route = "editExpense/{tripId}/{expenseId}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.LongType },
                    navArgument("expenseId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: 0L
                val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
                val currentUserId = rememberCurrentUserId() ?: return@composable

                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)

                EditExpenseScreen(
                    expenseId = expenseId,
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    onOpenSplitBill = {
                        navController.navigate("splitBill/$tripId/$expenseId")
                    }
                )
            }

            // 4. Split Bill
            composable("splitBill/{tripId}/{expenseId}") { backStackEntry ->
                val tripIdStr = backStackEntry.arguments?.getString("tripId")
                val tripId = tripIdStr?.toLongOrNull() ?: 0L
                val expenseIdStr = backStackEntry.arguments?.getString("expenseId")
                val expenseId = expenseIdStr?.toLongOrNull() ?: 0L
                val currentUserId = rememberCurrentUserId() ?: return@composable

                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)

                // ... (rest of your state collection) ...
                val people by vm.participants.collectAsState()
                val payerUser by vm.payerUserFlow.collectAsState()
                val currentShares by vm.currentSharesMapFlow.collectAsState()
                val expenseUi by vm.observeExpense(expenseId).collectAsState(initial = null)
                val currency by vm.draftCurrency.collectAsState()

                SplitBillScreen(
                    vm = vm,
                    totalAmount = expenseUi?.amount ?: 0.0,
                    currency = currency.ifBlank { "MYR" },
                    participants = people,
                    initialShares = currentShares,
                    payerUser = payerUser,
                    onPickPayerDone = { vm.setPayerUser(it) },
                    onDone = { _, _, _ -> navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            // 5. Budget Screen
            composable("budget/{tripId}") { backStackEntry ->
                val tripIdStr = backStackEntry.arguments?.getString("tripId")
                val tripId = tripIdStr?.toLongOrNull() ?: return@composable
                val currentUserId = rememberCurrentUserId() ?: return@composable

                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)
                val tripName = vm.tripNameFlow.collectAsState(initial = "Trip").value

                BudgetScreen(
                    vm = vm,
                    tripId = tripId,
                    tripName = tripName,
                    navController = navController,
                    onBack = { navController.popBackStack() },
                    onAddExpense = { navController.navigate("addExpense/$tripId") },
                    onOpenBalance = { navController.navigate("balance/$tripId") },
                    onOpenEditExpense = { expenseId ->
                        navController.navigate("editExpense/$tripId/$expenseId")
                    }
                )
            }

            // 6. Balance Screen
            composable("balance/{tripId}") { backStackEntry ->
                val tripIdStr = backStackEntry.arguments?.getString("tripId")
                val tripId = tripIdStr?.toLongOrNull() ?: return@composable
                val currentUserId = rememberCurrentUserId() ?: return@composable

                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)
                val currencyCode = vm.draftCurrency.collectAsState().value.ifBlank { "MYR" }

                BalanceScreen(
                    tripId = tripId,
                    vm = vm,
                    currencyCode = currencyCode,
                    onBackClick = { navController.popBackStack() },
                    onSettleClick = {},
                    onNavExpenses = { navController.navigate("budget/$tripId") },
                    onNavInsights = {},
                    onNavAddExpense = { navController.navigate("addExpense/$tripId") },
                    onNavBalance = {},
                    onNavExport = {},
                    onPersonClick = { userId ->
                        // ✅ Update route to pass tripId
                        navController.navigate("balanceDetail/$tripId/$userId")
                    }
                )
            }

            // 7. Balance Detail (Updated to include tripId)
            composable(
                route = "balanceDetail/{tripId}/{userId}", // <--- Updated Route
                arguments = listOf(
                    navArgument("tripId") { type = NavType.LongType },
                    navArgument("userId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong("tripId") ?: 0L
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                val currentUserId = rememberCurrentUserId() ?: return@composable

                // Now we can use the shared VM easily
                val vm = sharedExpenseViewModel(navController, tripId, expenseRepo, currentUserId)

                BalanceDetailScreen(
                    vm = vm,
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }
        }


        composable(
                route = "userProfile/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStack ->
                val otherUserId = backStack.arguments?.getLong("userId") ?: return@composable
                val tripHistoryDao = db.tripHistoryDao()
            val context = LocalContext.current
            val currentUserId by AuthPrefs.getUserId(context).collectAsState(initial = null)
                val profileVm: ProfileViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                                @Suppress("UNCHECKED_CAST")
                                return ProfileViewModel(
                                    repo = userRepo,   // make sure you have this variable in scope
                                    postRepo = postRepo,
                                    tripHistoryDao = tripHistoryDao,
                                    reviewRepo = reviewRepo
                                ) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
                        }
                    }
                )

            PublicProfileScreen(
                vm = profileVm,
                userId = otherUserId,
                currentUserId = currentUserId ?: 0L,
                onBack = { navController.popBackStack() },
                onReport = { userId ->
                    navController.navigate("report_user/$userId")
                },
                onPostClick = { postId ->
                    navController.navigate("postDetail/$postId")
                },
                onOpenProfile = { reviewerId ->
                    // Prevent navigating to self if clicking your own review
                    if (reviewerId != currentUserId) {
                        navController.navigate("userProfile/$reviewerId")
                    }
                }
            )
        }


            composable("checklist/{tripId}") { backStack ->
                val tripId =
                    backStack.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable
                val ctx = LocalContext.current
                val db = remember(ctx) { AppDatabase.get(ctx) }

                // Get current User ID (Synchronously for factory, or handle loading state)
                // Ideally, pass this via argument or collect safely.
                // For simplicity here, we'll assume auth is loaded or use a placeholder if null/loading logic exists elsewhere.
                val currentUserIdState = AuthPrefs.getUserId(ctx).collectAsState(initial = -1L)
                val currentUserId = currentUserIdState.value ?: -1L

                if (currentUserId == -1L) {
                    // Show loading or empty
                    return@composable
                }

                val userDao = remember { db.userDao() }

                val factory = remember(tripId, currentUserId) {
                    ChecklistVMFactory(
                        repo = checklistRepo,
                        tripRepository = tripRepo,
                        chatRepository = chatRepo, // 👈 Pass Chat Repo
                        userDao = userDao,         // 👈 Pass User DAO
                        tripId = tripId,
                        userId = currentUserId
                    )
                }
                val vm: ChecklistViewModel = viewModel(factory = factory)

                // Load trip name for the top bar
                val tripState = produceState(initialValue = null as TripEntity?, tripId) {
                    value = db.tripDao().getTripById(tripId)
                }
                val currentTripName = tripState.value?.name ?: "Trip"

                ChecklistScreen(
                    vm = vm,
                    currentTripName = currentTripName, // Renamed param for clarity
                    currentTripId = tripId,            // Pass current ID to highlight/exclude
                    onBack = { navController.popBackStack() },
                    onSwitchTrip = { newTripId ->
                        // Navigate to the checklist of the selected trip
                        navController.navigate("checklist/$newTripId") {
                            popUpTo("checklist/$tripId") {
                                inclusive = true
                            } // Replace current checklist screen
                        }
                    },
                    onOpenCategory = { cat ->
                        navController.navigate("checklistCategory/$tripId/${cat.categoryId}")
                    },
                    onOpenAddLists = {
                        navController.navigate("addChecklistCategories/$tripId")
                    }
                )
            }
// Detail page
            composable("checklistCategory/{tripId}/{categoryId}") { backStack ->
                val tripId =
                    backStack.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable
                val categoryId = backStack.arguments?.getString("categoryId")?.toLongOrNull()
                    ?: return@composable

                // 1. Get User ID
                val currentUserIdState = AuthPrefs.getUserId(ctx).collectAsState(initial = -1L)
                val currentUserId = currentUserIdState.value ?: -1L

                // 3. 🔹 FIX: Use the updated Factory constructor
                val factory = remember(tripId, currentUserId) {
                    ChecklistVMFactory(
                        repo = checklistRepo,
                        tripRepository = tripRepo,
                        chatRepository = chatRepo,
                        userDao = userDao,
                        tripId = tripId,
                        userId = currentUserId
                    )
                }

                val vm: ChecklistViewModel = viewModel(factory = factory)

                CategoryDetailScreen(
                    vm = vm,
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("addChecklistCategories/{tripId}") { backStack ->
                val tripId =
                    backStack.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable
                val ctx = LocalContext.current
                val appCtx = remember { ctx.applicationContext }
                val db = remember(appCtx) { AppDatabase.get(appCtx) }

                // 1. Get User ID
                val currentUserIdState = AuthPrefs.getUserId(ctx).collectAsState(initial = -1L)
                val currentUserId = currentUserIdState.value ?: -1L

                // 2. Initialize Repositories

                val userDao = remember { db.userDao() }

                // 🔹 UPDATE FACTORY
                val factory = remember(tripId, currentUserId) {
                    ChecklistVMFactory(
                        repo = checklistRepo,
                        tripRepository = tripRepo,
                        chatRepository = chatRepo,
                        userDao = userDao,
                        tripId = tripId,
                        userId = currentUserId
                    )
                }

                val vm: ChecklistViewModel = viewModel(factory = factory)

                AddListScreenWithVM(
                    vm = vm,
                    onClose = { navController.popBackStack() }
                )
            }

            // 🚩 Report user
            composable("report_user/{userId}",arguments = listOf(navArgument("userId") { type = NavType.LongType })) { backStackEntry ->
                val targetUserId = backStackEntry.arguments?.getLong("userId") ?: 0L
                val context = LocalContext.current

                val currentUser by AuthPrefs.getUserId(context).collectAsState(initial = -1L)

                val reportVm: ReportViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val dbLocal = AppDatabase.get(ctx)
                            @Suppress("UNCHECKED_CAST")
                            return ReportViewModel(ReportRepository(dbLocal.reportDao())) as T
                        }
                    }
                )

                ReportUserScreen(
                    targetUserId = targetUserId,
                    currentUserId = currentUser ?: -1L,
                    reportVm = reportVm,
                    onBack = { navController.popBackStack() },
                    navBack = {
                        android.widget.Toast.makeText(context, "Report submitted", android.widget.Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            }

        }
    }
    /** AuthGate waits for DataStore to emit, then routes to home/login/register. */
    @Composable
    private fun AuthGate(navController: NavHostController) {
        val ctx = LocalContext.current

        // Only block on 'registered' loading; userId may be null/not set yet.
        val registered: Boolean? by AuthPrefs.isRegistered(ctx).collectAsState(initial = null)
        val userId: Long? by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

        if (registered == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return
        }

        // Coerce userId for routing logic (null -> -1L = guest)
        val uid = userId ?: -1L

        LaunchedEffect(registered, uid) {
            val dest = if (registered == true && uid > 0) "home" else "login"
            navController.navigate(dest) {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }

@Composable
fun rememberItineraryViewModel(
    tripId: Long,
    db: AppDatabase,
): ItineraryPlannerViewModel? {
    val context = LocalContext.current

    // 1. Get User ID
    val userIdState = AuthPrefs.getUserId(context).collectAsState(initial = -1L)
    val currentUserId = userIdState.value

    if (currentUserId == null || currentUserId == -1L) {
        return null
    }

    // 2. ✅ NEW: Create ExpenseRepository here
    val expenseRepo = remember {
        com.example.tripshare.data.repo.ExpenseRepository(
            paymentDao = db.expensePaymentDao(),
            splitDao = db.costSplitDao(),
            tripDao = db.tripDao(),
            userDao = db.userDao(),
            settlementDao = db.settlementDao()
        )
    }

    // 3. Create Dependencies
    val notifVm: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(
            tripDao = db.tripDao(),
            waitlistDao = db.waitlistDao(),
            waitlistRepo = WaitlistRepository(db.waitlistDao(),db.tripDao(),db.userDao()),
            localNotificationDao = db.localNotificationDao(),
            appContext = context.applicationContext
        )
    )

    // 4. Return ViewModel
    return viewModel(
        key = "ItineraryVM_${tripId}_${currentUserId}",
        factory = ItineraryPlannerViewModel.provideFactory(
            repo = ItineraryRepository(db.itineraryDao(),db.tripDao()),
            tripId = tripId,
            notifVm = notifVm,
            expenseRepo = expenseRepo,
            tripDao = db.tripDao(),
            tripCommentDao = db.tripCommentDao(),
            currentUserId = currentUserId
        )
    )
}

@Composable
fun sharedExpenseViewModel(
    navController: NavController,
    tripId: Long,
    expenseRepo: ExpenseRepository,
    currentUserId: Long
): ExpenseViewModel {
    // ✅ FIX: Scope to the PARENT GRAPH ("trip_flow"), not the itinerary screen.
    // We construct the route string manually to match the graph definition below.
    val graphRoute = "trip_flow/$tripId"

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(graphRoute)
    }

    return viewModel(
        viewModelStoreOwner = parentEntry,
        factory = ExpenseViewModel.provideFactory(
            repo = expenseRepo,
            tripId = tripId,
            currentUserId = currentUserId
        )
    )
}

// put this near your NavHost (same file)
    private fun NavController.backToTripGroup(tripId: Long) {
        val route = "tripGroup/$tripId"           // <-- use your exact TripGroup route
        val popped = popBackStack(route = route, inclusive = false)
        if (!popped) {
            navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

private fun planTypeToExpenseCategory(type: PlanType): String {
    return when (type) {
        PlanType.Restaurant -> "Food"
        PlanType.Lodging -> "Hotel"
        PlanType.Activity -> "Activity"
        PlanType.Flight,
        PlanType.CarRental,
        PlanType.Rail,
        PlanType.Cruise -> "Transport"
    }
}

