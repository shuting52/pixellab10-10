package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BorderAll
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundType
import com.example.model.CanvasAspectRatio
import com.example.model.CanvasElement
import com.example.model.CanvasProject
import com.example.model.ImageElement
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.StickerElement
import com.example.model.TextElement
import com.example.ui.theme.ThemeManager
import com.example.util.appString
import com.example.viewmodel.ColorPickerTarget
import com.example.viewmodel.NavigationTab

@Composable
fun BottomToolbar(
    currentTab: NavigationTab,
    selectedElement: CanvasElement?,
    project: CanvasProject,
    onTabSelected: (NavigationTab) -> Unit,
    onOpenTextEditor: () -> Unit,
    onOpenFontPicker: () -> Unit,
    onOpenQuotes: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenShapes: () -> Unit,
    onOpenBgAssets: () -> Unit,
    onPickImageFromGallery: () -> Unit,
    onOpenColorPicker: (ColorPickerTarget) -> Unit,
    onUpdateText: ((TextElement) -> TextElement) -> Unit,
    onUpdateShape: ((ShapeElement) -> ShapeElement) -> Unit,
    onDeleteElement: () -> Unit,
    onBringToFront: () -> Unit,
    onSendToBack: () -> Unit,
    onSetBackgroundType: (BackgroundType) -> Unit,
    onSetAspectRatio: (CanvasAspectRatio) -> Unit,
    onSetVignette: (Float) -> Unit,
    onToggleStripes: () -> Unit,
    onSetBrightness: (Float) -> Unit,
    onSetContrast: (Float) -> Unit,
    onSetSaturation: (Float) -> Unit,
    onLoadPreset: (CanvasProject) -> Unit,
    presetTemplates: List<CanvasProject>,
    modifier: Modifier = Modifier,
    onDuplicateElement: (() -> Unit)? = null,
    onAddText: (() -> Unit)? = null,
    onAddNeonText: (() -> Unit)? = null,
    onAdd3dText: (() -> Unit)? = null,
    onAddBadgeText: (() -> Unit)? = null,
    onAddShape: ((ShapeType) -> Unit)? = null,
    onResetCanvas: (() -> Unit)? = null,
    onToggleGrid: (() -> Unit)? = null,
    isGridVisible: Boolean = false,
    onResetZoom: (() -> Unit)? = null,
    onSetBackgroundColor: ((Long) -> Unit)? = null,
    onUpdateElement: (((CanvasElement) -> CanvasElement) -> Unit)? = null,
    onOpenPresetsDialog: (() -> Unit)? = null,
    onCenterElementH: (() -> Unit)? = null,
    onCenterElementV: (() -> Unit)? = null,
    onRotateElementBy: ((Float) -> Unit)? = null,
    onToggleLockElement: (() -> Unit)? = null,
    onToggleVisibilityElement: (() -> Unit)? = null,
    onAddWatermarkStamp: (() -> Unit)? = null,
    onSetNoise: ((Float) -> Unit)? = null,
    onSetGlitch: ((Float) -> Unit)? = null,
    onSetHue: ((Float) -> Unit)? = null,
    onSetSepia: ((Float) -> Unit)? = null,
    onToggleInvert: (() -> Unit)? = null,
    onSetBorderWidth: ((Float) -> Unit)? = null,
    onSetBorderColor: ((Long) -> Unit)? = null,
    onSetVignetteColor: ((Long) -> Unit)? = null
) {
    val theme = ThemeManager.currentTheme

    // Outer Container with sharp Right Angles (直角矩形)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .liquidGlassRect(
                backgroundColor = theme.surfaceColor.copy(alpha = 0.94f),
                borderWidth = 1.2.dp,
                activeGlow = false
            )
    ) {
        // Upper Sub-tool actions shelf with dedicated controls for the current tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            when (currentTab) {
                NavigationTab.PRESETS -> {
                    PresetsSubShelf(
                        presets = presetTemplates,
                        onLoadPreset = onLoadPreset,
                        onOpenPresetsDialog = onOpenPresetsDialog,
                        onResetCanvas = onResetCanvas
                    )
                }

                NavigationTab.TEXT -> {
                    val textEl = selectedElement as? TextElement
                    if (textEl != null) {
                        TextToolsSubShelf(
                            element = textEl,
                            onOpenEditor = onOpenTextEditor,
                            onOpenFontPicker = onOpenFontPicker,
                            onOpenColorPicker = { onOpenColorPicker(ColorPickerTarget.TEXT_COLOR) },
                            onOpenStrokePicker = { onOpenColorPicker(ColorPickerTarget.TEXT_STROKE) },
                            onOpenShadowPicker = { onOpenColorPicker(ColorPickerTarget.TEXT_SHADOW) },
                            onOpenBgPicker = { onOpenColorPicker(ColorPickerTarget.TEXT_BG) },
                            onOpen3dPicker = { onOpenColorPicker(ColorPickerTarget.TEXT_3D) },
                            onUpdateText = onUpdateText,
                            onDuplicate = onDuplicateElement,
                            onDelete = onDeleteElement,
                            onBringToFront = onBringToFront,
                            onSendToBack = onSendToBack,
                            onCenterH = onCenterElementH,
                            onCenterV = onCenterElementV,
                            onRotate90 = { onRotateElementBy?.invoke(90f) },
                            onToggleLock = onToggleLockElement,
                            onToggleVisibility = onToggleVisibilityElement
                        )
                    } else {
                        NoTextSelectedShelf(
                            prompt = appString("no_element_hint"),
                            onAddText = onOpenTextEditor,
                            onOpenQuotes = onOpenQuotes,
                            onAddNeonText = onAddNeonText,
                            onAdd3dText = onAdd3dText,
                            onAddBadgeText = onAddBadgeText,
                            onAddWatermarkStamp = onAddWatermarkStamp
                        )
                    }
                }

                NavigationTab.OBJECTS -> {
                    when (selectedElement) {
                        is ShapeElement -> {
                            ShapeToolsSubShelf(
                                element = selectedElement,
                                onOpenFillColor = { onOpenColorPicker(ColorPickerTarget.SHAPE_FILL) },
                                onOpenStrokeColor = { onOpenColorPicker(ColorPickerTarget.SHAPE_STROKE) },
                                onUpdateShape = onUpdateShape,
                                onDuplicate = onDuplicateElement,
                                onDelete = onDeleteElement,
                                onBringToFront = onBringToFront,
                                onSendToBack = onSendToBack,
                                onCenterH = onCenterElementH,
                                onCenterV = onCenterElementV,
                                onRotate90 = { onRotateElementBy?.invoke(90f) },
                                onToggleLock = onToggleLockElement,
                                onToggleVisibility = onToggleVisibilityElement
                            )
                        }
                        is StickerElement, is ImageElement -> {
                            StickerImageToolsSubShelf(
                                element = selectedElement,
                                onUpdateElement = onUpdateElement,
                                onDuplicate = onDuplicateElement,
                                onDelete = onDeleteElement,
                                onBringToFront = onBringToFront,
                                onSendToBack = onSendToBack,
                                onCenterH = onCenterElementH,
                                onCenterV = onCenterElementV,
                                onRotate90 = { onRotateElementBy?.invoke(90f) },
                                onToggleLock = onToggleLockElement,
                                onToggleVisibility = onToggleVisibilityElement
                            )
                        }
                        else -> {
                            ElementsCreationSubShelf(
                                onOpenStickers = onOpenStickers,
                                onOpenShapes = onOpenShapes,
                                onPickGallery = onPickImageFromGallery,
                                onAddShape = onAddShape
                            )
                        }
                    }
                }

                NavigationTab.BACKGROUND -> {
                    BackgroundSubShelf(
                        project = project,
                        onSetBgType = onSetBackgroundType,
                        onOpenColorPicker = { onOpenColorPicker(ColorPickerTarget.CANVAS_BG) },
                        onOpenBgAssets = onOpenBgAssets,
                        onPickGallery = onPickImageFromGallery,
                        onSetAspectRatio = onSetAspectRatio,
                        onSetBackgroundColor = onSetBackgroundColor,
                        onToggleGrid = onToggleGrid,
                        isGridVisible = isGridVisible,
                        onResetZoom = onResetZoom
                    )
                }

                NavigationTab.EFFECTS -> {
                    EffectsSubShelf(
                        project = project,
                        onSetVignette = onSetVignette,
                        onToggleStripes = onToggleStripes,
                        onSetBrightness = onSetBrightness,
                        onSetContrast = onSetContrast,
                        onSetSaturation = onSetSaturation,
                        onSetNoise = onSetNoise,
                        onSetGlitch = onSetGlitch,
                        onSetHue = onSetHue,
                        onSetSepia = onSetSepia,
                        onToggleInvert = onToggleInvert,
                        onSetBorderWidth = onSetBorderWidth,
                        onSetBorderColor = onSetBorderColor,
                        onSetVignetteColor = onSetVignetteColor
                    )
                }
            }
        }

        // Bottom Navigation Bar with Sharp Right-Angle Tabs (直角Tab)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Pair(NavigationTab.PRESETS, Icons.Default.Layers),
                Pair(NavigationTab.TEXT, Icons.Default.TextFields),
                Pair(NavigationTab.OBJECTS, Icons.Default.Star),
                Pair(NavigationTab.BACKGROUND, Icons.Default.Crop),
                Pair(NavigationTab.EFFECTS, Icons.Default.AutoAwesome)
            )

            tabs.forEach { (tab, icon) ->
                val isSelected = currentTab == tab
                val titleString = when (tab) {
                    NavigationTab.PRESETS -> appString("tab_presets")
                    NavigationTab.TEXT -> appString("tab_text")
                    NavigationTab.OBJECTS -> appString("tab_elements")
                    NavigationTab.BACKGROUND -> appString("tab_canvas")
                    NavigationTab.EFFECTS -> appString("tab_effects")
                }

                // Sharp Right-Angle Tab Button (直角直边矩形Tab)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clip(RectangleShape)
                        .background(
                            color = if (isSelected) theme.primaryColor.copy(alpha = 0.25f) else Color(0x10141E28),
                            shape = RectangleShape
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) theme.primaryColor else Color(0x1EFFFFFF),
                            shape = RectangleShape
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 5.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top Right-Angle Indicator Bar when Selected (直角顶部高亮条)
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .background(theme.primaryColor, RectangleShape)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = titleString,
                            tint = if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.70f),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = titleString,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.70f)
                        )
                    }
                }
            }
        }
    }
}

// ---------------- 1. PRESETS SUB-SHELF (预设专属附属功能) ----------------

@Composable
private fun PresetsSubShelf(
    presets: List<CanvasProject>,
    onLoadPreset: (CanvasProject) -> Unit,
    onOpenPresetsDialog: (() -> Unit)?,
    onResetCanvas: (() -> Unit)?
) {
    val theme = ThemeManager.currentTheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quick Random Style Action (灵感随机)
        LiquidToolButton(
            icon = Icons.Default.AutoAwesome,
            label = appString("action_random_preset"),
            tint = theme.accentColor,
            onClick = {
                if (presets.isNotEmpty()) {
                    val randomPreset = presets.random()
                    onLoadPreset(randomPreset)
                }
            }
        )

        // Open All Templates Dialog (更多模板)
        if (onOpenPresetsDialog != null) {
            LiquidToolButton(
                icon = Icons.Default.PhotoLibrary,
                label = appString("action_more_presets"),
                onClick = onOpenPresetsDialog
            )
        }

        // Reset Canvas Action (清空重置)
        if (onResetCanvas != null) {
            LiquidToolButton(
                icon = Icons.Default.Refresh,
                label = appString("action_clear_canvas"),
                tint = Color(0xFFFFB703),
                onClick = onResetCanvas
            )
        }

        // Carousel of individual preset cards with visual preview thumbnails (截图3: 带预览图效果的预设)
        presets.forEach { preset ->
            PresetPreviewThumbnailCard(
                preset = preset,
                onClick = { onLoadPreset(preset) }
            )
        }
    }
}

@Composable
private fun PresetPreviewThumbnailCard(
    preset: CanvasProject,
    onClick: () -> Unit
) {
    val theme = ThemeManager.currentTheme
    val firstText = preset.elements.filterIsInstance<TextElement>().firstOrNull()

    Box(
        modifier = Modifier
            .width(96.dp)
            .height(58.dp)
            .clip(RectangleShape)
            .border(1.dp, theme.primaryColor.copy(alpha = 0.55f), RectangleShape)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Visual Preview Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        when (preset.backgroundType) {
                            BackgroundType.GRADIENT -> Brush.verticalGradient(
                                listOf(Color(preset.bgGradientStart), Color(preset.bgGradientEnd))
                            )
                            BackgroundType.SOLID -> Brush.linearGradient(
                                listOf(Color(preset.bgColor), Color(preset.bgColor))
                            )
                            else -> Brush.verticalGradient(
                                listOf(Color(0xFF233142), Color(0xFF0F172A))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (firstText != null) {
                    Text(
                        text = firstText.text.take(8),
                        fontSize = 11.sp,
                        fontWeight = if (firstText.isBold) FontWeight.Black else FontWeight.Bold,
                        color = Color(firstText.textColor),
                        maxLines = 1
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = theme.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Bottom Name Label Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xF0081220))
                    .padding(vertical = 2.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

// ---------------- 2. TEXT SUB-SHELF (文本专属附属功能) ----------------

@Composable
private fun TextToolsSubShelf(
    element: TextElement,
    onOpenEditor: () -> Unit,
    onOpenFontPicker: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onOpenStrokePicker: () -> Unit,
    onOpenShadowPicker: () -> Unit,
    onOpenBgPicker: () -> Unit,
    onOpen3dPicker: () -> Unit,
    onUpdateText: ((TextElement) -> TextElement) -> Unit,
    onDuplicate: (() -> Unit)?,
    onDelete: () -> Unit,
    onBringToFront: () -> Unit,
    onSendToBack: () -> Unit,
    onCenterH: (() -> Unit)? = null,
    onCenterV: (() -> Unit)? = null,
    onRotate90: (() -> Unit)? = null,
    onToggleLock: (() -> Unit)? = null,
    onToggleVisibility: (() -> Unit)? = null
) {
    val theme = ThemeManager.currentTheme
    var activeSlider by remember { mutableStateOf<String?>(null) } // "size", "spacing", "curve", "opacity", "stroke_w"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit Text
            LiquidToolButton(
                icon = Icons.Default.Edit,
                label = appString("action_edit"),
                onClick = onOpenEditor
            )

            // Font Size
            LiquidToolButton(
                icon = Icons.Default.FormatSize,
                label = "${element.fontSize.toInt()}sp",
                isSelected = activeSlider == "size",
                onClick = { activeSlider = if (activeSlider == "size") null else "size" }
            )

            // Color
            LiquidToolButton(
                icon = Icons.Default.ColorLens,
                label = appString("action_color"),
                indicatorColor = Color(element.textColor),
                onClick = onOpenColorPicker
            )

            // Font
            LiquidToolButton(
                icon = Icons.Default.FontDownload,
                label = appString("action_font"),
                onClick = onOpenFontPicker
            )

            // Bold/Italic/Underline
            LiquidToolButton(
                icon = Icons.Default.FormatBold,
                label = appString("action_style"),
                isSelected = element.isBold || element.isItalic || element.isUnderline,
                onClick = {
                    onUpdateText { it.copy(isBold = !it.isBold) }
                }
            )

            // Align
            LiquidToolButton(
                icon = when (element.alignment) {
                    0 -> Icons.Default.FormatAlignLeft
                    1 -> Icons.Default.FormatAlignCenter
                    else -> Icons.Default.FormatAlignRight
                },
                label = appString("action_align"),
                onClick = {
                    val nextAlign = when (element.alignment) {
                        0 -> 1
                        1 -> 2
                        else -> 0
                    }
                    onUpdateText { it.copy(alignment = nextAlign) }
                }
            )

            // Center Horizontally
            if (onCenterH != null) {
                LiquidToolButton(
                    icon = Icons.Default.CenterFocusStrong,
                    label = appString("action_center_h"),
                    onClick = onCenterH
                )
            }

            // Center Vertically
            if (onCenterV != null) {
                LiquidToolButton(
                    icon = Icons.Default.Adjust,
                    label = appString("action_center_v"),
                    onClick = onCenterV
                )
            }

            // Quick Rotate 90
            if (onRotate90 != null) {
                LiquidToolButton(
                    icon = Icons.Default.RotateRight,
                    label = "+90°",
                    onClick = onRotate90
                )
            }

            // Stroke / Outline
            LiquidToolButton(
                icon = Icons.Default.RadioButtonUnchecked,
                label = appString("action_stroke"),
                isSelected = element.strokeEnabled,
                onClick = {
                    if (!element.strokeEnabled) {
                        onUpdateText { it.copy(strokeEnabled = true) }
                    }
                    onOpenStrokePicker()
                }
            )

            // Stroke Width
            LiquidToolButton(
                icon = Icons.Default.FormatColorFill,
                label = appString("action_outline_width"),
                isSelected = activeSlider == "stroke_w",
                onClick = { activeSlider = if (activeSlider == "stroke_w") null else "stroke_w" }
            )

            // Shadow
            LiquidToolButton(
                icon = Icons.Default.Opacity,
                label = appString("action_shadow"),
                isSelected = element.shadowEnabled,
                onClick = {
                    if (!element.shadowEnabled) {
                        onUpdateText { it.copy(shadowEnabled = true) }
                    }
                    onOpenShadowPicker()
                }
            )

            // Background Box
            LiquidToolButton(
                icon = Icons.Default.CropSquare,
                label = appString("action_bg"),
                isSelected = element.bgBoxEnabled,
                onClick = {
                    if (!element.bgBoxEnabled) {
                        onUpdateText { it.copy(bgBoxEnabled = true) }
                    }
                    onOpenBgPicker()
                }
            )

            // 3D
            LiquidToolButton(
                icon = Icons.Default.ViewInAr,
                label = appString("action_3d"),
                isSelected = element.threeDEnabled,
                onClick = {
                    onUpdateText { it.copy(threeDEnabled = !it.threeDEnabled) }
                    if (!element.threeDEnabled) onOpen3dPicker()
                }
            )

            // Reflection
            LiquidToolButton(
                icon = Icons.Default.Wallpaper,
                label = appString("action_reflection"),
                isSelected = element.reflectionEnabled,
                onClick = {
                    onUpdateText { it.copy(reflectionEnabled = !it.reflectionEnabled) }
                }
            )

            // Spacing
            LiquidToolButton(
                icon = Icons.Default.SpaceBar,
                label = appString("action_spacing"),
                isSelected = activeSlider == "spacing",
                onClick = { activeSlider = if (activeSlider == "spacing") null else "spacing" }
            )

            // Curve Bend
            LiquidToolButton(
                icon = Icons.Default.RoundedCorner,
                label = appString("action_curve"),
                isSelected = activeSlider == "curve",
                onClick = { activeSlider = if (activeSlider == "curve") null else "curve" }
            )

            // Opacity
            LiquidToolButton(
                icon = Icons.Default.Opacity,
                label = "${(element.opacity * 100).toInt()}%",
                isSelected = activeSlider == "opacity",
                onClick = { activeSlider = if (activeSlider == "opacity") null else "opacity" }
            )

            // Lock / Unlock Layer
            if (onToggleLock != null) {
                LiquidToolButton(
                    icon = if (element.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    label = if (element.isLocked) appString("action_unlock") else appString("action_lock"),
                    isSelected = element.isLocked,
                    onClick = onToggleLock
                )
            }

            // Hide / Show Layer
            if (onToggleVisibility != null) {
                LiquidToolButton(
                    icon = if (element.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = appString("action_hide"),
                    onClick = onToggleVisibility
                )
            }

            // Duplicate
            if (onDuplicate != null) {
                LiquidToolButton(
                    icon = Icons.Default.Layers,
                    label = appString("action_duplicate"),
                    onClick = onDuplicate
                )
            }

            // Bring to Front
            LiquidToolButton(
                icon = Icons.Default.FlipToFront,
                label = appString("action_to_front"),
                onClick = onBringToFront
            )

            // Send to Back
            LiquidToolButton(
                icon = Icons.Default.FlipToBack,
                label = appString("action_to_back"),
                onClick = onSendToBack
            )

            // Delete
            LiquidToolButton(
                icon = Icons.Default.Delete,
                label = appString("action_delete"),
                tint = Color(0xFFFF5252),
                onClick = onDelete
            )
        }

        // Dedicated Interactive Adjuster Slider
        when (activeSlider) {
            "size" -> {
                InteractiveSliderRow(
                    label = "${appString("action_size")}: ${element.fontSize.toInt()}sp",
                    value = element.fontSize,
                    valueRange = 12f..130f,
                    onValueChange = { newSize -> onUpdateText { it.copy(fontSize = newSize) } }
                )
            }
            "spacing" -> {
                InteractiveSliderRow(
                    label = "${appString("action_spacing")}: ${element.letterSpacing.toInt()}",
                    value = element.letterSpacing,
                    valueRange = -5f..40f,
                    onValueChange = { newSpacing -> onUpdateText { it.copy(letterSpacing = newSpacing) } }
                )
            }
            "curve" -> {
                InteractiveSliderRow(
                    label = "${appString("action_curve")}: ${element.curveBend.toInt()}",
                    value = element.curveBend,
                    valueRange = -80f..80f,
                    onValueChange = { newCurve -> onUpdateText { it.copy(curveBend = newCurve) } }
                )
            }
            "opacity" -> {
                InteractiveSliderRow(
                    label = "${appString("action_opacity")}: ${(element.opacity * 100).toInt()}%",
                    value = element.opacity,
                    valueRange = 0.1f..1.0f,
                    onValueChange = { newOp -> onUpdateText { it.copy(opacity = newOp) } }
                )
            }
            "stroke_w" -> {
                InteractiveSliderRow(
                    label = "${appString("action_outline_width")}: ${element.strokeWidth.toInt()}px",
                    value = element.strokeWidth,
                    valueRange = 1f..30f,
                    onValueChange = { newWidth -> onUpdateText { it.copy(strokeWidth = newWidth, strokeEnabled = true) } }
                )
            }
        }
    }
}

@Composable
private fun NoTextSelectedShelf(
    prompt: String,
    onAddText: () -> Unit,
    onOpenQuotes: () -> Unit,
    onAddNeonText: (() -> Unit)?,
    onAdd3dText: (() -> Unit)?,
    onAddBadgeText: (() -> Unit)?,
    onAddWatermarkStamp: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quick standard text
        LiquidToolButton(
            icon = Icons.Default.TextFields,
            label = appString("action_quick_text"),
            onClick = onAddText
        )

        // Quotes collection
        LiquidToolButton(
            icon = Icons.Default.FormatQuote,
            label = appString("menu_quotes"),
            onClick = onOpenQuotes
        )

        // Neon Glow Text preset
        if (onAddNeonText != null) {
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("action_neon_text"),
                tint = Color(0xFF00F5D4),
                onClick = onAddNeonText
            )
        }

        // 3D Title preset
        if (onAdd3dText != null) {
            LiquidToolButton(
                icon = Icons.Default.ViewInAr,
                label = appString("action_3d_text"),
                tint = Color(0xFFFFBE0B),
                onClick = onAdd3dText
            )
        }

        // Badge Text preset
        if (onAddBadgeText != null) {
            LiquidToolButton(
                icon = Icons.Default.CropSquare,
                label = appString("action_badge_text"),
                tint = Color(0xFFB5179E),
                onClick = onAddBadgeText
            )
        }

        // Watermark Stamp preset
        if (onAddWatermarkStamp != null) {
            LiquidToolButton(
                icon = Icons.Default.Star,
                label = appString("action_quick_stamp"),
                tint = Color(0xFFFFD166),
                onClick = onAddWatermarkStamp
            )
        }
    }
}

// ---------------- 3. OBJECTS SUB-SHELF (元素专属附属功能) ----------------

@Composable
private fun ElementsCreationSubShelf(
    onOpenStickers: () -> Unit,
    onOpenShapes: () -> Unit,
    onPickGallery: () -> Unit,
    onAddShape: ((ShapeType) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Open Stickers Sheet
        LiquidToolButton(
            icon = Icons.Default.Image,
            label = appString("action_quick_sticker"),
            onClick = onOpenStickers
        )

        // Open Shapes Dialog
        LiquidToolButton(
            icon = Icons.Default.Star,
            label = appString("action_quick_shape"),
            onClick = onOpenShapes
        )

        // Pick Image From Gallery
        LiquidToolButton(
            icon = Icons.Default.PhotoLibrary,
            label = appString("action_quick_image"),
            onClick = onPickGallery
        )

        // Quick geometric shape insertion chips
        if (onAddShape != null) {
            LiquidToolButton(
                icon = Icons.Default.Star,
                label = "Star",
                onClick = { onAddShape(ShapeType.STAR) }
            )
            LiquidToolButton(
                icon = Icons.Default.Favorite,
                label = "Heart",
                onClick = { onAddShape(ShapeType.HEART) }
            )
            LiquidToolButton(
                icon = Icons.Default.RadioButtonUnchecked,
                label = "Circle",
                onClick = { onAddShape(ShapeType.CIRCLE) }
            )
            LiquidToolButton(
                icon = Icons.Default.CropSquare,
                label = "Rect",
                onClick = { onAddShape(ShapeType.ROUNDED_RECT) }
            )
            LiquidToolButton(
                icon = Icons.Default.NorthEast,
                label = "Arrow",
                onClick = { onAddShape(ShapeType.ARROW) }
            )
        }
    }
}

@Composable
private fun ShapeToolsSubShelf(
    element: ShapeElement,
    onOpenFillColor: () -> Unit,
    onOpenStrokeColor: () -> Unit,
    onUpdateShape: ((ShapeElement) -> ShapeElement) -> Unit,
    onDuplicate: (() -> Unit)?,
    onDelete: () -> Unit,
    onBringToFront: () -> Unit,
    onSendToBack: () -> Unit,
    onCenterH: (() -> Unit)? = null,
    onCenterV: (() -> Unit)? = null,
    onRotate90: (() -> Unit)? = null,
    onToggleLock: (() -> Unit)? = null,
    onToggleVisibility: (() -> Unit)? = null
) {
    var activeSlider by remember { mutableStateOf<String?>(null) } // "stroke_w", "radius", "opacity"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cycle Shape Type
            LiquidToolButton(
                icon = when (element.shapeType) {
                    ShapeType.STAR -> Icons.Default.Star
                    ShapeType.HEART -> Icons.Default.Favorite
                    ShapeType.CIRCLE -> Icons.Default.RadioButtonUnchecked
                    ShapeType.TRIANGLE -> Icons.Default.ChangeHistory
                    ShapeType.ARROW -> Icons.Default.NorthEast
                    else -> Icons.Default.CropSquare
                },
                label = appString("action_shape_type"),
                onClick = {
                    val nextType = when (element.shapeType) {
                        ShapeType.RECTANGLE -> ShapeType.ROUNDED_RECT
                        ShapeType.ROUNDED_RECT -> ShapeType.CIRCLE
                        ShapeType.CIRCLE -> ShapeType.STAR
                        ShapeType.STAR -> ShapeType.HEART
                        ShapeType.HEART -> ShapeType.TRIANGLE
                        ShapeType.TRIANGLE -> ShapeType.HEXAGON
                        ShapeType.HEXAGON -> ShapeType.ARROW
                        ShapeType.ARROW -> ShapeType.RECTANGLE
                    }
                    onUpdateShape { it.copy(shapeType = nextType) }
                }
            )

            // Fill Color
            LiquidToolButton(
                icon = Icons.Default.FormatColorFill,
                label = appString("tool_fill"),
                indicatorColor = Color(element.fillColor),
                onClick = onOpenFillColor
            )

            // Stroke Color
            LiquidToolButton(
                icon = Icons.Default.RadioButtonUnchecked,
                label = appString("tool_outline"),
                indicatorColor = Color(element.strokeColor),
                onClick = onOpenStrokeColor
            )

            // Stroke Width
            LiquidToolButton(
                icon = Icons.Default.Edit,
                label = appString("action_outline_width"),
                isSelected = activeSlider == "stroke_w",
                onClick = { activeSlider = if (activeSlider == "stroke_w") null else "stroke_w" }
            )

            // Corner Radius (if rounded rect)
            if (element.shapeType == ShapeType.ROUNDED_RECT || element.shapeType == ShapeType.RECTANGLE) {
                LiquidToolButton(
                    icon = Icons.Default.RoundedCorner,
                    label = appString("action_corner_radius"),
                    isSelected = activeSlider == "radius",
                    onClick = { activeSlider = if (activeSlider == "radius") null else "radius" }
                )
            }

            // Opacity
            LiquidToolButton(
                icon = Icons.Default.Opacity,
                label = "${(element.opacity * 100).toInt()}%",
                isSelected = activeSlider == "opacity",
                onClick = { activeSlider = if (activeSlider == "opacity") null else "opacity" }
            )

            // Center Horizontally
            if (onCenterH != null) {
                LiquidToolButton(
                    icon = Icons.Default.CenterFocusStrong,
                    label = appString("action_center_h"),
                    onClick = onCenterH
                )
            }

            // Center Vertically
            if (onCenterV != null) {
                LiquidToolButton(
                    icon = Icons.Default.Adjust,
                    label = appString("action_center_v"),
                    onClick = onCenterV
                )
            }

            // Rotate 90
            if (onRotate90 != null) {
                LiquidToolButton(
                    icon = Icons.Default.RotateRight,
                    label = "+90°",
                    onClick = onRotate90
                )
            }

            // Lock / Unlock
            if (onToggleLock != null) {
                LiquidToolButton(
                    icon = if (element.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    label = if (element.isLocked) appString("action_unlock") else appString("action_lock"),
                    isSelected = element.isLocked,
                    onClick = onToggleLock
                )
            }

            // Hide / Show
            if (onToggleVisibility != null) {
                LiquidToolButton(
                    icon = if (element.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = appString("action_hide"),
                    onClick = onToggleVisibility
                )
            }

            // Duplicate
            if (onDuplicate != null) {
                LiquidToolButton(
                    icon = Icons.Default.Layers,
                    label = appString("action_duplicate"),
                    onClick = onDuplicate
                )
            }

            // Bring to Front
            LiquidToolButton(
                icon = Icons.Default.FlipToFront,
                label = appString("action_to_front"),
                onClick = onBringToFront
            )

            // Send to Back
            LiquidToolButton(
                icon = Icons.Default.FlipToBack,
                label = appString("action_to_back"),
                onClick = onSendToBack
            )

            // Delete
            LiquidToolButton(
                icon = Icons.Default.Delete,
                label = appString("action_delete"),
                tint = Color(0xFFFF5252),
                onClick = onDelete
            )
        }

        // Active Slider
        when (activeSlider) {
            "stroke_w" -> {
                InteractiveSliderRow(
                    label = "${appString("action_outline_width")}: ${element.strokeWidth.toInt()}px",
                    value = element.strokeWidth,
                    valueRange = 0f..30f,
                    onValueChange = { w -> onUpdateShape { it.copy(strokeWidth = w) } }
                )
            }
            "radius" -> {
                InteractiveSliderRow(
                    label = "${appString("action_corner_radius")}: ${element.cornerRadius.toInt()}px",
                    value = element.cornerRadius,
                    valueRange = 0f..60f,
                    onValueChange = { r -> onUpdateShape { it.copy(cornerRadius = r) } }
                )
            }
            "opacity" -> {
                InteractiveSliderRow(
                    label = "${appString("action_opacity")}: ${(element.opacity * 100).toInt()}%",
                    value = element.opacity,
                    valueRange = 0.1f..1.0f,
                    onValueChange = { op -> onUpdateShape { it.copy(opacity = op) } }
                )
            }
        }
    }
}

@Composable
private fun StickerImageToolsSubShelf(
    element: CanvasElement,
    onUpdateElement: (((CanvasElement) -> CanvasElement) -> Unit)?,
    onDuplicate: (() -> Unit)?,
    onDelete: () -> Unit,
    onBringToFront: () -> Unit,
    onSendToBack: () -> Unit,
    onCenterH: (() -> Unit)? = null,
    onCenterV: (() -> Unit)? = null,
    onRotate90: (() -> Unit)? = null,
    onToggleLock: (() -> Unit)? = null,
    onToggleVisibility: (() -> Unit)? = null
) {
    var activeSlider by remember { mutableStateOf<String?>(null) } // "opacity", "scale"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Opacity
            LiquidToolButton(
                icon = Icons.Default.Opacity,
                label = "${(element.opacity * 100).toInt()}%",
                isSelected = activeSlider == "opacity",
                onClick = { activeSlider = if (activeSlider == "opacity") null else "opacity" }
            )

            // Scale
            LiquidToolButton(
                icon = Icons.Default.FormatSize,
                label = "${(element.scale * 100).toInt()}%",
                isSelected = activeSlider == "scale",
                onClick = { activeSlider = if (activeSlider == "scale") null else "scale" }
            )

            // Quick Rotate 45 deg
            LiquidToolButton(
                icon = Icons.Default.RotateRight,
                label = "+45°",
                onClick = {
                    onUpdateElement?.invoke { el -> el.copyWithCommon(rotation = (el.rotation + 45f) % 360f) }
                }
            )

            // Center Horizontally
            if (onCenterH != null) {
                LiquidToolButton(
                    icon = Icons.Default.CenterFocusStrong,
                    label = appString("action_center_h"),
                    onClick = onCenterH
                )
            }

            // Center Vertically
            if (onCenterV != null) {
                LiquidToolButton(
                    icon = Icons.Default.Adjust,
                    label = appString("action_center_v"),
                    onClick = onCenterV
                )
            }

            // Quick Rotate 90 deg
            if (onRotate90 != null) {
                LiquidToolButton(
                    icon = Icons.Default.RotateRight,
                    label = "+90°",
                    onClick = onRotate90
                )
            }

            // Lock / Unlock
            if (onToggleLock != null) {
                LiquidToolButton(
                    icon = if (element.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    label = if (element.isLocked) appString("action_unlock") else appString("action_lock"),
                    isSelected = element.isLocked,
                    onClick = onToggleLock
                )
            }

            // Hide / Show
            if (onToggleVisibility != null) {
                LiquidToolButton(
                    icon = if (element.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = appString("action_hide"),
                    onClick = onToggleVisibility
                )
            }

            // Duplicate
            if (onDuplicate != null) {
                LiquidToolButton(
                    icon = Icons.Default.Layers,
                    label = appString("action_duplicate"),
                    onClick = onDuplicate
                )
            }

            // Bring to Front
            LiquidToolButton(
                icon = Icons.Default.FlipToFront,
                label = appString("action_to_front"),
                onClick = onBringToFront
            )

            // Send to Back
            LiquidToolButton(
                icon = Icons.Default.FlipToBack,
                label = appString("action_to_back"),
                onClick = onSendToBack
            )

            // Delete
            LiquidToolButton(
                icon = Icons.Default.Delete,
                label = appString("action_delete"),
                tint = Color(0xFFFF5252),
                onClick = onDelete
            )
        }

        // Active Slider
        when (activeSlider) {
            "opacity" -> {
                InteractiveSliderRow(
                    label = "${appString("action_opacity")}: ${(element.opacity * 100).toInt()}%",
                    value = element.opacity,
                    valueRange = 0.1f..1.0f,
                    onValueChange = { op -> onUpdateElement?.invoke { it.copyWithCommon(opacity = op) } }
                )
            }
            "scale" -> {
                InteractiveSliderRow(
                    label = "Scale: ${(element.scale * 100).toInt()}%",
                    value = element.scale,
                    valueRange = 0.3f..2.5f,
                    onValueChange = { sc -> onUpdateElement?.invoke { it.copyWithCommon(scale = sc) } }
                )
            }
        }
    }
}

// ---------------- 4. BACKGROUND SUB-SHELF (画布专属附属功能) ----------------

@Composable
private fun BackgroundSubShelf(
    project: CanvasProject,
    onSetBgType: (BackgroundType) -> Unit,
    onOpenColorPicker: () -> Unit,
    onOpenBgAssets: () -> Unit,
    onPickGallery: () -> Unit,
    onSetAspectRatio: (CanvasAspectRatio) -> Unit,
    onSetBackgroundColor: ((Long) -> Unit)?,
    onToggleGrid: (() -> Unit)?,
    isGridVisible: Boolean,
    onResetZoom: (() -> Unit)?
) {
    val quickColors = listOf(
        0xFF1A1A24, 0xFFFFFFFF, 0xFF0B132B, 0xFF0077B6,
        0xFF7209B7, 0xFFD90429, 0xFF06D6A0, 0xFFFFD166
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color Background
        LiquidToolButton(
            icon = Icons.Default.ColorLens,
            label = appString("tool_color"),
            isSelected = project.backgroundType == BackgroundType.SOLID,
            onClick = {
                onSetBgType(BackgroundType.SOLID)
                onOpenColorPicker()
            }
        )

        // Gradient Background
        LiquidToolButton(
            icon = Icons.Default.Gradient,
            label = appString("tool_gradient"),
            isSelected = project.backgroundType == BackgroundType.GRADIENT,
            onClick = {
                onSetBgType(BackgroundType.GRADIENT)
                onOpenColorPicker()
            }
        )

        // Transparent PNG Background
        LiquidToolButton(
            icon = Icons.Default.GridOn,
            label = appString("action_transparent"),
            isSelected = project.backgroundType == BackgroundType.TRANSPARENT,
            onClick = { onSetBgType(BackgroundType.TRANSPARENT) }
        )

        // Wallpapers Texture Assets
        LiquidToolButton(
            icon = Icons.Default.Wallpaper,
            label = appString("tool_assets"),
            isSelected = project.backgroundType == BackgroundType.ASSET,
            onClick = onOpenBgAssets
        )

        // Pick Image From Gallery
        LiquidToolButton(
            icon = Icons.Default.PhotoLibrary,
            label = appString("tool_gallery"),
            isSelected = project.backgroundType == BackgroundType.GALLERY,
            onClick = onPickGallery
        )

        // Aspect Ratio Selector
        LiquidToolButton(
            icon = Icons.Default.Crop,
            label = project.aspectRatio.label,
            onClick = {
                val nextRatio = when (project.aspectRatio) {
                    CanvasAspectRatio.SQUARE -> CanvasAspectRatio.STORY
                    CanvasAspectRatio.STORY -> CanvasAspectRatio.PORTRAIT
                    CanvasAspectRatio.PORTRAIT -> CanvasAspectRatio.LANDSCAPE
                    CanvasAspectRatio.LANDSCAPE -> CanvasAspectRatio.STANDARD
                    CanvasAspectRatio.STANDARD -> CanvasAspectRatio.BANNER
                    CanvasAspectRatio.BANNER -> CanvasAspectRatio.SQUARE
                }
                onSetAspectRatio(nextRatio)
            }
        )

        // Toggle Grid Alignment Guidelines
        if (onToggleGrid != null) {
            LiquidToolButton(
                icon = Icons.Default.GridOn,
                label = appString("action_grid_guide"),
                isSelected = isGridVisible,
                onClick = onToggleGrid
            )
        }

        // Reset Zoom
        if (onResetZoom != null) {
            LiquidToolButton(
                icon = Icons.Default.ZoomOutMap,
                label = "100%",
                onClick = onResetZoom
            )
        }

        // Quick Color Swatches
        quickColors.forEach { col ->
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RectangleShape)
                    .background(Color(col), RectangleShape)
                    .border(
                        1.dp,
                        if (project.bgColor == col && project.backgroundType == BackgroundType.SOLID) Color.White else Color(0x33FFFFFF),
                        RectangleShape
                    )
                    .clickable {
                        onSetBgType(BackgroundType.SOLID)
                        onSetBackgroundColor?.invoke(col)
                    }
            )
        }
    }
}

// ---------------- 5. EFFECTS SUB-SHELF (特效专属附属功能) ----------------

@Composable
private fun EffectsSubShelf(
    project: CanvasProject,
    onSetVignette: (Float) -> Unit,
    onToggleStripes: () -> Unit,
    onSetBrightness: (Float) -> Unit,
    onSetContrast: (Float) -> Unit,
    onSetSaturation: (Float) -> Unit,
    onSetNoise: ((Float) -> Unit)? = null,
    onSetGlitch: ((Float) -> Unit)? = null,
    onSetHue: ((Float) -> Unit)? = null,
    onSetSepia: ((Float) -> Unit)? = null,
    onToggleInvert: (() -> Unit)? = null,
    onSetBorderWidth: ((Float) -> Unit)? = null,
    onSetBorderColor: ((Long) -> Unit)? = null,
    onSetVignetteColor: ((Long) -> Unit)? = null
) {
    var activeSlider by remember { mutableStateOf<String?>(null) } // "vignette", "noise", "glitch", "bright", "contrast", "sat", "hue", "sepia", "border"

    val vignetteColors = listOf(
        Pair("暗黑", 0xFF000000),
        Pair("纯白", 0xFFFFFFFF),
        Pair("冷紫", 0xFF7209B7),
        Pair("暖橙", 0xFFFB5607),
        Pair("深蓝", 0xFF001233)
    )

    val borderColors = listOf(
        Pair("雪白", 0xFFFFFFFF),
        Pair("黑曜", 0xFF000000),
        Pair("鎏金", 0xFFFFD166),
        Pair("霓虹青", 0xFF00F5D4),
        Pair("落樱粉", 0xFFFF006E),
        Pair("烈焰红", 0xFFD90429)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Lens Vignette
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = "${appString("action_vignette")}: ${(project.bgVignette * 100).toInt()}%",
                isSelected = project.bgVignette > 0f || activeSlider == "vignette",
                onClick = { activeSlider = if (activeSlider == "vignette") null else "vignette" }
            )

            // 2. Film Grain / Noise
            if (onSetNoise != null) {
                LiquidToolButton(
                    icon = Icons.Default.Grain,
                    label = "${appString("action_noise")}: ${(project.bgNoise * 100).toInt()}%",
                    isSelected = project.bgNoise > 0f || activeSlider == "noise",
                    onClick = { activeSlider = if (activeSlider == "noise") null else "noise" }
                )
            }

            // 3. RGB Glitch / Chromatic Aberration
            if (onSetGlitch != null) {
                LiquidToolButton(
                    icon = Icons.Default.Tune,
                    label = "${appString("action_glitch")}: ${(project.bgGlitch * 100).toInt()}%",
                    isSelected = project.bgGlitch > 0f || activeSlider == "glitch",
                    tint = Color(0xFF00F5D4),
                    onClick = { activeSlider = if (activeSlider == "glitch") null else "glitch" }
                )
            }

            // 4. Hue Rotation
            if (onSetHue != null) {
                LiquidToolButton(
                    icon = Icons.Default.ColorLens,
                    label = "${appString("action_hue")}: ${project.bgHue.toInt()}°",
                    isSelected = project.bgHue != 0f || activeSlider == "hue",
                    onClick = { activeSlider = if (activeSlider == "hue") null else "hue" }
                )
            }

            // 5. Vintage Sepia Tone
            if (onSetSepia != null) {
                LiquidToolButton(
                    icon = Icons.Default.Palette,
                    label = "${appString("action_sepia")}: ${(project.bgSepia * 100).toInt()}%",
                    isSelected = project.bgSepia > 0f || activeSlider == "sepia",
                    tint = Color(0xFFD4A373),
                    onClick = { activeSlider = if (activeSlider == "sepia") null else "sepia" }
                )
            }

            // 6. Invert Negative Colors Toggle
            if (onToggleInvert != null) {
                LiquidToolButton(
                    icon = Icons.Default.InvertColors,
                    label = appString("action_invert"),
                    isSelected = project.bgInvert,
                    onClick = onToggleInvert
                )
            }

            // 7. Scanline Stripes Toggle
            LiquidToolButton(
                icon = Icons.Default.SpaceBar,
                label = appString("action_stripes"),
                isSelected = project.bgStripes,
                onClick = onToggleStripes
            )

            // 8. Decorative Frame Border
            if (onSetBorderWidth != null) {
                LiquidToolButton(
                    icon = Icons.Default.BorderAll,
                    label = "${appString("action_border")}: ${project.bgBorderWidth.toInt()}px",
                    isSelected = project.bgBorderWidth > 0f || activeSlider == "border",
                    onClick = { activeSlider = if (activeSlider == "border") null else "border" }
                )
            }

            // 9. Brightness
            LiquidToolButton(
                icon = Icons.Default.Palette,
                label = "${appString("action_brightness")}: ${(project.bgBrightness * 100).toInt()}%",
                isSelected = activeSlider == "bright",
                onClick = { activeSlider = if (activeSlider == "bright") null else "bright" }
            )

            // 10. Contrast
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = "${appString("action_contrast")}: ${(project.bgContrast * 100).toInt()}%",
                isSelected = activeSlider == "contrast",
                onClick = { activeSlider = if (activeSlider == "contrast") null else "contrast" }
            )

            // 11. Saturation
            LiquidToolButton(
                icon = Icons.Default.Opacity,
                label = "${appString("action_saturation")}: ${(project.bgSaturation * 100).toInt()}%",
                isSelected = activeSlider == "sat",
                onClick = { activeSlider = if (activeSlider == "sat") null else "sat" }
            )

            // Preset: Film
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_film"),
                onClick = {
                    onSetVignette(0.40f)
                    onSetNoise?.invoke(0.30f)
                    onSetContrast(0.25f)
                    onSetBrightness(-0.05f)
                }
            )

            // Preset: Cyberpunk
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_cyber"),
                tint = Color(0xFF00F5D4),
                onClick = {
                    if (!project.bgStripes) onToggleStripes()
                    onSetGlitch?.invoke(0.40f)
                    onSetSaturation(0.45f)
                    onSetVignette(0.35f)
                    onSetContrast(0.25f)
                }
            )

            // Preset: Vaporwave
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_vaporwave"),
                tint = Color(0xFFFF006E),
                onClick = {
                    if (!project.bgStripes) onToggleStripes()
                    onSetHue?.invoke(45f)
                    onSetGlitch?.invoke(0.25f)
                    onSetSaturation(0.35f)
                    onSetContrast(0.20f)
                }
            )

            // Preset: Golden Sunset
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_sunset"),
                tint = Color(0xFFFFBE0B),
                onClick = {
                    onSetSepia?.invoke(0.40f)
                    onSetSaturation(0.35f)
                    onSetBrightness(0.08f)
                    onSetVignette(0.30f)
                }
            )

            // Preset: Retro VHS
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_vhs"),
                onClick = {
                    if (!project.bgStripes) onToggleStripes()
                    onSetNoise?.invoke(0.45f)
                    onSetGlitch?.invoke(0.35f)
                    onSetContrast(0.20f)
                }
            )

            // Preset: Polaroid
            LiquidToolButton(
                icon = Icons.Default.BorderAll,
                label = appString("fx_preset_polaroid"),
                onClick = {
                    onSetBorderWidth?.invoke(16f)
                    onSetBorderColor?.invoke(0xFFFFFFFF)
                    onSetVignette(0.25f)
                    onSetNoise?.invoke(0.15f)
                }
            )

            // Preset: Monochrome Noir
            LiquidToolButton(
                icon = Icons.Default.AutoAwesome,
                label = appString("fx_preset_noir"),
                onClick = {
                    onSetSaturation(-1.0f)
                    onSetContrast(0.40f)
                    onSetNoise?.invoke(0.25f)
                    onSetVignette(0.35f)
                }
            )

            // Reset All Effects
            LiquidToolButton(
                icon = Icons.Default.Refresh,
                label = appString("action_reset_effects"),
                tint = Color(0xFFFF5252),
                onClick = {
                    onSetVignette(0f)
                    if (project.bgStripes) onToggleStripes()
                    if (project.bgInvert) onToggleInvert?.invoke()
                    onSetBrightness(0f)
                    onSetContrast(0f)
                    onSetSaturation(0f)
                    onSetNoise?.invoke(0f)
                    onSetGlitch?.invoke(0f)
                    onSetHue?.invoke(0f)
                    onSetSepia?.invoke(0f)
                    onSetBorderWidth?.invoke(0f)
                    onSetVignetteColor?.invoke(0xFF000000)
                }
            )
        }

        // Active Slider for Effects
        when (activeSlider) {
            "vignette" -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    InteractiveSliderRow(
                        label = "${appString("action_vignette")}: ${(project.bgVignette * 100).toInt()}%",
                        value = project.bgVignette,
                        valueRange = 0f..1.0f,
                        onValueChange = onSetVignette
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${appString("action_vignette_color")}:",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        vignetteColors.forEach { (_, col) ->
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RectangleShape)
                                    .background(Color(col), RectangleShape)
                                    .border(
                                        1.dp,
                                        if (project.bgVignetteColor == col) Color.White else Color(0x33FFFFFF),
                                        RectangleShape
                                    )
                                    .clickable { onSetVignetteColor?.invoke(col) }
                            )
                        }
                    }
                }
            }
            "noise" -> {
                InteractiveSliderRow(
                    label = "${appString("action_noise")}: ${(project.bgNoise * 100).toInt()}%",
                    value = project.bgNoise,
                    valueRange = 0f..1.0f,
                    onValueChange = { onSetNoise?.invoke(it) }
                )
            }
            "glitch" -> {
                InteractiveSliderRow(
                    label = "${appString("action_glitch")}: ${(project.bgGlitch * 100).toInt()}%",
                    value = project.bgGlitch,
                    valueRange = 0f..1.0f,
                    onValueChange = { onSetGlitch?.invoke(it) }
                )
            }
            "hue" -> {
                InteractiveSliderRow(
                    label = "${appString("action_hue")}: ${project.bgHue.toInt()}°",
                    value = project.bgHue,
                    valueRange = -180f..180f,
                    onValueChange = { onSetHue?.invoke(it) }
                )
            }
            "sepia" -> {
                InteractiveSliderRow(
                    label = "${appString("action_sepia")}: ${(project.bgSepia * 100).toInt()}%",
                    value = project.bgSepia,
                    valueRange = 0f..1.0f,
                    onValueChange = { onSetSepia?.invoke(it) }
                )
            }
            "border" -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    InteractiveSliderRow(
                        label = "${appString("action_border_width")}: ${project.bgBorderWidth.toInt()}px",
                        value = project.bgBorderWidth,
                        valueRange = 0f..40f,
                        onValueChange = { onSetBorderWidth?.invoke(it) }
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${appString("action_border_color")}:",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        borderColors.forEach { (_, col) ->
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RectangleShape)
                                    .background(Color(col), RectangleShape)
                                    .border(
                                        1.dp,
                                        if (project.bgBorderColor == col) Color.White else Color(0x33FFFFFF),
                                        RectangleShape
                                    )
                                    .clickable { onSetBorderColor?.invoke(col) }
                            )
                        }
                    }
                }
            }
            "bright" -> {
                InteractiveSliderRow(
                    label = "${appString("action_brightness")}: ${(project.bgBrightness * 100).toInt()}%",
                    value = project.bgBrightness,
                    valueRange = -0.8f..0.8f,
                    onValueChange = onSetBrightness
                )
            }
            "contrast" -> {
                InteractiveSliderRow(
                    label = "${appString("action_contrast")}: ${(project.bgContrast * 100).toInt()}%",
                    value = project.bgContrast,
                    valueRange = -0.8f..0.8f,
                    onValueChange = onSetContrast
                )
            }
            "sat" -> {
                InteractiveSliderRow(
                    label = "${appString("action_saturation")}: ${(project.bgSaturation * 100).toInt()}%",
                    value = project.bgSaturation,
                    valueRange = -1.0f..1.0f,
                    onValueChange = onSetSaturation
                )
            }
        }
    }
}

// ---------------- Reusable Tool Button with Sharp Right Angles (直角矩形按钮) ----------------

@Composable
fun LiquidToolButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    indicatorColor: Color? = null,
    tint: Color? = null,
    onClick: () -> Unit
) {
    val theme = ThemeManager.currentTheme

    Box(
        modifier = Modifier
            .clip(RectangleShape)
            .background(
                color = if (isSelected) theme.primaryColor.copy(alpha = 0.32f) else Color(0x18182740),
                shape = RectangleShape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) theme.primaryColor else Color(0x1EFFFFFF),
                shape = RectangleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (indicatorColor != null) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RectangleShape)
                        .background(indicatorColor, RectangleShape)
                        .border(1.dp, Color.White, RectangleShape)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint ?: if (isSelected) theme.primaryColor else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InteractiveSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val theme = ThemeManager.currentTheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RectangleShape)
            .background(Color(0x330A101C), RectangleShape)
            .border(1.dp, theme.primaryColor.copy(alpha = 0.3f), RectangleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = theme.primaryColor,
                activeTrackColor = theme.primaryColor,
                inactiveTrackColor = Color(0x33FFFFFF)
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
