package com.company.tripplanner.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.tripplanner.data.models.UserProfile
import com.company.tripplanner.data.services.AuthService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val authService = AuthService()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _userProfile = mutableStateOf(UserProfile())
    val userProfile: State<UserProfile> = _userProfile

    private val _registerError = mutableStateOf<String?>(null)
    val registerError: State<String?> = _registerError

    private val _isAlreadyRegistered = mutableStateOf(false)
    val isAlreadyRegistered: State<Boolean> = _isAlreadyRegistered

    private val _isRegisterLoading = mutableStateOf(false)
    val isRegisterLoading: State<Boolean> = _isRegisterLoading

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    private val _isLoginLoading = mutableStateOf(false)
    val isLoginLoading: State<Boolean> = _isLoginLoading

    fun clearRegisterState() {
        _registerError.value = null
        _isAlreadyRegistered.value = false
        _isRegisterLoading.value = false
    }

    fun clearLoginState() {
        _loginError.value = null
        _isLoginLoading.value = false
    }

    init {
        val currentUser = authService.getCurrentUser()
        if (currentUser != null) {
            loadUserProfile(currentUser.uid)
        }
    }

    private fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("profiles").document(uid).get().await()
                val profile = document.toObject(UserProfile::class.java)
                if (profile != null) {
                    _userProfile.value = profile.copy(isRegistered = true)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun registerUser(name: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isRegisterLoading.value = true
            _registerError.value = null
            _isAlreadyRegistered.value = false
            val result = authService.signUp(email, pass, name)
            _isRegisterLoading.value = false
            if (result.isSuccess) {
                val uid = authService.getUserId() ?: return@launch
                val profile = UserProfile(fullName = name, email = email, password = pass, isRegistered = true)
                try {
                    firestore.collection("profiles").document(uid).set(profile).await()
                    _userProfile.value = profile
                    onSuccess()
                } catch (e: Exception) {
                    _registerError.value = e.localizedMessage ?: "Failed to save user profile"
                }
            } else {
                val exception = result.exceptionOrNull()
                _registerError.value = when {
                    exception is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                        _isAlreadyRegistered.value = true
                        "This email is already in use. Please login instead."
                    }
                    exception?.message?.contains("CONFIGURATION_NOT_FOUND") == true -> {
                        "Setup Required: Please enable 'Email/Password' in your Firebase Console under Authentication > Sign-in method."
                    }
                    else -> exception?.localizedMessage ?: "Registration failed"
                }
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _loginError.value = null
            val result = authService.signIn(email, pass)
            _isLoginLoading.value = false
            if (result.isSuccess) {
                val uid = authService.getUserId() ?: return@launch
                loadUserProfile(uid)
                onSuccess()
            } else {
                _loginError.value = "credentials are invalid"
            }
        }
    }

    fun onGoogleSignIn(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            val result = authService.signInWithGoogle(idToken)
            _isLoginLoading.value = false
            if (result.isSuccess) {
                val user = authService.getCurrentUser()
                if (user != null) {
                    val profile = UserProfile(fullName = user.displayName ?: "Voyage Explorer", email = user.email ?: "", isRegistered = true)
                    try {
                        firestore.collection("profiles").document(user.uid).set(profile).await()
                        _userProfile.value = profile
                        onSuccess()
                    } catch (e: Exception) {
                        // Profile save failed but user is logged in
                        _userProfile.value = profile
                        onSuccess()
                    }
                }
            } else {
                _loginError.value = "Google sign in failed"
            }
        }
    }
    
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.signOut()
            _userProfile.value = UserProfile()
            onSuccess()
        }
    }

    fun updateUserProfile(
        fullName: String,
        phoneNumber: String,
        photoUri: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val current = _userProfile.value
                val updatedProfile = current.copy(
                    fullName = fullName.trim(),
                    phoneNumber = phoneNumber.trim(),
                    profilePictureUri = photoUri.trim()
                )

                val uid = authService.getUserId()
                if (uid != null) {
                    val profileMap = mapOf(
                        "fullName" to fullName.trim(),
                        "email" to current.email,
                        "phoneNumber" to phoneNumber.trim(),
                        "profilePictureUri" to photoUri.trim(),
                        "isRegistered" to true
                    )
                    try {
                        firestore.collection("profiles")
                            .document(uid)
                            .set(profileMap, com.google.firebase.firestore.SetOptions.merge())
                            .await()
                    } catch (e: Exception) {
                        android.util.Log.w("UserViewModel", "Firestore update skipped/offline: ${e.message}")
                    }
                }

                _userProfile.value = updatedProfile
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to update profile")
            }
        }
    }

    fun validateLogin(email: String, pass: String): Boolean {
        // With Firebase, we usually just call login. 
        // For existing UI compatibility, I'll return true if basic check passes, 
        // but real check happens in login()
        return email.isNotEmpty() && pass.isNotEmpty()
    }
}
