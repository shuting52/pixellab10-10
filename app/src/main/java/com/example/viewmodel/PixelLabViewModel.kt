package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BackgroundAssetInfo
import com.example.model.BackgroundType
import com.example.model.CanvasAspectRatio
import com.example.model.CanvasElement
import com.example.model.CanvasProject
import com.example.model.FontInfo
import com.example.model.ImageElement
import com.example.model.QuoteCategoryInfo
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.StickerAssetInfo
import com.example.model.StickerElement
import com.example.model.TextElement
import com.example.util.AssetManagerHelper
import com.example.util.CanvasExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab(val title: String) {
    PRESETS("Presets"),
    TEXT("Text"),
    OBJECTS("Elements"),
    BACKGROUND("Canvas"),
    EFFECTS("Effects")
}

data class UiSheetState(
    val showLayers: Boolean = false,
    val showFontPicker: Boolean = false,
    val showQuotesPicker: Boolean = false,
    val showStickersPicker: Boolean = false,
    val showShapesPicker: Boolean = false,
    val showBgAssetsPicker: Boolean = false,
    val showExportDialog: Boolean = false,
    val showTextEditorDialog: Boolean = false,
    val showPresetSelector: Boolean = false,
    val showColorPicker: Boolean = false,
    val showLanguagePicker: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showCheckUpdatesDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val colorPickerTarget: ColorPickerTarget = ColorPickerTarget.TEXT_COLOR
)

enum class ColorPickerTarget {
    TEXT_COLOR,
    TEXT_STROKE,
    TEXT_SHADOW,
    TEXT_BG,
    TEXT_3D,
    CANVAS_BG,
    SHAPE_FILL,
    SHAPE_STROKE
}

class PixelLabViewModel(application: Application) : AndroidViewModel(application) {

    private val _project = MutableStateFlow(CanvasProject())
    val project: StateFlow<CanvasProject> = _project.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.TEXT)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _sheets = MutableStateFlow(UiSheetState())
    val sheets: StateFlow<UiSheetState> = _sheets.asStateFlow()

    private val _isGridVisible = MutableStateFlow(false)
    val isGridVisible: StateFlow<Boolean> = _isGridVisible.asStateFlow()

    private val _zoomLevel = MutableStateFlow(1f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Undo / Redo history stacks
    private val undoStack = mutableListOf<CanvasProject>()
    private val redoStack = mutableListOf<CanvasProject>()
    private val maxHistory = 20

    // Assets cache
    val fonts = MutableStateFlow<List<FontInfo>>(emptyList())
    val backgroundAssets = MutableStateFlow<List<BackgroundAssetInfo>>(emptyList())
    val stickerCategories = MutableStateFlow<Map<String, List<StickerAssetInfo>>>(emptyMap())
    val quotesCategories = MutableStateFlow<List<QuoteCategoryInfo>>(emptyList())
    val presetTemplates = MutableStateFlow<List<CanvasProject>>(emptyList())

    init {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            fonts.value = AssetManagerHelper.loadFonts(context)
            backgroundAssets.value = AssetManagerHelper.loadBackgroundPresets(context)
            stickerCategories.value = AssetManagerHelper.loadStickerCategories(context)
            quotesCategories.value = AssetManagerHelper.loadQuotes(context)
            presetTemplates.value = AssetManagerHelper.getDefaultPresetTemplates()
        }
    }

    private fun pushHistory() {
        undoStack.add(_project.value.copy())
        if (undoStack.size > maxHistory) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
    }

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_project.value.copy())
            val prev = undoStack.removeAt(undoStack.lastIndex)
            _project.value = prev
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_project.value.copy())
            val next = redoStack.removeAt(redoStack.lastIndex)
            _project.value = next
        }
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun toggleGrid() {
        _isGridVisible.update { !it }
    }

    fun setZoom(zoom: Float) {
        _zoomLevel.value = zoom.coerceIn(0.5f, 3.0f)
    }

    fun resetZoom() {
        _zoomLevel.value = 1f
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Sheet management
    fun updateSheetState(transform: (UiSheetState) -> UiSheetState) {
        _sheets.update(transform)
    }

    fun openColorPicker(target: ColorPickerTarget) {
        _sheets.update { it.copy(showColorPicker = true, colorPickerTarget = target) }
    }

    // Element selection
    val selectedElement: CanvasElement?
        get() = _project.value.elements.firstOrNull { it.id == _project.value.selectedElementId }

    fun selectElement(id: String?) {
        _project.update { it.copy(selectedElementId = id) }
        val el = _project.value.elements.firstOrNull { it.id == id }
        if (el is TextElement) {
            _currentTab.value = NavigationTab.TEXT
        } else if (el != null) {
            _currentTab.value = NavigationTab.OBJECTS
        }
    }

    fun updateSelectedElement(transform: (CanvasElement) -> CanvasElement) {
        val currentId = _project.value.selectedElementId ?: return
        pushHistory()
        _project.update { proj ->
            val updatedList = proj.elements.map { el ->
                if (el.id == currentId) transform(el) else el
            }
            proj.copy(elements = updatedList)
        }
    }

    fun updateElementPositionWithoutHistory(id: String, x: Float, y: Float) {
        _project.update { proj ->
            val updated = proj.elements.map { el ->
                if (el.id == id) el.copyWithCommon(x = x, y = y) else el
            }
            proj.copy(elements = updated)
        }
    }

    fun updateElementTransformWithoutHistory(id: String, scale: Float, rotation: Float) {
        _project.update { proj ->
            val updated = proj.elements.map { el ->
                if (el.id == id) el.copyWithCommon(scale = scale, rotation = rotation) else el
            }
            proj.copy(elements = updated)
        }
    }

    fun onTransformGestureEnd() {
        pushHistory()
    }

    fun deleteSelectedElement() {
        val id = _project.value.selectedElementId ?: return
        pushHistory()
        _project.update { proj ->
            val filtered = proj.elements.filterNot { it.id == id }
            proj.copy(elements = filtered, selectedElementId = filtered.lastOrNull()?.id)
        }
    }

    fun duplicateSelectedElement() {
        val current = selectedElement ?: return
        pushHistory()
        val duplicated: CanvasElement = when (current) {
            is TextElement -> current.copy(
                id = UUID.randomUUID().toString(),
                x = current.x + 30f,
                y = current.y + 30f
            )
            is StickerElement -> current.copy(
                id = UUID.randomUUID().toString(),
                x = current.x + 30f,
                y = current.y + 30f
            )
            is ShapeElement -> current.copy(
                id = UUID.randomUUID().toString(),
                x = current.x + 30f,
                y = current.y + 30f
            )
            is ImageElement -> current.copy(
                id = UUID.randomUUID().toString(),
                x = current.x + 30f,
                y = current.y + 30f
            )
        }
        _project.update { proj ->
            proj.copy(elements = proj.elements + duplicated, selectedElementId = duplicated.id)
        }
    }

    fun bringToFront() {
        val current = selectedElement ?: return
        pushHistory()
        _project.update { proj ->
            val elements = proj.elements.filterNot { it.id == current.id } + current
            proj.copy(elements = elements)
        }
    }

    fun sendToBack() {
        val current = selectedElement ?: return
        pushHistory()
        _project.update { proj ->
            val elements = listOf(current) + proj.elements.filterNot { it.id == current.id }
            proj.copy(elements = elements)
        }
    }

    fun moveLayerUp(id: String) {
        pushHistory()
        _project.update { proj ->
            val list = proj.elements.toMutableList()
            val idx = list.indexOfFirst { it.id == id }
            if (idx in 0 until list.lastIndex) {
                val temp = list[idx]
                list[idx] = list[idx + 1]
                list[idx + 1] = temp
            }
            proj.copy(elements = list)
        }
    }

    fun moveLayerDown(id: String) {
        pushHistory()
        _project.update { proj ->
            val list = proj.elements.toMutableList()
            val idx = list.indexOfFirst { it.id == id }
            if (idx > 0) {
                val temp = list[idx]
                list[idx] = list[idx - 1]
                list[idx - 1] = temp
            }
            proj.copy(elements = list)
        }
    }

    fun toggleVisibility(id: String) {
        pushHistory()
        _project.update { proj ->
            val updated = proj.elements.map { el ->
                if (el.id == id) el.copyWithCommon(isVisible = !el.isVisible) else el
            }
            proj.copy(elements = updated)
        }
    }

    fun toggleLock(id: String) {
        pushHistory()
        _project.update { proj ->
            val updated = proj.elements.map { el ->
                if (el.id == id) el.copyWithCommon(isLocked = !el.isLocked) else el
            }
            proj.copy(elements = updated)
        }
    }

    fun centerSelectedElementH() {
        val current = selectedElement ?: return
        pushHistory()
        updateSelectedElement { it.copyWithCommon(x = 0f) }
    }

    fun centerSelectedElementV() {
        val current = selectedElement ?: return
        pushHistory()
        updateSelectedElement { it.copyWithCommon(y = 0f) }
    }

    fun rotateSelectedElementBy(deltaDegrees: Float) {
        val current = selectedElement ?: return
        pushHistory()
        updateSelectedElement { it.copyWithCommon(rotation = (it.rotation + deltaDegrees) % 360f) }
    }

    fun toggleLockSelectedElement() {
        val current = selectedElement ?: return
        toggleLock(current.id)
    }

    fun toggleVisibilitySelectedElement() {
        val current = selectedElement ?: return
        toggleVisibility(current.id)
    }

    fun addWatermarkStamp() {
        pushHistory()
        val watermark = TextElement(
            text = "✨ CREATED WITH PIXELLAB",
            fontSize = 24f,
            fontAssetPath = "fonts/Fancy/Righteous-Regular.ttf",
            fontDisplayName = "Righteous",
            textColor = 0xEEFFFFFF,
            shadowEnabled = true,
            shadowColor = 0x88000000,
            shadowRadius = 8f,
            bgBoxEnabled = true,
            bgBoxColor = 0x66000000,
            bgBoxPaddingH = 16f,
            bgBoxPaddingV = 8f,
            bgBoxCornerRadius = 10f,
            x = 0f,
            y = 350f
        )
        _project.update { proj ->
            proj.copy(elements = proj.elements + watermark, selectedElementId = watermark.id)
        }
        _currentTab.value = NavigationTab.TEXT
    }

    // Add elements
    fun addText(text: String = "New Text", fontPath: String? = null, fontName: String? = null) {
        pushHistory()
        val newText = TextElement(
            text = text,
            fontAssetPath = fontPath ?: "fonts/Basic/Roboto-Regular.ttf",
            fontDisplayName = fontName ?: "Roboto Regular",
            fontSize = 46f,
            textColor = 0xFFFFFFFF,
            shadowEnabled = true,
            shadowColor = 0x88000000,
            shadowRadius = 10f
        )
        _project.update { proj ->
            proj.copy(elements = proj.elements + newText, selectedElementId = newText.id)
        }
        _currentTab.value = NavigationTab.TEXT
    }

    fun addSticker(assetPath: String, category: String) {
        pushHistory()
        val sticker = StickerElement(
            assetPath = assetPath,
            category = category,
            width = 180f,
            height = 180f
        )
        _project.update { proj ->
            proj.copy(elements = proj.elements + sticker, selectedElementId = sticker.id)
        }
        _currentTab.value = NavigationTab.OBJECTS
    }

    fun addShape(shapeType: ShapeType) {
        pushHistory()
        val shape = ShapeElement(
            shapeType = shapeType,
            width = 220f,
            height = 220f,
            fillColor = 0xFF00B4D8,
            strokeColor = 0xFFFFFFFF,
            strokeWidth = 0f
        )
        _project.update { proj ->
            proj.copy(elements = proj.elements + shape, selectedElementId = shape.id)
        }
        _currentTab.value = NavigationTab.OBJECTS
    }

    fun addImage(uri: String) {
        pushHistory()
        val img = ImageElement(
            uriString = uri,
            width = 260f,
            height = 260f
        )
        _project.update { proj ->
            proj.copy(elements = proj.elements + img, selectedElementId = img.id)
        }
        _currentTab.value = NavigationTab.OBJECTS
    }

    fun insertQuote(quoteText: String) {
        pushHistory()
        val current = selectedElement
        if (current is TextElement) {
            updateSelectedElement { (it as TextElement).copy(text = quoteText) }
        } else {
            addText(text = quoteText, fontPath = "fonts/Basic/Boogaloo-Regular.otf", fontName = "Boogaloo")
        }
    }

    // Canvas Background
    fun setBackgroundType(type: BackgroundType) {
        pushHistory()
        _project.update { it.copy(backgroundType = type) }
    }

    fun setBackgroundColor(color: Long) {
        pushHistory()
        _project.update { it.copy(backgroundType = BackgroundType.SOLID, bgColor = color) }
    }

    fun setBackgroundGradient(start: Long, end: Long) {
        pushHistory()
        _project.update {
            it.copy(
                backgroundType = BackgroundType.GRADIENT,
                bgGradientStart = start,
                bgGradientEnd = end
            )
        }
    }

    fun setBackgroundAsset(assetPath: String) {
        pushHistory()
        _project.update {
            it.copy(
                backgroundType = BackgroundType.ASSET,
                bgAssetPath = assetPath
            )
        }
    }

    fun setBackgroundGallery(uri: String) {
        pushHistory()
        _project.update {
            it.copy(
                backgroundType = BackgroundType.GALLERY,
                bgImageUri = uri
            )
        }
    }

    fun setAspectRatio(ratio: CanvasAspectRatio) {
        pushHistory()
        _project.update { it.copy(aspectRatio = ratio) }
    }

    fun setVignette(alpha: Float) {
        _project.update { it.copy(bgVignette = alpha.coerceIn(0f, 1f)) }
    }

    fun toggleStripes() {
        pushHistory()
        _project.update { it.copy(bgStripes = !it.bgStripes) }
    }

    fun setBrightness(value: Float) {
        _project.update { it.copy(bgBrightness = value.coerceIn(-1f, 1f)) }
    }

    fun setContrast(value: Float) {
        _project.update { it.copy(bgContrast = value.coerceIn(-1f, 1f)) }
    }

    fun setSaturation(value: Float) {
        _project.update { it.copy(bgSaturation = value.coerceIn(-1f, 1f)) }
    }

    fun setNoise(value: Float) {
        _project.update { it.copy(bgNoise = value.coerceIn(0f, 1f)) }
    }

    fun setGlitch(value: Float) {
        _project.update { it.copy(bgGlitch = value.coerceIn(0f, 1f)) }
    }

    fun setHue(value: Float) {
        _project.update { it.copy(bgHue = value.coerceIn(-180f, 180f)) }
    }

    fun setSepia(value: Float) {
        _project.update { it.copy(bgSepia = value.coerceIn(0f, 1f)) }
    }

    fun toggleInvert() {
        pushHistory()
        _project.update { it.copy(bgInvert = !it.bgInvert) }
    }

    fun setBorderWidth(value: Float) {
        _project.update { it.copy(bgBorderWidth = value.coerceIn(0f, 40f)) }
    }

    fun setBorderColor(color: Long) {
        _project.update { it.copy(bgBorderColor = color) }
    }

    fun setVignetteColor(color: Long) {
        _project.update { it.copy(bgVignetteColor = color) }
    }

    fun loadPreset(preset: CanvasProject) {
        pushHistory()
        _project.value = preset.copy(
            id = UUID.randomUUID().toString(),
            selectedElementId = preset.elements.firstOrNull()?.id
        )
        showToast("Loaded \"${preset.name}\"")
    }

    fun resetCanvas() {
        pushHistory()
        _project.value = CanvasProject()
        showToast("Canvas reset to default")
    }

    // Export & Share
    fun exportImage(format: String, resolutionMultiplier: Float) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val currentProj = _project.value
            val targetW = (currentProj.aspectRatio.defaultWidth * resolutionMultiplier).toInt()
            val targetH = (currentProj.aspectRatio.defaultHeight * resolutionMultiplier).toInt()

            val bitmap = CanvasExporter.renderToBitmap(context, currentProj, targetW, targetH)
            val uri = CanvasExporter.saveBitmapToGallery(context, bitmap, format)
            if (uri != null) {
                showToast("Saved to Pictures/PixelLab!")
            } else {
                showToast("Failed to save image")
            }
        }
    }

    fun shareImage(format: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val currentProj = _project.value
            val bitmap = CanvasExporter.renderToBitmap(context, currentProj)
            CanvasExporter.shareBitmap(context, bitmap, format)
        }
    }
}
