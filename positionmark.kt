package com.atakmap.android.plugintemplate

import android.graphics.Color
import com.atakmap.android.cot.CotMapComponent
import com.atakmap.android.cot.detail.SensorDetailHandler
import com.atakmap.android.cot.detail.SensorDetailHandler.*
import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.coremap.maps.time.CoordinatedTime


class positionmark() {
    fun drawCotLocal(
        // Constructors for the info we require when calling this function
        lat: Double,
        lon:Double,
        hae: Double,
        ce: Double,
        le: Double,
        uid: String,
        type: String
    ){
        //initialise an ATAK COT object
        val  generatedCOT: CotEvent = CotEvent()

        //Generate initialise time
        val time = CoordinatedTime()
        generatedCOT.uid = uid
        generatedCOT.type = type
        generatedCOT.time = time
        generatedCOT.start = time
        generatedCOT.stale = time.addMinutes(10)
        generatedCOT.how = "h-e"

        //set the Geo Co-ords of the COT event
        generatedCOT.setPoint(CotPoint(lat,lon,hae, ce,le))

        //val markDetail: CotDetail = generatedCOT.detail

        val detail = CotDetail("detail")
        generatedCOT.detail = detail            // Need to add COTDetail called detail first, see lines 741-774 https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/2cbe60ae47272eeb735f9716ff5a780131340ab9/atak/ATAK/app/src/main/java/com/atakmap/android/cot/CotMapComponent.java
        val sensor = CotDetail("sensor")

        sensor.setAttribute(RANGE_ATTRIBUTE, java.lang.String.valueOf(50))
        sensor.setAttribute(AZIMUTH_ATTRIBUTE, java.lang.String.valueOf(202.3681640625))
        sensor.setAttribute(FOV_ATTRIBUTE, java.lang.String.valueOf(197))
        sensor.setAttribute(MAG_REF_ATTRIBUTE, java.lang.String.valueOf(0))
        sensor.setAttribute(FOV_ALPHA, java.lang.String.valueOf(0.3))
        sensor.setAttribute(FOV_RED, java.lang.String.valueOf(1.0))
        sensor.setAttribute(FOV_GREEN, java.lang.String.valueOf(1.0))
        sensor.setAttribute(FOV_BLUE, java.lang.String.valueOf(1.0))
        sensor.setAttribute(STROKE_COLOR, java.lang.String.valueOf(Color.GREEN))
        sensor.setAttribute(STROKE_WEIGHT, java.lang.String.valueOf(0.5))
        sensor.setAttribute(VFOV_ATTRIBUTE, java.lang.String.valueOf(45))
        sensor.setAttribute(ROLL_ATTRIBUTE, java.lang.String.valueOf(0))
        sensor.setAttribute(ELEVATION_ATTRIBUTE, java.lang.String.valueOf(0))
        //sensor.setAttribute(HIDE_FOV, false.toString())

        generatedCOT.detail.addChild(sensor)
        //Write COT to map using internal dispatcher
        CotMapComponent.getInternalDispatcher().dispatch(generatedCOT)

        CotMapComponent.getExternalDispatcher().dispatch(generatedCOT)
    }
}