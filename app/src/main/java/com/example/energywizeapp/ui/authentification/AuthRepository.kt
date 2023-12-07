package com.example.energywizeapp.ui.authentification

import com.example.firebaseauthyt.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String, username: String, street: String, firstName: String, surname: String): Flow<Resource<AuthResult>>
}
