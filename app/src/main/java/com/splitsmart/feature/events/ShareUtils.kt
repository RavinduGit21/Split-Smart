package com.splitsmart.feature.events

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import android.widget.Toast
import androidx.core.view.drawToBitmap
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtils {

	fun shareReportBitmap(context: Context, bitmap: Bitmap) {
		val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
		val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		val outFile = File(cacheDir, "event_report_${time}.png")
		try {
			FileOutputStream(outFile).use { fos ->
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
			}
			val uri: Uri = FileProvider.getUriForFile(context, "com.splitsmart.fileprovider", outFile)
			val share = Intent(Intent.ACTION_SEND).apply {
				type = "image/png"
				putExtra(Intent.EXTRA_STREAM, uri)
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			}
			// If context is not an Activity, add NEW_TASK
			if (context !is Activity) {
				share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			}
			context.startActivity(Intent.createChooser(share, "Share Event Report").apply {
				if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			})
		} catch (t: Throwable) {
			Toast.makeText(context, "Failed to share report: ${'$'}{t.message}", Toast.LENGTH_LONG).show()
		}
	}

	fun renderComposableToBitmap(context: Context, widthPx: Int, heightPx: Int, content: @Composable () -> Unit): Bitmap {
		return try {
			val composeView = ComposeView(context).apply {
				setContent(content)
				val widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(widthPx.coerceAtLeast(1), android.view.View.MeasureSpec.EXACTLY)
				val heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(10_000, android.view.View.MeasureSpec.AT_MOST)
				measure(widthSpec, heightSpec)
				layout(0, 0, measuredWidth.coerceAtLeast(1), measuredHeight.coerceAtLeast(1))
			}
			val outWidth = composeView.measuredWidth.coerceIn(1, 4096)
			val outHeight = composeView.measuredHeight.coerceIn(1, 6000)
			val bmp = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
			val canvas = Canvas(bmp)
			composeView.draw(canvas)
			bmp
		} catch (t: Throwable) {
			// Fallback tiny bitmap with error
			val fallback = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
			fallback.eraseColor(0xFFFFFFFF.toInt())
			fallback
		}
	}

	fun captureActivityBitmap(activity: Activity): Bitmap? = try {
		val root = activity.window?.decorView?.rootView ?: return null
		root.drawToBitmap(Bitmap.Config.ARGB_8888)
	} catch (t: Throwable) { null }

	fun renderInActivity(
		activity: Activity,
		widthPx: Int,
		content: @Composable () -> Unit,
		onReady: (Bitmap) -> Unit
	) {
		val container = FrameLayout(activity).apply {
			layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			alpha = 0f // invisible host
		}
		val root = activity.findViewById<ViewGroup>(android.R.id.content)
		root.addView(container)
		val composeView = ComposeView(activity)
		container.addView(composeView)
		composeView.setContent(content)
		container.doOnPreDraw {
			try {
				val widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(widthPx.coerceAtLeast(1), android.view.View.MeasureSpec.EXACTLY)
				val heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(12_000, android.view.View.MeasureSpec.AT_MOST)
				container.measure(widthSpec, heightSpec)
				container.layout(0, 0, container.measuredWidth, container.measuredHeight)
				val bmp = container.drawToBitmap(Bitmap.Config.ARGB_8888)
				onReady(bmp)
			} catch (t: Throwable) {
				Toast.makeText(activity, "Render failed: ${'$'}{t.message}", Toast.LENGTH_LONG).show()
			} finally {
				root.removeView(container)
			}
		}
	}
}


