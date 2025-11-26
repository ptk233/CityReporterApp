package com.example.cityreporter.data.repository

import com.example.cityreporter.data.api.CityReporterApi
import com.example.cityreporter.data.models.*
import com.example.cityreporter.data.preferences.UserPreferences
import com.example.cityreporter.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: CityReporterApi,
    private val userPreferences: UserPreferences
) {
    
    suspend fun login(email: String, password: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                // Save auth data to preferences
                userPreferences.saveAuthData(
                    token = loginResponse.token,
                    refreshToken = loginResponse.refreshToken,
                    user = loginResponse.user
                )
                emit(Resource.Success(loginResponse))
            } else {
                emit(Resource.Error("Nieprawidłowy email lub hasło"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Login error")
            emit(Resource.Error(e.message ?: "Błąd podczas logowania"))
        }
    }
    
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phoneNumber: String?
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.register(
                RegisterRequest(email, password, name, phoneNumber)
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = when (response.code()) {
                    409 -> "Email już jest zarejestrowany"
                    400 -> "Nieprawidłowe dane"
                    else -> "Błąd rejestracji"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Register error")
            emit(Resource.Error(e.message ?: "Błąd podczas rejestracji"))
        }
    }
    
    suspend fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userPreferences.updateUser(user)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("Nie można pobrać danych użytkownika"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get user error")
            emit(Resource.Error(e.message ?: "Błąd pobierania danych"))
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAuthData()
    }
    
    suspend fun changePassword(oldPassword: String, newPassword: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.changePassword(
                mapOf(
                    "oldPassword" to oldPassword,
                    "newPassword" to newPassword
                )
            )
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Nieprawidłowe stare hasło"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Change password error")
            emit(Resource.Error(e.message ?: "Błąd zmiany hasła"))
        }
    }
    
    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn
    
    fun getCurrentUserFlow(): Flow<User?> = userPreferences.userData
}