package com.example.shareme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shareme.ui.screen.edit.EditScreen
import com.example.shareme.ui.screen.edit.addEditNoteScreen
import com.example.shareme.core.util.Screen
import com.example.shareme.ui.screen.content.Content
import com.example.shareme.ui.theme.ShareMETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ShareMETheme {
                val navController = rememberNavController()
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Content.route
                        ) {
                            composable(
                                route = Screen.Content.route
                            ) {
                                Content(navController = navController)
                            }
                            composable(
                                route = Screen.AddEditNoteScreen.route +
                                        "?noteId={noteId}&noteColor={noteColor}",
                                arguments = listOf(
                                    navArgument("noteId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument("noteColor") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                )
                            ) {
                                val color = it.arguments?.getInt("noteColor") ?: -1
                                addEditNoteScreen(
                                    navController = navController,
                                    color = color
                                )
                            }
                            composable(
                                route = Screen.EditNoteScreen.route +
                                        "?noteId={noteId}&noteTitle={noteTitle}&noteContent={noteContent}&noteColor={noteColor}",
                                arguments = listOf(
                                    navArgument("noteId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument("noteTitle") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    },
                                    navArgument("noteContent") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    },
                                    navArgument("noteColor") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    }
                                )
                            ) {
                                val noteId = it.arguments?.getInt("noteId") ?: -1
                                val noteTitle = it.arguments?.getString("noteTitle") ?: ""
                                val noteContent = it.arguments?.getString("noteContent") ?: ""
                                val noteColor = it.arguments?.getInt("noteColor") ?: -1

                                EditScreen(
                                    navController = navController,
                                    noteId = noteId,
                                    noteTitle = noteTitle,
                                    noteContent = noteContent,
                                    noteColor = noteColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
