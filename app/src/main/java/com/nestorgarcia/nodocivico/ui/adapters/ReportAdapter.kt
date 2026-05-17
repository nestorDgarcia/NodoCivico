package com.nestorgarcia.nodocivico.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nestorgarcia.nodocivico.databinding.ItemReportBinding
import com.nestorgarcia.nodocivico.model.Priority
import com.nestorgarcia.nodocivico.model.Report
import com.nestorgarcia.nodocivico.model.ReportStatus

class ReportAdapter(
    private val onClick: (Report) -> Unit
) : ListAdapter<Report, ReportAdapter.ReportViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(oldItem: Report, newItem: Report) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Report, newItem: Report) =
            oldItem == newItem
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            binding.tvTitle.text = report.title
            binding.tvLocation.text = report.location.ifEmpty { "Sin ubicación" }
            binding.tvDate.text = android.text.format.DateFormat
                .format("dd/MM/yyyy", report.createdAt).toString()

            // Estado
            val (statusText, statusColor) = when (report.status) {
                ReportStatus.OPEN        -> Pair("Abierto",     "#DC2626")
                ReportStatus.IN_PROGRESS -> Pair("En proceso",  "#F59E0B")
                ReportStatus.CLOSED      -> Pair("Cerrado",     "#16A34A")
            }
            binding.chipStatus.text = statusText
            binding.chipStatus.setChipBackgroundColor(
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(statusColor)
                )
            )

            // Prioridad
            val priorityText = when (report.priority) {
                Priority.LOW    -> "🟢 Baja"
                Priority.MEDIUM -> "🟡 Media"
                Priority.HIGH   -> "🔴 Alta"
            }
            binding.tvPriority.text = priorityText

            // Indicador de sincronización
            binding.tvSyncIndicator.text = if (report.isSynced) "✓ Sync" else "⏳ Pendiente"
            binding.tvSyncIndicator.setTextColor(
                android.graphics.Color.parseColor(
                    if (report.isSynced) "#16A34A" else "#F59E0B"
                )
            )

            binding.root.setOnClickListener { onClick(report) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}