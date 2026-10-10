package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import com.example.model.BackgroundAssetInfo
import com.example.model.BackgroundType
import com.example.model.CanvasAspectRatio
import com.example.model.CanvasProject
import com.example.model.FontInfo
import com.example.model.QuoteCategoryInfo
import com.example.model.ShapeElement
import com.example.model.ShapeType
import com.example.model.StickerAssetInfo
import com.example.model.TextElement
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

object AssetManagerHelper {

    private val typefaceCache = ConcurrentHashMap<String, Typeface>()
    private val bitmapCache = ConcurrentHashMap<String, Bitmap>()

    fun getTypeface(context: Context, assetPath: String?): Typeface {
        if (assetPath.isNullOrBlank()) {
            return Typeface.DEFAULT
        }
        return typefaceCache.getOrPut(assetPath) {
            try {
                Typeface.createFromAsset(context.assets, assetPath)
            } catch (e: Exception) {
                Typeface.DEFAULT
            }
        }
    }

    fun getBitmapFromAsset(context: Context, assetPath: String): Bitmap? {
        return bitmapCache.getOrPut(assetPath) {
            try {
                context.assets.open(assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)
                } ?: return null
            } catch (e: Exception) {
                return null
            }
        }
    }

    fun loadFonts(context: Context): List<FontInfo> {
        val list = mutableListOf<FontInfo>()
        val categories = listOf("Basic", "Fancy", "Poster", "non-Latin")
        val am = context.assets

        for (category in categories) {
            try {
                val files = am.list("fonts/$category") ?: emptyArray()
                for (file in files) {
                    if (file.endsWith(".ttf", ignoreCase = true) || file.endsWith(".otf", ignoreCase = true)) {
                        val displayName = file.substringBeforeLast(".")
                            .replace("_", " ")
                            .replace("-", " ")
                        list.add(
                            FontInfo(
                                assetPath = "fonts/$category/$file",
                                name = displayName,
                                category = category
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore missing directory
            }
        }
        return list
    }

    fun loadBackgroundPresets(context: Context): List<BackgroundAssetInfo> {
        val list = mutableListOf<BackgroundAssetInfo>()
        val am = context.assets
        try {
            val folders = am.list("bg") ?: emptyArray()
            for (folder in folders) {
                try {
                    val files = am.list("bg/$folder") ?: emptyArray()
                    val fullImage = files.firstOrNull { it.startsWith("1.") }
                    val thumb = files.firstOrNull { it.startsWith("thumb.") } ?: fullImage

                    if (fullImage != null) {
                        val cleanTitle = folder.replaceFirst(Regex("^\\d+\\."), "")
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        list.add(
                            BackgroundAssetInfo(
                                folder = folder,
                                imagePath = "bg/$folder/$fullImage",
                                thumbPath = "bg/$folder/${thumb ?: fullImage}",
                                title = cleanTitle
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Ignore folder error
                }
            }
        } catch (e: Exception) {
            // Ignore bg list error
        }
        return list
    }

    fun loadStickerCategories(context: Context): Map<String, List<StickerAssetInfo>> {
        val result = mutableMapOf<String, MutableList<StickerAssetInfo>>()
        val am = context.assets

        // 1. Load stickers/
        try {
            val stickerFolders = am.list("stickers") ?: emptyArray()
            for (folder in stickerFolders) {
                try {
                    val files = am.list("stickers/$folder") ?: emptyArray()
                    val categoryName = mapStickerFolderToCategory(folder)
                    for (file in files) {
                        if (file.endsWith(".png", ignoreCase = true) && !file.equals("thumb.png", ignoreCase = true)) {
                            val info = StickerAssetInfo(
                                assetPath = "stickers/$folder/$file",
                                category = categoryName,
                                name = "$folder $file"
                            )
                            result.getOrPut(categoryName) { mutableListOf() }.add(info)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        // 2. Load emojis/
        try {
            val emojiFolders = am.list("emojis") ?: emptyArray()
            for (folder in emojiFolders) {
                try {
                    val files = am.list("emojis/$folder") ?: emptyArray()
                    for (file in files) {
                        if (file.endsWith(".png", ignoreCase = true)) {
                            val info = StickerAssetInfo(
                                assetPath = "emojis/$folder/$file",
                                category = "Emojis",
                                name = "$folder $file"
                            )
                            result.getOrPut("Emojis") { mutableListOf() }.add(info)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        return result
    }

    private fun mapStickerFolderToCategory(folder: String): String {
        return when {
            folder.startsWith("emoji") -> "Emojis"
            folder.contains("hat") -> "Hats & Accessories"
            folder.contains("hand") -> "Gestures"
            folder.contains("heart") -> "Hearts"
            folder.contains("ribbon") -> "Ribbons"
            folder.contains("stamp") -> "Badges & Stamps"
            folder.contains("star") -> "Stars"
            folder.contains("mustache") -> "Faces & Props"
            folder.contains("postit") -> "Post-it Notes"
            folder.contains("bubble") -> "Speech Bubbles"
            folder.contains("cake") || folder.contains("cupcake") -> "Celebration"
            else -> "Decorations"
        }
    }

    fun loadQuotes(context: Context): List<QuoteCategoryInfo> {
        val list = mutableListOf<QuoteCategoryInfo>()
        val am = context.assets

        // English quotes
        try {
            val enFiles = am.list("quotes/en") ?: emptyArray()
            for (file in enFiles) {
                try {
                    val content = am.open("quotes/en/$file").bufferedReader().use { it.readText() }
                    val quotesArray = JSONObject(content).optJSONArray("quotes")
                    if (quotesArray != null) {
                        val quotes = (0 until quotesArray.length()).map { quotesArray.getString(it) }
                        val cleanTitle = file.replaceFirst(Regex("^\\d+\\."), "")
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        list.add(
                            QuoteCategoryInfo(
                                id = "en_$file",
                                title = cleanTitle,
                                language = "English",
                                quotes = quotes
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        // French quotes
        try {
            val frContent = am.open("quotes/fr/tous").bufferedReader().use { it.readText() }
            val quotesArray = JSONObject(frContent).optJSONArray("quotes")
            if (quotesArray != null) {
                val quotes = (0 until quotesArray.length()).map { quotesArray.getString(it) }
                list.add(
                    QuoteCategoryInfo(
                        id = "fr_tous",
                        title = "Citations Françaises",
                        language = "Français",
                        quotes = quotes
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore
        }

        return list
    }

    fun getDefaultPresetTemplates(): List<CanvasProject> {
        return listOf(
            CanvasProject(
                name = "Classic 3D",
                aspectRatio = CanvasAspectRatio.SQUARE,
                backgroundType = BackgroundType.SOLID,
                bgColor = 0xFF192A56,
                elements = listOf(
                    TextElement(
                        text = "PixelLab",
                        fontSize = 58f,
                        fontAssetPath = "fonts/Basic/Boogaloo-Regular.otf",
                        textColor = 0xFF00D2D3,
                        threeDEnabled = true,
                        threeDDepth = 18f,
                        threeDColor = 0xFF0B5345,
                        shadowEnabled = true,
                        shadowColor = 0x99000000,
                        shadowRadius = 16f,
                        isBold = true
                    )
                )
            ),
            CanvasProject(
                name = "Neon Cyber",
                aspectRatio = CanvasAspectRatio.SQUARE,
                backgroundType = BackgroundType.SOLID,
                bgColor = 0xFF0F172A,
                bgVignette = 0.5f,
                bgVignetteColor = 0xFF000000,
                elements = listOf(
                    TextElement(
                        text = "NEON GLOW",
                        fontSize = 50f,
                        fontAssetPath = "fonts/Basic/Days.otf",
                        textColor = 0xFFFF007F,
                        strokeEnabled = true,
                        strokeColor = 0xFFFFFFFF,
                        strokeWidth = 3f,
                        shadowEnabled = true,
                        shadowColor = 0xFFFF007F,
                        shadowRadius = 24f,
                        isBold = true
                    )
                )
            ),
            CanvasProject(
                name = "Paper Note",
                aspectRatio = CanvasAspectRatio.SQUARE,
                backgroundType = BackgroundType.ASSET,
                bgAssetPath = "bg/1.paper/1.png",
                elements = listOf(
                    TextElement(
                        text = "Creative Notes",
                        fontSize = 44f,
                        fontAssetPath = "fonts/Fancy/Calligraffiti.ttf",
                        textColor = 0xFF2D3436,
                        isBold = false
                    )
                )
            ),
            CanvasProject(
                name = "Bold Meme",
                aspectRatio = CanvasAspectRatio.SQUARE,
                backgroundType = BackgroundType.SOLID,
                bgColor = 0xFF000000,
                elements = listOf(
                    TextElement(
                        text = "TOP TEXT",
                        y = -180f,
                        fontSize = 54f,
                        fontAssetPath = "fonts/Poster/AlfaSlabOne-Regular.ttf",
                        textColor = 0xFFFFFFFF,
                        strokeEnabled = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 6f,
                        isBold = true
                    ),
                    TextElement(
                        text = "BOTTOM TEXT",
                        y = 180f,
                        fontSize = 54f,
                        fontAssetPath = "fonts/Poster/AlfaSlabOne-Regular.ttf",
                        textColor = 0xFFFFFFFF,
                        strokeEnabled = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 6f,
                        isBold = true
                    )
                )
            ),
            CanvasProject(
                name = "Keep Calm",
                aspectRatio = CanvasAspectRatio.PORTRAIT,
                backgroundType = BackgroundType.SOLID,
                bgColor = 0xFFB71540,
                elements = listOf(
                    ShapeElement(
                        shapeType = ShapeType.STAR,
                        y = -220f,
                        width = 80f,
                        height = 80f,
                        fillColor = 0xFFFFFFFF
                    ),
                    TextElement(
                        text = "KEEP CALM\nAND\nCREATE ART",
                        y = 40f,
                        fontSize = 46f,
                        fontAssetPath = "fonts/Basic/Comfortaa_Regular.ttf",
                        textColor = 0xFFFFFFFF,
                        lineSpacing = 16f,
                        alignment = 1,
                        isBold = true
                    )
                )
            ),
            CanvasProject(
                name = "Logo Transparent",
                aspectRatio = CanvasAspectRatio.SQUARE,
                backgroundType = BackgroundType.TRANSPARENT,
                elements = listOf(
                    ShapeElement(
                        shapeType = ShapeType.CIRCLE,
                        width = 240f,
                        height = 240f,
                        fillColor = 0xFF00B4D8,
                        strokeColor = 0xFFFFFFFF,
                        strokeWidth = 8f
                    ),
                    TextElement(
                        text = "P",
                        fontSize = 80f,
                        fontAssetPath = "fonts/Basic/Chunkfive.otf",
                        textColor = 0xFFFFFFFF,
                        isBold = true
                    )
                )
            )
        )
    }
}
