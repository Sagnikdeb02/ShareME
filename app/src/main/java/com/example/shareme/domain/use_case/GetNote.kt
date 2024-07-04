package com.example.shareme.domain.use_case

import com.example.shareme.data.model.Note
import com.example.shareme.data.repository.NoteRepository

class GetNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Int): Note? {
        return repository.getNoteById(id)
    }
}