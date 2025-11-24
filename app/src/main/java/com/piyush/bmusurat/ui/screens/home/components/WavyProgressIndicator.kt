package com.piyush.bmusurat.ui.screens.home.components

import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WavyProgressIndicator(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val thickStrokeWidth = with(density) { 6.5.dp.toPx() }
    val thickStroke = remember(thickStrokeWidth) {
        Stroke(width = thickStrokeWidth, cap = StrokeCap.Round)
    }

    CircularWavyProgressIndicator(
        modifier = modifier,
        stroke = thickStroke,
        trackStroke = thickStroke
    )
}
