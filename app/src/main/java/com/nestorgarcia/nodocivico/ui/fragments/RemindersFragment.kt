package com.nestorgarcia.nodocivico.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nestorgarcia.nodocivico.databinding.FragmentRemindersBinding
import com.nestorgarcia.nodocivico.ui.adapters.ReminderAdapter
import com.nestorgarcia.nodocivico.viewmodel.ReminderViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory
import java.util.Calendar

class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!

    private val reminderViewModel: ReminderViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeReminders()

        binding.fabNewReminder.setOnClickListener {
            showCreateReminderDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReminderAdapter { reminder ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar recordatorio")
                .setMessage("¿Eliminar este recordatorio?")
                .setPositiveButton("Eliminar") { _, _ ->
                    reminderViewModel.delete(reminder)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        binding.recyclerReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReminders.adapter = adapter
    }

    private fun observeReminders() {
        reminderViewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
            binding.tvEmpty.visibility =
                if (reminders.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerReminders.visibility =
                if (reminders.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showCreateReminderDialog() {
        val dialogView = layoutInflater.inflate(
            com.nestorgarcia.nodocivico.R.layout.dialog_create_reminder, null
        )
        val etMessage = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            com.nestorgarcia.nodocivico.R.id.etReminderMessage
        )
        val tvSelectedDate = dialogView.findViewById<android.widget.TextView>(
            com.nestorgarcia.nodocivico.R.id.tvSelectedDate
        )
        val btnPickDate = dialogView.findViewById<com.google.android.material.button.MaterialButton>(
            com.nestorgarcia.nodocivico.R.id.btnPickDate
        )

        var selectedCalendar: Calendar? = null

        btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                TimePickerDialog(requireContext(), { _, hour, minute ->
                    selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }
                    tvSelectedDate.text = String.format(
                        "%02d/%02d/%d %02d:%02d", day, month + 1, year, hour, minute
                    )
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo recordatorio")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val message = etMessage.text.toString().trim()
                if (message.isEmpty()) {
                    Toast.makeText(requireContext(), "Escribe un mensaje", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (selectedCalendar == null) {
                    Toast.makeText(requireContext(), "Selecciona fecha y hora", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                // reportId = 0 para recordatorio general
                reminderViewModel.insert(0, message, selectedCalendar!!.timeInMillis)
                Toast.makeText(requireContext(), "Recordatorio creado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}