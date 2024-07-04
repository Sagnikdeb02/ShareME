package com.example.shareme.ui.screen.content

import com.example.shareme.data.model.Note
import com.example.shareme.domain.util.NoteOrder
import com.example.shareme.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder =NoteOrder.Date(OrderType.Descending),
    val isOrderSelectionVisible: Boolean = false
)