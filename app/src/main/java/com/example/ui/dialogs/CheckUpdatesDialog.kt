package com.example.ui.dialogs

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.liquidGlassCapsule
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.ThemeManager
import com.example.util.appString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Composable
fun CheckUpdatesDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = ThemeManager.currentTheme
    val scope = rememberCoroutineScope()

    var isChecking by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("点击下方按钮检测仓库最新版本并直接安装 APK") }

    fun downloadAndInstallApk() {
        isDownloading = true
        statusMessage = "正在拉取最新版本 APK 并发起安装..."
        Toast.makeText(context, "正在从 shuting52/Textoon 拉取最新 APK...", Toast.LENGTH_SHORT).show()

        scope.launch {
            try {
                val apkUrl = "https://github.com/shuting52/Textoon/releases/latest/download/app-release.apk"
                val downloadIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(apkUrl), "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(downloadIntent)
                Toast.makeText(context, "已拉取 APK，请在弹出的系统安装器中确认替换安装！", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Fallback to browser releases page
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shuting52/Textoon/releases"))
                    browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(browserIntent)
                    Toast.makeText(context, "正在通过浏览器下载最新版 APK 进行安装替换", Toast.LENGTH_SHORT).show()
                } catch (e2: Exception) {
                    Toast.makeText(context, "拉取失败，请检查网络连接", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isDownloading = false
                statusMessage = "APK 拉取指令已下发，请完成安装替换"
            }
        }
    }

    fun checkAndInstall() {
        if (isChecking || isDownloading) return
        isChecking = true
        statusMessage = "正在检测 shuting52/Textoon 仓库更新内容..."

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build()
                    val req = Request.Builder()
                        .url("https://api.github.com/repos/shuting52/Textoon/releases/latest")
                        .header("Accept", "application/vnd.github.v3+json")
                        .build()
                    client.newCall(req).execute()
                }
            } catch (e: Exception) {
                // Continue to download even if rate limited or network issue
            } finally {
                isChecking = false
                downloadAndInstallApk()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = Color(0xF208101E),
                    borderWidth = 1.2.dp,
                    activeGlow = true
                ),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Title and Close button
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
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = currentTheme.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = appString("title_check_updates"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Center Icon with Pulse / Loading
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(currentTheme.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecking || isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = currentTheme.primaryColor,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = currentTheme.primaryColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Text
                Text(
                    text = statusMessage,
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ONLY ONE BUTTON: 检查更新 (拉取最新版本APK并安装)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCapsule(
                            backgroundColor = currentTheme.primaryColor,
                            activeGlow = true
                        )
                        .clickable { checkAndInstall() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isChecking || isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = if (isDownloading) "正在拉取 APK..." else if (isChecking) "正在检测更新..." else "检查更新",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
