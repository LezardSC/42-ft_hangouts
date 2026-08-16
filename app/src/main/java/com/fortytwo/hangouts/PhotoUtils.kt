package com.fortytwo.hangouts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView

object PhotoUtils {

    fun bindAvatar(imageView: ImageView, uriString: String?, displayName: String, reqSize: Int) {
        val photoBitmap = uriString?.let { decodeSampledBitmap(imageView.context, Uri.parse(it), reqSize) }

        val bitmap = if (photoBitmap != null) {
            circularBitmap(photoBitmap, reqSize)
        } else {
            initialsBitmap(imageView.context, displayName, reqSize)
        }
        imageView.setImageDrawable(BitmapDrawable(imageView.resources, bitmap))
    }

    fun takePersistablePermission(context: Context, uri: Uri) {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun releasePersistedPermission(context: Context, uriString: String?) {
        if (uriString == null) return
        runCatching {
            context.contentResolver.releasePersistableUriPermission(
                Uri.parse(uriString), Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun circularBitmap(source: Bitmap, size: Int): Bitmap {
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val shortSide = minOf(source.width, source.height).toFloat()
        val scale = size / shortSide
        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(
                (size - source.width * scale) / 2f,
                (size - source.height * scale) / 2f
            )
        }

        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
            setLocalMatrix(matrix)
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.shader = shader }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        return output
    }

    private fun initialsBitmap(context: Context, displayName: String, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val radius = size / 2f

        val containerColor = themeColor(context, R.attr.colorPrimaryContainer, Color.LTGRAY)
        val onContainerColor = themeColor(context, R.attr.colorOnPrimaryContainer, Color.DKGRAY)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = containerColor }
        canvas.drawCircle(radius, radius, radius, circlePaint)

        val initial = displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = onContainerColor
            textSize = size * 0.45f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val textY = radius - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(initial, radius, textY, textPaint)

        return bitmap
    }

    private fun themeColor(context: Context, attrRes: Int, fallback: Int): Int {
        val typedValue = TypedValue()
        return if (context.theme.resolveAttribute(attrRes, typedValue, true)) typedValue.data else fallback
    }

    private fun decodeSampledBitmap(context: Context, uri: Uri, reqSize: Int): Bitmap? = runCatching {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        var streamOpened = false
        resolver.openInputStream(uri)?.use { stream ->
            streamOpened = true
            BitmapFactory.decodeStream(stream, null, bounds)
        }
        if (!streamOpened) return@runCatching null

        var sampleSize = 1
        while (bounds.outHeight / (sampleSize * 2) >= reqSize && bounds.outWidth / (sampleSize * 2) >= reqSize) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        resolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }.getOrNull()
}
