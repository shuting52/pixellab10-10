package com.example.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.TextElement
import com.example.ui.components.BottomToolbar
import com.example.ui.components.CanvasView
import com.example.ui.components.GlobalAppBackground
import com.example.ui.components.LiquidBackground
import com.example.ui.components.TopBar
import com.example.ui.dialogs.AboutDialog
import com.example.ui.dialogs.BackgroundAssetDialog
import com.example.ui.dialogs.CheckUpdatesDialog
import com.example.ui.dialogs.ColorPickerDialog
import com.example.ui.dialogs.ExportDialog
import com.example.ui.dialogs.FontPickerDialog
import com.example.ui.dialogs.LanguagePickerDialog
import com.example.ui.dialogs.LayersSheet
import com.example.ui.dialogs.PresetsDialog
import com.example.ui.dialogs.QuotesDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.ShapesDialog
import com.example.ui.dialogs.StickersSheet
import com.example.ui.dialogs.TextEditorDialog
import com.example.util.LanguageManager
import com.example.util.appString
import com.example.viewmodel.ColorPickerTarget
import com.example.viewmodel.PixelLabViewModel

@Composable
fun PixelLabScreen(
    viewModel: PixelLabViewModel = viewModel()
) {
    val context = LocalContext.current
    val project by viewModel.project.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val sheets by viewModel.sheets.collectAsState()
    val isGridVisible by viewModel.isGridVisible.collectAsState()
    val zoomLevel by viewModel.zoomLevel.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val fonts by viewModel.fonts.collectAsState()
    val backgroundAssets by viewModel.backgroundAssets.collectAsState()
    val stickerCategories by viewModel.stickerCategories.collectAsState()
    val quotesCategories by viewModel.quotesCategories.collectAsState()
    val presetTemplates by viewModel.presetTemplates.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Toast handler
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Photo Picker for importing images to canvas or background
    var isPickingForBackground = remember { false }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (isPickingForBackground) {
                viewModel.setBackgroundGallery(uri.toString())
            } else {
                viewModel.addImage(uri.toString())
            }
        }
    }

    val selectedEl = viewModel.selectedElement

    Box(modifier = Modifier.fillMaxSize()) {
        // Living dynamic liquid glass / custom video/image global backdrop
        GlobalAppBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    canUndo = viewModel.canUndo,
                    canRedo = viewModel.canRedo,
                    isGridVisible = isGridVisible,
                    layerCount = project.elements.size,
                    currentAspectRatio = project.aspectRatio,
                    onSetAspectRatio = { viewModel.setAspectRatio(it) },
                    onUndo = { viewModel.undo() },
                    onRedo = { viewModel.redo() },
                    onToggleGrid = { viewModel.toggleGrid() },
                    onToggleZoom = {
                        if (zoomLevel > 1f) viewModel.resetZoom() else viewModel.setZoom(1.5f)
                    },
                    onOpenLayers = {
                        viewModel.updateSheetState { it.copy(showLayers = true) }
                    },
                    onAddText = { initialText ->
                        viewModel.addText(initialText)
                    },
                    onOpenStickers = {
                        viewModel.updateSheetState { it.copy(showStickersPicker = true) }
                    },
                    onOpenShapes = {
                        viewModel.updateSheetState { it.copy(showShapesPicker = true) }
                    },
                    onOpenQuotes = {
                        viewModel.updateSheetState { it.copy(showQuotesPicker = true) }
                    },
                    onOpenLanguagePicker = {
                        viewModel.updateSheetState { it.copy(showLanguagePicker = true) }
                    },
                    onOpenSettings = {
                        viewModel.updateSheetState { it.copy(showSettingsDialog = true) }
                    },
                    onOpenCheckUpdates = {
                        viewModel.updateSheetState { it.copy(showCheckUpdatesDialog = true) }
                    },
                    onOpenAbout = {
                        viewModel.updateSheetState { it.copy(showAboutDialog = true) }
                    },
                    onPickImageFromGallery = {
                        isPickingForBackground = false
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onOpenExport = {
                        viewModel.updateSheetState { it.copy(showExportDialog = true) }
                    },
                    onOpenPresets = {
                        viewModel.updateSheetState { it.copy(showPresetSelector = true) }
                    },
                    onResetCanvas = {
                        viewModel.resetCanvas()
                    }
                )
            },
            bottomBar = {
                BottomToolbar(
                    currentTab = currentTab,
                    selectedElement = selectedEl,
                    project = project,
                    onTabSelected = { viewModel.setTab(it) },
                    onOpenTextEditor = {
                        viewModel.updateSheetState { it.copy(showTextEditorDialog = true) }
                    },
                    onOpenFontPicker = {
                        viewModel.updateSheetState { it.copy(showFontPicker = true) }
                    },
                    onOpenQuotes = {
                        viewModel.updateSheetState { it.copy(showQuotesPicker = true) }
                    },
                    onOpenStickers = {
                        viewModel.updateSheetState { it.copy(showStickersPicker = true) }
                    },
                    onOpenShapes = {
                        viewModel.updateSheetState { it.copy(showShapesPicker = true) }
                    },
                    onOpenBgAssets = {
                        viewModel.updateSheetState { it.copy(showBgAssetsPicker = true) }
                    },
                    onPickImageFromGallery = {
                        isPickingForBackground = currentTab == com.example.viewmodel.NavigationTab.BACKGROUND
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onOpenColorPicker = { target ->
                        viewModel.openColorPicker(target)
                    },
                    onUpdateText = { transform ->
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) transform(el) else el
                        }
                    },
                    onUpdateShape = { transform ->
                        viewModel.updateSelectedElement { el ->
                            if (el is ShapeElement) transform(el) else el
                        }
                    },
                    onDeleteElement = { viewModel.deleteSelectedElement() },
                    onBringToFront = { viewModel.bringToFront() },
                    onSendToBack = { viewModel.sendToBack() },
                    onSetBackgroundType = { viewModel.setBackgroundType(it) },
                    onSetAspectRatio = { viewModel.setAspectRatio(it) },
                    onSetVignette = { viewModel.setVignette(it) },
                    onToggleStripes = { viewModel.toggleStripes() },
                    onSetBrightness = { viewModel.setBrightness(it) },
                    onSetContrast = { viewModel.setContrast(it) },
                    onSetSaturation = { viewModel.setSaturation(it) },
                    onLoadPreset = { viewModel.loadPreset(it) },
                    presetTemplates = presetTemplates,
                    onDuplicateElement = { viewModel.duplicateSelectedElement() },
                    onAddText = { viewModel.addText() },
                    onAddNeonText = {
                        viewModel.addText(
                            text = "NEON GLOW",
                            fontPath = "fonts/Fancy/PermanentMarker-Regular.ttf",
                            fontName = "Permanent Marker"
                        )
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(
                                textColor = 0xFF00F5D4,
                                shadowEnabled = true,
                                shadowColor = 0xFF00BBF9,
                                shadowRadius = 24f,
                                strokeEnabled = true,
                                strokeColor = 0xFFFFFFFF,
                                strokeWidth = 2f
                            ) else el
                        }
                    },
                    onAdd3dText = {
                        viewModel.addText(
                            text = "3D PIXEL",
                            fontPath = "fonts/Fancy/Righteous-Regular.ttf",
                            fontName = "Righteous"
                        )
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(
                                textColor = 0xFFFFBE0B,
                                threeDEnabled = true,
                                threeDDepth = 16f,
                                threeDColor = 0xFFFB5607,
                                shadowEnabled = true,
                                shadowColor = 0xAA000000,
                                shadowRadius = 14f
                            ) else el
                        }
                    },
                    onAddBadgeText = {
                        viewModel.addText(
                            text = "TAG #01",
                            fontPath = "fonts/Basic/Roboto-Regular.ttf",
                            fontName = "Roboto"
                        )
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(
                                textColor = 0xFFFFFFFF,
                                bgBoxEnabled = true,
                                bgBoxColor = 0xDD7209B7,
                                bgBoxPaddingH = 24f,
                                bgBoxPaddingV = 12f,
                                bgBoxCornerRadius = 0f
                            ) else el
                        }
                    },
                    onAddShape = { shapeType -> viewModel.addShape(shapeType) },
                    onResetCanvas = { viewModel.resetCanvas() },
                    onToggleGrid = { viewModel.toggleGrid() },
                    isGridVisible = isGridVisible,
                    onResetZoom = { viewModel.resetZoom() },
                    onSetBackgroundColor = { col -> viewModel.setBackgroundColor(col) },
                    onUpdateElement = { transform -> viewModel.updateSelectedElement(transform) },
                    onOpenPresetsDialog = { viewModel.updateSheetState { it.copy(showPresetSelector = true) } },
                    onCenterElementH = { viewModel.centerSelectedElementH() },
                    onCenterElementV = { viewModel.centerSelectedElementV() },
                    onRotateElementBy = { deg -> viewModel.rotateSelectedElementBy(deg) },
                    onToggleLockElement = { viewModel.toggleLockSelectedElement() },
                    onToggleVisibilityElement = { viewModel.toggleVisibilitySelectedElement() },
                    onAddWatermarkStamp = { viewModel.addWatermarkStamp() },
                    onSetNoise = { viewModel.setNoise(it) },
                    onSetGlitch = { viewModel.setGlitch(it) },
                    onSetHue = { viewModel.setHue(it) },
                    onSetSepia = { viewModel.setSepia(it) },
                    onToggleInvert = { viewModel.toggleInvert() },
                    onSetBorderWidth = { viewModel.setBorderWidth(it) },
                    onSetBorderColor = { viewModel.setBorderColor(it) },
                    onSetVignetteColor = { viewModel.setVignetteColor(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CanvasView(
                    project = project,
                    isGridVisible = isGridVisible,
                    zoomLevel = zoomLevel,
                    onSelectElement = { id -> viewModel.selectElement(id) },
                    onUpdatePosition = { id, x, y -> viewModel.updateElementPositionWithoutHistory(id, x, y) },
                    onUpdateTransform = { id, scale, rot -> viewModel.updateElementTransformWithoutHistory(id, scale, rot) },
                    onTransformEnd = { viewModel.onTransformGestureEnd() },
                    onDeleteElement = { viewModel.deleteSelectedElement() },
                    onDuplicateElement = { viewModel.duplicateSelectedElement() }
                )
            }
        }
    }

    // Modal Dialogs
    if (sheets.showLanguagePicker) {
        LanguagePickerDialog(
            currentLanguage = LanguageManager.currentLanguage,
            onLanguageSelected = { newLang ->
                LanguageManager.setLanguage(newLang)
                viewModel.showToast("${newLang.flag} ${newLang.nativeName}")
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showLanguagePicker = false) }
            }
        )
    }

    if (sheets.showTextEditorDialog) {
        val currentText = (selectedEl as? TextElement)?.text ?: appString("new_text")
        TextEditorDialog(
            initialText = currentText,
            onConfirm = { newText ->
                if (selectedEl is TextElement) {
                    viewModel.updateSelectedElement { (it as TextElement).copy(text = newText) }
                } else {
                    viewModel.addText(text = newText)
                }
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showTextEditorDialog = false) }
            }
        )
    }

    if (sheets.showFontPicker) {
        val currentFontPath = (selectedEl as? TextElement)?.fontAssetPath
        val sample = (selectedEl as? TextElement)?.text ?: "PixelLab"
        FontPickerDialog(
            fonts = fonts,
            selectedFontPath = currentFontPath,
            sampleText = sample,
            onFontSelected = { fontInfo ->
                viewModel.updateSelectedElement { el ->
                    if (el is TextElement) {
                        el.copy(
                            fontAssetPath = fontInfo.assetPath,
                            fontDisplayName = fontInfo.name
                        )
                    } else el
                }
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showFontPicker = false) }
            }
        )
    }

    if (sheets.showQuotesPicker) {
        QuotesDialog(
            categories = quotesCategories,
            onQuoteSelected = { quote ->
                viewModel.insertQuote(quote)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showQuotesPicker = false) }
            }
        )
    }

    if (sheets.showStickersPicker) {
        StickersSheet(
            stickerCategories = stickerCategories,
            onStickerSelected = { sticker ->
                viewModel.addSticker(sticker.assetPath, sticker.category)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showStickersPicker = false) }
            }
        )
    }

    if (sheets.showShapesPicker) {
        ShapesDialog(
            onShapeSelected = { shapeType ->
                viewModel.addShape(shapeType)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showShapesPicker = false) }
            }
        )
    }

    if (sheets.showBgAssetsPicker) {
        BackgroundAssetDialog(
            backgrounds = backgroundAssets,
            onBackgroundSelected = { bg ->
                viewModel.setBackgroundAsset(bg.imagePath)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showBgAssetsPicker = false) }
            }
        )
    }

    if (sheets.showLayers) {
        LayersSheet(
            elements = project.elements,
            selectedId = project.selectedElementId,
            onSelectLayer = { id -> viewModel.selectElement(id) },
            onToggleLock = { id -> viewModel.toggleLock(id) },
            onToggleVisibility = { id -> viewModel.toggleVisibility(id) },
            onMoveUp = { id -> viewModel.moveLayerUp(id) },
            onMoveDown = { id -> viewModel.moveLayerDown(id) },
            onDuplicate = { viewModel.duplicateSelectedElement() },
            onDelete = { viewModel.deleteSelectedElement() },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showLayers = false) }
            }
        )
    }

    if (sheets.showPresetSelector) {
        PresetsDialog(
            presets = presetTemplates,
            onPresetSelected = { preset ->
                viewModel.loadPreset(preset)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showPresetSelector = false) }
            }
        )
    }

    if (sheets.showExportDialog) {
        ExportDialog(
            project = project,
            onSaveToGallery = { format, multiplier ->
                viewModel.exportImage(format, multiplier)
            },
            onShare = { format ->
                viewModel.shareImage(format)
            },
            onDismiss = {
                viewModel.updateSheetState { it.copy(showExportDialog = false) }
            }
        )
    }

    if (sheets.showColorPicker) {
        val target = sheets.colorPickerTarget
        val currentColor = when (target) {
            ColorPickerTarget.TEXT_COLOR -> (selectedEl as? TextElement)?.textColor ?: 0xFFFFFFFF
            ColorPickerTarget.TEXT_STROKE -> (selectedEl as? TextElement)?.strokeColor ?: 0xFF000000
            ColorPickerTarget.TEXT_SHADOW -> (selectedEl as? TextElement)?.shadowColor ?: 0x88000000
            ColorPickerTarget.TEXT_BG -> (selectedEl as? TextElement)?.bgBoxColor ?: 0x88000000
            ColorPickerTarget.TEXT_3D -> (selectedEl as? TextElement)?.threeDColor ?: 0xFF0077B6
            ColorPickerTarget.CANVAS_BG -> project.bgColor
            ColorPickerTarget.SHAPE_FILL -> (selectedEl as? ShapeElement)?.fillColor ?: 0xFF00B4D8
            ColorPickerTarget.SHAPE_STROKE -> (selectedEl as? ShapeElement)?.strokeColor ?: 0xFFFFFFFF
        }

        ColorPickerDialog(
            target = target,
            currentColor = currentColor,
            onColorSelected = { color ->
                when (target) {
                    ColorPickerTarget.TEXT_COLOR -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(textColor = color) else el
                        }
                    }
                    ColorPickerTarget.TEXT_STROKE -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(strokeColor = color, strokeEnabled = true) else el
                        }
                    }
                    ColorPickerTarget.TEXT_SHADOW -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(shadowColor = color, shadowEnabled = true) else el
                        }
                    }
                    ColorPickerTarget.TEXT_BG -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(bgBoxColor = color, bgBoxEnabled = true) else el
                        }
                    }
                    ColorPickerTarget.TEXT_3D -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is TextElement) el.copy(threeDColor = color, threeDEnabled = true) else el
                        }
                    }
                    ColorPickerTarget.CANVAS_BG -> {
                        viewModel.setBackgroundColor(color)
                    }
                    ColorPickerTarget.SHAPE_FILL -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is ShapeElement) el.copy(fillColor = color) else el
                        }
                    }
                    ColorPickerTarget.SHAPE_STROKE -> {
                        viewModel.updateSelectedElement { el ->
                            if (el is ShapeElement) el.copy(strokeColor = color, strokeWidth = if (el.strokeWidth == 0f) 4f else el.strokeWidth) else el
                        }
                    }
                }
            },
            onGradientSelected = if (target == ColorPickerTarget.CANVAS_BG) {
                { start, end -> viewModel.setBackgroundGradient(start, end) }
            } else null,
            onDismiss = {
                viewModel.updateSheetState { it.copy(showColorPicker = false) }
            }
        )
    }

    if (sheets.showSettingsDialog) {
        SettingsDialog(
            onDismiss = {
                viewModel.updateSheetState { it.copy(showSettingsDialog = false) }
            }
        )
    }

    if (sheets.showCheckUpdatesDialog) {
        CheckUpdatesDialog(
            onDismiss = {
                viewModel.updateSheetState { it.copy(showCheckUpdatesDialog = false) }
            }
        )
    }

    if (sheets.showAboutDialog) {
        AboutDialog(
            onDismiss = {
                viewModel.updateSheetState { it.copy(showAboutDialog = false) }
            }
        )
    }
}
