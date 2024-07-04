package com.example.shareme.core.util

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.shareme.NoteApp
import com.example.shareme.ui.screen.edit.AddEditNoteViewModel
import com.example.shareme.ui.screen.content.NoteViewModel

object ShareMeViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            NoteViewModel(accessReadMeApplication().useCases)
        }
        initializer {
            AddEditNoteViewModel(noteUseCases = accessReadMeApplication().useCases, savedStateHandle = createSavedStateHandle())
        }
    }

}

// access the application class from the initializer of the viewmodel provider.
fun CreationExtras.accessReadMeApplication(): NoteApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NoteApp)