package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coffeeshop.designsystem.common.DarkBrown

@Composable
fun LoadingOverlay(
    alfa: Float = 0.35f,
    progressIndicatorSizeDp: Dp = 56.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = alfa)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(progressIndicatorSizeDp),
            color = DarkBrown
        )
    }
}

@Composable
@Preview
private fun LoadingOverlayPreview() = LoadingOverlay()