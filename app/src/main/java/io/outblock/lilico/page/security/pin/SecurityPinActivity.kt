package io.outblock.lilico.page.security.pin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivitySecurityPinBinding

class SecurityPinActivity : BaseActivity() {

    private val type by lazy { intent.getIntExtra(EXTRA_TYPE, 0) }
    private val action by lazy { intent.getParcelableExtra<Intent>(EXTRA_ACTION) }
    private lateinit var binding: ActivitySecurityPinBinding
    private lateinit var presenter: SecurityPinPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = SecurityPinPresenter(this, binding, type, action)
    }

    companion object {
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_ACTION = "extra_action"
        const val TYPE_CHECK = 1
        const val TYPE_RESET = 2
        const val TYPE_CREATE = 3

        fun launch(context: Context, type: Int = TYPE_CHECK, action: Intent? = null) {
            context.startActivity(Intent(context, SecurityPinActivity::class.java).apply {
                putExtra(EXTRA_TYPE, type)
                putExtra(EXTRA_ACTION, action)
            })
        }
    }
}