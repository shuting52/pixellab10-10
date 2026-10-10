package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.RadialGradient
import android.graphics.Rect as AndroidRect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BackgroundType
import com.example.model.CanvasElement
import com.example.model.CanvasProject
import com.example.model.ImageElement
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.StickerElement
import com.example.model.TextElement
import com.example.ui.theme.GlobalBgMode
import com.example.ui.theme.ThemeManager
import com.example.util.AssetManagerHelper
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CanvasView(
    project: CanvasProject,
    isGridVisible: Boolean,
    zoomLevel: Float,
    onSelectElement: (String?) -> Unit,
    onUpdatePosition: (String, Float, Float) -> Unit,
    onUpdateTransform: (String, Float, Float) -> Unit,
    onTransformEnd: () -> Unit,
    onDeleteElement: () -> Unit,
    onDuplicateElement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isCustomThemeActive = ThemeManager.currentBgMode == GlobalBgMode.CUSTOM_VIDEO ||
                              ThemeManager.currentBgMode == GlobalBgMode.CUSTOM_IMAGE ||
                              ThemeManager.customMediaUri != null

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onSelectElement(null)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val aspect = project.aspectRatio.ratio

            Box(
                modifier = Modifier
                    .aspectRatio(aspect, matchHeightConstraintsFirst = aspect < 1f)
                    .fillMaxSize(0.92f)
                    .shadow(if (isCustomThemeActive) 0.dp else 24.dp, RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(
                                androidx.compose.ui.graphics.Color(0x8000E5FF),
                                androidx.compose.ui.graphics.Color(0x337C4DFF)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isCustomThemeActive || project.backgroundType == BackgroundType.TRANSPARENT) Color.Transparent else Color.White)
                    .testTag("canvas_viewport")
            ) {
                // Interactive Canvas Surface
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(project.selectedElementId) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                val selectedId = project.selectedElementId
                                if (selectedId != null) {
                                    val el = project.elements.firstOrNull { it.id == selectedId }
                                    if (el != null && !el.isLocked) {
                                        if (pan != Offset.Zero) {
                                            onUpdatePosition(selectedId, el.x + pan.x, el.y + pan.y)
                                        }
                                        if (zoom != 1f || rotation != 0f) {
                                            val newScale = (el.scale * zoom).coerceIn(0.2f, 5.0f)
                                            val newRot = (el.rotation + rotation) % 360f
                                            onUpdateTransform(selectedId, newScale, newRot)
                                        }
                                    }
                                }
                            }
                        }
                        .pointerInput(project.elements) {
                            detectTapGestures { tapOffset ->
                                val w = size.width
                                val h = size.height
                                val centerX = w / 2f
                                val centerY = h / 2f

                                // Check elements in reverse order (top layer first)
                                var hitId: String? = null
                                for (el in project.elements.asReversed()) {
                                    if (!el.isVisible) continue
                                    val elCenterX = centerX + el.x
                                    val elCenterY = centerY + el.y

                                    // Approximate hit box based on scale
                                    val hitRadius = when (el) {
                                        is TextElement -> (el.fontSize * el.scale * 1.5f).coerceAtLeast(40f)
                                        is StickerElement -> (el.width * el.scale * 0.6f).coerceAtLeast(40f)
                                        is ShapeElement -> (el.width * el.scale * 0.6f).coerceAtLeast(40f)
                                        is ImageElement -> (el.width * el.scale * 0.6f).coerceAtLeast(40f)
                                    }
                                    val dx = tapOffset.x - elCenterX
                                    val dy = tapOffset.y - elCenterY
                                    if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                                        hitId = el.id
                                        break
                                    }
                                }
                                onSelectElement(hitId)
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val centerX = canvasWidth / 2f
                    val centerY = canvasHeight / 2f

                    val hasColorFilter = project.bgBrightness != 0f || project.bgContrast != 0f ||
                            project.bgSaturation != 0f || project.bgSepia > 0f ||
                            project.bgInvert || project.bgHue != 0f

                    // 1. Draw Background
                    drawProjectBackground(context, project, canvasWidth, canvasHeight)

                    // 2. Draw Elements (with optional ColorMatrix filter layer)
                    drawIntoCanvas { composeCanvas ->
                        val nativeCanvas = composeCanvas.nativeCanvas

                        if (hasColorFilter) {
                            val cm = ColorMatrix()
                            if (project.bgSaturation != 0f) {
                                cm.setSaturation((1f + project.bgSaturation).coerceAtLeast(0f))
                            }
                            if (project.bgBrightness != 0f || project.bgContrast != 0f) {
                                val scale = 1f + project.bgContrast
                                val translate = project.bgBrightness * 255f
                                val bcMatrix = ColorMatrix(floatArrayOf(
                                    scale, 0f, 0f, 0f, translate,
                                    0f, scale, 0f, 0f, translate,
                                    0f, 0f, scale, 0f, translate,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                cm.postConcat(bcMatrix)
                            }
                            if (project.bgSepia > 0f) {
                                val s = project.bgSepia
                                val invS = 1f - s
                                val sepiaMatrix = ColorMatrix(floatArrayOf(
                                    invS + s * 0.393f, s * 0.769f, s * 0.189f, 0f, 0f,
                                    s * 0.349f, invS + s * 0.686f, s * 0.168f, 0f, 0f,
                                    s * 0.272f, s * 0.534f, invS + s * 0.131f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                cm.postConcat(sepiaMatrix)
                            }
                            if (project.bgInvert) {
                                val invertMatrix = ColorMatrix(floatArrayOf(
                                    -1f, 0f, 0f, 0f, 255f,
                                    0f, -1f, 0f, 0f, 255f,
                                    0f, 0f, -1f, 0f, 255f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                cm.postConcat(invertMatrix)
                            }
                            if (project.bgHue != 0f) {
                                val hueMatrix = ColorMatrix()
                                hueMatrix.setRotate(0, project.bgHue)
                                hueMatrix.setRotate(1, project.bgHue)
                                hueMatrix.setRotate(2, project.bgHue)
                                cm.postConcat(hueMatrix)
                            }
                            val filterPaint = AndroidPaint().apply {
                                colorFilter = ColorMatrixColorFilter(cm)
                            }
                            nativeCanvas.saveLayer(0f, 0f, canvasWidth, canvasHeight, filterPaint)
                        }

                        for (element in project.elements) {
                            if (!element.isVisible) continue

                            nativeCanvas.save()
                            nativeCanvas.translate(centerX + element.x, centerY + element.y)
                            nativeCanvas.rotate(element.rotation)
                            nativeCanvas.scale(element.scale, element.scale)

                            when (element) {
                                is TextElement -> drawNativeText(context, nativeCanvas, element)
                                is StickerElement -> drawNativeSticker(context, nativeCanvas, element)
                                is ShapeElement -> drawNativeShape(nativeCanvas, element)
                                is ImageElement -> drawNativeImage(context, nativeCanvas, element)
                            }

                            nativeCanvas.restore()
                        }

                        if (hasColorFilter) {
                            nativeCanvas.restore()
                        }
                    }

                    // 3. Draw FX Overlays (Vignette, Stripes, Noise, Glitch & Border Frame)
                    drawProjectEffects(project, canvasWidth, canvasHeight)

                    // 4. Draw Active Element Selection Box
                    val selected = project.elements.firstOrNull { it.id == project.selectedElementId }
                    if (selected != null && selected.isVisible) {
                        drawSelectionBox(selected, centerX, centerY)
                    }

                    // 5. Draw Grid if enabled
                    if (isGridVisible) {
                        drawGridOverlay(canvasWidth, canvasHeight)
                    }
                }

                // Quick Action Floating Controls for Selected Element
                val selected = project.elements.firstOrNull { it.id == project.selectedElementId }
                if (selected != null && selected.isVisible && !selected.isLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                            shadowElevation = 6.dp
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = onDuplicateElement,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Duplicate Layer",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onDeleteElement,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete Layer",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawProjectBackground(
    context: Context,
    project: CanvasProject,
    w: Float,
    h: Float
) {
    val isCustomThemeActive = ThemeManager.currentBgMode == GlobalBgMode.CUSTOM_VIDEO ||
                              ThemeManager.currentBgMode == GlobalBgMode.CUSTOM_IMAGE ||
                              ThemeManager.customMediaUri != null

    if (isCustomThemeActive) {
        // 当上传了视频/图片主题是背景全透明方式呈现
        return
    }

    when (project.backgroundType) {
        BackgroundType.TRANSPARENT -> {
            drawCheckerboard(w, h)
        }
        BackgroundType.SOLID -> {
            drawRect(Color(project.bgColor), size = androidx.compose.ui.geometry.Size(w, h))
        }
        BackgroundType.GRADIENT -> {
            drawRect(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(project.bgGradientStart), Color(project.bgGradientEnd))
                ),
                size = androidx.compose.ui.geometry.Size(w, h)
            )
        }
        BackgroundType.ASSET -> {
            val assetPath = project.bgAssetPath
            if (assetPath != null) {
                val bmp = AssetManagerHelper.getBitmapFromAsset(context, assetPath)
                if (bmp != null) {
                    drawIntoCanvas {
                        it.nativeCanvas.drawBitmap(
                            bmp,
                            null,
                            RectF(0f, 0f, w, h),
                            AndroidPaint(AndroidPaint.FILTER_BITMAP_FLAG)
                        )
                    }
                } else {
                    drawRect(Color(project.bgColor))
                }
            } else {
                drawRect(Color(project.bgColor))
            }
        }
        BackgroundType.GALLERY -> {
            val uriStr = project.bgImageUri
            if (uriStr != null) {
                try {
                    val stream = context.contentResolver.openInputStream(Uri.parse(uriStr))
                    val bmp = android.graphics.BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (bmp != null) {
                        drawIntoCanvas {
                            it.nativeCanvas.drawBitmap(
                                bmp,
                                null,
                                RectF(0f, 0f, w, h),
                                AndroidPaint(AndroidPaint.FILTER_BITMAP_FLAG)
                            )
                        }
                    } else {
                        drawRect(Color(project.bgColor))
                    }
                } catch (e: Exception) {
                    drawRect(Color(project.bgColor))
                }
            } else {
                drawRect(Color(project.bgColor))
            }
        }
    }
}

private fun DrawScope.drawCheckerboard(w: Float, h: Float) {
    val step = 20f
    var x = 0f
    var row = 0
    while (x < w) {
        var y = 0f
        var col = 0
        while (y < h) {
            val isEven = (row + col) % 2 == 0
            val color = if (isEven) Color(0xFFEEEEEE) else Color(0xFFCCCCCC)
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(step, step)
            )
            y += step
            col++
        }
        x += step
        row++
    }
}

private fun DrawScope.drawProjectEffects(project: CanvasProject, w: Float, h: Float) {
    if (project.bgVignette > 0f) {
        drawIntoCanvas { composeCanvas ->
            val radius = (w.coerceAtLeast(h) * 0.75f)
            val alphaInt = (project.bgVignette * 255).toInt().coerceIn(0, 255)
            val edgeColor = (alphaInt shl 24) or (project.bgVignetteColor.toInt() and 0x00FFFFFF)
            val paint = AndroidPaint().apply {
                shader = RadialGradient(
                    w / 2f, h / 2f, radius,
                    android.graphics.Color.TRANSPARENT, edgeColor, Shader.TileMode.CLAMP
                )
            }
            composeCanvas.nativeCanvas.drawRect(0f, 0f, w, h, paint)
        }
    }

    if (project.bgStripes) {
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color.Black.copy(alpha = 0.12f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 2.dp.toPx()
            )
            y += 12f
        }
    }

    // Noise / Film Grain
    if (project.bgNoise > 0f) {
        drawIntoCanvas { composeCanvas ->
            val nativeCanvas = composeCanvas.nativeCanvas
            val noiseAlpha = (project.bgNoise * 45).toInt().coerceIn(6, 80)
            val lightDot = AndroidPaint().apply {
                color = AndroidColor.argb(noiseAlpha, 255, 255, 255)
                strokeWidth = 1.5f
            }
            val darkDot = AndroidPaint().apply {
                color = AndroidColor.argb(noiseAlpha, 0, 0, 0)
                strokeWidth = 1.5f
            }
            var seed = 123456789L
            val dotCount = (w * h * 0.0006f * project.bgNoise).toInt().coerceIn(150, 3000)
            for (i in 0 until dotCount) {
                seed = (seed * 1103515245L + 12345L) and 0x7fffffffL
                val px = (seed % w.toLong()).toFloat()
                seed = (seed * 1103515245L + 12345L) and 0x7fffffffL
                val py = (seed % h.toLong()).toFloat()
                val p = if (i % 2 == 0) lightDot else darkDot
                nativeCanvas.drawPoint(px, py, p)
            }
        }
    }

    // RGB Glitch / Chromatic Aberration
    if (project.bgGlitch > 0f) {
        drawIntoCanvas { composeCanvas ->
            val nativeCanvas = composeCanvas.nativeCanvas
            val glitchOffset = project.bgGlitch * 16f
            val cyanPaint = AndroidPaint().apply {
                color = AndroidColor.argb((project.bgGlitch * 55).toInt().coerceIn(10, 75), 0, 255, 255)
            }
            val redPaint = AndroidPaint().apply {
                color = AndroidColor.argb((project.bgGlitch * 55).toInt().coerceIn(10, 75), 255, 0, 80)
            }
            var gSeed = 987654321L
            val sliceCount = (6 * project.bgGlitch).toInt() + 2
            for (s in 0 until sliceCount) {
                gSeed = (gSeed * 1103515245L + 12345L) and 0x7fffffffL
                val sliceY = (gSeed % h.toLong()).toFloat()
                gSeed = (gSeed * 1103515245L + 12345L) and 0x7fffffffL
                val sliceH = (gSeed % 18L).toFloat() + 4f
                nativeCanvas.drawRect(-glitchOffset, sliceY, w - glitchOffset, sliceY + sliceH, cyanPaint)
                nativeCanvas.drawRect(glitchOffset, sliceY, w + glitchOffset, sliceY + sliceH, redPaint)
            }
        }
    }

    // Decorative Frame Border
    if (project.bgBorderWidth > 0f) {
        drawIntoCanvas { composeCanvas ->
            val bPaint = AndroidPaint().apply {
                color = project.bgBorderColor.toInt()
                style = AndroidPaint.Style.STROKE
                strokeWidth = project.bgBorderWidth * 2f
            }
            composeCanvas.nativeCanvas.drawRect(0f, 0f, w, h, bPaint)
        }
    }
}

private fun DrawScope.drawGridOverlay(w: Float, h: Float) {
    val gridSize = 40.dp.toPx()
    val gridColor = Color(0x3300B4D8)

    var x = gridSize
    while (x < w) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 1f
        )
        x += gridSize
    }

    var y = gridSize
    while (y < h) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1f
        )
        y += gridSize
    }
}

private fun DrawScope.drawSelectionBox(element: CanvasElement, centerX: Float, centerY: Float) {
    val elX = centerX + element.x
    val elY = centerY + element.y

    val boundW: Float
    val boundH: Float
    when (element) {
        is TextElement -> {
            boundW = (element.text.length * element.fontSize * 0.5f * element.scale).coerceAtLeast(100f)
            boundH = (element.fontSize * 1.4f * element.scale).coerceAtLeast(50f)
        }
        is StickerElement -> {
            boundW = element.width * element.scale
            boundH = element.height * element.scale
        }
        is ShapeElement -> {
            boundW = element.width * element.scale
            boundH = element.height * element.scale
        }
        is ImageElement -> {
            boundW = element.width * element.scale
            boundH = element.height * element.scale
        }
    }

    val halfW = boundW / 2f + 16f
    val halfH = boundH / 2f + 16f

    // Draw selection border rectangle
    drawRect(
        color = Color(0xFF00B4D8),
        topLeft = Offset(elX - halfW, elY - halfH),
        size = androidx.compose.ui.geometry.Size(halfW * 2f, halfH * 2f),
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    )

    // Corner handle dots
    val handleColor = Color(0xFF00B4D8)
    val handleRadius = 5.dp.toPx()
    drawCircle(handleColor, handleRadius, Offset(elX - halfW, elY - halfH))
    drawCircle(handleColor, handleRadius, Offset(elX + halfW, elY - halfH))
    drawCircle(handleColor, handleRadius, Offset(elX - halfW, elY + halfH))
    drawCircle(handleColor, handleRadius, Offset(elX + halfW, elY + halfH))
}

private fun drawNativeText(context: Context, canvas: android.graphics.Canvas, el: TextElement) {
    val typeface = AssetManagerHelper.getTypeface(context, el.fontAssetPath)
    val basePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        textSize = el.fontSize
        textAlign = when (el.alignment) {
            0 -> AndroidPaint.Align.LEFT
            2 -> AndroidPaint.Align.RIGHT
            else -> AndroidPaint.Align.CENTER
        }
        isFakeBoldText = el.isBold
        textSkewX = if (el.isItalic) -0.25f else 0f
        isUnderlineText = el.isUnderline
        alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
    }

    val lines = el.text.split("\n")
    val fm = basePaint.fontMetrics
    val lineHeight = (fm.descent - fm.ascent) + el.lineSpacing
    val totalHeight = lines.size * lineHeight

    var maxW = 0f
    for (line in lines) {
        val w = basePaint.measureText(line)
        if (w > maxW) maxW = w
    }
    val halfW = maxW / 2f
    val startY = -totalHeight / 2f + (-fm.ascent)

    // Background Box
    if (el.bgBoxEnabled) {
        val bgPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
            color = el.bgBoxColor.toInt()
            style = AndroidPaint.Style.FILL
        }
        val rect = RectF(
            -halfW - el.bgBoxPaddingH,
            -totalHeight / 2f - el.bgBoxPaddingV,
            halfW + el.bgBoxPaddingH,
            totalHeight / 2f + el.bgBoxPaddingV
        )
        canvas.drawRoundRect(rect, el.bgBoxCornerRadius, el.bgBoxCornerRadius, bgPaint)
    }

    // 3D Depth
    if (el.threeDEnabled && el.threeDDepth > 0f) {
        val threeDPaint = AndroidPaint(basePaint).apply {
            color = el.threeDColor.toInt()
            style = AndroidPaint.Style.FILL
            clearShadowLayer()
        }
        val depth = el.threeDDepth.toInt().coerceIn(1, 30)
        for (step in depth downTo 1) {
            var curY = startY
            for (line in lines) {
                canvas.drawText(line, step.toFloat(), curY + step.toFloat(), threeDPaint)
                curY += lineHeight
            }
        }
    }

    // Floor Reflection
    if (el.reflectionEnabled) {
        canvas.save()
        val reflectPaint = AndroidPaint(basePaint).apply {
            alpha = ((el.reflectionAlpha * el.opacity) * 255).toInt().coerceIn(0, 255)
            color = el.textColor.toInt()
        }
        canvas.translate(0f, (totalHeight / 2f) + el.reflectionOffset)
        canvas.scale(1f, -0.65f)
        var curY = -fm.ascent
        for (line in lines) {
            canvas.drawText(line, 0f, curY, reflectPaint)
            curY += lineHeight
        }
        canvas.restore()
    }

    // Stroke
    if (el.strokeEnabled && el.strokeWidth > 0f) {
        val strokePaint = AndroidPaint(basePaint).apply {
            color = el.strokeColor.toInt()
            style = AndroidPaint.Style.STROKE
            strokeWidth = el.strokeWidth
            strokeJoin = AndroidPaint.Join.ROUND
            strokeCap = AndroidPaint.Cap.ROUND
        }
        var curY = startY
        for (line in lines) {
            canvas.drawText(line, 0f, curY, strokePaint)
            curY += lineHeight
        }
    }

    // Main Text
    val mainPaint = AndroidPaint(basePaint).apply {
        color = el.textColor.toInt()
        style = AndroidPaint.Style.FILL
        if (el.shadowEnabled) {
            setShadowLayer(el.shadowRadius, el.shadowDx, el.shadowDy, el.shadowColor.toInt())
        }
    }

    if (el.curveBend != 0f) {
        val path = AndroidPath()
        val span = maxW * 1.25f
        val controlY = el.curveBend * 2.2f
        path.moveTo(-span / 2f, 0f)
        path.quadTo(0f, controlY, span / 2f, 0f)
        for (line in lines) {
            if (el.strokeEnabled && el.strokeWidth > 0f) {
                val strokePaint = AndroidPaint(basePaint).apply {
                    color = el.strokeColor.toInt()
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = el.strokeWidth
                    strokeJoin = AndroidPaint.Join.ROUND
                    strokeCap = AndroidPaint.Cap.ROUND
                }
                canvas.drawTextOnPath(line, path, 0f, 0f, strokePaint)
            }
            canvas.drawTextOnPath(line, path, 0f, 0f, mainPaint)
        }
    } else {
        var curY = startY
        for (line in lines) {
            canvas.drawText(line, 0f, curY, mainPaint)
            curY += lineHeight
        }
    }
}

private fun drawNativeSticker(context: Context, canvas: android.graphics.Canvas, el: StickerElement) {
    val bmp = AssetManagerHelper.getBitmapFromAsset(context, el.assetPath) ?: return
    val paint = AndroidPaint(AndroidPaint.FILTER_BITMAP_FLAG or AndroidPaint.ANTI_ALIAS_FLAG).apply {
        alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
    }
    val dst = RectF(-el.width / 2f, -el.height / 2f, el.width / 2f, el.height / 2f)
    canvas.drawBitmap(bmp, null, dst, paint)
}

private fun drawNativeShape(canvas: android.graphics.Canvas, el: ShapeElement) {
    val halfW = el.width / 2f
    val halfH = el.height / 2f

    val fillPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = el.fillColor.toInt()
        style = AndroidPaint.Style.FILL
        alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
    }

    val strokePaint = if (el.strokeWidth > 0f) {
        AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
            color = el.strokeColor.toInt()
            style = AndroidPaint.Style.STROKE
            strokeWidth = el.strokeWidth
            alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
        }
    } else null

    val rect = RectF(-halfW, -halfH, halfW, halfH)
    when (el.shapeType) {
        ShapeType.RECTANGLE -> {
            canvas.drawRect(rect, fillPaint)
            strokePaint?.let { canvas.drawRect(rect, it) }
        }
        ShapeType.ROUNDED_RECT -> {
            canvas.drawRoundRect(rect, el.cornerRadius, el.cornerRadius, fillPaint)
            strokePaint?.let { canvas.drawRoundRect(rect, el.cornerRadius, el.cornerRadius, it) }
        }
        ShapeType.CIRCLE -> {
            val r = halfW.coerceAtMost(halfH)
            canvas.drawCircle(0f, 0f, r, fillPaint)
            strokePaint?.let { canvas.drawCircle(0f, 0f, r, it) }
        }
        ShapeType.STAR -> {
            val path = createStarPath(halfW.coerceAtMost(halfH), 5)
            canvas.drawPath(path, fillPaint)
            strokePaint?.let { canvas.drawPath(path, it) }
        }
        ShapeType.HEART -> {
            val path = createHeartPath(el.width, el.height)
            canvas.drawPath(path, fillPaint)
            strokePaint?.let { canvas.drawPath(path, it) }
        }
        ShapeType.TRIANGLE -> {
            val path = AndroidPath().apply {
                moveTo(0f, -halfH)
                lineTo(halfW, halfH)
                lineTo(-halfW, halfH)
                close()
            }
            canvas.drawPath(path, fillPaint)
            strokePaint?.let { canvas.drawPath(path, it) }
        }
        ShapeType.ARROW -> {
            val path = AndroidPath().apply {
                moveTo(-halfW, -halfH * 0.4f)
                lineTo(0f, -halfH * 0.4f)
                lineTo(0f, -halfH)
                lineTo(halfW, 0f)
                lineTo(0f, halfH)
                lineTo(0f, halfH * 0.4f)
                lineTo(-halfW, halfH * 0.4f)
                close()
            }
            canvas.drawPath(path, fillPaint)
            strokePaint?.let { canvas.drawPath(path, it) }
        }
        ShapeType.HEXAGON -> {
            val r = halfW.coerceAtMost(halfH)
            val path = AndroidPath().apply {
                for (i in 0 until 6) {
                    val a = i * Math.PI / 3 - Math.PI / 6
                    val px = (r * kotlin.math.cos(a)).toFloat()
                    val py = (r * kotlin.math.sin(a)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            canvas.drawPath(path, fillPaint)
            strokePaint?.let { canvas.drawPath(path, it) }
        }
    }
}

private fun drawNativeImage(context: Context, canvas: android.graphics.Canvas, el: ImageElement) {
    var bmp: Bitmap? = null
    if (!el.assetPath.isNullOrBlank()) {
        bmp = AssetManagerHelper.getBitmapFromAsset(context, el.assetPath)
    } else if (!el.uriString.isNullOrBlank()) {
        try {
            val stream = context.contentResolver.openInputStream(Uri.parse(el.uriString))
            bmp = android.graphics.BitmapFactory.decodeStream(stream)
            stream?.close()
        } catch (e: Exception) {
            // Ignore
        }
    }

    if (bmp != null) {
        val paint = AndroidPaint(AndroidPaint.FILTER_BITMAP_FLAG or AndroidPaint.ANTI_ALIAS_FLAG).apply {
            alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
        }
        val dst = RectF(-el.width / 2f, -el.height / 2f, el.width / 2f, el.height / 2f)
        canvas.drawBitmap(bmp, null, dst, paint)
    }
}

private fun createStarPath(radius: Float, numPoints: Int = 5): AndroidPath {
    val path = AndroidPath()
    val innerRadius = radius * 0.45f
    val angleStep = Math.PI / numPoints
    var angle = -Math.PI / 2

    path.moveTo((radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat())
    for (i in 0 until numPoints) {
        angle += angleStep
        path.lineTo((innerRadius * cos(angle)).toFloat(), (innerRadius * sin(angle)).toFloat())
        angle += angleStep
        path.lineTo((radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat())
    }
    path.close()
    return path
}

private fun createHeartPath(width: Float, height: Float): AndroidPath {
    val path = AndroidPath()
    val w = width * 0.5f
    val h = height * 0.5f

    path.moveTo(0f, h * 0.6f)
    path.cubicTo(-w * 1.1f, -h * 0.2f, -w * 0.6f, -h * 1.1f, 0f, -h * 0.4f)
    path.cubicTo(w * 0.6f, -h * 1.1f, w * 1.1f, -h * 0.2f, 0f, h * 0.6f)
    path.close()
    return path
}
