package com.example.projet_android.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.Info
import com.example.projet_android.R
import com.example.projet_android.activities.AfficherActivity
import kotlinx.android.synthetic.main.item_info_layout.view.*


class AfficherAdapter(afficherAdtivity: AfficherActivity)  : RecyclerView.Adapter<AfficherAdapter.VH>(), Filterable {

    //Tag
    val TAG = "AfficherAdapter"

    //liste
    var listInfos : List<Info> = mutableListOf()
    var listInfosCopy : List<Info> = mutableListOf()
    var listInfosSelected : MutableList<Info> = mutableListOf()

    //activity belong
    val activity = afficherAdtivity

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        listInfosCopy = listInfos

        val v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_info_layout, parent, false)


        val cardView = v.findViewById<CardView>(R.id.cardviewInfo)

        cardView.setOnClickListener {
            it as CardView
            val info = it.tag as Info
            activity.launchFrag(info.link)
        }

        val checkbox = v.findViewById<CheckBox>(R.id.checkInfo)

        checkbox.setOnClickListener {
            it as CheckBox
            val info = it.tag as Info
            info.checkSuppr = false

            if (it.isChecked())
                listInfosSelected.add(info)
            else
                listInfosSelected.remove(info);

            Log.d(TAG, "taille list info selected : ${listInfosSelected.size}")
        }



        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.title.text = listInfos[position].title
        holder.itemView.description.text = listInfos[position].description

        holder.itemView.checkInfo.tag = listInfos[position]
        holder.itemView.cardviewInfo.tag = listInfos[position]

        holder.itemView.checkInfo.isChecked = listInfos[position].checkSuppr

        holder.itemView as CardView

        (if(position % 2 == 0){
            holder.itemView.setCardBackgroundColor(0xFFbba2e0.toInt())
        } else {
            holder.itemView.setCardBackgroundColor(0xFFb191bf.toInt())
        })

    }

    override fun getItemCount(): Int {
        return listInfos.size
    }

    var myFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            val tempList : MutableList<Info> = mutableListOf()
            if (constraint != null && listInfos != null) {
                for (p in listInfos) {
                    if (p.title.contains(constraint.toString()) || p.description.contains(constraint.toString()))
                        tempList.add(p)
                }
                filterResults.values = tempList
                filterResults.count = tempList.size
            }
            return filterResults
        }

        override fun publishResults(contraint: CharSequence, results: FilterResults) {
            if(contraint.isEmpty())
                listInfos = listInfosCopy
            else
                listInfos = results.values as MutableList<Info>
            if (results.count > 0)
                notifyDataSetChanged()
        }
    }


    override fun getFilter(): Filter {
        return myFilter;
    }

}