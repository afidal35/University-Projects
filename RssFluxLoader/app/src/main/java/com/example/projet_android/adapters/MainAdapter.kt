package com.example.projet_android.adapters

import android.content.Context
import android.content.Intent
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.R
import com.example.projet_android.activities.AjouterFluxActivity
import com.example.projet_android.activities.CriteresAffichageActivity
import com.example.projet_android.activities.TelechargementActivity
import kotlinx.android.synthetic.main.item_info_layout.view.*

class MainAdapter(var mContext : Context) : RecyclerView.Adapter<MainAdapter.VH>() {

    private val titles = arrayOf("Ajouter Flux",
        "Télécharger Flux", "Filtrer Infos")

    private val details = arrayOf("Pour ajouter des nouveaux Flux RSS dans notre BDD.", "Pour télécharger des Flux RSS et parcourir les " +
            "différents flux.", "Choisir des options d'affichages de la tables contenant les informations relatives aux flux.")

    private val images = intArrayOf(R.drawable.ajouter, R.drawable.telecharger, R.drawable.filtrer)

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): VH {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.main_card_view, viewGroup, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemDetail.text = details[position]
        holder.itemImage.setImageResource(images[position])

        holder.itemView.setOnClickListener { v: View ->
            if (holder.itemTitle.text.contains("Ajouter Flux"))
                mContext.startActivity(Intent(mContext, AjouterFluxActivity::class.java))
            else if (holder.itemTitle.text.contains("Télécharger Flux"))
                mContext.startActivity(Intent(mContext, TelechargementActivity::class.java))
            else
                mContext.startActivity(Intent(mContext, CriteresAffichageActivity::class.java))
        }

    }

    override fun getItemCount(): Int {
        return titles.size
    }
}