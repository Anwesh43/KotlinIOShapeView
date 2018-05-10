package com.example.ioview

/**
 * Created by anweshmishra on 10/05/18.
 */
import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*

class IOView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }

    }

    data class Animator (var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class IOShape (var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = Math.min(w, h) / 15
            paint.color = Color.WHITE
            paint.strokeWidth = size / 5
            paint.strokeCap = Paint.Cap.ROUND
            canvas.save()
            canvas.translate(w/2, h/2)
            for (i in 0..1) {
                canvas.save()
                canvas.translate(-(size/2) * (1 - i) * state.scales[0], 0f)
                canvas.rotate(30f * i * state.scales[1])
                canvas.drawLine(0f, -size/2, 0f, size/2, paint)
                canvas.restore()
            }
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(size, (h/2 + size) * (1 - state.scales[2]), size/2, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer (var view : IOView) {

        private val animator : Animator = Animator(view)

        private val ioShape : IOShape = IOShape(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            ioShape.draw(canvas, paint)
            animator.animate {
                ioShape.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ioShape.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : IOView {
            val view : IOView = IOView(activity)
            activity.setContentView(view)
            return view
        }
    }
}