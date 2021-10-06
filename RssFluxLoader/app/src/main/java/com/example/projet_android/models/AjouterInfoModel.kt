package com.example.projet_android.models

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.projet_android.FileDownloadJobServiceIntent
import com.example.projet_android.Flux
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projet_android.Info
import com.example.projet_android.database.Dao
import com.example.projet_android.database.MyDatabase
import java.io.FileDescriptor
import java.io.FileInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class AjouterInfoModel (application: Application) : AndroidViewModel(application) {

    //TAG
    private val TAG = "AjouterInfoModel"
    private val KEY_PARCELABLE = "Key_Parcelable_extra_JobServiceI"
    private val KEY_LONG = "Key_Long_extra_JobServiceI"

    //Dao
    val dao: Dao = MyDatabase.getDatabase(application).myDao()

    //DownloadManager
    val downloadManager = //getApplication<Application>()
        application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    //Map
    private var idDownload : MutableMap<Long,Long> = mutableMapOf()

    //receiver
    val receiver = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {

            /* récupérer l'identifiant de l'action download */
            val reference: Long? = p1?.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1
            )

            if (p0 == null)
                return

            /* vérifier si l'identifiant identique à notre action download */
            if( !idDownload.contains(reference) )
                return

            Log.d(TAG, "onReceive: id download $reference hasbeen downloaded")

            //récupérer ParcelFileDescriptor pour le fichier téléchargé
            val desc: ParcelFileDescriptor = downloadManager.openDownloadedFile(reference!!)

            val work_background = Intent(p0,FileDownloadJobServiceIntent::class.java)
            work_background.putExtra(FileDownloadJobServiceIntent.KEY_PARCELABLE,desc)
            work_background.putExtra(FileDownloadJobServiceIntent.KEY_LONG, idDownload[reference])

            FileDownloadJobServiceIntent.enquework(p0,work_background)

        }

    }

    fun downloadUri(flux: Flux) {
        val url = flux.url.trim()
        val req = DownloadManager.Request(Uri.parse(url))
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        req.setTitle("Download")
        req.setDescription("Downloading file...")
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis())
        idDownload.put(downloadManager.enqueue(req),flux.idFlux)
    }

    fun downloadFluxByList(listFlux: List<Flux>) {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        for (flux in listFlux) {
            downloadUri(flux)
        }

        getApplication<Application>().registerReceiver(receiver, filter)
    }

    /* called when the ViewModel no longer used */
    override fun onCleared() {
        super.onCleared()
        /* unregister BroadcastReceiver */
        try {
            getApplication<Application>().unregisterReceiver(receiver)
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, "onCleared: Try to unregisterReceiver but not reicever were attached")
        }
    }

    //list of all Flux in DB
    val list = dao.loadAllFlux()


}