package com.atakmap.android.plugintemplate

import android.graphics.drawable.Drawable

class imageLoader(): myPublisher {
    private var myImage: Drawable? = null

    override fun imageLoaded(): Drawable {
        return myImage!!
    }

    fun saveImage(im: Drawable) {
        myImage = im
    }
}