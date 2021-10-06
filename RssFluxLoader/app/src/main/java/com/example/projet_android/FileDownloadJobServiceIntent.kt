package com.example.projet_android

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.projet_android.database.MyDatabase
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.FileDescriptor
import java.io.FileInputStream
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class FileDownloadJobServiceIntent : JobIntentService() {

    //TAG
    private val TAG : String = "FileDownloadJobServiceI"

    companion object {
        private val JOB_ID : Int = 1234

        val KEY_PARCELABLE = "Key_Parcelable_extra_FileDownloadJobServiceI"
        val KEY_LONG = "Key_Long_extra_FileDownloadJobServiceI"

        fun enquework(context : Context, work : Intent) {
            enqueueWork(context,FileDownloadJobServiceIntent::class.java, JOB_ID, work)
        }

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")

        val desc = intent.extras?.getParcelable(
            KEY_PARCELABLE
        ) as ParcelFileDescriptor?

        val idFlux: Long = intent.getLongExtra(
            KEY_LONG,-1
        )

        Log.d(TAG, "onHandleWork - idflux : $idFlux")

        if (desc != null) {
            val xml = parserToDocument(desc)
            parsingItemXML(xml,idFlux = idFlux)
        }

    }

    fun parserToDocument(desc: ParcelFileDescriptor) : Document {

        val fileDescriptor: FileDescriptor = desc.fileDescriptor

        //on peut créer maintenant un FileInputStream et lire le fichier
        val fileInputStream = FileInputStream(fileDescriptor)
        val dbf : DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val db : DocumentBuilder = dbf.newDocumentBuilder();

        return  db.parse(fileInputStream);
    }

    fun parsingItemXML(xml: Document, idFlux: Long) {
        val itemListNode = xml.getElementsByTagName("item")
        //Log.d(TAG, "pasringItemXML: size ${itemListNode.length}")
        for (i in 0..itemListNode.length - 1) {
            //Log.d(TAG, "pasringItemXML: item title : ${itemListNode.item(i).textContent}")
            addItemTodb(itemListNode.item(i),idFlux = idFlux)
        }
    }

    fun addItemTodb(item: Node, idFlux: Long) {
        val itemElements = item.childNodes
        val title = getTextFromNameBalise("title",itemElements)
        val description = getTextFromNameBalise("description",itemElements)
        val link = getTextFromNameBalise("link",itemElements)

        val info = Info(title = title, description = description, link = link, idFlux = idFlux)

        MyDatabase.getDatabase(this).myDao().insertInfos(info)
        Log.d(TAG, "id info flux : $idFlux : titre : $title link : $link description : $description")
    }

    fun getTextFromNameBalise(balise : String, nodelist: NodeList) : String {

        for (i in 0 until nodelist.length){
            if (nodelist.item(i).nodeName.equals(balise)) {
                return nodelist.item(i).textContent
            }
        }

        return "$balise - error not found"
    }

    /*fun downloadUri(uri: Uri){
        val req = DownloadManager.Request(uri)
        idDownload = dm.enqueue(req)
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)


        /*
                                            FOR A DOWNLOAD PROGRESS BAR :

        https://stackoverflow.com/questions/15795872/show-download-progress-inside-activity-using-downloadmanager

                                                SEE ALSO :

        https://stackoverflow.com/questions/14798569/show-download-manager-progress-inside-activity

        val mProgressBar = findViewById<View>(R.id.progressBar1) as ProgressBar
        Thread {
            var downloading = true
            while (downloading) {
                val q = DownloadManager.Query()
                q.setFilterById(idDownload)
                val cursor: Cursor = dm.query(q)
                cursor.moveToFirst()
                val bytes_downloaded: Int = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytes_total: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) === DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }

                final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                runOnUiThread { mProgressBar.progress = dl_progress.toInt() }
                cursor.close()

            }
        }.start()
        */
    }*/

    /* SHOW MESSAGE ON DOWLOAD MANAGER
    private fun statusMessage(c: Cursor): String? {
        var msg = "???"
        msg = when (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            DownloadManager.STATUS_FAILED -> "Download failed!"
            DownloadManager.STATUS_PAUSED -> "Download paused!"
            DownloadManager.STATUS_PENDING -> "Download pending!"
            DownloadManager.STATUS_RUNNING -> "Download in progress!"
            DownloadManager.STATUS_SUCCESSFUL -> "Download complete!"
            else -> "Download is nowhere in sight"
        }
        return msg
    }
    */

}