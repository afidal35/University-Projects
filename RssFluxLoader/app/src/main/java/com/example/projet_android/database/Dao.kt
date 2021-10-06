package com.example.projet_android.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*
import com.example.projet_android.Flux
import com.example.projet_android.Info

@Dao
interface Dao {

    // Flux table functions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFlux(vararg flux: Flux): List<Long>

    @Query("SELECT * FROM Flux WHERE lower(Flux.source) like lower(:c)")
    fun loadSomeFlux(c: String): LiveData<List<Flux>>

    @Query( "SELECT * FROM Flux ")
    fun loadAllFlux(): LiveData<List<Flux>>

    @Delete
    fun deleteFlux(p: List<Flux>)

    @Query("DELETE FROM flux")
    fun nukeTableFlux()

    @Query("DELETE FROM Flux WHERE Flux.source LIKE :pref || \"%\" ")
    fun deleteFluxByPrefix(pref: String)


    // Info table functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfos(vararg info: Info): List<Long>

    @Query( "SELECT * FROM Info ")
    fun loadAllInfos(): LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.title) like lower(:c)")
    fun loadSomeInfos(c: String): LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE Info.isNew = 1")
    fun loadInfosNew() : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.title) LIKE \"%\" || lower(:pref) || \"%\" ")
    fun loadInfoLikeTitle(pref: String) : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.description) LIKE \"%\" || lower(:pref) || \"%\" ")
    fun loadInfoLikeDesc(pref: String) : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.title) LIKE \"%\" || lower(:pref) || \"%\" AND Info.isNew = 1 ")
    fun loadInfoLikeTitleAndNew(pref: String) : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.description) LIKE \"%\" || lower(:pref) || \"%\" AND Info.isNew = 1 ")
    fun loadInfoLikeDescAndNew(pref: String) : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.title) LIKE \"%\" || lower(:title) || \"%\" AND lower(Info.description) LIKE \"%\" || lower(:desc) || \"%\" ")
    fun loadInfoLikeDescAndTitle(title: String,desc: String) : LiveData<List<Info>>

    @Query("SELECT * FROM Info WHERE lower(Info.title) LIKE \"%\" || lower(:title) || \"%\" AND lower(Info.description) LIKE \"%\" || lower(:desc) || \"%\" AND Info.isNew = 1 ")
    fun loadInfoLikeDescAndNewAndTitle(title: String,desc: String) : LiveData<List<Info>>

    @Delete
    fun deleteInfo(p: List<Info>)

    @Query("DELETE FROM Info")
    fun nukeTableInfo()

    @Update
    fun infoSetNotNew(listInfo : List<Info>)

    @Query("DELETE FROM Info WHERE Info.title LIKE :pref || \"%\" ")
    fun deleteInfoByPrefix(pref: String)

}