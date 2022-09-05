package io.outblock.lilico.page.ar.presenter

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ItemNftVrMediaBinding
import io.outblock.lilico.page.ar.model.ArContentModel
import io.outblock.lilico.utils.exoplayer.createExoPlayer
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.safeRun
import io.outblock.lilico.utils.uiScope
import kotlinx.coroutines.delay

class ArContentPresenter(
    private val arFragment: ArFragment,
    private val image: String?,
    private val video: String?,
) : BasePresenter<ArContentModel> {
    private var binded = false

    private val videoPlayer by lazy { createExoPlayer(arFragment.requireActivity()) }

    init {
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            ViewRenderable
                .builder()
                .setView(arFragment.requireContext(), R.layout.item_nft_vr_media)
                .build()
                .thenAccept { addToScene(it, hitResult.createAnchor()) }
        }
    }

    override fun bind(model: ArContentModel) {
        model.onResume?.let { onResume() }
        if (!video.isNullOrBlank()) {
            model.onPause?.let { safeRun { videoPlayer.pause() } }
            model.onRestart?.let { safeRun { videoPlayer.play() } }
            model.onDestroy?.let { safeRun { videoPlayer.release() } }
        }
    }

    private fun addToScene(renderable: ViewRenderable, anchor: Anchor) {
        if (binded) {
            return
        }
        val node = AnchorNode(anchor)
        node.setParent(arFragment.arSceneView.scene)
        node.renderable = renderable
        binded = true

        val view = renderable.view
        bindMedia(ItemNftVrMediaBinding.bind(view))
    }

    private fun bindMedia(binding: ItemNftVrMediaBinding) {
        Glide.with(binding.coverView)
            .asBitmap()
            .load(image)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    ioScope {
                        val ratio = resource.width / resource.height;
                        if (ratio != 1) {
                            with(binding.mediaWrapper.layoutParams) {
                                height = (150.dp2px() * resource.height * 1f / resource.width).toInt();
                                binding.mediaWrapper.layoutParams = this
                            }
                        }
                        uiScope {
                            binding.coverView.setImageBitmap(resource)
                            bindVideo(binding)
                        }
                    }
                }
            })
    }

    private fun bindVideo(binding: ItemNftVrMediaBinding) {
        binding.videoView.setVisible(!video.isNullOrBlank())
//        if (video.isNullOrBlank()) {
//            return
//        }
//        with(videoPlayer) {
//            setVideoTextureView(binding.videoView)
//            setMediaItem(MediaItem.fromUri(video))
//            repeatMode = ExoPlayer.REPEAT_MODE_ALL
//            volume = 0f
//            prepare()
//            play()
//        }
    }

    private fun onResume() {
        uiScope {
            delay(1000)
            arFragment.arSceneView.session?.apply {
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                configure(config)
                arFragment.arSceneView.setupSession(this)
            }
        }
    }
}