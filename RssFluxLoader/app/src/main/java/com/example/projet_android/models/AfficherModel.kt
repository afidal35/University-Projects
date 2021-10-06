package com.example.projet_android.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projet_android.Info
import com.example.projet_android.database.Dao
import com.example.projet_android.database.MyDatabase

class AfficherModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AfficherModel"

    //Dao
    val dao: Dao = MyDatabase.getDatabase(application).myDao()

    fun supprimerInfo(i: List<Info>?) {
        if (i == null || i.isEmpty())
            return
        Thread { dao.deleteInfo(i) }.start()
    }

    fun setEmptyListInfoSelected() {
        listInfosSelected.value = mutableListOf()
    }

    //liste des infos qu'on affiche
    var listInfo: LiveData<List<Info>>? = null

    var listInfoAffiche: MutableLiveData<List<Info>> = MutableLiveData()

    val listInfosSelected: MutableLiveData<MutableList<Info>> = MutableLiveData()

    fun searchByCritere (
        new: Boolean, title: Boolean,
        titleText: String?, description: Boolean,
        descriptionText: String?
    ): LiveData<List<Info>> {

        var listInfo : LiveData<List<Info>>? = null

        if (new && title && titleText != null && description && descriptionText != null) {
            listInfo = dao.loadInfoLikeDescAndNewAndTitle(title = titleText, desc = descriptionText)
        } else if (title && titleText != null && description && descriptionText != null) {
            listInfo =  dao.loadInfoLikeDescAndTitle(title = titleText, desc = descriptionText)
        } else if (new && description && descriptionText != null) {
            listInfo = dao.loadInfoLikeDescAndNew(descriptionText)
        } else if (new && title && titleText != null) {
            listInfo =  dao.loadInfoLikeTitleAndNew(titleText)
        } else if (new) {
            listInfo =  dao.loadInfosNew()
        } else if (title && titleText != null) {
            listInfo =  dao.loadInfoLikeTitle(titleText)
        } else if (description && descriptionText != null) {
            listInfo = dao.loadInfoLikeDesc(descriptionText)
        } else {
            Log.e(TAG, "searchByCritere: on rentre dans aucune case")
            listInfo =  dao.loadAllInfos()
        }

        //setInfosNotNew(listInfo.value)

        return listInfo
    }

    fun setInfosNotNew(list: List<Info>?) {

        if (list != null) {
            Log.d(TAG, "setInfosNotNew: list size to change size : ${list.size}")
            for (i in list) {
                i.isNew = false
            }

            Thread {
                dao.infoSetNotNew(list)
            }.apply {
                start()
            }
        }
        else {
            Log.e(TAG, "setInfosNotNew: liste non mis à jour !")
        }
    }

}