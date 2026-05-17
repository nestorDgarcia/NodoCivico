package com.nestorgarcia.nodocivico.ui.fragments

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nestorgarcia.nodocivico.databinding.FragmentSyncStatusBinding
import com.nestorgarcia.nodocivico.receiver.ConnectivityReceiver
import com.nestorgarcia.nodocivico.viewmodel.SyncState
import com.nestorgarcia.nodocivico.viewmodel.SyncViewModel
import com.nestorgarcia.nodocivico.viewmodel.ViewModelFactory

class SyncStatusFragment : Fragment() {

    private var _binding: FragmentSyncStatusBinding? = null
    private val binding get() = _binding!!

    private val syncViewModel: SyncViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var connectivityReceiver: ConnectivityReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        observeSyncState()
        setupButtons()
        registerConnectivityReceiver()
    }

    private fun observeData() {
        syncViewModel.pendingCount.observe(viewLifecycleOwner) { pending ->
            binding.tvPending.text = "⏳ Pendientes de envío: $pending"
            binding.btnSync.isEnabled = pending > 0
        }

        syncViewModel.syncedCount.observe(viewLifecycleOwner) { synced ->
            binding.tvSynced.text = "✓ Sincronizados: $synced"
        }

        syncViewModel.failedCount.observe(viewLifecycleOwner) { failed ->
            binding.tvFailed.text = "✗ Fallidos: $failed"
            binding.tvFailed.setTextColor(
                android.graphics.Color.parseColor(
                    if (failed > 0) "#DC2626" else "#16A34A"
                )
            )
        }
    }

    private fun observeSyncState() {
        syncViewModel.syncState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SyncState.Syncing -> {
                    binding.btnSync.isEnabled = false
                    binding.btnSync.text = "⏳ Sincronizando..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is SyncState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSync.text = "🔄  Sincronizar ahora"
                    Toast.makeText(
                        requireContext(),
                        "✓ Sincronización completada",
                        Toast.LENGTH_SHORT
                    ).show()
                    syncViewModel.resetState()
                }
                is SyncState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSync.isEnabled = true
                    binding.btnSync.text = "🔄  Sincronizar ahora"
                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                    syncViewModel.resetState()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSync.text = "🔄  Sincronizar ahora"
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnSync.setOnClickListener {
            syncViewModel.syncNow()
        }

        binding.btnClearHistory.setOnClickListener {
            syncViewModel.clearHistory()
            Toast.makeText(requireContext(), "Historial limpiado", Toast.LENGTH_SHORT).show()
        }

        binding.btnFetchCategories.setOnClickListener {
            syncViewModel.fetchCategories()
            Toast.makeText(requireContext(), "Categorías actualizadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerConnectivityReceiver() {
        connectivityReceiver = ConnectivityReceiver { isConnected ->
            if (_binding != null) {
                binding.tvConnectionStatus.text =
                    if (isConnected) "🟢 En línea" else "🔴 Sin conexión"
                binding.tvConnectionStatus.setTextColor(
                    android.graphics.Color.parseColor(
                        if (isConnected) "#16A34A" else "#DC2626"
                    )
                )
                // Habilitar sync solo si hay conexión y hay pendientes
                if (!isConnected) binding.btnSync.isEnabled = false
            }
        }

        @Suppress("DEPRECATION")
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, filter)

        // Verificar estado actual al abrir
        val isConnected = ConnectivityReceiver.isConnected(requireContext())
        binding.tvConnectionStatus.text =
            if (isConnected) "🟢 En línea" else "🔴 Sin conexión"
        binding.tvConnectionStatus.setTextColor(
            android.graphics.Color.parseColor(
                if (isConnected) "#16A34A" else "#DC2626"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(connectivityReceiver)
        _binding = null
    }
}