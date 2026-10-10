package com.example.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ShapeType
import com.example.ui.components.liquidGlassCapsule
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.ThemeManager
import com.example.util.appString
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapesDialog(
    onShapeSelected: (ShapeType) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = ThemeManager.currentTheme

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
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
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(theme.primaryColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = theme.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = appString("title_shapes"),
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

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ShapeType.entries) { shapeType ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .liquidGlassCapsule(
                                    backgroundColor = Color(0x18182740),
                                    borderWidth = 1.dp
                                )
                                .clickable {
                                    onShapeSelected(shapeType)
                                    onDismiss()
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(36.dp)) {
                                        val fill = theme.primaryColor
                                        val w = size.width
                                        val h = size.height

                                        when (shapeType) {
                                            ShapeType.RECTANGLE -> {
                                                drawRect(fill, size = Size(w, h))
                                            }
                                            ShapeType.ROUNDED_RECT -> {
                                                drawRoundRect(fill, cornerRadius = CornerRadius(8f, 8f), size = Size(w, h))
                                            }
                                            ShapeType.CIRCLE -> {
                                                drawCircle(fill, radius = w / 2f)
                                            }
                                            ShapeType.STAR -> {
                                                val path = Path()
                                                val radius = w / 2f
                                                val inner = radius * 0.45f
                                                var angle = -Math.PI / 2
                                                val step = Math.PI / 5
                                                path.moveTo((radius + radius * cos(angle)).toFloat(), (radius + radius * sin(angle)).toFloat())
                                                for (i in 0 until 5) {
                                                    angle += step
                                                    path.lineTo((radius + inner * cos(angle)).toFloat(), (radius + inner * sin(angle)).toFloat())
                                                    angle += step
                                                    path.lineTo((radius + radius * cos(angle)).toFloat(), (radius + radius * sin(angle)).toFloat())
                                                }
                                                path.close()
                                                drawPath(path, fill)
                                            }
                                            ShapeType.TRIANGLE -> {
                                                val path = Path()
                                                path.moveTo(w / 2f, 0f)
                                                path.lineTo(w, h)
                                                path.lineTo(0f, h)
                                                path.close()
                                                drawPath(path, fill)
                                            }
                                            ShapeType.HEART -> {
                                                val path = Path()
                                                path.moveTo(w / 2f, h * 0.8f)
                                                path.cubicTo(w * 0.1f, h * 0.5f, 0f, h * 0.2f, w * 0.25f, h * 0.1f)
                                                path.cubicTo(w * 0.45f, h * 0.05f, w / 2f, h * 0.3f, w / 2f, h * 0.3f)
                                                path.cubicTo(w / 2f, h * 0.3f, w * 0.55f, h * 0.05f, w * 0.75f, h * 0.1f)
                                                path.cubicTo(w, h * 0.2f, w * 0.9f, h * 0.5f, w / 2f, h * 0.8f)
                                                path.close()
                                                drawPath(path, fill)
                                            }
                                            ShapeType.HEXAGON -> {
                                                val path = Path()
                                                val r = w / 2f
                                                for (i in 0 until 6) {
                                                    val a = i * Math.PI / 3 - Math.PI / 6
                                                    val px = (r + r * cos(a)).toFloat()
                                                    val py = (r + r * sin(a)).toFloat()
                                                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                                                }
                                                path.close()
                                                drawPath(path, fill)
                                            }
                                            ShapeType.ARROW -> {
                                                val path = Path()
                                                path.moveTo(0f, h * 0.35f)
                                                path.lineTo(w * 0.6f, h * 0.35f)
                                                path.lineTo(w * 0.6f, h * 0.15f)
                                                path.lineTo(w, h * 0.5f)
                                                path.lineTo(w * 0.6f, h * 0.85f)
                                                path.lineTo(w * 0.6f, h * 0.65f)
                                                path.lineTo(0f, h * 0.65f)
                                                path.close()
                                                drawPath(path, fill)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = shapeType.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
