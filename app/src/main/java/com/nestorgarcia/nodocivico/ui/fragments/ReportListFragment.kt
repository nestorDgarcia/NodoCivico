package com.nestorgarcia.nodocivico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nestorgarcia.nodocivico.databinding.FragmentReportListBinding
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.ui.adapters.ReportAdapter
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class ReportListFragment : Fragment() {

    private var _binding: FragmentReportListBinding? = null
    private val binding get() = _binding!!

    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var adapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        observeReports()

        binding.fabNewReport.setOnClickListener {
            findNavController().navigate(
                com.nestorgarcia.nodocivico.R.id.createReportFragment
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter { report ->
            val action = ReportListFragmentDirections
                .actionListToDetail(report.id)
            findNavController().navigate(action)
        }
        binding.recyclerReports.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReports.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipAll.setOnClickListener {
            reportViewModel.setFilter(null)
        }
        binding.chipOpen.setOnClickListener {
            reportViewModel.setFilter(ReportStatus.OPEN)
        }
        binding.chipInProgress.setOnClickListener {
            reportViewModel.setFilter(ReportStatus.IN_PROGRESS)
        }
        binding.chipClosed.setOnClickListener {
            reportViewModel.setFilter(ReportStatus.CLOSED)
        }
    }

    private fun observeReports() {
        reportViewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            val source = if (filter == null)
                reportViewModel.allReports
            else
                reportViewModel.getByStatus(filter)

            source.observe(viewLifecycleOwner) { reports ->
                adapter.submitList(reports)
                binding.tvEmpty.visibility =
                    if (reports.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerReports.visibility =
                    if (reports.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}