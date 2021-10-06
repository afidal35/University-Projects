package com.example.projet_android.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.projet_android.database.MyDatabase

class CritereAfficherModel (application: Application) : AndroidViewModel(application) {

    val dao = MyDatabase.getDatabase(application).myDao()

    val allinfo = dao.loadAllInfos()

}