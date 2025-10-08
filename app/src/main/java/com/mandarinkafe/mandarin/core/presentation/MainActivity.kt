package com.mandarinkafe.mandarin.core.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mandarinkafe.mandarin.core.presentation.theme.MandarinTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MandarinTheme {
                MainScreen()
            }
        }
    }
}