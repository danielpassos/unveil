package me.passos.libs.unveil.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.passos.libs.unveil.Unveil
import me.passos.libs.unveil.UnveilHost

/**
 * The root composable of the app.
 */
@Composable
fun App() {
    Unveil.configure {
        register(CustomConfigurationPlugin())
        register(CustomBoxPlugin())
    }
    Unveil.enable()

    UnveilHost(enabled = Unveil.isEnabled) {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello, Unveil!",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
