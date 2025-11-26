package com.example.cityreporter.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityreporter.data.models.LoginResponse
import com.example.cityreporter.data.models.User
import com.example.cityreporter.data.repository.AuthRepository
import com.example.cityreporter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<User>?>(null)
    val registerState: StateFlow<Resource<User>?> = _registerState.asStateFlow()
    
    val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn()
    val currentUser: Flow<User?> = authRepository.getCurrentUserFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                _loginState.value = result
            }
        }
    }
    
    fun register(email: String, password: String, name: String, phoneNumber: String?) {
        viewModelScope.launch {
            authRepository.register(email, password, name, phoneNumber).collect { result ->
                _registerState.value = result
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    fun clearLoginState() {
        _loginState.value = null
    }
    
    fun clearRegisterState() {
        _registerState.value = null
    }
}