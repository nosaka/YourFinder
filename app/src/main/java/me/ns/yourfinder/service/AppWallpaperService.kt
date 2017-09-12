package me.ns.yourfinder.service

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import me.ns.yourfinder.activity.TransientActivity
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.data.FinderDao


/**
 * WallpaperService
 *
 * Created by shintaro.nosaka on 2017/09/07.
 */
class AppWallpaperService : WallpaperService() {

    private data class FinderConfig(var finder: Finder) {

        companion object {
            val INTERVAL_RECEPTION_DOUBLE_TOUCH = 300L
        }

        internal val radius: Float = 120f
        private val halfRadius: Float = radius / 2

        internal var x: Float = 0.0f
            set(value) {
                top = value + halfRadius
                bottom = value - halfRadius
                field = value
            }
        internal var y: Float = 0.0f
            set(value) {
                left = value + halfRadius
                right = value - halfRadius
                field = value
            }

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


        private var top: Float = 0f
        private var bottom: Float = 0f
        private var left: Float = 0f
        private var right: Float = 0f

        internal var touched: Boolean = false

        internal var isDoubleTouch: Boolean = false

        init {
            x = finder.x
            y = finder.y
        }

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

        private var mFinderConfigs = ArrayList<FinderConfig>()

        private lateinit var finderDao: FinderDao

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            finderDao = AppDatabase.getInMemoryDatabase(this@AppWallpaperService).finderDao()
            val finders = finderDao.all()
            finders.forEach {
                val config = FinderConfig(finder = it)
                mFinderConfigs.add(config)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame(init = true)
        }

        private fun drawFrame(init: Boolean = false, commit: Boolean = false) {
            surfaceHolder.lockCanvas().apply {
                try {
                    drawColor(Color.WHITE)

                    mFinderConfigs.forEach {
                        if (init && it.finder.init) {
                            it.apply {
                                x = (width / 2).toFloat()
                                y = (height / 2).toFloat()
                                it.finder.x = x
                                it.finder.y = y
                            }
                            it.finder.init = false
                            finderDao.update(it.finder)
                        }
                        it.apply {
                            drawCircle(x, y, if (touched) radius + INFLATE_SIZE else radius, if (touched) touchedPaint else paint)
                            val text = finder.name
                            val max: Int = if (radius / textPaint.textSize > text?.length ?: 0) {
                                text!!.length
                            } else {
                                (radius / textPaint.textSize).toInt()
                            }
                            val textBounds = Rect()
                            textPaint.getTextBounds(text, 0, max, textBounds);
                            drawText(text, 0, max, x, y - textBounds.exactCenterY(), textPaint)
                        }
                        if (commit) {
                            it.finder.x = it.x
                            it.finder.y = it.y

                            finderDao.update(it.finder)
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
                        mFinderConfigs.forEach {
                            if (touch) {
                                // 既にタッチ済みの場合は他オブジェクトの処理をスキップする
                                return@forEach
                            }
                            it.checkTouched(actionDown = event)
                            touch = it.touched
                            when {
                                it.isDoubleTouch -> {
                                    if (it.finder.id != null) startActivity(TransientActivity.intent(this@AppWallpaperService, it.finder.id!!))
                                }
                                else -> {
                                }
                            }
                            it.startDoubleTouchReception()
                        }

                    }
                    MotionEvent.ACTION_MOVE -> {
                        mFinderConfigs.forEach {
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
                        mFinderConfigs.forEach {
                            it.touched = false
                        }
                        drawFrame(commit = true)
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

