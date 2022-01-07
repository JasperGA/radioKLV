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
import transapps.mapi.MapView
import transapps.maps.plugin.tool.Group
import transapps.maps.plugin.tool.Tool

class PluginTemplateTool(private val context: Context?) : Tool(), ToolDescriptor {
    override fun getDescription(): String? {
        return context?.getString(R.string.app_name)
    }

    override fun getIcon(): Drawable? {
        return context?.resources?.getDrawable(R.drawable.ic_launcher)
    }

    override fun getGroups(): Array<Group?>? {
        return arrayOf(
                Group.GENERAL
        )
    }

    override fun getShortDescription(): String? {
        return context?.getString(R.string.app_name)
    }

    override fun getTool(): Tool? {
        return this
    }

    override fun onActivate(arg0: Activity?, arg1: MapView?, arg2: ViewGroup?,
                            arg3: Bundle?,
                            arg4: ToolCallback?) {

        // Hack to close the dropdown that automatically opens when a tool
        // plugin is activated.
        arg4?.onToolDeactivated(this)
        // Intent to launch the dropdown or tool

        //arg2.setVisibility(ViewGroup.INVISIBLE);
        val i = Intent(
                PluginTemplateDropDownReceiver.Companion.SHOW_PLUGIN)
        AtakBroadcast.getInstance().sendBroadcast(i)
    }

    override fun onDeactivate(arg0: ToolCallback?) {}
}