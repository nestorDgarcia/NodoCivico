package com.nestorgarcia.nodocivico

import android.app.Application
import com.nestorgarcia.nodocivico.data.local.AppDatabase

class NodoCivicoApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}