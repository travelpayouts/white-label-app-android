package com.travelapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.travelapp.ui.activities.MainActivity

class StartActivity : AppCompatActivity() {

    private companion object {
        private const val LINK_KEY = "link"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startMainActivity()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        startMainActivity()
    }

    private fun startMainActivity() {
        val newIntent = Intent(this, MainActivity::class.java)
        newIntent.data = if (intent.extras != null) {
            intent.extras?.getString(LINK_KEY)?.toUri()
        } else {
            intent.data
        }

        startActivity(newIntent)
        finish()
    }

}