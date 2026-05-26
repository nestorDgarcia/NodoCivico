package com.nestorgarcia.nodocivico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nestorgarcia.nodocivico.R
import com.nestorgarcia.nodocivico.databinding.FragmentEditReportBinding
import com.nestorgarcia.nodocivico.model.Priority
import com.nestorgarcia.nodocivico.viewmodel.CategoryViewModel
import com.nestorgarcia.nodocivico.viewmodel.ReportState
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class EditReportFragment : Fragment() {

    private var _binding: FragmentEditReportBinding? = null
    private val binding get() = _binding!!
    private val args: EditReportFragmentArgs by navArgs()

    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private var selectedCategoryId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPrioritySpinner()
        observeReport()
        observeState()

        binding.btnUpdate.setOnClickListener {
            updateReport()
        }
    }

    private fun observeReport() {
        reportViewModel.getById(args.reportId).observe(viewLifecycleOwner) { report ->
            if (report == null) return@observe

            binding.etTitle.setText(report.title)
            binding.etDescription.setText(report.description)
            binding.etLocation.setText(report.location)

            // Prioridad
            val priorityIndex = when (report.priority) {
                Priority.LOW    -> 0
                Priority.MEDIUM -> 1
                Priority.HIGH   -> 2
            }
            binding.spinnerPriority.setSelection(priorityIndex)

            // Categorías
            categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
                val names = categories.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    names
                )
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter

                val categoryIndex = categories.indexOfFirst { it.id == report.categoryId }
                if (categoryIndex >= 0) binding.spinnerCategory.setSelection(categoryIndex)

                binding.spinnerCategory.setOnItemSelectedListener(
                    object : android.widget.AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: android.widget.AdapterView<*>,
                            v: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedCategoryId = categories[position].id
                        }
                        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                    }
                )
            }
        }
    }

    private fun setupPrioritySpinner() {
        val priorities = listOf("🟢 Baja", "🟡 Media", "🔴 Alta")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            priorities
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter
    }

    private fun updateReport() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "El título es obligatorio"
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "La descripción es obligatoria"
            return
        }

        val priority = when (binding.spinnerPriority.selectedItemPosition) {
            0 -> Priority.LOW
            2 -> Priority.HIGH
            else -> Priority.MEDIUM
        }

        reportViewModel.getById(args.reportId).value?.let { existing ->
            val updated = existing.copy(
                title = title,
                description = description,
                location = location,
                priority = priority,
                categoryId = selectedCategoryId ?: existing.categoryId,
                isSynced = false
            )
            reportViewModel.update(updated)
        }
    }

    private fun observeState() {
        reportViewModel.reportState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReportState.Loading -> {
                    binding.btnUpdate.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ReportState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Reporte actualizado", Toast.LENGTH_SHORT).show()
                    reportViewModel.resetState()
                    findNavController().navigateUp()
                }
                is ReportState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpdate.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    reportViewModel.resetState()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpdate.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}