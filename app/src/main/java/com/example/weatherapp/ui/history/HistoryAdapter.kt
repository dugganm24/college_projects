package com.example.weatherapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.local.SavedWeather

class HistoryAdapter(private var historyList: List<SavedWeather>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationText: TextView = itemView.findViewById(R.id.locationText)
        val temperatureText: TextView = itemView.findViewById(R.id.temperatureText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_weather, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        val temperature = item.temperatureCelsius
        val formattedTemperature = String.format("%.1f", temperature)
        holder.locationText.text = item.locationName
        holder.temperatureText.text = "$formattedTemperature°C"
        holder.dateText.text = item.date
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<SavedWeather>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
