package com.example.shareme.ui.screen.content

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shareme.core.util.ShareMeViewModelProvider
import com.example.shareme.data.model.Note
import com.example.shareme.ui.screen.edit.AddEditNoteEvent
import com.example.shareme.ui.screen.edit.AddEditNoteViewModel
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.shareme.firebase.FirebaseNote

@Composable
fun AddNoteScreen(
    navController: NavController,
    firebaseRef: DatabaseReference,
    viewModel: AddEditNoteViewModel = viewModel(factory = ShareMeViewModelProvider.Factory),
) {
    val scaffoldState = rememberScaffoldState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val noteBackgroundColor = remember {
        Animatable(
            Color(if (viewModel.noteColor.value != -1) viewModel.noteColor.value else Color.White.toArgb())
        )
    }

    val scope = rememberCoroutineScope()

    // Handle events like showing snackbar or navigating back
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val noteColor = viewModel.noteColor.value
                    val note = FirebaseNote(
                        title = title,
                        content = content,
                        color = noteColor
                    )
                    saveNoteToFirebase(firebaseRef, note)
                    navController.navigateUp() // Navigate back after saving
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundColor.value)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Color picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                                color = if (viewModel.noteColor.value == colorInt) {
                                    Color.Black
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    noteBackgroundColor.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                }
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Title input
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(text = "Enter title...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Content input
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxSize(),
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    decorationBox = { innerTextField ->
                        if (content.isEmpty()) {
                            Text(text = "Enter content...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}

/**
 * Save a note to Firebase Realtime Database
 */
fun saveNoteToFirebase(firebaseRef: DatabaseReference, note: FirebaseNote) {
    val noteId = firebaseRef.push().key
    if (noteId != null) {
        firebaseRef.child(noteId).setValue(note)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Note saved successfully!")
                } else {
                    println("Failed to save note: ${task.exception?.message}")
                }
            }
    }
}
