package com.example.projet_android.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.*
import com.example.projet_android.adapters.FluxAdapter
import com.example.projet_android.models.AjouterInfoModel
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.FileDescriptor
import java.io.FileInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class TelechargementActivity : AppCompatActivity() {

    val TAG = "TelechargementActivity"

    //View
    val recylcerFlux by lazy { findViewById<RecyclerView>(R.id.recycler_flux) }
    val adapter by lazy { FluxAdapter() }
    val buttonDownload by lazy { findViewById<Button>(R.id.buttonDownload) }

    //Model
    val infoModel: AjouterInfoModel by lazy{ ViewModelProvider(this).get(AjouterInfoModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browse_flux_activity)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recylcerFlux.layoutManager = LinearLayoutManager(this)
        recylcerFlux.adapter = adapter

        infoModel.list.observe(this, {
            adapter.listFlux = it
            adapter.notifyDataSetChanged()

            Log.d(TAG, "size list flux: ${adapter.listFlux.size}")
            for (i in it.indices) {
                Log.d(TAG, "i : ${it[i].source} , ${it[i].tag} , ${it[i].url}")
            }

        })

        buttonDownload.setOnClickListener {
            infoModel.downloadFluxByList(adapter.listFluxSelected)
            Log.d(TAG, "taille de la liiste sélectionné : ${adapter.listFluxSelected.size}")
        }

        Log.d(TAG, "onCreate: flux in DB ${infoModel.dao.loadAllFlux().value?.size} ")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.telecharger_flux_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.action_add_flux ->
                startActivity(Intent(this, AjouterFluxActivity::class.java))
            R.id.action_browse_info ->
                startActivity(Intent(this, CriteresAffichageActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

}