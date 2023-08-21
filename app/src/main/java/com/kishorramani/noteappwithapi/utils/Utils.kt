package com.kishorramani.noteappwithapi.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class Utils {
    suspend fun <T> safeAPICall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T,
    ): NetworkResult<T> {
        return withContext(dispatcher) {
            try {
                NetworkResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> NetworkResult.Error("IO Exception")
                    is HttpException -> {
                        val errorObj = JSONObject(throwable.response()?.errorBody()!!.charStream().readText())
                        NetworkResult.Error(errorObj.getString("message"))
                    }
                    else -> {
                        NetworkResult.Error("Something went wrong", null)
                    }
                }
            }
        }
    }
}

interface INoteRepository {

}

class onlineNoteRepository: INoteRepository{}
class offlineNoteRepository: INoteRepository{}

//changes inside HILT module for providing Repository Object