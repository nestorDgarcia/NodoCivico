package com.nestorgarcia.nodocivico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorgarcia.nodocivico.model.User
import com.nestorgarcia.nodocivico.repository.UserRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    val currentUser: LiveData<User?> = userRepository.getCurrentUser()

    fun login(username: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val user = userRepository.login(username, password)
                if (user != null) {
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al iniciar sesión")
            }
        }
    }

    fun register(username: String, password: String, zone: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val existing = userRepository.getByUsername(username)
                if (existing != null) {
                    _authState.value = AuthState.Error("El usuario ya existe")
                    return@launch
                }
                userRepository.register(username, password, zone)
                val user = userRepository.login(username, password)
                if (user != null) {
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Error al registrar usuario")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error inesperado")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}