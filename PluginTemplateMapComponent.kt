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

class PluginTemplateMapComponent : DropDownMapComponent() {
    private var pluginContext: Context? = null
    private var ddr: PluginTemplateDropDownReceiver? = null
    override fun onCreate(context: Context?, intent: Intent?,
                          view: MapView?) {
        context?.setTheme(R.style.ATAKPluginTheme)
        super.onCreate(context, intent, view)
        pluginContext = context
        ddr = PluginTemplateDropDownReceiver(
                view, context)
        Log.d(TAG, "registering the plugin filter")
        val ddFilter = DocumentedIntentFilter()
        ddFilter.addAction(PluginTemplateDropDownReceiver.Companion.SHOW_PLUGIN)
        registerDropDownReceiver(ddr, ddFilter)
    }

    override fun onDestroyImpl(context: Context?, view: MapView?) {
        super.onDestroyImpl(context, view)
    }

    companion object {
        private val TAG: String? = "PluginTemplateMapComponent"
    }
}