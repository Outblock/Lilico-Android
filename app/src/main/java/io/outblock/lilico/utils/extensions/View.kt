package io.outblock.lilico.utils.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import java.io.File


/**
 * @author John
 * @since 2018-12-15 16:04
 */

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.setVisible(visible: Boolean = true, invisible: Boolean = false) {
    when {
        visible -> this.visibility = View.VISIBLE
        invisible -> this.visibility = View.INVISIBLE
        else -> this.visibility = View.GONE
    }
}

fun View.toggleSelect() {
    isSelected = !isSelected
}

fun View.getBitmapFromView(): Bitmap { //Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable = background
    if (bgDrawable != null) {
        //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
    } else {
        //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    }
    // draw the view on the canvas
    draw(canvas)
    //return the bitmap
    return returnedBitmap
}

fun View.saveToImageFile(file: File) {
//    val bitmap = getBitmapFromView()
//    ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG)
}

fun View.scale(scale: Float): View {
    scaleX = scale
    scaleY = scale
    return this
}
