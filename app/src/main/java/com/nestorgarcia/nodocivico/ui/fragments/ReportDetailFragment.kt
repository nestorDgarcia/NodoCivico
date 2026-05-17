package com.nestorgarcia.nodocivico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nestorgarcia.nodocivico.databinding.FragmentReportDetailBinding
import com.nestorgarcia.nodocivico.model.Priority
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class ReportDetailFragment : Fragment() {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ReportDetailFragmentArgs by navArgs()

    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeReport()
        observeStatusHistory()
    }

    private fun observeReport() {
        reportViewModel.getById(args.reportId).observe(viewLifecycleOwner) { report ->
            if (report == null) return@observe

            binding.tvTitle.text = report.title
            binding.tvDescription.text = report.description
            binding.tvLocation.text = report.location.ifEmpty { "Sin ubicación" }
            binding.tvDate.text = android.text.format.DateFormat
                .format("dd/MM/yyyy HH:mm", report.createdAt).toString()

            // Prioridad
            binding.tvPriority.text = when (report.priority) {
                Priority.LOW    -> "🟢 Baja"
                Priority.MEDIUM -> "🟡 Media"
                Priority.HIGH   -> "🔴 Alta"
            }

            // Estado con color
            val (statusText, statusColor) = when (report.status) {
                ReportStatus.OPEN        -> Pair("Abierto",    "#DC2626")
                ReportStatus.IN_PROGRESS -> Pair("En proceso", "#F59E0B")
                ReportStatus.CLOSED      -> Pair("Cerrado",    "#16A34A")
            }
            binding.chipStatus.text = statusText
            binding.chipStatus.setChipBackgroundColor(
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(statusColor)
                )
            )

            // Sync
            binding.tvSyncStatus.text = if (report.isSynced)
                "✓ Sincronizado con el servidor"
            else
                "⏳ Pendiente de sincronización"

            // Botón cambiar estado
            binding.btnChangeStatus.setOnClickListener {
                showStatusDialog(report.id, report.status)
            }

            // Botón editar
            binding.btnEdit.setOnClickListener {
                val action = ReportDetailFragmentDirections
                    .actionDetailToEdit(report.id)
                findNavController().navigate(action)
            }

            // Botón eliminar
            binding.btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar reporte")
                    .setMessage("¿Estás seguro de que deseas eliminar este reporte?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        reportViewModel.deleteById(report.id)
                        findNavController().navigateUp()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun observeStatusHistory() {
        reportViewModel.getStatusHistory(args.reportId).observe(viewLifecycleOwner) { history ->
            val historyText = history.joinToString("\n") { item ->
                val date = android.text.format.DateFormat
                    .format("dd/MM/yyyy HH:mm", item.changedAt)
                val status = when (item.status) {
                    ReportStatus.OPEN        -> "Abierto"
                    ReportStatus.IN_PROGRESS -> "En proceso"
                    ReportStatus.CLOSED      -> "Cerrado"
                }
                "• $date — $status${if (item.note != null) ": ${item.note}" else ""}"
            }
            binding.tvHistory.text = historyText.ifEmpty { "Sin historial" }
        }
    }

    private fun showStatusDialog(reportId: Int, currentStatus: ReportStatus) {
        val options = arrayOf("Abierto", "En proceso", "Cerrado")
        val currentIndex = when (currentStatus) {
            ReportStatus.OPEN        -> 0
            ReportStatus.IN_PROGRESS -> 1
            ReportStatus.CLOSED      -> 2
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar estado")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                val newStatus = when (which) {
                    0 -> ReportStatus.OPEN
                    1 -> ReportStatus.IN_PROGRESS
                    else -> ReportStatus.CLOSED
                }
                reportViewModel.updateStatus(reportId, newStatus)
                Toast.makeText(requireContext(), "Estado actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}