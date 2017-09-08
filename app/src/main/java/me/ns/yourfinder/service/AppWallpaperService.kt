package me.ns.yourfinder.service

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import me.ns.yourfinder.activity.MainActivity


/**
 * WallpaperService
 *
 * Created by shintaro.nosaka on 2017/09/07.
 */
class AppWallpaperService : WallpaperService() {

    private data class CircleConfig(internal val radius: Float = 120f) {

        companion object {
            val INTERVAL_RECEPTION_DOUBLE_TOUCH = 300L
        }

        private val halfRadius: Float = radius / 2

        internal val paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            setShadowLayer(8f, 0f, 0f, Color.GRAY)
        }
        internal val textPaint = Paint().apply {
            color = Color.GRAY
            textSize = 32f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        internal var x: Float = 0f
            set(value) {
                top = value + halfRadius
                bottom = value - halfRadius
                field = value
            }
        internal var y: Float = 0f
            set(value) {
                left = value + halfRadius
                right = value - halfRadius
                field = value
            }
        private var top: Float = 0f
        private var bottom: Float = 0f
        private var left: Float = 0f
        private var right: Float = 0f

        internal var touched: Boolean = false
        internal var isDoubleTouch: Boolean = false
        internal fun checkTouched(actionDown: MotionEvent) {
            touched = actionDown.x in bottom..top && actionDown.y in right..left
        }

        internal fun startDoubleTouchReception() {
            if (touched) {
                if (!isDoubleTouch) {
                    isDoubleTouch = true
                    Handler().postDelayed({
                        isDoubleTouch = false
                    }, INTERVAL_RECEPTION_DOUBLE_TOUCH)
                }
            } else {
                isDoubleTouch = false
            }
        }

        internal fun updatePosition(in_x: Float, in_y: Float) {
            x = in_x
            y = in_y
        }
    }

    inner class LiveEngine : Engine() {

        private val INFLATE_SIZE = 16

        private val touchedPaint = Paint().apply {
            color = Color.LTGRAY
            isAntiAlias = true
            setShadowLayer(5f, 0f, 0f, Color.GRAY)
        }

        private var mCircleConfigs = ArrayList<CircleConfig>()

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            mCircleConfigs.add(CircleConfig(120f))
            mCircleConfigs.add(CircleConfig(120f))
            mCircleConfigs.add(CircleConfig(120f))
            mCircleConfigs.add(CircleConfig(120f))
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame(true)
        }

        private fun drawFrame(init: Boolean = false) {
            surfaceHolder.lockCanvas().apply {
                try {
                    drawColor(Color.WHITE)

                    mCircleConfigs.forEach {
                        if (init) {
                            it.apply {
                                x = (width / 2).toFloat()
                                y = (height / 2).toFloat()
                            }
                        }
                        it.apply {
                            drawCircle(x, y, if (touched) radius + INFLATE_SIZE else radius, if (touched) touchedPaint else paint)
                            val text = "洗剤"
                            val max: Int = if (radius / textPaint.textSize > text.length) {
                                text.length
                            } else {
                                (radius / textPaint.textSize).toInt()
                            }
                            val textBounds = Rect()
                            textPaint.getTextBounds(text, 0, max, textBounds);
                            drawText(text, 0, max, x, y - textBounds.exactCenterY(), textPaint)
                        }
                    }

                } finally {
                    surfaceHolder.unlockCanvasAndPost(this)
                }
            }
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        var touch = false
                        mCircleConfigs.forEach {
                            if (touch) {
                                // 既にタッチ済みの場合は他オブジェクトの処理をスキップする
                                return@forEach
                            }
                            it.checkTouched(actionDown = event)
                            touch = it.touched
                            when {
                                it.isDoubleTouch -> {
                                    startActivity(Intent(this@AppWallpaperService, MainActivity::class.java))
//                                    AlertDialog.Builder(this@AppWallpaperService)
//                                            .setView(EditText(this@AppWallpaperService))
//                                            .setPositiveButton("設定", { _, _ ->
//
//                                            })
//                                            .setPositiveButton("変更", { _, _ ->
//
//                                            })
//                                            .setNegativeButton("キャンセル", null)
//                                            .show()
                                }
                                else -> {
                                }
                            }
                            it.startDoubleTouchReception()
                        }

                    }
                    MotionEvent.ACTION_MOVE -> {
                        mCircleConfigs.forEach {
                            it.apply {
                                if (touched) {
                                    it.updatePosition(in_x = event.x, in_y = event.y)
                                    return@forEach
                                }
                            }
                        }
                        drawFrame()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        mCircleConfigs.forEach {
                            it.touched = false
                        }
                        drawFrame()
                    }
                    else -> {
                        // 処理なし
                    }
                }
            }
        }

    }


    override fun onCreateEngine(): Engine = LiveEngine()


}

