package io.outblock.lilico.page.walletcreate.fragments.pincode.pin.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.outblock.lilico.BuildConfig
import io.outblock.lilico.R
import io.outblock.lilico.page.walletcreate.fragments.pincode.pin.KeyboardItem
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible

class PinKeyboard : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    private var onKeyboardActionListener: ((key: KeyboardItem) -> Unit)? = null

    private var keys: List<List<KeyboardItem>> = listOf()

    private var keyboardType = KeyboardType.T9


    init {
        setVisible(false)
        setOnClickListener { }
    }

    fun bindKeys(keys: List<List<KeyboardItem>>, keyboardType: KeyboardType) {
        this.keys = keys
        this.keyboardType = keyboardType
    }

    fun show() {
        if (isVisible()) {
            return
        }

        if (childCount == 0) {
            LayoutInflater.from(context).inflate(R.layout.widget_safe_keyboard, this)

            if (keys.isEmpty() && BuildConfig.DEBUG) {
                throw RuntimeException("keys not set")
            }

            if (onKeyboardActionListener == null && BuildConfig.DEBUG) {
                throw RuntimeException("onKeyboardActionListener not set")
            }

            createKeyboardLayout().setup()
        }

        setVisible(true)
    }

    fun keys() = keys

    fun hide() {
        if (!isVisible()) {
            return
        }
        setVisible(false)
    }

    fun setOnKeyboardActionListener(listener: (key: KeyboardItem) -> Unit) {
        this.onKeyboardActionListener = listener
    }

    private fun createKeyboardLayout(): KeyboardLayout {
        val containerView = findViewById<LinearLayout>(R.id.container_view)
        return T9Keyboard(containerView, keys, onKeyboardActionListener);
    }
}

abstract class KeyboardLayout(
    containerView: LinearLayout,
    keys: List<List<KeyboardItem>>,
    onKeyboardActionListener: ((key: KeyboardItem) -> Unit)? = null,
) {
    val context: Context = containerView.context

    abstract fun setup()
}

enum class KeyboardType {
    T9,
    TRADITIONAL,
}