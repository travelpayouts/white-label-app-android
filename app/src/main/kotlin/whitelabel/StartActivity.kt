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

    // Параметр не-nullable намеренно: рекламные адаптеры Appodeal поднимают
    // androidx.activity до 1.9.x, где ComponentActivity.onNewIntent объявлен с
    // @NonNull. С Intent? сборка у партнёра, включившего рекламу, не проходит.
    // Non-null совместим и со старой версией, где тип платформенный.
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