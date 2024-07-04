package com.example.shareme.ui.screen.content

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shareme.R
import com.example.shareme.core.util.ShareMeViewModelProvider
import com.example.shareme.data.model.Note
import com.example.shareme.ui.screen.content.sections.OrderSection
import com.example.shareme.core.util.Screen
import kotlinx.coroutines.launch
import android.Manifest



@Composable
fun Content(
    navController: NavController,
    viewModel: NoteViewModel = viewModel(factory = ShareMeViewModelProvider.Factory)
) {

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with using the camera
            } else {
                // Permission denied, handle accordingly
            }
        }
    )

    SideEffect {
        launcher.launch(Manifest.permission.CAMERA)
    }
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()

    val alpha = if (isDarkTheme) {
        0.3f
    } else {
        0.9f
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.AddEditNoteScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF9747FF)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add new task", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_3),
                contentDescription = null,
                alignment = Alignment.TopStart,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.2f))
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(60.dp)
            ) {
                Column {
                    Text(
                        text = "Hello Sagnik",
                        fontSize = 26.sp,
                        textAlign = TextAlign.Left,
                    )
                    Text(text = "All your works are here")
                }
                Spacer(modifier = Modifier.padding(horizontal = 75.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_2),
                    contentDescription = null,
                    modifier = Modifier
                        .background(
                            color = Color(0xFFCDA9EA),
                            shape = RoundedCornerShape(30.dp)
                        )
                        .size(60.dp, 60.dp)
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonOption(
                    Title = "Send file",
                    ImageOp = R.drawable.send_file,
                    ColorOp = Color(0xFFB4C4FF),
                    context = LocalContext.current
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                ButtonOption(
                    Title = "Receive file",
                    ImageOp = R.drawable.receive_file,
                    ColorOp = Color(0xFFCFF3E9),
                    context = LocalContext.current
                )
            }

            Spacer(modifier = Modifier.padding(it))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonOption(
                    Title = "Import file",
                    ImageOp = R.drawable.img_4,
                    ColorOp = Color(0xFFC191FF),
                    context = LocalContext.current
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                ButtonOption(
                    Title = "Favourite",
                    ImageOp = R.drawable.img_5,
                    ColorOp = Color(0xFFF4D8B1),
                    context = LocalContext.current
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Your works")
                IconButton(
                    onClick = { viewModel.onEvent(NotesEvent.ToggleOrderSection) }
                ) {
                    Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")

                }
            }

            AnimatedVisibility(
                visible = state.isOrderSelectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()

            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    noteOrder = state.noteOrder,
                    onOrderChange = {
                        viewModel.onEvent(NotesEvent.Order(it))
                    }
                )
            }

            LazyColumn(

            ) {
                items(state.notes) { note ->
                    ListItem(
                        note = note,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.AddEditNoteScreen.route +
                                            "?noteId=${note.id}&noteColor=${note.color}"
                                )
                            },
                        onDeleteClick = {
                            viewModel.onEvent(NotesEvent.DeleteNote(note))
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Note deleted",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onEvent(NotesEvent.RestoreNote)
                                }
                            }
                        },
                        navController = navController
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}


@Composable
fun ListItem(
    note: Note,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    navController: NavController,
) {
    OutlinedButton(
        onClick = {
            navController.navigate(
                "edit_note_screen?noteId=${note.id}&noteTitle=${note.title}&noteContent=${note.content}&noteColor=${note.color}"
            )
        },
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .size(85.dp),
        colors = ButtonDefaults.buttonColors(Color(note.color))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable { },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.favourite),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(200.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = note.title, fontSize = 20.sp, color = Color.Black)
                //Text(text = , fontSize = 10.sp, color = Color.Black)
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable { }
            ){
                Image(
                    painter = painterResource(id = R.drawable.img_7),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_6),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
fun ButtonOption(
    Title : String,
    ImageOp : Int ,
    ColorOp : Color,
    context: Context
){

    Button(
        onClick = {
            openCamera(context)
        },
        modifier = Modifier
            .size(width = 180.dp, height = 80.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(ColorOp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ){
                Image(painter = painterResource(id = ImageOp),
                    contentDescription = null,
                    alignment = Alignment.TopStart,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(text = Title)
        }
    }
}

fun openCamera(context: Context) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    context.startActivity(cameraIntent)
}
