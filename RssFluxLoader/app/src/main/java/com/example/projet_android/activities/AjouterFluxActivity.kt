package com.example.projet_android.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projet_android.Flux
import com.example.projet_android.R
import com.example.projet_android.models.AjouterFluxModel
import kotlin.properties.Delegates

class AjouterFluxActivity : AppCompatActivity() {

    private val TAG = "AjouterFluxActivity"

    private val titre by lazy{ findViewById(R.id.titre) as EditText }
    val description by lazy{ findViewById(R.id.description) as EditText }
    private val lien by lazy{ findViewById( R.id.lien) as EditText }
    private val fluxModel: AjouterFluxModel by lazy{ ViewModelProvider(this).get(AjouterFluxModel::class.java) }

    private var sizeList by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_flux_activity)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

       /* val list = generateExample()
        for (i in list.indices) {
            fluxModel.addFlux(list[i])
        }*/

    }

    fun ajouter(view: View) {

        val t: String = titre.text.toString().trim()
        if( TextUtils.isEmpty( t )){
            Toast.makeText(this, "titre vide", Toast.LENGTH_SHORT).show()
            return
        }

        val desc: String = description.text.toString().trim()
        if( desc == ""  ) {
            Toast.makeText(this, "description vide", Toast.LENGTH_SHORT).show()
            return;
        }

        val url: String = lien.text.toString().trim()
        if( url == ""  ) {
            Toast.makeText(this, "url vide", Toast.LENGTH_SHORT).show()
            return;
        }

        val f  = Flux(source = t, tag = desc, url = url)

        fluxModel.addFlux(f)
        titre.text.clear()
        description.text.clear()
        lien.text.clear()

    }

    fun generateExample() : List<Flux> {
        val flux : Flux = Flux( source = "Le monde",tag =  "Un site qui publie des revues de press", url = "https://www.lemonde.fr/rss/une.xml")
        val flux1 : Flux = Flux( source = "France24 Pacifique",tag =  "Un site qui publie des revues de press france24 sur le pacifique",  url = "https://www.france24.com/fr/asie-pacifique/rss")
        val flux3 : Flux = Flux( source = "France24", tag = "Un site qui présente les actus en France",  url = "https://www.france24.com/fr/france/rss")
        val flux4 : Flux = Flux( source = "France24 Moyen Orient",tag =  "Un site qui nous des présentes des actus sur le moyen orient",  url = "https://www.france24.com/fr/moyen-orient/rss")
        return arrayListOf<Flux>(flux, flux1, flux3, flux4)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ajouter_flux_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.action_download_flux ->
                startActivity(Intent(this, TelechargementActivity::class.java))
            R.id.action_browse_info ->
                startActivity(Intent(this, CriteresAffichageActivity::class.java))
            }
        return super.onOptionsItemSelected(item)
    }


}