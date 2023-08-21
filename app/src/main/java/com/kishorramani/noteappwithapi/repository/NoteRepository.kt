package com.kishorramani.noteappwithapi.repository

import com.kishorramani.noteappwithapi.api.NoteAPI
import com.kishorramani.noteappwithapi.models.NoteRequest
import com.kishorramani.noteappwithapi.models.NoteResponse
import com.kishorramani.noteappwithapi.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteAPI: NoteAPI) {

    private val _notesFlow = MutableStateFlow<NetworkResult<List<NoteResponse>>>(NetworkResult.Loading())
    val notesFlow: StateFlow<NetworkResult<List<NoteResponse>>>
        get() = _notesFlow

    private val _statusFlow = MutableStateFlow<NetworkResult<Pair<Boolean, String>>>(NetworkResult.Loading())
    val statusFlow: StateFlow<NetworkResult<Pair<Boolean, String>>>
        get() = _statusFlow

    suspend fun getNotes() {
        val response = noteAPI.getNotes()
        if (response.isSuccessful && response.body() != null) {
            _notesFlow.emit(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _notesFlow.emit(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _notesFlow.emit(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun createNote(noteRequest: NoteRequest) {
        _statusFlow.emit(NetworkResult.Loading())
        val response = noteAPI.createNote(noteRequest)
        handleResponse(response, "Note Created")
    }

    suspend fun deleteNote(noteId: String) {
        _statusFlow.emit(NetworkResult.Loading())
        val response = noteAPI.deleteNote(noteId)
        handleResponse(response, "Note Deleted")
    }

    suspend fun updateNote(noteId: String, noteRequest: NoteRequest) {
        _statusFlow.emit(NetworkResult.Loading())
        val response = noteAPI.updateNote(noteId, noteRequest)
        handleResponse(response, "Note Updated")
    }

    private suspend fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusFlow.emit(NetworkResult.Success(Pair(true, message)))
        } else {
            _statusFlow.emit(NetworkResult.Success(Pair(false, "Something went wrong")))
        }
    }
}