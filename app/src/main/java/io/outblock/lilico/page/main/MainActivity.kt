package io.outblock.lilico.page.main

import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import io.outblock.lilico.databinding.ActivityMainBinding
import io.outblock.lilico.firebase.auth.firebaseCustomLogin
import io.outblock.lilico.page.main.presenter.MainContentPresenter
import io.outblock.lilico.page.walletcreate.WalletCreateActivity
import io.outblock.lilico.utils.*

class MainActivity : BaseActivity() {

    private lateinit var contentPresenter: MainContentPresenter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentPresenter = MainContentPresenter(this, binding)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}