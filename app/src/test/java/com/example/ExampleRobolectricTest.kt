package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CanvasAspectRatio
import com.example.model.CanvasProject
import com.example.model.TextElement
import com.example.util.AssetManagerHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PixelLab", appName)
    }

    @Test
    fun `preset templates loaded`() {
        val presets = AssetManagerHelper.getDefaultPresetTemplates()
        assertTrue(presets.isNotEmpty())
        assertEquals("Classic 3D", presets[0].name)
    }

    @Test
    fun `canvas project elements manipulation`() {
        val project = CanvasProject()
        assertTrue(project.elements.isNotEmpty())
        val textEl = project.elements.first() as TextElement
        assertEquals("PixelLab", textEl.text)
    }
}
