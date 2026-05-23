package com.coffeeshop.auth.internal.data.firebase

import android.app.Activity
import android.util.Log
import com.coffeeshop.auth.internal.di.AuthScope
import com.coffeeshop.common.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AuthScope
internal class FirebasePhoneAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onAutoVerified: (idToken: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        result.user?.getIdToken(false)
                            ?.addOnSuccessListener { tokenResult ->
                                val token = tokenResult.token
                                if (token != null) onAutoVerified(token)
                                else onError(IllegalStateException("Firebase ID token is null after auto-verification"))
                            }
                            ?.addOnFailureListener { onError(it) }
                            ?: onError(IllegalStateException("Firebase user is null after auto-verification"))
                    }
                    .addOnFailureListener { onError(it) }
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                onError(e)
            }

            override fun onCodeSent(
                newVerificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = newVerificationId
                resendToken = token
                onCodeSent()
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        resendToken?.let { optionsBuilder.setForceResendingToken(it) }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    suspend fun signInWithCode(smsCode: String): Result<String> {
        val id = verificationId
            ?: return Result.Error(IllegalStateException("verificationId is null — send SMS first"))

        return try {
            val credential = PhoneAuthProvider.getCredential(id, smsCode)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val token = authResult.user?.getIdToken(false)?.await()?.token
                ?: return Result.Error(IllegalStateException("Firebase ID token is null"))
            Result.Success(token)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithCode error", e)
            Result.Error(e)
        }
    }

    private companion object {
        const val TAG = "FirebasePhoneAuthManager"
    }
}
