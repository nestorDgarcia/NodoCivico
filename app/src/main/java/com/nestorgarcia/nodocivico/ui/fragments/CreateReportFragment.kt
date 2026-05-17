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
import com.nestorgarcia.nodocivico.databinding.FragmentCreateReportBinding
import com.nestorgarcia.nodocivico.model.Priority
import com.nestorgarcia.nodocivico.model.Report
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.viewmodel.CategoryViewModel
import com.nestorgarcia.nodocivico.viewmodel.ReportState
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.UserViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class CreateReportFragment : Fragment() {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!

    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private var selectedCategoryId: Int? = null
    private var currentUserId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupPrioritySpinner()
        observeState()

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            currentUserId = user?.id
        }

        binding.btnSave.setOnClickListener {
            saveReport(isSynced = false)
        }

        binding.btnSaveOnline.setOnClickListener {
            saveReport(isSynced = false)
        }
    }

    private fun setupCategorySpinner() {
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.setOnItemSelectedListener(
                object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>,
                        view: View?,
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

    private fun setupPrioritySpinner() {
        val priorities = listOf("🟢 Baja", "🟡 Media", "🔴 Alta")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            priorities
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter
        binding.spinnerPriority.setSelection(1) // Media por defecto
    }

    private fun saveReport(isSynced: Boolean) {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        // Validaciones
        if (title.isEmpty()) {
            binding.etTitle.error = "El título es obligatorio"
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "La descripción es obligatoria"
            return
        }
        if (selectedCategoryId == null) {
            Toast.makeText(requireContext(), "Selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val priority = when (binding.spinnerPriority.selectedItemPosition) {
            0 -> Priority.LOW
            2 -> Priority.HIGH
            else -> Priority.MEDIUM
        }

        val report = Report(
            title = title,
            description = description,
            categoryId = selectedCategoryId,
            userId = currentUserId!!,
            priority = priority,
            status = ReportStatus.OPEN,
            location = location,
            isSynced = isSynced
        )

        reportViewModel.insert(report)
    }

    private fun observeState() {
        reportViewModel.reportState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReportState.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ReportState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Reporte guardado", Toast.LENGTH_SHORT).show()
                    reportViewModel.resetState()
                    findNavController().navigateUp()
                }
                is ReportState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    reportViewModel.resetState()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}