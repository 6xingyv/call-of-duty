package io.mocha.duty.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorPage(onRetry: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("出错了", style = MaterialTheme.typography.headlineMedium)
        content()
        Button(onClick = onRetry, Modifier.fillMaxWidth()) {
            Text("刷新")
        }
    }
}