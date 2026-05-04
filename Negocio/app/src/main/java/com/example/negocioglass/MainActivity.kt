package com.example.negocioglass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.negocioglass.ui.screens.NegocioGlassApp
import com.example.negocioglass.ui.theme.NegocioGlassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NegocioGlassTheme {
                NegocioGlassApp(application)
            }
        }
    }
}