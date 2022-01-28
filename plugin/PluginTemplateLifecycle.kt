package com.atakmap.android.plugintemplate.plugin

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
import android.content.res.Configuration
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
import com.atakmap.coremap.log.Log
import transapps.maps.plugin.lifecycle.Lifecycle
import java.lang.Exception
import java.net.URL
import java.util.*

class PluginTemplateLifecycle(private val pluginContext: Context?) : Lifecycle {
    private val overlays: MutableCollection<MapComponent?>?
    private var mapView: MapView?
    override fun onConfigurationChanged(arg0: Configuration?) {
        if (overlays != null) {
            for (c in overlays) c?.onConfigurationChanged(arg0)
        }
    }

    override fun onCreate(arg0: Activity?,
                          arg1: transapps.mapi.MapView?) {
        if (arg1 == null || arg1.view !is MapView) {
            Log.w(TAG, "This plugin is only compatible with ATAK MapView")
            return
        }
        mapView = arg1.view as MapView
        overlays
            ?.add(PluginTemplateMapComponent())

        // create components
        val iter = overlays
                ?.iterator()
        var c: MapComponent?
        if (iter != null) {
            while (iter.hasNext()) {
                c = iter.next()
                try {
                    c?.onCreate(
                        pluginContext,
                        arg0?.getIntent(),
                        mapView
                    )
                } catch (e: Exception) {
                    Log.w(
                        TAG,
                        "Unhandled exception trying to create overlays MapComponent",
                        e
                    )
                    iter.remove()
                }
            }
        }
    }

    override fun onDestroy() {
        for (c in overlays!!) c?.onDestroy(pluginContext, mapView)
    }

    override fun onFinish() {
        // XXX - no corresponding MapComponent method
    }

    override fun onPause() {
        for (c in overlays!!) c?.onPause(pluginContext, mapView)
    }

    override fun onResume() {
        for (c in overlays!!) c?.onResume(pluginContext, mapView)
    }

    override fun onStart() {
        for (c in overlays!!) c?.onStart(pluginContext, mapView)
    }

    override fun onStop() {
        for (c in overlays!!) c?.onStop(pluginContext, mapView)
    }

    companion object {
        private val TAG: String? = "PluginTemplateLifecycle"
    }

    init {
        overlays = LinkedList()
        mapView = null
        PluginNativeLoader.init(pluginContext)
    }
}