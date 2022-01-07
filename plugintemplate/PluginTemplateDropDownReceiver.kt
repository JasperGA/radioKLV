package com.atakmap.android.plugintemplate

import android.os.Bundle
import com.atakmap.android.plugintemplate.plugin.pluginMainFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.jvm.Synchronized
import com.atakmap.android.plugintemplate.plugin.PluginNativeLoader
import transapps.maps.plugin.tool.ToolDescriptor
import android.graphics.drawable.Drawable
import android.app.Activity
import android.content.Context
import transapps.maps.plugin.tool.Tool.ToolCallback
import android.content.Intent
import android.view.View
import android.widget.Button
import com.atakmap.android.plugintemplate.PluginTemplateDropDownReceiver
import com.atakmap.android.ipc.AtakBroadcast
import com.atakmap.android.maps.MapComponent
import com.atakmap.android.plugintemplate.plugin.PluginTemplateLifecycle
import com.atakmap.android.plugintemplate.PluginTemplateMapComponent
import com.atakmap.android.dropdown.DropDownMapComponent
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.dropdown.DropDown.OnStateListener
import android.widget.TextView
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.maps.MapView
import com.atakmap.android.plugintemplate.plugin.R
import com.atakmap.coremap.log.Log
import kotlin.math.round

class PluginTemplateDropDownReceiver(mapView: MapView?,
                                     private val pluginContext: Context?) : DropDownReceiver(mapView), OnStateListener {
    private val templateView: View?
    private val myFirstFragment: View?

    //initialise some values to generate a COT
    val plot = positionmark() //Initialise our Cot plotting Class
    val lat = 37.421998
    var lon = -122.084
    val hae = 0.00      //height above ellipsoid in metres
    val ce = 2.00       //circular error radius in metres
    val le = 2.00       //linear error in metres (height above target)
    val uid = "test uid"
    val friendly = "a-f-G-U-C-I"
    val hostile = "a-h-G-U-C-I"
    var type = ""

    /**************************** PUBLIC METHODS  */
    public override fun disposeImpl() {}

    /**************************** INHERITED METHODS  */
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getAction() ?: return
        if (action == SHOW_PLUGIN) {
            Log.d(TAG, "showing plugin drop down")
            showDropDown(myFirstFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this)
        }
        val callsignTextBox = myFirstFragment?.findViewById(R.id.callsign) as TextView
        val mylat = myFirstFragment.findViewById(R.id.myLat) as TextView
        val mylong = myFirstFragment.findViewById(R.id.myLong) as TextView
        val myalt = myFirstFragment.findViewById(R.id.myAlt) as TextView
        val swHostile = myFirstFragment.findViewById(R.id.plHostile) as Button
        val swFriend = myFirstFragment.findViewById(R.id.plFriend) as Button
        //  val aleganceText = myFirstFragment.findViewById(R.id.hostileornot) as TextView

        val takCallsign = getMapView().deviceCallsign
        val takMyLat = mapView.selfMarker.point.latitude
        val takMyLong = mapView.selfMarker.point.longitude
        val takMyAlt = mapView.selfMarker.point.altitude

        callsignTextBox.setText(takCallsign)
        mylat.setText(takMyLat.toString())
        mylong.setText(takMyLong.toString())
        myalt.setText(round(takMyAlt).toString() + "  ft MSL")

        swHostile.setOnClickListener(){
            plot.drawCotLocal(lat,lon,hae,ce,le,uid,hostile)
            lon++ // Increment the Longitude every time we push the button

        }

        swFriend.setOnClickListener(){
            plot.drawCotLocal(lat,lon,hae,ce,le,uid,friendly)
            lon++ // Increment the Longitude every time we push the button
        }
    }

    override fun onDropDownSelectionRemoved() {}
    override fun onDropDownVisible(v: Boolean) {}
    override fun onDropDownSizeChanged(width: Double, height: Double) {}
    override fun onDropDownClose() {}

    companion object {
        val TAG = PluginTemplateDropDownReceiver::class.java
                .simpleName
        val SHOW_PLUGIN: String? = "com.atakmap.android.plugintemplate.SHOW_PLUGIN"
    }

    /**************************** CONSTRUCTOR  */
    init {

        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        templateView = PluginLayoutInflater.inflate(pluginContext,
                R.layout.main_layout, null)
        myFirstFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.fragment_plugin_main, null)
    }
}