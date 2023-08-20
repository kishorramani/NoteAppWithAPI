package com.kishorramani.noteappwithapi.di

import com.kishorramani.noteappwithapi.api.AuthInterceptor
import com.kishorramani.noteappwithapi.api.NoteAPI
import com.kishorramani.noteappwithapi.api.UserAPI
import com.kishorramani.noteappwithapi.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Retrofit.Builder): UserAPI {
        return retrofitBuilder.build().create(UserAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun providesNoteAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): NoteAPI {
        return retrofitBuilder.client(okHttpClient).build().create(NoteAPI::class.java)
    }
}