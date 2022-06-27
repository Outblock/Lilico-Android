package io.outblock.lilico.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.fragment.app.FragmentActivity
import io.outblock.lilico.R
import io.outblock.lilico.databinding.WidgetSendButtonBinding
import io.outblock.lilico.page.security.securityVerification
import io.outblock.lilico.utils.findActivity
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import kotlin.math.min

class SendButton : TouchScaleCardView {

    private val duration = 1.2f * DateUtils.SECOND_IN_MILLIS

    private var binding = WidgetSendButtonBinding.inflate(LayoutInflater.from(context))

    private var indicatorProgress = 0.0f

    private var progressTask = Runnable { updateProgress() }

    private var isPressedDown = false

    private var onProcessing: (() -> Unit)? = null

    private var defaultText: String

    private var state = ButtonState.DEFAULT

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SendButton, defStyleAttr, 0)
        defaultText = array.getString(R.styleable.SendButton_defaultText).orEmpty()
        array.recycle()
        init()
    }

    private fun init() {
        addView(binding.root)
        with(binding) {
            holdToSend.text = defaultText
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (state != ButtonState.DEFAULT) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressedDown = true
                postDelayed(progressTask, 16)
                logd(TAG, "onTouchEvent down")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onTouchUp()
        }

        return true
    }

    fun setOnProcessing(onProcessing: () -> Unit) {
        this.onProcessing = onProcessing
    }

    private fun onTouchUp() {
        logd(TAG, "onTouchUp()")
        isPressedDown = false
        if (state == ButtonState.DEFAULT) {
            changeState(ButtonState.DEFAULT)
        }
    }

    private fun changeState(state: ButtonState) {
        this.state = state
        with(binding) {
            when (state) {
                ButtonState.DEFAULT -> {
                    setScaleEnable(true)
                    progressBar.isIndeterminate = false
                    progressBar.setProgress(0, true)
                    indicatorProgress = 0f
                }
                ButtonState.VERIFICATION -> {
                    progressBar.setProgress(100, true)
                    verification()
                }
                ButtonState.LOADING -> {
                    setScaleEnable(false)
                    progressBar.isIndeterminate = true
                }
            }
        }
    }

    private fun updateProgress() {
        if (indicatorProgress > duration || !isPressedDown) {
            return
        }
        indicatorProgress += 16

        val progress = min(100, ((indicatorProgress / duration) * 100).toInt())

        binding.progressBar.progress = progress

        if (progress == 100) {
            changeState(ButtonState.VERIFICATION)
        }

        if (progress < 100) {
            postDelayed(progressTask, 16)
        }
    }

    private fun verification() {
        uiScope {
            val isVerified = securityVerification(findActivity(this) as FragmentActivity)
            if (isVerified) {
                changeState(ButtonState.LOADING)
                onProcessing?.invoke()
            } else {
                changeState(ButtonState.DEFAULT)
            }
        }
    }

    companion object {
        private val TAG = SendButton::class.java.simpleName
    }
}

private enum class ButtonState {
    DEFAULT,
    VERIFICATION,
    LOADING,
}