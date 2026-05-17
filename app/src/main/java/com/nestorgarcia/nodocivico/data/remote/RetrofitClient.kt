package com.nestorgarcia.nodocivico.data.remote

import android.content.Context
import com.nestorgarcia.nodocivico.ui.fragments.SettingsFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var instance: ApiService? = null
    private var currentBaseUrl: String = ""

    fun getApi(context: Context): ApiService {
        val prefs = context.getSharedPreferences(
            SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE
        )
        val baseUrl = prefs.getString(
            SettingsFragment.KEY_API_URL,
            SettingsFragment.DEFAULT_URL
        ) + "/"

        // Reconstruir si cambió la URL
        if (instance == null || baseUrl != currentBaseUrl) {
            currentBaseUrl = baseUrl
            instance = buildRetrofit(baseUrl).create(ApiService::class.java)
        }
        return instance!!
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}