package com.atakmap.android.plugintemplate

import android.graphics.drawable.Drawable
import com.atakmap.android.cot.detail.CotDetailHandler
import com.atakmap.android.maps.MapItem
import com.atakmap.comms.CommsMapComponent
import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent

class cotImageHandler(): CotDetailHandler("__image") {
    var image: Drawable? = null
    var imageCOT: String? = null

    override fun toItemMetadata(
        item: MapItem?,
        event: CotEvent?,
        detail: CotDetail?
    ): CommsMapComponent.ImportResult {
        imageCOT = detail.toString()
        return CommsMapComponent.ImportResult.SUCCESS
    }

    override fun toCotDetail(p0: MapItem?, p1: CotEvent?, p2: CotDetail?): Boolean {
        TODO("Not yet implemented")
        return true
    }

    fun getString(): String? {
        return imageCOT
    }
}