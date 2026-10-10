package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.liquidGlassCapsule
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.ThemeManager
import com.example.util.appString
import com.example.viewmodel.ColorPickerTarget

@Composable
fun ColorPickerDialog(
    target: ColorPickerTarget,
    currentColor: Long,
    onColorSelected: (Long) -> Unit,
    onGradientSelected: ((Long, Long) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val theme = ThemeManager.currentTheme
    val initialColor = Color(currentColor)
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val presetColors = listOf(
        0xFFFFFFFF, 0xFF000000, 0xFF00B4D8, 0xFF0077B6, 0xFF023E8A, 0xFF03045E,
        0xFFFFB703, 0xFFFB8500, 0xFFEF476F, 0xFFE63946, 0xFFD90429, 0xFF7209B7,
        0xFF560BAD, 0xFF3F37C9, 0xFF4361EE, 0xFF4CC9F0, 0xFF06D6A0, 0xFF2EC4B6,
        0xFF588157, 0xFF3A5A40, 0xFF343A40, 0xFF495057, 0xFF6C757D, 0xFFADB5BD
    )

    val presetGradients = listOf(
        Pair(0xFF00B4D8, 0xFF03045E),
        Pair(0xFFFFB703, 0xFFEF476F),
        Pair(0xFF7209B7, 0xFF4361EE),
        Pair(0xFF06D6A0, 0xFF0077B6),
        Pair(0xFFFF595E, 0xFFFFCA3A),
        Pair(0xFF1982C4, 0xFF8AC926),
        Pair(0xFF4A0E4E, 0xFF00B4D8),
        Pair(0xFF2B2D42, 0xFF8D99AE)
    )

    val activeColor = Color(red, green, blue, alpha)
    val activeColorLong = ((alpha * 255).toLong() shl 24) or
            ((red * 255).toLong() shl 16) or
            ((green * 255).toLong() shl 8) or
            (blue * 255).toLong()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = Color(0xF008101E),
                    borderWidth = 1.2.dp,
                    activeGlow = true
                ),
            color = Color.Transparent
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header (Capsule style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val title = when (target) {
                        ColorPickerTarget.TEXT_COLOR -> appString("action_color")
                        ColorPickerTarget.TEXT_STROKE -> appString("action_stroke")
                        ColorPickerTarget.TEXT_SHADOW -> appString("action_shadow")
                        ColorPickerTarget.TEXT_BG -> appString("action_bg")
                        ColorPickerTarget.TEXT_3D -> appString("action_3d")
                        ColorPickerTarget.CANVAS_BG -> appString("title_canvas_bg")
                        ColorPickerTarget.SHAPE_FILL -> appString("tool_fill")
                        ColorPickerTarget.SHAPE_STROKE -> appString("tool_outline")
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(activeColor)
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
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

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                val tabs = if (onGradientSelected != null) {
                    listOf(appString("tab_presets"), appString("action_edit"), appString("tool_gradient"))
                } else {
                    listOf(appString("tab_presets"), appString("action_edit"))
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = theme.primaryColor
                ) {
                    tabs.forEachIndexed { idx, tabTitle ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = {
                                Text(
                                    tabTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == idx) theme.primaryColor else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTab) {
                    0 -> {
                        // Presets Palette Grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(presetColors) { colLong ->
                                val c = Color(colLong)
                                val isSelected = (colLong and 0x00FFFFFF) == (activeColorLong and 0x00FFFFFF)

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) theme.primaryColor else Color(0x33FFFFFF),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onColorSelected(colLong)
                                            onDismiss()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (c.red > 0.5f && c.green > 0.5f) Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Custom RGBA Sliders
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            RgbaSlider(label = "R", value = red, onValueChange = { red = it }, trackColor = Color(0xFFFF5252))
                            RgbaSlider(label = "G", value = green, onValueChange = { green = it }, trackColor = Color(0xFF69F0AE))
                            RgbaSlider(label = "B", value = blue, onValueChange = { blue = it }, trackColor = Color(0xFF448AFF))
                            RgbaSlider(label = "A", value = alpha, onValueChange = { alpha = it }, trackColor = Color.White)

                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    onColorSelected(activeColorLong)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = theme.primaryColor,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(percent = 50)
                            ) {
                                Text(appString("action_apply"), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    2 -> {
                        // Gradients
                        if (onGradientSelected != null) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.height(200.dp)
                            ) {
                                items(presetGradients) { (start, end) ->
                                    Box(
                                        modifier = Modifier
                                            .height(48.dp)
                                            .liquidGlassCapsule(
                                                borderWidth = 1.dp
                                            )
                                            .background(
                                                brush = Brush.horizontalGradient(listOf(Color(start), Color(end))),
                                                shape = RoundedCornerShape(percent = 50)
                                            )
                                            .clickable {
                                                onGradientSelected(start, end)
                                                onDismiss()
                                            }
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

@Composable
private fun RgbaSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    trackColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = trackColor,
            fontSize = 12.sp,
            modifier = Modifier.width(16.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = trackColor,
                activeTrackColor = trackColor
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = (value * 255).toInt().toString(),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            modifier = Modifier.width(28.dp)
        )
    }
}
