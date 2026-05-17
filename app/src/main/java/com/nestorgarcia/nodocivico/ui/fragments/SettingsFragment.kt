package com.nestorgarcia.nodocivico.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nestorgarcia.nodocivico.databinding.FragmentSettingsBinding
import com.nestorgarcia.nodocivico.receiver.ConnectivityReceiver

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PREFS_NAME = "nodo_civico_prefs"
        const val KEY_API_URL = "api_url"
        const val DEFAULT_URL = "http://10.0.2.2:5000"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupButtons()
        updateConnectionStatus()
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedUrl = prefs.getString(KEY_API_URL, DEFAULT_URL)
        binding.etApiUrl.setText(savedUrl)
    }

    private fun setupButtons() {
        binding.btnSaveSettings.setOnClickListener {
            val url = binding.etApiUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "La URL no puede estar vacía", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_API_URL, url).apply()
            Toast.makeText(requireContext(), "Ajustes guardados", Toast.LENGTH_SHORT).show()
        }

        binding.btnUseEmulator.setOnClickListener {
            binding.etApiUrl.setText("http://10.0.2.2:5000")
        }

        binding.btnUseDevice.setOnClickListener {
            binding.etApiUrl.setText("http://192.168.1.14:5000")
        }
    }

    private fun updateConnectionStatus() {
        val isConnected = ConnectivityReceiver.isConnected(requireContext())
        binding.tvConnectionStatus.text = if (isConnected)
            "🟢 Conectado a internet"
        else
            "🔴 Sin conexión a internet"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}