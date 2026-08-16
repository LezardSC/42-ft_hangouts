package com.fortytwo.hangouts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.color.MaterialColors

object PhotoUtils {

    fun bindAvatar(imageView: ImageView, uriString: String?, displayName: String, reqSize: Int) {
        val photoBitmap = uriString?.let { decodeSampledBitmap(imageView.context, Uri.parse(it), reqSize) }

        val drawable = if (photoBitmap != null) {
            RoundedBitmapDrawableFactory.create(imageView.resources, photoBitmap).apply { isCircular = true }
        } else {
            BitmapDrawable(imageView.resources, initialsBitmap(imageView.context, displayName, reqSize))
        }
        imageView.setImageDrawable(drawable)
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

    private fun initialsBitmap(context: Context, displayName: String, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val radius = size / 2f

        val containerColor = MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorPrimaryContainer, Color.LTGRAY
        )
        val onContainerColor = MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorOnPrimaryContainer, Color.DKGRAY
        )

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
