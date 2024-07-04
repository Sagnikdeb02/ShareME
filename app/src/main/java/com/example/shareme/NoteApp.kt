package com.example.shareme

import android.app.Application
import com.example.shareme.data.local.NoteDataBase
import com.example.shareme.domain.repository.NoteRepositoryImpl
import com.example.shareme.data.repository.NoteRepository
import com.example.shareme.domain.use_case.AddNote
import com.example.shareme.domain.use_case.DeleteNote
import com.example.shareme.domain.use_case.GetNote
import com.example.shareme.domain.use_case.GetNotes
import com.example.shareme.domain.use_case.NoteUseCases


class NoteApp: Application() {
    lateinit var useCases: NoteUseCases

    override fun onCreate() {
        super.onCreate()
        val noteDatabase: NoteDataBase by lazy {
            NoteDataBase.getDatabase(this)
        }
        val repository: NoteRepository by lazy {
            NoteRepositoryImpl(noteDatabase.noteDao)
        }
        useCases = NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }
}