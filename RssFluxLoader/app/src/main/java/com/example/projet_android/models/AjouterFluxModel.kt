package com.example.projet_android.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.projet_android.Flux
import com.example.projet_android.database.Dao
import com.example.projet_android.database.MyDatabase

class AjouterFluxModel (application: Application) : AndroidViewModel(application) {

    val dao: Dao = MyDatabase.getDatabase(application).myDao()

    fun addFlux(f: Flux) {
        Thread {
            val l = dao.insertFlux(f)
            Log.d("flux added", "${l.size} flux")
        }.start()
    }

}