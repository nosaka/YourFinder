package me.ns.yourfinder.util

import android.graphics.*


/**
 * Created by shintaro.nosaka on 2017/09/15.
 */
object BitmapUtil {

    fun createScaledBitmap(pathName: String, width: Int, height: Int): Bitmap {
        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.ARGB_8888
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, option)

        if (option.outWidth < width || option.outHeight < height) {
            // 縦、横のどちらかが指定値より小さい場合は普通にBitmap生成
            return BitmapFactory.decodeFile(pathName);
        }

        val scaleWidth = width / option.outWidth
        val scaleHeight = height / option.outHeight
        var newSize = 0
        var oldSize = 0
        if (scaleWidth > scaleHeight) {
            newSize = width
            oldSize = option.outWidth
        } else {
            newSize = height
            oldSize = option.outHeight
        }

        // option.inSampleSizeに設定する値を求める
        // option.inSampleSizeは2の乗数のみ設定可能
        var sampleSize = 1
        var tmpSize = oldSize
        while (tmpSize > newSize) {
            sampleSize *= 2
            tmpSize = oldSize / sampleSize
        }
        if (sampleSize != 1) {
            sampleSize /= 2
        }
        option.inJustDecodeBounds = false
        option.inSampleSize = sampleSize

        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathName, option), height, width, false)
    }

    fun createCircleBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val size: Int = (radius * 2).toInt()
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, size, size)

        // prepare canvas for transfer
        paint.isAntiAlias = true
        paint.color = 0xFFFFFFFF.toInt()
        paint.style = Paint.Style.FILL
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), (bitmap.height / 2).toFloat(), paint)


        // draw bitmap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}