package com.example.projet_android.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.Flux
import com.example.projet_android.R
import kotlinx.android.synthetic.main.item_flux_layout.view.*

//import kotlinx.android.synthetic.main.item_layout.view.*

class FluxAdapter()  : RecyclerView.Adapter<FluxAdapter.VH>() {

    var listFlux = listOf<Flux>()
    var listFluxSelected = mutableListOf<Flux>()
    val TAG = "FluxAdapter"

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val v = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_flux_layout, parent, false)

        val checkbox = v.findViewById<CheckBox>(R.id.check)

        checkbox.setOnClickListener {
            it as CheckBox
            val flux = it.tag as Flux
            flux.checkDownload = it.isChecked

            if (flux.checkDownload)
                listFluxSelected.add(flux)
            else
                listFluxSelected.remove(flux);

            Log.d(TAG, "taille list Selected : ${listFluxSelected.size}" )
        }

        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.source.text = listFlux[position].source
        holder.itemView.tag_item.text = listFlux[position].tag
        holder.itemView.lien.text = listFlux[position].url

        holder.itemView.check.tag = listFlux[position]
        holder.itemView.check.isChecked = listFlux[position].checkDownload

        holder.itemView as CardView
        holder.itemView.setCardBackgroundColor(0xFFa254f2.toInt())
        /*(if(position % 2 == 0){
            holder.itemView.setCardBackgroundColor(0xFFa254f2.toInt())
        } else {
            holder.itemView.setCardBackgroundColor(0xFFefbad3.toInt())
        })*/

    }

    override fun getItemCount(): Int {
        return listFlux.size
    }
}