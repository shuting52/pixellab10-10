package com.example.model

import java.util.UUID

enum class CanvasAspectRatio(val label: String, val ratio: Float, val defaultWidth: Int, val defaultHeight: Int) {
    SQUARE("1:1 (Square)", 1f, 1080, 1080),
    STORY("9:16 (Story / Reel)", 9f / 16f, 1080, 1920),
    PORTRAIT("4:5 (Portrait)", 4f / 5f, 1080, 1350),
    LANDSCAPE("16:9 (Landscape)", 16f / 9f, 1920, 1080),
    STANDARD("4:3 (Standard)", 4f / 3f, 1440, 1080),
    BANNER("3:1 (Banner)", 3f / 1f, 1500, 500)
}

enum class BackgroundType {
    SOLID, GRADIENT, ASSET, TRANSPARENT, GALLERY
}

enum class ShapeType(val displayName: String) {
    RECTANGLE("矩形 / Rect"),
    ROUNDED_RECT("圆角矩形 / Round"),
    CIRCLE("圆形 / Circle"),
    STAR("星形 / Star"),
    HEART("爱心 / Heart"),
    TRIANGLE("三角形 / Tri"),
    HEXAGON("六边形 / Hex"),
    ARROW("箭头 / Arrow")
}

sealed class CanvasElement {
    abstract val id: String
    abstract val x: Float
    abstract val y: Float
    abstract val rotation: Float
    abstract val scale: Float
    abstract val opacity: Float
    abstract val isLocked: Boolean
    abstract val isVisible: Boolean

    abstract fun copyWithCommon(
        x: Float = this.x,
        y: Float = this.y,
        rotation: Float = this.rotation,
        scale: Float = this.scale,
        opacity: Float = this.opacity,
        isLocked: Boolean = this.isLocked,
        isVisible: Boolean = this.isVisible
    ): CanvasElement
}

data class TextElement(
    override val id: String = UUID.randomUUID().toString(),
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val opacity: Float = 1f,
    override val isLocked: Boolean = false,
    override val isVisible: Boolean = true,
    val text: String = "New Text",
    val fontSize: Float = 46f,
    val fontAssetPath: String? = "fonts/Basic/Roboto-Regular.ttf",
    val fontDisplayName: String = "Roboto Regular",
    val textColor: Long = 0xFFFFFFFF,
    val textGradientStart: Long? = null,
    val textGradientEnd: Long? = null,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val alignment: Int = 1, // 0: Left, 1: Center, 2: Right
    val letterSpacing: Float = 0f,
    val lineSpacing: Float = 0f,
    val strokeEnabled: Boolean = false,
    val strokeColor: Long = 0xFF000000,
    val strokeWidth: Float = 4f,
    val shadowEnabled: Boolean = false,
    val shadowColor: Long = 0xAA000000,
    val shadowRadius: Float = 12f,
    val shadowDx: Float = 4f,
    val shadowDy: Float = 4f,
    val innerShadowEnabled: Boolean = false,
    val innerShadowColor: Long = 0x88000000,
    val innerShadowRadius: Float = 6f,
    val embossEnabled: Boolean = false,
    val threeDEnabled: Boolean = false,
    val threeDDepth: Float = 12f,
    val threeDColor: Long = 0xFF0077B6,
    val threeDRotationX: Float = 0f,
    val threeDRotationY: Float = 0f,
    val reflectionEnabled: Boolean = false,
    val reflectionOffset: Float = 6f,
    val reflectionAlpha: Float = 0.35f,
    val bgBoxEnabled: Boolean = false,
    val bgBoxColor: Long = 0x88000000,
    val bgBoxPaddingH: Float = 20f,
    val bgBoxPaddingV: Float = 10f,
    val bgBoxCornerRadius: Float = 12f,
    val curveBend: Float = 0f // -100f to +100f
) : CanvasElement() {
    override fun copyWithCommon(
        x: Float,
        y: Float,
        rotation: Float,
        scale: Float,
        opacity: Float,
        isLocked: Boolean,
        isVisible: Boolean
    ): CanvasElement = copy(
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        opacity = opacity,
        isLocked = isLocked,
        isVisible = isVisible
    )
}

data class StickerElement(
    override val id: String = UUID.randomUUID().toString(),
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val opacity: Float = 1f,
    override val isLocked: Boolean = false,
    override val isVisible: Boolean = true,
    val assetPath: String,
    val category: String = "Sticker",
    val width: Float = 180f,
    val height: Float = 180f
) : CanvasElement() {
    override fun copyWithCommon(
        x: Float,
        y: Float,
        rotation: Float,
        scale: Float,
        opacity: Float,
        isLocked: Boolean,
        isVisible: Boolean
    ): CanvasElement = copy(
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        opacity = opacity,
        isLocked = isLocked,
        isVisible = isVisible
    )
}

data class ShapeElement(
    override val id: String = UUID.randomUUID().toString(),
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val opacity: Float = 1f,
    override val isLocked: Boolean = false,
    override val isVisible: Boolean = true,
    val shapeType: ShapeType = ShapeType.ROUNDED_RECT,
    val width: Float = 220f,
    val height: Float = 220f,
    val fillColor: Long = 0xFF00B4D8,
    val strokeColor: Long = 0xFFFFFFFF,
    val strokeWidth: Float = 0f,
    val cornerRadius: Float = 24f
) : CanvasElement() {
    override fun copyWithCommon(
        x: Float,
        y: Float,
        rotation: Float,
        scale: Float,
        opacity: Float,
        isLocked: Boolean,
        isVisible: Boolean
    ): CanvasElement = copy(
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        opacity = opacity,
        isLocked = isLocked,
        isVisible = isVisible
    )
}

data class ImageElement(
    override val id: String = UUID.randomUUID().toString(),
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val opacity: Float = 1f,
    override val isLocked: Boolean = false,
    override val isVisible: Boolean = true,
    val uriString: String? = null,
    val assetPath: String? = null,
    val width: Float = 260f,
    val height: Float = 260f
) : CanvasElement() {
    override fun copyWithCommon(
        x: Float,
        y: Float,
        rotation: Float,
        scale: Float,
        opacity: Float,
        isLocked: Boolean,
        isVisible: Boolean
    ): CanvasElement = copy(
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        opacity = opacity,
        isLocked = isLocked,
        isVisible = isVisible
    )
}

data class CanvasProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "PixelLab Project",
    val aspectRatio: CanvasAspectRatio = CanvasAspectRatio.SQUARE,
    val backgroundType: BackgroundType = BackgroundType.SOLID,
    val bgColor: Long = 0xFF1E2E3E,
    val bgGradientStart: Long = 0xFF0077B6,
    val bgGradientEnd: Long = 0xFF03045E,
    val bgAssetPath: String? = null,
    val bgImageUri: String? = null,
    val bgVignette: Float = 0f, // 0..1
    val bgVignetteColor: Long = 0xFF000000,
    val bgStripes: Boolean = false,
    val bgBrightness: Float = 0f, // -1..1
    val bgContrast: Float = 0f, // -1..1
    val bgSaturation: Float = 0f, // -1..1
    val bgNoise: Float = 0f, // 0..1
    val bgGlitch: Float = 0f, // 0..1
    val bgHue: Float = 0f, // -180..180
    val bgSepia: Float = 0f, // 0..1
    val bgInvert: Boolean = false,
    val bgBorderWidth: Float = 0f, // 0..40
    val bgBorderColor: Long = 0xFFFFFFFF,
    val elements: List<CanvasElement> = listOf(
        TextElement(
            text = "PixelLab",
            fontSize = 58f,
            textColor = 0xFFFFFFFF,
            shadowEnabled = true,
            shadowColor = 0xAA000000,
            shadowRadius = 14f,
            isBold = true
        )
    ),
    val selectedElementId: String? = elements.firstOrNull()?.id
)

data class FontInfo(
    val assetPath: String,
    val name: String,
    val category: String
)

data class BackgroundAssetInfo(
    val folder: String,
    val imagePath: String,
    val thumbPath: String,
    val title: String
)

data class StickerAssetInfo(
    val assetPath: String,
    val category: String,
    val name: String
)

data class QuoteCategoryInfo(
    val id: String,
    val title: String,
    val language: String,
    val quotes: List<String>
)
