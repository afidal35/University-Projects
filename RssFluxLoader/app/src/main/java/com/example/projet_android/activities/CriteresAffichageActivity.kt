package com.example.projet_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.projet_android.R
import com.example.projet_android.models.AjouterInfoModel
import com.example.projet_android.models.CritereAfficherModel
import java.lang.StringBuilder

class CriteresAffichageActivity : AppCompatActivity() {

    private val TAG = "CriteresAffichageActivity"

    //view
    private val checkBoxInfoAll by lazy { findViewById<CheckBox>(R.id.checkBoxInfoAll) }
    private val checkBoxInfoByNameDescription by lazy { findViewById<CheckBox>(R.id.checkBoxInfoByNameDescription) }
    private val checkBoxInfoByNameTitle by lazy { findViewById<CheckBox>(R.id.checkBoxInfoByNameTitle) }
    private val checkBoxInfoNew by lazy { findViewById<CheckBox>(R.id.checkBoxInfoNew) }

    private val editTextInfoByNameDescription by lazy { findViewById<EditText>(R.id.EditTextInfoByNameDescription) }
    private val editTextInfoByNameTitle by lazy { findViewById<EditText>(R.id.EditTextInfoByNameTitle) }

    private val textView by lazy { findViewById<TextView>(R.id.textView) }

    //model
    val infomodel by lazy { ViewModelProvider(this).get(CritereAfficherModel::class.java) }

    companion object{
        //KEY critère
        val KEY_CRITERE_ALL = "CRITERE_ALL"
        val KEY_CODE_CRITERE_NEW= "CRITERE_NEW"
        val KEY_CODE_CRITERE_BY_DESCRIPTION = "CRITERE_BY_NAME"
        val KEY_CODE_CRITERE_BY_TITLE = "CRITERE_BY_TITLE"
        val KEY_MESSAGE_TITLE = "MESSAGE_TITLE"
        val KEY_MESSAGE_DESCRIPTION = "MESSAGE_DESCRIPTION"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteres_affichage)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkBoxInfoAll.setOnCheckedChangeListener { compoundButton, b ->
                checkBoxInfoByNameDescription.isEnabled = !b
                checkBoxInfoByNameTitle.isEnabled = !b
                checkBoxInfoNew.isEnabled = !b
        }

        checkBoxInfoNew.setOnCheckedChangeListener { compoundButton, b ->
            checkBoxInfoAll.isEnabled = !(checkBoxInfoNew.isChecked
                    || checkBoxInfoByNameTitle.isChecked
                    || checkBoxInfoByNameDescription.isChecked)
        }

        checkBoxInfoByNameTitle.setOnCheckedChangeListener { compoundButton, b ->
            checkBoxInfoAll.isEnabled = !(checkBoxInfoNew.isChecked
                    || checkBoxInfoByNameTitle.isChecked
                    || checkBoxInfoByNameDescription.isChecked)
            editTextInfoByNameTitle.isEnabled = b
        }

        checkBoxInfoByNameDescription.setOnCheckedChangeListener { compoundButton, b ->
            checkBoxInfoAll.isEnabled = !(checkBoxInfoNew.isChecked
                    || checkBoxInfoByNameTitle.isChecked
                    || checkBoxInfoByNameDescription.isChecked)
            editTextInfoByNameDescription.isEnabled = b
        }

        infomodel.allinfo.observe(this,{
            val text_affichage = resources.getString(R.string.criteres_affichages)
            textView.text = text_affichage + ", il y a " + it.size + " informations"
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.criteres_affichages_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.consulter ->
                startActivity(Intent(this, AfficherActivity::class.java))
            R.id.action_add_flux ->
                    startActivity(Intent(this, AjouterFluxActivity::class.java))
            R.id.action_download_flux ->
                    startActivity(Intent(this, TelechargementActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun startAfficher(view: View) {
        val intent = Intent(this, AfficherActivity::class.java).apply {
            putExtra(KEY_CRITERE_ALL, checkBoxInfoAll.isChecked)
            putExtra(KEY_CODE_CRITERE_NEW, checkBoxInfoNew.isChecked)
            putExtra(KEY_CODE_CRITERE_BY_DESCRIPTION, checkBoxInfoByNameDescription.isChecked)
            putExtra(KEY_CODE_CRITERE_BY_TITLE, checkBoxInfoByNameTitle.isChecked)

            if (checkBoxInfoByNameDescription.isChecked && editTextInfoByNameDescription.text.toString().isNotEmpty()) {
                putExtra(KEY_MESSAGE_DESCRIPTION, editTextInfoByNameDescription.text.toString())
            }

            if (checkBoxInfoByNameTitle.isChecked && editTextInfoByNameTitle.text.toString().isNotEmpty()) {
                putExtra(KEY_MESSAGE_TITLE, editTextInfoByNameTitle.text.toString())
            }

            Log.d(
                TAG,
                "onCreate: new : ${checkBoxInfoNew.isChecked} title : ${checkBoxInfoByNameTitle.isChecked} titleText : ${editTextInfoByNameTitle.text.toString()} " +
                        "description : ${checkBoxInfoByNameDescription.isChecked} descriptionText : ${editTextInfoByNameDescription.text.toString()} "
            )
        }
        startActivity(intent)
    }
}