package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight


/**
 * RecyclerView interagieren nicht direkt mit Viewa
 * Das erledigt der ViewHolder
 */

class SleepTrackerAdapter :
    RecyclerView.Adapter<SleepTrackerAdapter.ViewHolder>() {

    // Data: List of Datas
    var data = listOf<SleepNight>()
        // if Data is changed -> Update RC
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // RC muss die größe der Daten wissen
    override fun getItemCount() = data.size

    // Show data in a specific Position --> 1 Item wo die daten herkommen und angezeigt werden sollen
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    // 1 item of RC --> Parent View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        private val quality: TextView = itemView.findViewById(R.id.quality_string)
        private val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)
        /*
         init {...} Click Listeners für Item einbauen
          */


        fun bind(item: SleepNight) {
            val res = itemView.context.resources
            sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            quality.text = convertNumericQualityToString(item.sleepQuality, res)
            qualityImage.setImageResource(
                when (item.sleepQuality) {
                    0 -> R.drawable.ic_sleep_0
                    1 -> R.drawable.ic_sleep_1
                    2 -> R.drawable.ic_sleep_2
                    3 -> R.drawable.ic_sleep_3
                    4 -> R.drawable.ic_sleep_4
                    5 -> R.drawable.ic_sleep_5
                    else -> R.drawable.ic_launcher_sleep_tracker_foreground
                }
            )
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}
