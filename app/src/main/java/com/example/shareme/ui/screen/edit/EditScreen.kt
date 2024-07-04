package com.example.shareme.ui.screen.edit

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shareme.core.util.ShareMeViewModelProvider
import com.example.shareme.core.component.transparentHintTextField
import com.example.shareme.data.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun EditScreen(
    navController: NavController,
    viewModel: AddEditNoteViewModel = viewModel(factory = ShareMeViewModelProvider.Factory),
    noteId: Int,
    noteTitle: String,
    noteContent: String,
    noteColor: Int
){
    val scaffoldState = rememberScaffoldState()

    val noteBackGroundAnimateble = remember {
        Animatable(
            Color(noteColor)
        )
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.initializeNoteState(noteId, noteTitle, noteContent, noteColor)

        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is AddEditNoteViewModel.UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditNoteEvent.SaveNote)
            },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save note")
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackGroundAnimateble.value)
                .padding(it)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
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
                                    noteBackGroundAnimateble.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(
                                            durationMillis = 500
                                        )
                                    )
                                }
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            transparentHintTextField(
                text = viewModel.noteTitle.value.text,
                hint = viewModel.noteTitle.value.hint,
                onValueChange = {
                    viewModel.onEvent(
                        AddEditNoteEvent.EnteredTitle(it)
                    )
                },
                onFocusChange = {
                    viewModel.onEvent(
                        AddEditNoteEvent.ChangeTitleFocus(it)
                    )
                },
                isHintVisible = viewModel.noteTitle.value.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            transparentHintTextField(
                text = viewModel.noteContent.value.text,
                hint = viewModel.noteContent.value.hint,
                onValueChange = {
                    viewModel.onEvent(
                        AddEditNoteEvent.EnteredContent(it)
                    )
                },
                onFocusChange = {
                    viewModel.onEvent(
                        AddEditNoteEvent.ChangeContentFocus(it)
                    )
                },
                isHintVisible = viewModel.noteContent.value.isHintVisible,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}
