package com.example.shareme.domain.use_case

import com.example.shareme.data.model.Note
import com.example.shareme.data.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note){
        repository.deleteNote(note)
    }
}