/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.gheovgos.webble.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.gheovgos.webble.R
import com.gheovgos.webble.presentation.theme.WebbleTheme
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }

        // Extarct .pbw file
        val outputDir = "${filesDir.absolutePath}/extracted"
        PbwExtractor.extractPbwFile(this, outputDir)

        // Verify the extracted file
        val extractedDir = File(outputDir)
        if (extractedDir.exists() && extractedDir.isDirectory) {
            Log.d("Extraction", "Files extracted to: $outputDir")
            val files = extractedDir.listFiles()
            files?.forEach { file ->
                Log.d("Extracted File", file.name)
            }
        } else {
            Log.e("Extraction", "Failed to extract files")
        }
    }

}

@Composable
fun WearApp(greetingName: String) {
    WebbleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}

object PbwExtractor {
    fun extractPbwFile(context: Context, outputDir: String) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.dino)
            val zipInput = ZipInputStream(inputStream)
            var entry: ZipEntry?

            val outputDirFile = File(outputDir)
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs()
            }

            while ((zipInput.nextEntry.also { entry = it }) != null) {
                val entryName = entry!!.name
                val outputFile = File(outputDir, entryName)

                if (entry!!.isDirectory) {
                    outputFile.mkdirs()
                    continue
                }

                val parent = outputFile.parentFile
                parent?.mkdirs()

                val fos = FileOutputStream(outputFile)
                val buffer = ByteArray(1024)
                var length: Int
                while ((zipInput.read(buffer).also { length = it }) > 0) {
                    fos.write(buffer, 0, length)
                }
                fos.close()
                zipInput.closeEntry()
            }
            zipInput.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
