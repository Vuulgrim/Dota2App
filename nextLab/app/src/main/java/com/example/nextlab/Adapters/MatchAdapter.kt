package com.example.nextlab.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nextlab.Models.Match
import com.example.nextlab.R
import com.example.nextlab.databinding.MatchitemBinding

class MatchAdapter(val listener: Listener): RecyclerView.Adapter<MatchAdapter.MatchHolder>() {
    val matchList = ArrayList<Match>()
    //содержит ссылки на все View
    class MatchHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = MatchitemBinding.bind(item)
        fun bind(match: Match, listener: Listener) = with(binding){
            matchDate.text = match.date
            matchWinner.text = match.winner
            matchDuration.text = match.durationOfMatch
            avgMmr.text = match.averageMMR
            itemView.setOnClickListener {
                var pos = absoluteAdapterPosition
                Log.w("Help", pos.toString())
                listener.onClick(match, pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.matchitem, parent, false)
        return MatchHolder(view)
    }

    override fun getItemCount(): Int {
        return matchList.size
    }

    override fun onBindViewHolder(holder: MatchHolder, position: Int) {
        holder.bind(matchList[position], listener)
    }

    fun addMatch(match: Match) {
        matchList.add(match)
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClick(match: Match, position: Int)
    }
}