package me.ns.yourfinder.service

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.data.FinderDao
import me.ns.yourfinder.util.BitmapUtil


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

        internal var radius: Float = 120f

        internal var x: Float = 0.0f
            set(value) {
                top = value + radius
                bottom = value - radius
                field = value
            }
        internal var y: Float = 0.0f
            set(value) {
                left = value + radius
                right = value - radius
                field = value
            }

        internal var bitmap: Bitmap? = null

        internal var top: Float = 0f
            get() = field
        internal var bottom: Float = 0f
            get() = field
        internal var left: Float = 0f
            get() = field
        internal var right: Float = 0f
            get() = field

        internal var touched: Boolean = false

        internal var isDoubleTouch: Boolean = false

        init {
            x = finder.x
            y = finder.y
            val size = (radius * 2).toInt()
            finder.iconUrl?.let {
                val scaledBitmap = BitmapUtil.createScaledBitmap(it, size, size)
                bitmap = BitmapUtil.createCircleBitmap(scaledBitmap, radius)
            }
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

        private val INFLATE_RADIUS = 24f

        private val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            setShadowLayer(8f, 0f, 0f, Color.GRAY)
        }

        private val touchedPaint = Paint().apply {
            isAntiAlias = true
            color = Color.LTGRAY
            setShadowLayer(8f, 0f, 0f, Color.GRAY)
        }


        private val textPaint = Paint().apply {
            color = Color.GRAY
            textSize = 32f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        private var finderConfigs = ArrayList<FinderConfig>()

        private lateinit var finderDao: FinderDao

        private lateinit var wallpaperManager: WallpaperManager

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            wallpaperManager = WallpaperManager.getInstance(this@AppWallpaperService)

            finderDao = AppDatabase.getInMemoryDatabase(this@AppWallpaperService).finderDao()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            initializeFinderConfigs()
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                initializeFinderConfigs()
                drawFrame()
            }
        }

        private fun initializeFinderConfigs() {
            finderConfigs.clear()
            val finders = finderDao.all()
            finders.forEach {
                val config = FinderConfig(finder = it)
                finderConfigs.add(config)
            }

            val centerX = wallpaperManager.desiredMinimumHeight.toFloat() / 2
            val centerY = wallpaperManager.desiredMinimumWidth.toFloat() / 2

            finderConfigs.forEach {
                if (it.finder.init) {
                    it.apply {
                        x = centerX
                        y = centerY
                        it.finder.x = x
                        it.finder.y = y
                    }
                    it.finder.init = false
                    finderDao.update(it.finder)
                }
            }
        }

        private fun recordFinderConfigsCoordinate() {
            finderConfigs.forEach {
                it.finder.x = it.x
                it.finder.y = it.y
            }
            val finders = finderConfigs.map { it.finder }
            finderDao.update(finders)
        }

        private fun drawFrame() {
            surfaceHolder.lockCanvas().apply {
                try {
                    drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    finderConfigs.forEach {
                        it.apply {

                            val drawDiameter = if (touched) (radius + INFLATE_RADIUS) * 2 else radius * 2
                            val drawRadius = if (touched) radius + INFLATE_RADIUS else radius
                            if (bitmap != null) {
                                // 画像設定時
                                bitmap?.let {
                                    val dst = RectF(0f, 0f, drawDiameter, drawDiameter)
                                    dst.offset((x - drawRadius), (y - drawRadius))
                                    val src = Rect(0, 0, it.width, it.height)

                                    drawBitmap(it, src, dst, if (touched) touchedPaint else paint)
                                }
                            } else {
                                // 画像未設定時
                                drawCircle(x, y, drawRadius, if (touched) touchedPaint else paint)
                            }

                            val text = finder.name ?: ""
                            val max: Int = if (drawDiameter / textPaint.textSize > text.length) {
                                text.length
                            } else {
                                (drawDiameter / textPaint.textSize).toInt()
                            }
                            val textBounds = Rect()
                            textPaint.getTextBounds(text, 0, max, textBounds)
                            drawText(text, 0, max, x, y - textBounds.exactCenterY(), textPaint)

                        }
                    }
                    finderConfigs.forEach {
                        it.apply {

                        }
                    }
                } finally {
                    surfaceHolder.unlockCanvasAndPost(this)
                }
            }
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            var redraw = false
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        var touch = false
                        finderConfigs.forEach {
                            if (touch) {
                                // 既にタッチ済みの場合は他オブジェクトの処理をスキップする
                                return@forEach
                            }
                            it.checkTouched(actionDown = event)
                            touch = it.touched
//                            when {
//                                it.isDoubleTouch -> {
//                                    if (it.finder.id != null) startActivity(TransientActivity.intent(this@AppWallpaperService, it.finder.id!!))
//                                }
//                                else -> {
//                                }
//                            }
                            it.startDoubleTouchReception()
                        }

                        finderConfigs.sortWith(compareBy({ it.touched }, { it.finder.id }))

                    }
                    MotionEvent.ACTION_MOVE -> {
                        finderConfigs.forEach {
                            it.apply {
                                if (touched) {
                                    redraw = true
                                    it.updatePosition(in_x = event.x, in_y = event.y)
                                    return@forEach
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        finderConfigs.forEach {
                            redraw = redraw or it.touched
                            it.touched = false
                        }
                        recordFinderConfigsCoordinate()
                    }
                    else -> {
                        // 処理なし
                    }
                }
            }
            if (redraw) {
                drawFrame()
            }

        }

    }

    override fun onCreateEngine(): Engine = LiveEngine()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

}

