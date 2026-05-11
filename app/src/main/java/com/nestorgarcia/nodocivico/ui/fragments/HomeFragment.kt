package com.nestorgarcia.nodocivico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nestorgarcia.nodocivico.R
import com.nestorgarcia.nodocivico.databinding.FragmentHomeBinding
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.SyncViewModel
import com.nestorgarcia.nodocivico.viewmodel.UserViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val syncViewModel: SyncViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        setupButtons()
    }

    private fun observeData() {
        // Nombre del usuario
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.tvWelcome.text = if (user != null)
                "Hola, ${user.username} 👋"
            else
                "Bienvenido a Nodo Cívico"
            binding.tvZone.text = user?.zone ?: ""
        }

        // Conteos de reportes
        reportViewModel.allReports.observe(viewLifecycleOwner) { reports ->
            binding.tvTotalReports.text = reports.size.toString()
            binding.tvOpenReports.text = reports.count {
                it.status == ReportStatus.OPEN
            }.toString()
            binding.tvInProgressReports.text = reports.count {
                it.status == ReportStatus.IN_PROGRESS
            }.toString()
            binding.tvClosedReports.text = reports.count {
                it.status == ReportStatus.CLOSED
            }.toString()
        }

        // Estado de sincronización
        syncViewModel.pendingCount.observe(viewLifecycleOwner) { pending ->
            if (pending > 0) {
                binding.chipSyncStatus.text = "⚠ $pending pendientes"
                binding.chipSyncStatus.setChipBackgroundColorResource(
                    com.google.android.material.R.color.design_default_color_error
                )
            } else {
                binding.chipSyncStatus.text = "✓ Sincronizado"
                binding.chipSyncStatus.setChipBackgroundColorResource(
                    android.R.color.holo_green_dark
                )
            }
        }
    }

    private fun setupButtons() {
        binding.btnNewReport.setOnClickListener {
            findNavController().navigate(R.id.createReportFragment)
        }
        binding.btnViewReports.setOnClickListener {
            findNavController().navigate(R.id.reportListFragment)
        }
        binding.btnViewSync.setOnClickListener {
            findNavController().navigate(R.id.syncStatusFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}