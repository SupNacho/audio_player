package ru.supnacho.audioplayer

import android.content.Context
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.supnacho.audioplayer.domain.files.PathExtractor

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PathExtractorTest {

    private lateinit var context: Context

    @Before
    fun setUp(){
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testExtractFileDirectory(){
        val inputString = "content://com.android.externalstorage.documents/document/0E10-0E08%3AMusic%2FSound_14676.wav"
        val expectedString = "/storage/sdcard/Music/Sound_14676.wav"
        assertEquals(expectedString, PathExtractor.getPath(context, inputString.toUri()))
    }
}
