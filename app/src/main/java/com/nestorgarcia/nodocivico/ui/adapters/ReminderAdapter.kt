package com.nestorgarcia.nodocivico.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nestorgarcia.nodocivico.databinding.ItemReminderBinding
import com.nestorgarcia.nodocivico.model.Reminder

class ReminderAdapter(
    private val onDelete: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem == newItem
    }

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.tvMessage.text = reminder.message
            binding.tvDate.text = android.text.format.DateFormat
                .format("dd/MM/yyyy HH:mm", reminder.triggerAt).toString()
            binding.tvTriggered.text = if (reminder.isTriggered) "✓ Enviado" else "⏳ Pendiente"
            binding.tvTriggered.setTextColor(
                android.graphics.Color.parseColor(
                    if (reminder.isTriggered) "#16A34A" else "#F59E0B"
                )
            )
            binding.btnDelete.setOnClickListener { onDelete(reminder) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}