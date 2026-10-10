package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.BackgroundType
import com.example.model.CanvasElement
import com.example.model.CanvasProject
import com.example.model.ImageElement
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.StickerElement
import com.example.model.TextElement
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.cos
import kotlin.math.sin

object CanvasExporter {

    fun renderToBitmap(
        context: Context,
        project: CanvasProject,
        targetWidth: Int = project.aspectRatio.defaultWidth,
        targetHeight: Int = project.aspectRatio.defaultHeight
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val previewRefSize = 1000f
        val scaleFactor = (targetWidth / previewRefSize).coerceAtLeast(0.5f)
        val centerX = targetWidth / 2f
        val centerY = targetHeight / 2f

        val hasColorFilter = project.bgBrightness != 0f || project.bgContrast != 0f ||
                project.bgSaturation != 0f || project.bgSepia > 0f ||
                project.bgInvert || project.bgHue != 0f

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
            val filterPaint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(cm)
            }
            canvas.saveLayer(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), filterPaint)
        }

        // 1. Draw Background
        when (project.backgroundType) {
            BackgroundType.TRANSPARENT -> {
                // Keep transparent or draw nothing for PNG
            }
            BackgroundType.SOLID -> {
                val paint = Paint().apply {
                    color = project.bgColor.toInt()
                    style = Paint.Style.FILL
                }
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)
            }
            BackgroundType.GRADIENT -> {
                val paint = Paint().apply {
                    shader = LinearGradient(
                        0f, 0f, 0f, targetHeight.toFloat(),
                        project.bgGradientStart.toInt(),
                        project.bgGradientEnd.toInt(),
                        Shader.TileMode.CLAMP
                    )
                    style = Paint.Style.FILL
                }
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)
            }
            BackgroundType.ASSET -> {
                val assetPath = project.bgAssetPath
                if (assetPath != null) {
                    val bgBitmap = AssetManagerHelper.getBitmapFromAsset(context, assetPath)
                    if (bgBitmap != null) {
                        val srcRect = Rect(0, 0, bgBitmap.width, bgBitmap.height)
                        val dstRect = Rect(0, 0, targetWidth, targetHeight)
                        canvas.drawBitmap(bgBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
                    } else {
                        canvas.drawColor(project.bgColor.toInt())
                    }
                } else {
                    canvas.drawColor(project.bgColor.toInt())
                }
            }
            BackgroundType.GALLERY -> {
                if (project.bgImageUri != null) {
                    try {
                        val uri = Uri.parse(project.bgImageUri)
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            val bgBmp = android.graphics.BitmapFactory.decodeStream(stream)
                            if (bgBmp != null) {
                                val srcRect = Rect(0, 0, bgBmp.width, bgBmp.height)
                                val dstRect = Rect(0, 0, targetWidth, targetHeight)
                                canvas.drawBitmap(bgBmp, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
                            }
                        }
                    } catch (e: Exception) {
                        canvas.drawColor(project.bgColor.toInt())
                    }
                } else {
                    canvas.drawColor(project.bgColor.toInt())
                }
            }
        }

        // 2. Draw Elements in order
        for (element in project.elements) {
            if (!element.isVisible) continue

            canvas.save()
            val elX = centerX + element.x * scaleFactor
            val elY = centerY + element.y * scaleFactor
            canvas.translate(elX, elY)
            canvas.rotate(element.rotation)
            val combinedScale = element.scale * scaleFactor
            canvas.scale(combinedScale, combinedScale)

            when (element) {
                is TextElement -> drawTextElement(context, canvas, element)
                is StickerElement -> drawStickerElement(context, canvas, element)
                is ShapeElement -> drawShapeElement(canvas, element)
                is ImageElement -> drawImageElement(context, canvas, element)
            }

            canvas.restore()
        }

        if (hasColorFilter) {
            canvas.restore()
        }

        // 3. Apply Overlays: Vignette, Stripes, Noise, Glitch & Border Frame
        if (project.bgVignette > 0f) {
            val radius = (targetWidth.coerceAtLeast(targetHeight) * 0.75f)
            val vignettePaint = Paint().apply {
                val alphaInt = (project.bgVignette * 255).toInt().coerceIn(0, 255)
                val edgeColor = (alphaInt shl 24) or (project.bgVignetteColor.toInt() and 0x00FFFFFF)
                shader = RadialGradient(
                    targetWidth / 2f, targetHeight / 2f, radius,
                    AndroidColor.TRANSPARENT, edgeColor, Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), vignettePaint)
        }

        if (project.bgStripes) {
            val stripePaint = Paint().apply {
                color = AndroidColor.argb(30, 0, 0, 0)
                strokeWidth = 4f
            }
            var y = 0f
            while (y < targetHeight) {
                canvas.drawLine(0f, y, targetWidth.toFloat(), y, stripePaint)
                y += 12f
            }
        }

        if (project.bgNoise > 0f) {
            val noiseAlpha = (project.bgNoise * 45).toInt().coerceIn(6, 80)
            val lightDot = Paint().apply {
                color = AndroidColor.argb(noiseAlpha, 255, 255, 255)
                strokeWidth = 2f
            }
            val darkDot = Paint().apply {
                color = AndroidColor.argb(noiseAlpha, 0, 0, 0)
                strokeWidth = 2f
            }
            var seed = 123456789L
            val dotCount = (targetWidth * targetHeight * 0.0006f * project.bgNoise).toInt().coerceIn(200, 5000)
            for (i in 0 until dotCount) {
                seed = (seed * 1103515245L + 12345L) and 0x7fffffffL
                val px = (seed % targetWidth.toLong()).toFloat()
                seed = (seed * 1103515245L + 12345L) and 0x7fffffffL
                val py = (seed % targetHeight.toLong()).toFloat()
                val p = if (i % 2 == 0) lightDot else darkDot
                canvas.drawPoint(px, py, p)
            }
        }

        if (project.bgGlitch > 0f) {
            val glitchOffset = project.bgGlitch * 20f * scaleFactor
            val cyanPaint = Paint().apply {
                color = AndroidColor.argb((project.bgGlitch * 55).toInt().coerceIn(10, 75), 0, 255, 255)
            }
            val redPaint = Paint().apply {
                color = AndroidColor.argb((project.bgGlitch * 55).toInt().coerceIn(10, 75), 255, 0, 80)
            }
            var gSeed = 987654321L
            val sliceCount = (6 * project.bgGlitch).toInt() + 2
            for (s in 0 until sliceCount) {
                gSeed = (gSeed * 1103515245L + 12345L) and 0x7fffffffL
                val sliceY = (gSeed % targetHeight.toLong()).toFloat()
                gSeed = (gSeed * 1103515245L + 12345L) and 0x7fffffffL
                val sliceH = (gSeed % 24L).toFloat() + 6f
                canvas.drawRect(-glitchOffset, sliceY, targetWidth.toFloat() - glitchOffset, sliceY + sliceH, cyanPaint)
                canvas.drawRect(glitchOffset, sliceY, targetWidth.toFloat() + glitchOffset, sliceY + sliceH, redPaint)
            }
        }

        if (project.bgBorderWidth > 0f) {
            val bPaint = Paint().apply {
                color = project.bgBorderColor.toInt()
                style = Paint.Style.STROKE
                strokeWidth = project.bgBorderWidth * 2f * scaleFactor
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bPaint)
        }

        return bitmap
    }

    private fun drawTextElement(context: Context, canvas: Canvas, el: TextElement) {
        val typeface = AssetManagerHelper.getTypeface(context, el.fontAssetPath)
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = el.fontSize
            textAlign = when (el.alignment) {
                0 -> Paint.Align.LEFT
                2 -> Paint.Align.RIGHT
                else -> Paint.Align.CENTER
            }
            isFakeBoldText = el.isBold
            textSkewX = if (el.isItalic) -0.25f else 0f
            isUnderlineText = el.isUnderline
            alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
        }

        val lines = el.text.split("\n")
        val fm = basePaint.fontMetrics
        val lineHeight = (fm.descent - fm.ascent) + el.lineSpacing
        val totalTextHeight = lines.size * lineHeight

        // Calculate maximum line width for background box
        var maxLineWidth = 0f
        for (line in lines) {
            val w = basePaint.measureText(line)
            if (w > maxLineWidth) maxLineWidth = w
        }

        val halfWidth = maxLineWidth / 2f
        val startY = -totalTextHeight / 2f + (-fm.ascent)

        // Draw Background Box if enabled
        if (el.bgBoxEnabled) {
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = el.bgBoxColor.toInt()
                style = Paint.Style.FILL
            }
            val padH = el.bgBoxPaddingH
            val padV = el.bgBoxPaddingV
            val boxRect = RectF(
                -halfWidth - padH,
                -totalTextHeight / 2f - padV,
                halfWidth + padH,
                totalTextHeight / 2f + padV
            )
            canvas.drawRoundRect(boxRect, el.bgBoxCornerRadius, el.bgBoxCornerRadius, bgPaint)
        }

        // Draw 3D Depth layers if enabled
        if (el.threeDEnabled && el.threeDDepth > 0f) {
            val threeDPaint = Paint(basePaint).apply {
                color = el.threeDColor.toInt()
                style = Paint.Style.FILL
                clearShadowLayer()
            }
            val depthSteps = el.threeDDepth.toInt().coerceIn(1, 40)
            for (step in depthSteps downTo 1) {
                val dx = step * 1f
                val dy = step * 1f
                var curY = startY
                for (line in lines) {
                    canvas.drawText(line, dx, curY + dy, threeDPaint)
                    curY += lineHeight
                }
            }
        }

        // Draw Floor Reflection if enabled
        if (el.reflectionEnabled) {
            canvas.save()
            val reflectPaint = Paint(basePaint).apply {
                alpha = ((el.reflectionAlpha * el.opacity) * 255).toInt().coerceIn(0, 255)
                color = el.textColor.toInt()
            }
            // Flip vertically below text
            canvas.translate(0f, (totalTextHeight / 2f) + el.reflectionOffset)
            canvas.scale(1f, -0.7f)
            var curY = -fm.ascent
            for (line in lines) {
                canvas.drawText(line, 0f, curY, reflectPaint)
                curY += lineHeight
            }
            canvas.restore()
        }

        // Draw Stroke if enabled
        if (el.strokeEnabled && el.strokeWidth > 0f) {
            val strokePaint = Paint(basePaint).apply {
                color = el.strokeColor.toInt()
                style = Paint.Style.STROKE
                strokeWidth = el.strokeWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            var curY = startY
            for (line in lines) {
                canvas.drawText(line, 0f, curY, strokePaint)
                curY += lineHeight
            }
        }

        // Main Text Paint
        val mainPaint = Paint(basePaint).apply {
            color = el.textColor.toInt()
            style = Paint.Style.FILL
            if (el.shadowEnabled) {
                setShadowLayer(el.shadowRadius, el.shadowDx, el.shadowDy, el.shadowColor.toInt())
            }
        }

        if (el.curveBend != 0f) {
            val path = Path()
            val span = maxLineWidth * 1.25f
            val controlY = el.curveBend * 2.2f
            path.moveTo(-span / 2f, 0f)
            path.quadTo(0f, controlY, span / 2f, 0f)
            for (line in lines) {
                if (el.strokeEnabled && el.strokeWidth > 0f) {
                    val strokePaint = Paint(basePaint).apply {
                        color = el.strokeColor.toInt()
                        style = Paint.Style.STROKE
                        strokeWidth = el.strokeWidth
                        strokeJoin = Paint.Join.ROUND
                        strokeCap = Paint.Cap.ROUND
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

    private fun drawStickerElement(context: Context, canvas: Canvas, el: StickerElement) {
        val bmp = AssetManagerHelper.getBitmapFromAsset(context, el.assetPath) ?: return
        val w = el.width
        val h = el.height
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
        }
        val dst = RectF(-w / 2f, -h / 2f, w / 2f, h / 2f)
        canvas.drawBitmap(bmp, null, dst, paint)
    }

    private fun drawShapeElement(canvas: Canvas, el: ShapeElement) {
        val w = el.width
        val h = el.height
        val halfW = w / 2f
        val halfH = h / 2f

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = el.fillColor.toInt()
            style = Paint.Style.FILL
            alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
        }

        val strokePaint = if (el.strokeWidth > 0f) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = el.strokeColor.toInt()
                style = Paint.Style.STROKE
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
                val radius = halfW.coerceAtMost(halfH)
                canvas.drawCircle(0f, 0f, radius, fillPaint)
                strokePaint?.let { canvas.drawCircle(0f, 0f, radius, it) }
            }
            ShapeType.STAR -> {
                val path = createStarPath(halfW.coerceAtMost(halfH), 5)
                canvas.drawPath(path, fillPaint)
                strokePaint?.let { canvas.drawPath(path, it) }
            }
            ShapeType.HEART -> {
                val path = createHeartPath(w, h)
                canvas.drawPath(path, fillPaint)
                strokePaint?.let { canvas.drawPath(path, it) }
            }
            ShapeType.TRIANGLE -> {
                val path = Path().apply {
                    moveTo(0f, -halfH)
                    lineTo(halfW, halfH)
                    lineTo(-halfW, halfH)
                    close()
                }
                canvas.drawPath(path, fillPaint)
                strokePaint?.let { canvas.drawPath(path, it) }
            }
            ShapeType.ARROW -> {
                val path = Path().apply {
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
                val path = Path().apply {
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

    private fun drawImageElement(context: Context, canvas: Canvas, el: ImageElement) {
        var bmp: Bitmap? = null
        if (!el.assetPath.isNullOrBlank()) {
            bmp = AssetManagerHelper.getBitmapFromAsset(context, el.assetPath)
        } else if (!el.uriString.isNullOrBlank()) {
            try {
                context.contentResolver.openInputStream(Uri.parse(el.uriString))?.use {
                    bmp = android.graphics.BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        if (bmp != null) {
            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
                alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
            }
            val dst = RectF(-el.width / 2f, -el.height / 2f, el.width / 2f, el.height / 2f)
            canvas.drawBitmap(bmp!!, null, dst, paint)
        }
    }

    private fun createStarPath(radius: Float, numPoints: Int = 5): Path {
        val path = Path()
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

    private fun createHeartPath(width: Float, height: Float): Path {
        val path = Path()
        val w = width * 0.5f
        val h = height * 0.5f

        path.moveTo(0f, h * 0.6f)
        path.cubicTo(-w * 1.1f, -h * 0.2f, -w * 0.6f, -h * 1.1f, 0f, -h * 0.4f)
        path.cubicTo(w * 0.6f, -h * 1.1f, w * 1.1f, -h * 0.2f, 0f, h * 0.6f)
        path.close()
        return path
    }

    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        format: String = "PNG",
        quality: Int = 100
    ): Uri? {
        val filename = "PixelLab_${System.currentTimeMillis()}.${format.lowercase()}"
        val compressFormat = if (format.equals("JPEG", ignoreCase = true) || format.equals("JPG", ignoreCase = true)) {
            Bitmap.CompressFormat.JPEG
        } else {
            Bitmap.CompressFormat.PNG
        }
        val mimeType = if (compressFormat == Bitmap.CompressFormat.JPEG) "image/jpeg" else "image/png"

        var outputStream: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PixelLab")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = resolver.openOutputStream(imageUri)
                    if (outputStream != null) {
                        bitmap.compress(compressFormat, quality, outputStream)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PixelLab"
                val dir = File(imagesDir)
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, filename)
                outputStream = FileOutputStream(file)
                bitmap.compress(compressFormat, quality, outputStream)
                imageUri = Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageUri = null
        } finally {
            outputStream?.flush()
            outputStream?.close()
        }

        return imageUri
    }

    fun shareBitmap(context: Context, bitmap: Bitmap, format: String = "PNG") {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "pixellab_export_${System.currentTimeMillis()}.${format.lowercase()}")
            val stream = FileOutputStream(file)
            val compressFormat = if (format.equals("JPEG", ignoreCase = true)) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
            bitmap.compress(compressFormat, 100, stream)
            stream.flush()
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = if (compressFormat == Bitmap.CompressFormat.JPEG) "image/jpeg" else "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
