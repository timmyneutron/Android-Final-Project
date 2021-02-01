package com.timmyneutron.finalproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthInitActivity : AppCompatActivity() {
    companion object {
        val rcSignIn = 7
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                rcSignIn
            )
        } else {
            Log.d("XXX", "error signing in")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            } else {
                Log.d("XXX", "error signing in")
                finish()
            }
        }
    }
}