package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.StickerAssetInfo
import com.example.ui.components.liquidGlassCapsule
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.ThemeManager
import com.example.util.AssetManagerHelper
import com.example.util.appString

@Composable
fun StickersSheet(
    stickerCategories: Map<String, List<StickerAssetInfo>>,
    onStickerSelected: (StickerAssetInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val categoryNames = remember(stickerCategories) { stickerCategories.keys.toList() }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val theme = ThemeManager.currentTheme

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = Color(0xF008101E),
                    borderWidth = 1.2.dp,
                    activeGlow = true
                ),
            color = Color.Transparent
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header (Capsule style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(theme.primaryColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = null,
                            tint = theme.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = appString("title_stickers"),
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

                Spacer(modifier = Modifier.height(10.dp))

                if (categoryNames.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedCategoryIndex.coerceIn(0, categoryNames.lastIndex),
                        edgePadding = 8.dp,
                        containerColor = Color.Transparent,
                        contentColor = theme.primaryColor
                    ) {
                        categoryNames.forEachIndexed { index, name ->
                            val isSelected = selectedCategoryIndex == index
                            Tab(
                                selected = isSelected,
                                onClick = { selectedCategoryIndex = index },
                                text = {
                                    Text(
                                        text = name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val currentCategoryName = categoryNames.getOrNull(selectedCategoryIndex)
                val currentStickers = stickerCategories[currentCategoryName] ?: emptyList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentStickers) { item ->
                        val bitmap = remember(item.assetPath) {
                            AssetManagerHelper.getBitmapFromAsset(context, item.assetPath)
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .liquidGlassCapsule(
                                    backgroundColor = Color(0x18182740),
                                    borderWidth = 1.dp
                                )
                                .clickable {
                                    onStickerSelected(item)
                                    onDismiss()
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = item.assetPath,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
