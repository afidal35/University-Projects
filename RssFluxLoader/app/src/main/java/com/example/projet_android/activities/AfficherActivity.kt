package com.example.projet_android.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.FragmentInfoWebView
import com.example.projet_android.R
import com.example.projet_android.adapters.AfficherAdapter
import com.example.projet_android.models.AfficherModel

class AfficherActivity : AppCompatActivity(), FragmentInfoWebView.FragmentInteractionListener {

    //TAG
    val TAG = "AfficherActivity"

    private val recyclerInfo by lazy { findViewById<RecyclerView>(R.id.recycler_info) }
    private val adapter by lazy { AfficherAdapter(this) }
    private val infoModel: AfficherModel by lazy { ViewModelProvider(this).get(AfficherModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_afficher)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val critere_all = intent.getBooleanExtra(CriteresAffichageActivity.KEY_CRITERE_ALL, false)
        val critere_description =
            intent.getBooleanExtra(CriteresAffichageActivity.KEY_CODE_CRITERE_BY_DESCRIPTION, false)
        val critere_title =
            intent.getBooleanExtra(CriteresAffichageActivity.KEY_CODE_CRITERE_BY_TITLE, false)
        val critere_new =
            intent.getBooleanExtra(CriteresAffichageActivity.KEY_CODE_CRITERE_NEW, false)

        var critere_title_text: String? = null
        if (critere_title) {
            critere_title_text = intent.getStringExtra(CriteresAffichageActivity.KEY_MESSAGE_TITLE)
        }

        var critere_desccription_text: String? = null
        if (critere_description) {
            critere_desccription_text =
                intent.getStringExtra(CriteresAffichageActivity.KEY_MESSAGE_DESCRIPTION)
        }

        Log.d(
            TAG,
            "onCreate: new : $critere_new title : $critere_title titleText : $critere_title_text " +
                    "description : $critere_description descriptionText : $critere_desccription_text "
        )

        recyclerInfo.layoutManager = LinearLayoutManager(this)
        recyclerInfo.hasFixedSize()
        recyclerInfo.adapter = adapter

        infoModel.listInfo = infoModel.searchByCritere(
            new = critere_new,
            title = critere_title,
            titleText = critere_title_text,
            description = critere_description,
            descriptionText = critere_desccription_text
        )

        infoModel.listInfo?.observe(this, {
            infoModel.listInfoAffiche.value = it
        })

        infoModel.listInfoAffiche.observe(this,{
            adapter.listInfos = it
            adapter.notifyDataSetChanged()
            Log.d(TAG, "onCreate: flux in DB ${it.size} ")
        })

        infoModel.listInfosSelected.observe(this, {
            adapter.listInfosSelected = it
            adapter.notifyDataSetChanged()
        })

    }

    fun supprimerInfo(view: View) {
        AlertDialog.Builder(this)
            .setMessage("Supprimer cette/ces infos ?")
            .setTitle("SUPPRESSION")
            .setCancelable(false)
            .setPositiveButton("Oui") { dialog: DialogInterface, t: Int ->
                infoModel.supprimerInfo(adapter.listInfosSelected)
                infoModel.setEmptyListInfoSelected()
                dialog.dismiss()
            }
            .setNegativeButton("Non") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Annuler") { dialog, _ -> dialog.cancel() }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?) : Boolean {
        menuInflater.inflate(R.menu.recherche_menu, menu)
        val search = menu?.findItem(R.id.action_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText.toString())
                return true
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.action_add_flux ->
                startActivity(Intent(this, AjouterFluxActivity::class.java))
            R.id.action_download_flux ->
                startActivity(Intent(this, TelechargementActivity::class.java))
            R.id.action_home ->
                startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        infoModel.setInfosNotNew(adapter.listInfos)
    }

    fun lance(f : Fragment) {
        Log.d(TAG,"size of stack" + FragmentActivity().supportFragmentManager.backStackEntryCount)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()  //determine l’id sur lequel on attachera le fragment
        val idPlaceFragment = R.id.fram_frag_afficher

        // si deja un fragment a cette place-la, on le supprime de la
        // pile, et on fait un remplacement
        Log.d(TAG,"existe " + (supportFragmentManager.findFragmentById(idPlaceFragment) != null))
        if (supportFragmentManager.findFragmentById(idPlaceFragment) != null) {
            supportFragmentManager.popBackStack() //j’efface tout
            ft.replace(idPlaceFragment, f).addToBackStack(f.javaClass.toString())
            Log.d(TAG, "onCreate: replace")
        }

        //sinon on fait un ajout simple, dans les deux
        //cas on ajoute a la pile.
        else {
            ft.add(idPlaceFragment, f).addToBackStack(f.javaClass.toString())
            Log.d(TAG, "onCreate: add")
        }

        ft.commit();
    }

    override fun launchFrag(url: String) {
        lance(FragmentInfoWebView.newInstance(url))
    }
}