package com.local.fatateer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.local.fatateer.ui.FatateerApp
import com.local.fatateer.ui.theme.FatateerTheme
import com.local.fatateer.ui.theme.rememberThemeController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val controller = rememberThemeController()
            val systemDark = isSystemInDarkTheme()
            // re-read override
            val dark = controller.isDark(systemDark)
            // force recomposition when override changes
            @Suppress("UNUSED_VARIABLE")
            val tick = controller.darkOverride

            FatateerTheme(darkTheme = dark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FatateerApp(
                        isDark = dark,
                        onToggleTheme = { controller.cycle() }
                    )
                }
            }
        }
    }
}
