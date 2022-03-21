package io.outblock.lilico.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import io.outblock.lilico.R
import io.outblock.lilico.databinding.WidgetSendButtonBinding
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.logd
import kotlin.math.min

class SendButton : MaterialCardView {

    private val duration = 2 * DateUtils.SECOND_IN_MILLIS

    private var binding = WidgetSendButtonBinding.inflate(LayoutInflater.from(context))

    private var indicatorProgress = 0.0f

    private var progressTask = Runnable { updateProgress() }

    private var isPressedDown = false

    private var onProcessing: (() -> Unit)? = null

    private var defaultText: String
    private var processingText: String

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SendButton, defStyleAttr, 0)
        defaultText = array.getString(R.styleable.SendButton_defaultText).orEmpty()
        processingText = array.getString(R.styleable.SendButton_processingText).orEmpty()
        array.recycle()
        init()
    }

    private fun init() {
        addView(binding.root)
        with(binding) {
            holdToSend.text = defaultText
            processingText.text = this@SendButton.processingText
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isHoldAble()) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressedDown = true
                postDelayed(progressTask, 16)
                logd(TAG, "onTouchEvent down")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> cancel()
        }

        return true
    }

    fun setOnProcessing(onProcessing: () -> Unit) {
        this.onProcessing = onProcessing
    }

    private fun cancel() {
        logd(TAG, "cancel()")
        isPressedDown = false
        indicatorProgress = 0.0f
        binding.progressBar.progress = 0.0f
    }

    private fun updateProgress() {
        if (indicatorProgress > duration || !isPressedDown) {
            return
        }
        indicatorProgress += 16

        val progress = min(1.0f, indicatorProgress / duration)

        binding.progressBar.progress = progress

        if (progress == 1f) {
            switchToProcessing()
        }

        if (progress < 1) {
            postDelayed(progressTask, 16)
        }
    }

    private fun isHolding() = isPressedDown && !isHoldAble()

    private fun isHoldAble() = !binding.processingText.isVisible()

    private fun switchToProcessing() {
        with(binding) {
            TransitionManager.go(Scene(binding.root as ViewGroup), Fade().apply { duration = 100 })
            progressBar.setVisible(false)
            holdToSend.setVisible(false)
            checkedIcon.setVisible(true)
            processingText.setVisible(true)
            (checkedIcon.drawable as? AnimatedVectorDrawable)?.start()
        }
        onProcessing?.invoke()
    }

    companion object {
        private val TAG = SendButton::class.java.simpleName
    }
}