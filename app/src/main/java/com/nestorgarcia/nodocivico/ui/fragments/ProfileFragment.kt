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
import com.nestorgarcia.nodocivico.R
import com.nestorgarcia.nodocivico.databinding.FragmentProfileBinding
import com.nestorgarcia.nodocivico.viewmodel.ReportViewModel
import com.nestorgarcia.nodocivico.viewmodel.UserViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        setupButtons()
    }

    private fun observeData() {
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvUsername.text = user.username
            binding.tvZone.text = "📍 ${user.zone}"
            binding.tvEmail.text = if (user.email.isNotEmpty())
                "✉️ ${user.email}" else "✉️ Sin correo registrado"
            binding.tvMemberSince.text = "📅 Miembro desde: ${
                android.text.format.DateFormat.format("dd/MM/yyyy", user.createdAt)
            }"
        }

        reportViewModel.allReports.observe(viewLifecycleOwner) { reports ->
            binding.tvTotalCreated.text = reports.size.toString()
            binding.tvTotalClosed.text = reports.count {
                it.status == com.nestorgarcia.nodocivico.model.ReportStatus.CLOSED
            }.toString()
            binding.tvTotalPending.text = reports.count {
                !it.isSynced
            }.toString()
        }
    }

    private fun setupButtons() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión") { _, _ ->
                    findNavController().navigate(R.id.loginFragment)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun showEditProfileDialog() {
        val user = userViewModel.currentUser.value ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etZone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            R.id.etEditZone
        )
        val etEmail = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            R.id.etEditEmail
        )
        etZone.setText(user.zone)
        etEmail.setText(user.email)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val zone = etZone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                if (zone.isEmpty()) {
                    Toast.makeText(requireContext(), "La zona no puede estar vacía", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val updated = user.copy(zone = zone, email = email)
                userViewModel.update(updated)
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}