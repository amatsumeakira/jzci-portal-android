package com.jzci.portal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jzci.portal.ui.screens.LoginScreen
import com.jzci.portal.ui.screens.DashboardScreen
import com.jzci.portal.ui.theme.JZCIPortalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("jzci_prefs", MODE_PRIVATE)
        val sessionId = prefs.getString("session_id", null)
        val username = prefs.getString("username", null)

        if (sessionId != null) {
            JZCIPortalApp.instance.apiService.setSession(sessionId)
        }

        setContent {
            JZCIPortalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (sessionId != null) {
                        DashboardScreen(username = username)
                    } else {
                        LoginScreen()
                    }
                }
            }
        }
    }
}
