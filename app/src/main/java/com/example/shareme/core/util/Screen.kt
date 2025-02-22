package com.example.shareme.core.util

sealed class Screen(val route: String){
    object Content: Screen("Content_Screen")
    object AddEditNoteScreen: Screen("add_edit_note_screen")
    object EditNoteScreen: Screen("edit_note_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object LogInScreen : Screen("log_in_screen")
    object FristScreen : Screen("first_screen")
    object SignOut : Screen("sign_out")
    object AddNoteScreen : Screen("add_note_screen")
    object ShowNoteScreen : Screen("show_note_screen")
}
