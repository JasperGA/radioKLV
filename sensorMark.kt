package com.atakmap.android.plugintemplate

import android.graphics.Color
import com.atakmap.android.cot.CotMapComponent
import com.atakmap.android.cot.detail.SensorDetailHandler
import com.atakmap.android.cot.detail.SensorDetailHandler.*
import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.coremap.maps.time.CoordinatedTime
import org.json.JSONArray
import org.json.JSONObject
import java.lang.String
import com.atakmap.android.ipc.AtakBroadcast

import com.atakmap.app.R.string.uid

import android.content.Intent
import com.atakmap.coremap.maps.coords.GeoPoint


class sensorMark() {
    fun drawCotLocal(
        // Constructors for the info we require when calling this function
        data: JSONObject
    )
    : GeoPoint? {
        //initialise an ATAK COT object
        val  generatedCOT: CotEvent = CotEvent()

        /*
         Get relevant data from JSON
         */
        // Get device coordinates
        val jsonCoords: JSONObject = data.getJSONObject("coord")
        val lat: Double = jsonCoords.getDouble ("latitude-deg")
        val long: Double = jsonCoords.getDouble("longitude-deg")
        val alt: Double = jsonCoords.getDouble("altitude-msl-m")

        // Get device azimuth
        val jsonOri: JSONObject = data.getJSONObject("orientation")
        val jsonAzimuth: JSONObject = jsonOri.getJSONObject("dir-angle")
        val azimuth: Double = jsonAzimuth.getDouble("azimuth-true-deg")

        // Get magnetic declination
        val jsonMag: JSONObject = jsonOri.getJSONObject("magnetic-declination")
        val magDec: Long = jsonMag.getLong("value-deg")

        // Get pitch and roll
        val pitch: Long = jsonOri.getLong("vertical-angle-deg")
        val roll: Long = jsonOri.getLong("bank-deg")




        //Generate initialise time
        val time = CoordinatedTime()
        generatedCOT.uid = data.getString("source")
        generatedCOT.type = "b-m-p-s-p-loc"
        generatedCOT.time = time
        generatedCOT.start = time
        generatedCOT.stale = time.addMinutes(10)
        generatedCOT.how = "h-e"

        //set the Geo Co-ords of the COT event
        generatedCOT.setPoint(CotPoint(lat,long,alt, 0.0,0.0))

        val detail = CotDetail("detail")
        generatedCOT.detail = detail            // Need to add COTDetail called detail first, see lines 741-774 https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/2cbe60ae47272eeb735f9716ff5a780131340ab9/atak/ATAK/app/src/main/java/com/atakmap/android/cot/CotMapComponent.java
        val sensor = CotDetail("sensor")

        sensor.setAttribute(RANGE_ATTRIBUTE, java.lang.String.valueOf(50))
        sensor.setAttribute(AZIMUTH_ATTRIBUTE, java.lang.String.valueOf(azimuth))
        sensor.setAttribute(FOV_ATTRIBUTE, java.lang.String.valueOf(45))
        sensor.setAttribute(MAG_REF_ATTRIBUTE, java.lang.String.valueOf(magDec))
        sensor.setAttribute(FOV_ALPHA, java.lang.String.valueOf(0.3))
        sensor.setAttribute(FOV_RED, java.lang.String.valueOf(1.0))
        sensor.setAttribute(FOV_GREEN, java.lang.String.valueOf(1.0))
        sensor.setAttribute(FOV_BLUE, java.lang.String.valueOf(1.0))
        sensor.setAttribute(STROKE_COLOR, java.lang.String.valueOf(Color.GREEN))
        sensor.setAttribute(STROKE_WEIGHT, java.lang.String.valueOf(0.5))
        sensor.setAttribute(VFOV_ATTRIBUTE, java.lang.String.valueOf(pitch))
        sensor.setAttribute(ROLL_ATTRIBUTE, java.lang.String.valueOf(roll))
        sensor.setAttribute(ELEVATION_ATTRIBUTE, java.lang.String.valueOf(alt))
        sensor.setAttribute(MODEL_ATTRIBUTE,  "unknown")
        sensor.setAttribute("videoUID", "WhatUID?")
        sensor.setAttribute("videoUrl", "http://111.1.1.1")
        //sensor.setAttribute(HIDE_FOV, false.toString())

        /*
            Attempt to add video link to COT event
            See https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/03e312ecb5aaf54b948ec90bc5d6edfb7b7514b2/atak/ATAK/app/src/main/java/com/atakmap/android/video/manager/VideoXMLHandler.java
         */
        val video = CotDetail("__video")

        val aliasDetail = CotDetail("ConnectionEntry")

        aliasDetail.setAttribute("address", "255.255.255.1")
        aliasDetail.setAttribute("uid", "WhatUID?")
        aliasDetail.setAttribute("alias", "ce.getAlias()")
        aliasDetail.setAttribute("port", String.valueOf(1234))
        aliasDetail.setAttribute("roverPort", String.valueOf(4321))
        aliasDetail.setAttribute("rtspReliable", String.valueOf(true))
        aliasDetail.setAttribute("ignoreEmbeddedKLV", String.valueOf(true))
        aliasDetail.setAttribute("path", "ce.getPath()")
        aliasDetail.setAttribute("protocol", "tcp")
        aliasDetail.setAttribute("networkTimeout", String.valueOf(1))
        aliasDetail.setAttribute("bufferTime", String.valueOf(3))
        aliasDetail.setAttribute("sensor", data.getString("source"))

        video.addChild(aliasDetail)

        generatedCOT.detail.addChild(sensor)
        generatedCOT.detail.addChild(video)

        //Write COT to map using internal dispatcher
        CotMapComponent.getInternalDispatcher().dispatch(generatedCOT)

        CotMapComponent.getExternalDispatcher().dispatch(generatedCOT)

        return CotPoint(lat,long,alt, 0.0,0.0).toGeoPoint()
    }
}