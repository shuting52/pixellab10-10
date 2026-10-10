package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlobalBgMode
import com.example.ui.theme.ThemeManager
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated liquid chromatic mesh background that generates organic, flowing
 * glowing liquid glass orbs and caustics, matching the active Capsule Theme.
 */
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier
) {
    val theme = ThemeManager.currentTheme
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_mesh")

    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 13000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF060913))
    ) {
        val width = size.width
        val height = size.height

        // Orb 1: Primary theme color
        val orb1X = width * (0.35f + 0.25f * cos(phase1))
        val orb1Y = height * (0.30f + 0.20f * sin(phase1))
        val orb1Radius = width.coerceAtLeast(height) * 0.55f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    theme.primaryColor.copy(alpha = 0.32f),
                    theme.secondaryColor.copy(alpha = 0.16f),
                    Color.Transparent
                ),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            ),
            radius = orb1Radius,
            center = Offset(orb1X, orb1Y)
        )

        // Orb 2: Accent theme color
        val orb2X = width * (0.70f + 0.25f * sin(phase2))
        val orb2Y = height * (0.65f + 0.22f * cos(phase2))
        val orb2Radius = width.coerceAtLeast(height) * 0.60f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    theme.accentColor.copy(alpha = 0.35f),
                    theme.secondaryColor.copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            ),
            radius = orb2Radius,
            center = Offset(orb2X, orb2Y)
        )

        // Orb 3: Secondary theme color
        val orb3X = width * (0.50f + 0.20f * cos(phase2 * 0.8f))
        val orb3Y = height * (0.45f + 0.18f * sin(phase1 * 0.8f))
        val orb3Radius = width.coerceAtLeast(height) * 0.45f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    theme.secondaryColor.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            ),
            radius = orb3Radius,
            center = Offset(orb3X, orb3Y)
        )
    }
}

/**
 * Universal Global Background that supports custom uploaded video, uploaded image,
 * preset dynamic liquid video simulations, or native liquid chromatic mesh.
 * Changes apply in real-time globally across the application.
 */
@Composable
fun GlobalAppBackground(
    modifier: Modifier = Modifier
) {
    val bgMode = ThemeManager.currentBgMode
    val customUri = ThemeManager.customMediaUri
    val theme = ThemeManager.currentTheme

    Box(modifier = modifier.fillMaxSize()) {
        when (bgMode) {
            GlobalBgMode.CUSTOM_VIDEO -> {
                if (customUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(customUri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    mp.setVolume(0f, 0f)
                                    start()
                                }
                                setOnErrorListener { _, _, _ -> true }
                            }
                        },
                        update = { view ->
                            view.setVideoURI(customUri)
                            view.start()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LiquidBackground()
                }
            }

            GlobalBgMode.CUSTOM_IMAGE -> {
                if (customUri != null) {
                    AsyncImage(
                        model = customUri,
                        contentDescription = "Custom Global Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LiquidBackground()
                }
            }

            GlobalBgMode.PRESET_VIDEO_FLOW,
            GlobalBgMode.PRESET_VIDEO_AURORA,
            GlobalBgMode.PRESET_VIDEO_COSMOS -> {
                PresetVideoSimulation(preset = bgMode)
            }

            GlobalBgMode.LIQUID_DYNAMIC -> {
                LiquidBackground()
            }
        }

        // Global frosted glass atmospheric overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xCC050A14),
                            Color(0x99081220),
                            Color(0xEA050A14)
                        )
                    )
                )
        )
    }
}

@Composable
fun PresetVideoSimulation(
    preset: GlobalBgMode,
    modifier: Modifier = Modifier
) {
    val theme = ThemeManager.currentTheme
    val infiniteTransition = rememberInfiniteTransition(label = "preset_sim")

    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04060C))
    ) {
        val w = size.width
        val h = size.height

        when (preset) {
            GlobalBgMode.PRESET_VIDEO_FLOW -> {
                for (i in 0..4) {
                    val progress = (t + i * 1.25f) % 6.28318f
                    val cx = w * (0.5f + 0.35f * sin(progress))
                    val cy = h * (0.5f + 0.35f * cos(progress * 1.3f))
                    val r = w * (0.4f + 0.1f * sin(progress * 0.7f))
                    val col = if (i % 2 == 0) theme.primaryColor else theme.accentColor
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(col.copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(cx, cy)
                    )
                }
            }

            GlobalBgMode.PRESET_VIDEO_AURORA -> {
                // Vertical wave curtains
                for (step in 0..5) {
                    val x = w * (step / 5f)
                    val offsetCurv = sin(t + step) * (w * 0.15f)
                    val r = h * 0.65f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                (if (step % 2 == 0) theme.secondaryColor else theme.primaryColor).copy(alpha = 0.30f),
                                Color.Transparent
                            ),
                            center = Offset(x + offsetCurv, h * 0.5f),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(x + offsetCurv, h * 0.5f)
                    )
                }
            }

            else -> {
                // Nebula pulse
                val pulse = (1f + 0.3f * sin(t))
                val r = (w.coerceAtLeast(h) * 0.5f) * pulse
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            theme.accentColor.copy(alpha = 0.45f),
                            theme.secondaryColor.copy(alpha = 0.20f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.5f),
                        radius = r
                    ),
                    radius = r,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
            }
        }
    }
}

/**
 * Modern Liquid Glass Capsule modifier builder.
 * Incorporates capsule pills, specular gradient sheens, and theme-aware chromatic borders.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = GlassBackground,
    borderWidth: Dp = 1.dp,
    activeGlow: Boolean = false
): Modifier {
    val theme = ThemeManager.currentTheme
    return this
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                listOf(
                    if (activeGlow) theme.primaryColor.copy(alpha = 0.35f) else backgroundColor.copy(alpha = 0.85f),
                    if (activeGlow) theme.secondaryColor.copy(alpha = 0.25f) else backgroundColor.copy(alpha = 0.65f)
                )
            ),
            shape = shape
        )
        .border(
            width = borderWidth,
            brush = Brush.verticalGradient(
                listOf(
                    if (activeGlow) theme.primaryColor else GlassBorderLight,
                    if (activeGlow) theme.accentColor else GlassBorder
                )
            ),
            shape = shape
        )
}

/**
 * Pill-shaped Liquid Glass Capsule container (Capsule Theme Spec).
 */
fun Modifier.liquidGlassCapsule(
    backgroundColor: Color = GlassBackground,
    borderWidth: Dp = 1.dp,
    activeGlow: Boolean = false
): Modifier = this.liquidGlassSurface(
    shape = RoundedCornerShape(percent = 50),
    backgroundColor = backgroundColor,
    borderWidth = borderWidth,
    activeGlow = activeGlow
)

/**
 * Sharp Right-Angle Liquid Glass container (直角矩形玻璃卡片).
 */
fun Modifier.liquidGlassRect(
    backgroundColor: Color = GlassBackground,
    borderWidth: Dp = 1.dp,
    activeGlow: Boolean = false
): Modifier = this.liquidGlassSurface(
    shape = RectangleShape,
    backgroundColor = backgroundColor,
    borderWidth = borderWidth,
    activeGlow = activeGlow
)
