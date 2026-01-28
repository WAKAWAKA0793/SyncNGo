package com.example.tripshare.ui.community

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.model.PostEntity
import com.example.tripshare.data.repo.PostRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.compose.material.icons.filled.Image as ImageIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    repo: PostRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // 👇 NEW: hold the selected image Uri (from gallery)
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 👇 NEW: launcher to pick from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), // opens system picker
    ) { url: Uri? ->
        pickedImageUri = url // store it so we can show preview + save later
    }

    val canPost = title.isNotBlank() && content.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        val userId = AuthPrefs.getUserId(ctx).firstOrNull() ?: -1L
                        val user = if (userId != -1L) db.userDao().findById(userId) else null

                        if (!canPost || user == null) {
                            error = "You must be logged in and fill all fields"
                            return@launch
                        }

                        // 1. COPY THE IMAGE (Fix for disappearing images)
                        val permanentImagePath = pickedImageUri?.let { uri ->
                            ctx.saveImageToInternalStorage(uri)
                        }

                        // 2. SAVE THE PERMANENT PATH TO DB
                        repo.addPost(
                            PostEntity(
                                title = title.trim(),
                                content = content.trim(),
                                userName = user.name,
                                userId = user.id,
                                userAvatar = user.profilePhoto,
                                // Use the new path, NOT pickedImageUri
                                imageUrl = permanentImagePath
                            )
                        )

                        onBack()
                    }
                },
                enabled = canPost,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Post")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Content
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            // Row: Add Photo button + maybe "Change" if already picked
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // open gallery for images only
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Icon(
                        Icons.Default.ImageIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (pickedImageUri == null) "Add photo"
                        else "Change photo"
                    )
                }

                if (pickedImageUri != null) {
                    Text(
                        text = "1 image selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Preview (only if user picked an image)
            if (pickedImageUri != null) {
                AsyncImage(
                    model = pickedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


// Helper to copy image to internal storage
fun Context.saveImageToInternalStorage(uri: Uri): String? {
    return try {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        // Create a unique file name
        val fileName = "post_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        // Return the permanent file path (as a string URI)
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}