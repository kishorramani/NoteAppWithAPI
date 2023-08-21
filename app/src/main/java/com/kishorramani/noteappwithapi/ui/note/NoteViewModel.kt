package com.kishorramani.noteappwithapi.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kishorramani.noteappwithapi.models.NoteRequest
import com.kishorramani.noteappwithapi.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    val notesFlow get() = noteRepository.notesFlow
    val statusFlow get() = noteRepository.statusFlow

    fun getAllNotes() {
        viewModelScope.launch {
            noteRepository.getNotes()
        }
    }

    fun createNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            noteRepository.createNote(noteRequest)
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
        }
    }

    fun updateNote(id: String, noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.updateNote(id, noteRequest)
        }
    }
}