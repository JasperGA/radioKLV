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
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * Boilerplate code for loading native.
 */
object PluginNativeLoader {
    private val TAG: String? = "NativeLoader"
    private var ndl: String? = null

    /**
     * If a plugin wishes to make use of this class, they will need to copy it into their plugin.
     * The classloader that loads this class is a key component of getting System.load to work
     * properly.   If it is desirable to use this in a plugin, it will need to be a direct copy in a
     * non-conflicting package name.
     */
    @Synchronized
    fun init(context: Context?) {
        if (ndl == null) {
            try {
                if (context != null) {
                    ndl = context?.getPackageManager()
                        .getApplicationInfo(context.getPackageName(),
                            0).nativeLibraryDir
                }
            } catch (e: Exception) {
                throw IllegalArgumentException(
                        "native library loading will fail, unable to grab the nativeLibraryDir from the package name")
            }
        }
    }

    /**
     * Security guidance from our recent audit:
     * Pass an absolute path to System.load(). Avoid System.loadLibrary() because its behavior
     * depends upon its implementation which often relies on environmental features that can be
     * manipulated. Use only validated, sanitized absolute paths.
     */
    fun loadLibrary(name: String?) {
        if (ndl != null) {
            val lib = (ndl + File.separator
                    + System.mapLibraryName(name))
            if (File(lib).exists()) {
                System.load(lib)
            }
        } else {
            throw IllegalArgumentException("NativeLoader not initialized")
        }
    }
}