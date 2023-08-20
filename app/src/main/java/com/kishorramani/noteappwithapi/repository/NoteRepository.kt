package com.kishorramani.noteappwithapi.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kishorramani.noteappwithapi.api.NoteAPI
import com.kishorramani.noteappwithapi.models.NoteRequest
import com.kishorramani.noteappwithapi.models.NoteResponse
import com.kishorramani.noteappwithapi.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteAPI: NoteAPI) {

    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _notesLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData: LiveData<NetworkResult<Pair<Boolean, String>>>
        get() = _statusLiveData

    suspend fun getNotes() {
        _notesLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.getNotes()
        if (response.isSuccessful && response.body() != null) {
            _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _notesLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _notesLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.createNote(noteRequest)
        handleResponse(response, "Note Created")
    }

    suspend fun deleteNote(noteId: String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.deleteNote(noteId)
        handleResponse(response, "Note Deleted")
    }

    suspend fun updateNote(noteId: String, noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteAPI.updateNote(noteId, noteRequest)
        handleResponse(response, "Note Updated")
    }

    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(Pair(true, message)))
        } else {
            _statusLiveData.postValue(NetworkResult.Success(Pair(false, "Something went wrong")))
        }
    }
}