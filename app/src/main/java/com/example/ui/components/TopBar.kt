package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CanvasAspectRatio
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.ThemeManager
import com.example.util.appString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    isGridVisible: Boolean,
    layerCount: Int,
    currentAspectRatio: CanvasAspectRatio = CanvasAspectRatio.SQUARE,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleGrid: () -> Unit,
    onToggleZoom: () -> Unit,
    onOpenLayers: () -> Unit,
    onAddText: (String) -> Unit,
    onOpenStickers: () -> Unit,
    onOpenShapes: () -> Unit,
    onOpenQuotes: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCheckUpdates: () -> Unit,
    onOpenAbout: () -> Unit,
    onPickImageFromGallery: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenPresets: () -> Unit,
    onResetCanvas: () -> Unit,
    onSetAspectRatio: ((CanvasAspectRatio) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddMenu by remember { mutableStateOf(false) }
    var showRatioMenu by remember { mutableStateOf(false) }
    val theme = ThemeManager.currentTheme

    // Liquid Glass Capsule floating Top Bar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .liquidGlassCapsule(
                backgroundColor = theme.surfaceColor.copy(alpha = 0.90f),
                borderWidth = 1.2.dp,
                activeGlow = false
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding Title: [T] Textoon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(theme.primaryColor, theme.accentColor)
                            )
                        )
                        .border(1.dp, Color(0x99FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = appString("app_name"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 1. [+] Menu (截图4: 包含文字/贴纸/形状以及设置与语言切换)
            Box {
                IconButton(
                    onClick = { showAddMenu = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(theme.primaryColor.copy(alpha = 0.22f))
                        .testTag("topbar_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add & More Menu",
                        tint = theme.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false },
                    modifier = Modifier
                        .background(Color(0xF50B1528))
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(theme.primaryColor.copy(alpha = 0.6f), GlassBorder)),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    DropdownMenuItem(
                        text = { Text(appString("menu_text"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            onAddText(com.example.util.LanguageManager.getString("new_text"))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(appString("menu_current_date"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                            onAddText(sdf.format(Date()))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(appString("menu_stickers"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            onOpenStickers()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(appString("menu_shapes"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.FormatQuote, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            onOpenShapes()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(appString("menu_from_gallery"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            onPickImageFromGallery()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(appString("menu_quotes"), color = Color.White, fontWeight = FontWeight.Medium) },
                        leadingIcon = { Icon(Icons.Default.FormatQuote, contentDescription = null, tint = theme.primaryColor) },
                        onClick = {
                            showAddMenu = false
                            onOpenQuotes()
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = Color(0x33FFFFFF)
                    )

                    // 截图4转移进来的功能：设置 (主题/上传背景)
                    DropdownMenuItem(
                        text = {
                            Text(
                                appString("menu_settings"),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = theme.primaryColor)
                        },
                        onClick = {
                            showAddMenu = false
                            onOpenSettings()
                        }
                    )

                    // 截图4转移进来的功能：语言切换
                    DropdownMenuItem(
                        text = {
                            Text(
                                appString("menu_language"),
                                color = theme.primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Language, contentDescription = null, tint = theme.primaryColor)
                        },
                        onClick = {
                            showAddMenu = false
                            onOpenLanguagePicker()
                        }
                    )

                    // 检查更新 (新版本检测)
                    DropdownMenuItem(
                        text = {
                            Text(
                                appString("menu_check_updates"),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = theme.accentColor)
                        },
                        onClick = {
                            showAddMenu = false
                            onOpenCheckUpdates()
                        }
                    )

                    // 关于 (作者信息/QQ联系)
                    DropdownMenuItem(
                        text = {
                            Text(
                                appString("menu_about"),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null, tint = theme.primaryColor)
                        },
                        onClick = {
                            showAddMenu = false
                            onOpenAbout()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(2.dp))

            // 2. 截图5: 保存功能 (Save / Export project)
            IconButton(
                onClick = onOpenExport,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_save_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Project",
                    tint = theme.primaryColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            // 3. 截图5: 画布比例功能 (Aspect Ratio)
            Box {
                IconButton(
                    onClick = {
                        if (onSetAspectRatio != null) {
                            showRatioMenu = true
                        } else {
                            onOpenExport()
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("topbar_ratio_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = "Aspect Ratio",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(19.dp)
                    )
                }

                DropdownMenu(
                    expanded = showRatioMenu,
                    onDismissRequest = { showRatioMenu = false },
                    modifier = Modifier
                        .background(Color(0xF50B1528))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                ) {
                    val ratios = listOf(
                        CanvasAspectRatio.SQUARE,
                        CanvasAspectRatio.STORY,
                        CanvasAspectRatio.PORTRAIT,
                        CanvasAspectRatio.LANDSCAPE,
                        CanvasAspectRatio.STANDARD,
                        CanvasAspectRatio.BANNER
                    )
                    ratios.forEach { ratio ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = ratio.label,
                                    color = if (ratio == currentAspectRatio) theme.primaryColor else Color.White,
                                    fontWeight = if (ratio == currentAspectRatio) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                showRatioMenu = false
                                onSetAspectRatio?.invoke(ratio)
                            }
                        )
                    }
                }
            }

            // 4. 截图5: 图层功能 (Layers with Badge)
            IconButton(
                onClick = onOpenLayers,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_layers_button")
            ) {
                BadgedBox(
                    badge = {
                        if (layerCount > 0) {
                            Badge(
                                containerColor = theme.primaryColor,
                                contentColor = Color.Black
                            ) {
                                Text("$layerCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Layers",
                        tint = if (layerCount > 0) theme.primaryColor else Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // 5. 撤销 (Undo)
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_undo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) Color.White else Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(19.dp)
                )
            }

            // 6. 重做 (Redo)
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_redo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) Color.White else Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(19.dp)
                )
            }

            // 7. 网格 (Grid)
            IconButton(
                onClick = onToggleGrid,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_grid_button")
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Toggle Grid",
                    tint = if (isGridVisible) theme.primaryColor else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(19.dp)
                )
            }

            // 8. 缩放还原 (Zoom)
            IconButton(
                onClick = onToggleZoom,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_zoom_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Toggle Zoom",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(19.dp)
                )
            }

            // 9. 分享 (Share)
            IconButton(
                onClick = onOpenExport,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("topbar_share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Image",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
