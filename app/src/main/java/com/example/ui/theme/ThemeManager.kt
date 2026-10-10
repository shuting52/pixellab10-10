package com.example.ui.theme

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class CapsuleThemeMode(
    val id: String,
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val surfaceColor: Color
) {
    CYAN_AURORA(
        id = "cyan_aurora",
        displayName = "液态玻璃 · 极光青",
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF0077B6),
        accentColor = Color(0xFF7C4DFF),
        surfaceColor = Color(0xCC091224)
    ),
    NEON_PURPLE(
        id = "neon_purple",
        displayName = "液态玻璃 · 幻夜紫",
        primaryColor = Color(0xFFB388FF),
        secondaryColor = Color(0xFF7C4DFF),
        accentColor = Color(0xFFFF4081),
        surfaceColor = Color(0xCC140A26)
    ),
    EMERALD_MINT(
        id = "emerald_mint",
        displayName = "液态玻璃 · 薄荷翠",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF00B0FF),
        accentColor = Color(0xFF1DE9B6),
        surfaceColor = Color(0xCC061A16)
    ),
    AMBER_GOLD(
        id = "amber_gold",
        displayName = "液态玻璃 · 流光金",
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFFFF6D00),
        accentColor = Color(0xFFFFAB40),
        surfaceColor = Color(0xCC1E1508)
    ),
    ICE_FROST(
        id = "ice_frost",
        displayName = "液态玻璃 · 冰晶蓝",
        primaryColor = Color(0xFF80D8FF),
        secondaryColor = Color(0xFF448AFF),
        accentColor = Color(0xFFE0F7FA),
        surfaceColor = Color(0xCC08162A)
    )
}

enum class GlobalBgMode(val label: String) {
    LIQUID_DYNAMIC("动态液态流光 (默认)"),
    CUSTOM_IMAGE("自定义上传图片"),
    CUSTOM_VIDEO("自定义上传视频"),
    PRESET_VIDEO_FLOW("预设视频 · 幻彩流体"),
    PRESET_VIDEO_AURORA("预设视频 · 极光夜色"),
    PRESET_VIDEO_COSMOS("预设视频 · 星云脉冲")
}

object ThemeManager {
    var currentTheme by mutableStateOf(CapsuleThemeMode.CYAN_AURORA)
        private set

    var currentBgMode by mutableStateOf(GlobalBgMode.LIQUID_DYNAMIC)
        private set

    var customMediaUri by mutableStateOf<Uri?>(null)
        private set

    fun setTheme(mode: CapsuleThemeMode) {
        currentTheme = mode
    }

    fun setCustomImage(uri: Uri) {
        customMediaUri = uri
        currentBgMode = GlobalBgMode.CUSTOM_IMAGE
    }

    fun setCustomVideo(uri: Uri) {
        customMediaUri = uri
        currentBgMode = GlobalBgMode.CUSTOM_VIDEO
    }

    fun setPresetVideo(mode: GlobalBgMode) {
        currentBgMode = mode
    }

    fun resetToDefault() {
        customMediaUri = null
        currentBgMode = GlobalBgMode.LIQUID_DYNAMIC
    }
}
