package com.example.ui.dialogs

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.liquidGlassCapsule
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.CapsuleThemeMode
import com.example.ui.theme.GlobalBgMode
import com.example.ui.theme.ThemeManager
import com.example.util.appString

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = ThemeManager.currentTheme
    val currentBg = ThemeManager.currentBgMode

    // Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            ThemeManager.setCustomVideo(uri)
            Toast.makeText(context, "自定义视频背景已实时同步至全局！", Toast.LENGTH_SHORT).show()
        }
    }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            ThemeManager.setCustomImage(uri)
            Toast.makeText(context, "自定义图片背景已实时同步至全局！", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = Color(0xF008101E),
                    borderWidth = 1.5.dp,
                    activeGlow = true
                ),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (Capsule style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(currentTheme.primaryColor.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = currentTheme.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = appString("title_settings"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Liquid Glass Capsule Theme Selection
                Text(
                    text = appString("theme_switcher"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CapsuleThemeMode.entries.forEach { mode ->
                        val isSelected = currentTheme == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlassCapsule(
                                    backgroundColor = if (isSelected) mode.surfaceColor else Color(0x18172338),
                                    borderWidth = if (isSelected) 1.5.dp else 1.dp,
                                    activeGlow = isSelected
                                )
                                .clickable {
                                    ThemeManager.setTheme(mode)
                                    Toast.makeText(context, "已切换为：${mode.displayName}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(mode.primaryColor, mode.accentColor)
                                            )
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = mode.displayName,
                                    color = if (isSelected) mode.primaryColor else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(mode.primaryColor)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("当前应用", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: Global Background Customization (Video / Image Upload)
                Text(
                    text = appString("global_bg_title"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appString("realtime_applied_hint"),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Media Upload Action Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Upload Video Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassCapsule(
                                backgroundColor = if (currentBg == GlobalBgMode.CUSTOM_VIDEO) currentTheme.primaryColor.copy(alpha = 0.3f) else Color(0x221A2840),
                                activeGlow = currentBg == GlobalBgMode.CUSTOM_VIDEO
                            )
                            .clickable {
                                videoPickerLauncher.launch("video/*")
                            }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = currentTheme.primaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(appString("upload_custom_video"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Upload Image Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassCapsule(
                                backgroundColor = if (currentBg == GlobalBgMode.CUSTOM_IMAGE) currentTheme.primaryColor.copy(alpha = 0.3f) else Color(0x221A2840),
                                activeGlow = currentBg == GlobalBgMode.CUSTOM_IMAGE
                            )
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = currentTheme.accentColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(appString("upload_custom_image"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preset Dynamic Liquid Video Buttons
                Text(
                    text = appString("preset_dynamic_video"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        Pair(GlobalBgMode.PRESET_VIDEO_FLOW, "幻彩流体"),
                        Pair(GlobalBgMode.PRESET_VIDEO_AURORA, "极光夜色"),
                        Pair(GlobalBgMode.PRESET_VIDEO_COSMOS, "星云脉冲")
                    )

                    presets.forEach { (mode, title) ->
                        val active = currentBg == mode
                        Box(
                            modifier = Modifier
                                .liquidGlassCapsule(
                                    backgroundColor = if (active) currentTheme.primaryColor.copy(alpha = 0.35f) else Color(0x18182740),
                                    activeGlow = active
                                )
                                .clickable {
                                    ThemeManager.setPresetVideo(mode)
                                    Toast.makeText(context, "已应用预设流体背景：$title", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(title, color = if (active) currentTheme.primaryColor else Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Default Background Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCapsule(
                            backgroundColor = Color(0x18FFFFFF)
                        )
                        .clickable {
                            ThemeManager.resetToDefault()
                            Toast.makeText(context, "已恢复默认液态流光背景", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(appString("reset_default_bg"), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCapsule(
                            backgroundColor = currentTheme.primaryColor.copy(alpha = 0.8f),
                            activeGlow = true
                        )
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appString("action_ok"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
