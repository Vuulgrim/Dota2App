package com.example.nextlab.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nextlab.Models.MatchDetails
import com.example.nextlab.R
import com.example.nextlab.databinding.MatchdetailsitemBinding

class MatchDetailsAdapter: RecyclerView.Adapter<MatchDetailsAdapter.MatchdetailsHolder>() {
    val matchdetailList = ArrayList<MatchDetails>()
    class MatchdetailsHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = MatchdetailsitemBinding.bind(item)
        fun bind(matchdetails: MatchDetails) = with(binding) {
            nickname.text = matchdetails.player_name
            hero.text = matchdetails.hero
            networth.text = matchdetails.networth
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchdetailsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.matchdetailsitem, parent, false)
        return MatchdetailsHolder(view)
    }

    override fun getItemCount(): Int {
        return matchdetailList.size
    }

    fun adddetails(matchdetails: MatchDetails) {
        matchdetailList.add(matchdetails)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MatchdetailsHolder, position: Int) {
        holder.bind(matchdetailList[position])
    }
}