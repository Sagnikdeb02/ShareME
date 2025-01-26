package com.example.shareme.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shareme.core.util.ShareMeViewModelProvider
import com.example.shareme.data.model.Note
import com.example.shareme.ui.screen.content.sections.EmailInputDialog
import com.example.shareme.ui.screen.content.sections.ImagePickerFromGallery
import com.example.shareme.ui.screen.edit.AddEditNoteEvent
import com.example.shareme.ui.screen.edit.AddEditNoteViewModel
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun UploadNoteScreen(
    client: Client,
    database: FirebaseDatabase,
    navController: NavController,
    viewModel: AddEditNoteViewModel = viewModel(factory = ShareMeViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showEmailDialog by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val noteBackgroundColor = remember {
        Animatable(Color(viewModel.noteColor.value.takeIf { it != -1 } ?: Color.White.toArgb()))
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackBar -> scaffoldState.snackbarHostState.showSnackbar(event.message)
                is AddEditNoteViewModel.UiEvent.SaveNote -> navController.navigateUp()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty() && selectedImageUri != null) {
                        showEmailDialog = true
                    } else {
                        Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save note")
            }
        },
        snackbarHost = { SnackbarHost(hostState = scaffoldState.snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundColor.value)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Note.noteColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.noteColor.value == colorInt) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                coroutineScope.launch {
                                    noteBackgroundColor.animateTo(Color(colorInt), tween(500))
                                }
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            BasicTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (noteTitle.isEmpty()) Text("Enter title...", color = Color.Gray)
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            BasicTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (noteContent.isEmpty()) Text("Enter content...", color = Color.Gray)
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ImagePickerFromGallery { uri -> selectedImageUri = uri }
        }
    }

    if (showEmailDialog) {
        EmailInputDialog(
            onDone = { receiverEmail ->
                showEmailDialog = false
                coroutineScope.launch {
                    selectedImageUri?.let { imageUri ->
                        val imageUrl = uploadImageToServer(imageUri, client, context)
                        saveNoteToFirebase(
                            noteTitle, noteContent, imageUrl, viewModel.noteColor.value, receiverEmail, database, scaffoldState
                        )
                    }
                }
            },
            onCancel = { showEmailDialog = false }
        )
    }
}

private suspend fun uploadImageToServer(imageUri: Uri, client: Client, context: Context): String {
    return try {
        val imageFile = uriToFile(imageUri, context) ?: throw Exception("File creation failed")
        val storage = Storage(client)
        val result = storage.createFile(
            bucketId = "6794e8a7001fcddb099c",
            fileId = ID.unique(),
            file = InputFile.fromFile(imageFile)
        )
        result.id
    } catch (e: Exception) {
        Log.e("UploadImage", "Error: ${e.message}")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
        ""
    }
}

private fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        inputStream?.use { it.copyTo(FileOutputStream(tempFile)) }
        tempFile
    } catch (e: Exception) {
        Log.e("UriToFile", "Error: ${e.message}")
        null
    }
}

private suspend fun saveNoteToFirebase(
    title: String,
    content: String,
    imageUrl: String,
    color: Int,
    receiverEmail: String,
    database: FirebaseDatabase,
    scaffoldState: androidx.compose.material.ScaffoldState
) {
    val note = FirebaseNote(
        title = title,
        content = content,
        color = color,
        imageUrl = imageUrl,
        receiverEmail = receiverEmail
    )
    database.reference.child("notes").push().setValue(note)
        .addOnSuccessListener {
            CoroutineScope(Dispatchers.Main).launch {
                scaffoldState.snackbarHostState.showSnackbar("Note sent to $receiverEmail")
            }
        }
        .addOnFailureListener { e ->
            Log.e("SaveNote", "Error: ${e.message}")
            CoroutineScope(Dispatchers.Main).launch {
                scaffoldState.snackbarHostState.showSnackbar("Failed to send note")
            }
        }
}
