package com.andrefillype.iwanttobelieve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.andrefillype.iwanttobelieve.presentation.navigation.AppNavigation
import com.andrefillype.iwanttobelieve.ui.theme.IWantToBelieveTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            IWantToBelieveTheme {
                val container = (applicationContext as IWantToBelieveApplication).appContainer
                AppNavigation(appContainer = container)
            }
        }
    }
}