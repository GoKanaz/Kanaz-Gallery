package com.gokanaz.gallery.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class CustomZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val startPoint = PointF()
    private val midPoint = PointF()
    
    private var mode = NONE
    private var oldDistance = 1f
    
    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private const val CLICK = 3
    }
    
    init {
        scaleType = ScaleType.MATRIX
        setOnTouchListener { _, event -> handleTouch(event) }
    }
    
    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                startPoint.set(event.x, event.y)
                mode = DRAG
            }
            
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = getDistance(event)
                if (oldDistance > 10f) {
                    savedMatrix.set(matrix)
                    getMidPoint(midPoint, event)
                    mode = ZOOM
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                performClick()
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    matrix.postTranslate(event.x - startPoint.x, event.y - startPoint.y)
                } else if (mode == ZOOM) {
                    val newDistance = getDistance(event)
                    if (newDistance > 10f) {
                        matrix.set(savedMatrix)
                        val scale = newDistance / oldDistance
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y)
                    }
                }
            }
        }
        
        imageMatrix = matrix
        return true
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
    
    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    
    private fun getMidPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }
    
    fun resetZoom() {
        matrix.reset()
        imageMatrix = matrix
    }
}
