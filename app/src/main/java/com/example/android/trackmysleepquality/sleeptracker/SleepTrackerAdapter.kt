package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemBinding
import kotlinx.android.synthetic.main.list_item.view.*


/**
 * RecyclerView interagieren nicht direkt mit Viewa
 * Das erledigt der ViewHolder
 */

class SleepTrackerAdapter :
    ListAdapter<SleepNight, SleepTrackerAdapter.ViewHolder>(SleepNightDiffUtilCallBack()) {

    // Show data in a specific Position --> 1 Item wo die daten herkommen und angezeigt werden sollen
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    // 1 item of RC --> Parent View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // ViewHolder with Data Binding of List items
    class ViewHolder private constructor(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SleepNight) {
            // Binding Data from Binding Adapter
            /*
            Evaluates the pending bindings, updating any Views that have expressions bound to modified variables. T
            his must be run on the UI thread.
             */
            binding.sleep = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                // Data Binding Item List
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class SleepNightDiffUtilCallBack : DiffUtil.ItemCallback<SleepNight>() {
    // Prüft ob item gelöscht bewegt etc wurden
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }

}
