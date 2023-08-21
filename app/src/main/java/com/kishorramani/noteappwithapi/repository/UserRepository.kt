package com.kishorramani.noteappwithapi.repository

import com.kishorramani.noteappwithapi.api.UserAPI
import com.kishorramani.noteappwithapi.models.UserRequest
import com.kishorramani.noteappwithapi.models.UserResponse
import com.kishorramani.noteappwithapi.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {

    private val _userResponseStateFlow = MutableStateFlow<NetworkResult<UserResponse>>(NetworkResult.Loading())
    val userResponseStateFlow: StateFlow<NetworkResult<UserResponse>>
        get() = _userResponseStateFlow

    suspend fun registerUser(userRequest: UserRequest) {
        //_userResponseStateFlow.emit(NetworkResult.Loading())
        val response = userAPI.signup(userRequest)
        handleResponse(response)
    }

    suspend fun loginUser(userRequest: UserRequest) {
        //_userResponseStateFlow.emit(NetworkResult.Loading())
        val response = userAPI.signin(userRequest)
        handleResponse(response)
    }

    private suspend fun handleResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _userResponseStateFlow.emit(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseStateFlow.emit(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userResponseStateFlow.emit(NetworkResult.Error("Something Went Wrong"))
        }
    }
}