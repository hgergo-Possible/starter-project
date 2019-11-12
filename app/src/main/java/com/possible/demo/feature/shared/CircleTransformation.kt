package com.possible.demo.feature.shared

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.squareup.picasso.Transformation
import kotlin.math.min

/**
 * [com.squareup.picasso.Picasso] Transformation copping an image to be circular.
 *
 * It first crops the image to be squared then crops it to be a circle.
 */
class CircleTransformation : Transformation {

    override fun key(): String = "circleTransformation"

    override fun transform(source: Bitmap): Bitmap {
        val squareSize = min(source.width, source.height)

        val pivotX = (source.width - squareSize) / 2
        val pivotY = (source.height - squareSize) / 2
        val squaredBitmap = if (source.width == source.height) {
            source
        } else {
            val squaredBitmap = Bitmap.createBitmap(source, pivotX, pivotY, squareSize, squareSize)
            source.recycle()
            return squaredBitmap
        }

        val resultBitmap = Bitmap.createBitmap(squareSize, squareSize, source.config)

        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val radius = squareSize / 2f
        val centerXY = squareSize / 2f
        canvas.drawCircle(centerXY, centerXY, radius, paint)

        squaredBitmap.recycle()
        return resultBitmap
    }

}