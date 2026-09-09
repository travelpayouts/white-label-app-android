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

    // The parameter is non-null on purpose: the Appodeal ad adapters raise
    // androidx.activity to 1.9.x, where ComponentActivity.onNewIntent is declared
    // @NonNull. With Intent? the build fails for any partner who turns ads on.
    // Non-null also compiles against the older version, where the type is a
    // platform type.
    override fun onNewIntent(intent: Intent) {
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