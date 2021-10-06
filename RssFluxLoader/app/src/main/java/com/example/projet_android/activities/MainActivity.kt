package com.example.projet_android.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.Flux
import com.example.projet_android.R
import com.example.projet_android.adapters.MainAdapter
import com.example.projet_android.database.Dao
import com.example.projet_android.database.MyDatabase
import com.example.projet_android.models.AjouterFluxModel
import com.example.projet_android.models.AjouterInfoModel


class MainActivity : AppCompatActivity() {

    val fluxModel: AjouterFluxModel by lazy{ ViewModelProvider(this).get(AjouterFluxModel::class.java) }
    val infoModel: AjouterInfoModel by lazy{ ViewModelProvider(this).get(AjouterInfoModel::class.java) }
    private val recycler by lazy { findViewById<RecyclerView>(R.id.recycler_main) }
    private val adapter by lazy { MainAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        if(supportActionBar  == null )
            Log.d("MainActivity", "no ActionBar")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        recycler.adapter = adapter

        val dao: Dao = MyDatabase.getDatabase(application).myDao()

        Thread {
            dao.insertFlux(
                    Flux(source = "France24 Moyen Orient", tag = "Un site qui nous des présentes des actus sur le moyen orient", url = "https://www.france24.com/fr/moyen-orient/rss"),
                    Flux(source = "France24", tag = "Un site qui présente les actus en France", url = "https://www.france24.com/fr/france/rss"),
                    Flux(source = "France24 Pacifique", tag = "Un site qui publie des revues de press france24 sur le pacifique", url = "https://www.france24.com/fr/asie-pacifique/rss"),
                    Flux(source = "Le monde", tag = "Un site qui publie des revues de press", url = "https://www.lemonde.fr/rss/une.xml")
            )
        }.apply {
            start()
            join()
        }

        Thread{
            val p = dao.loadAllFlux()
            if( p.value != null) {
                for (flux in p.value!!) {
                    Log.d("MainActivity lu", flux.url)
                }
            }
        }.apply {
            start()
            join()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.action_add_flux ->
                startActivity(Intent(this, AjouterFluxActivity::class.java))
            R.id.action_download_flux ->
                startActivity(Intent(this, TelechargementActivity::class.java))
            R.id.action_browse_info ->
                startActivity(Intent(this, CriteresAffichageActivity::class.java))
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}