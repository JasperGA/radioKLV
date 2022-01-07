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
import transapps.maps.plugin.tool.Tool.ToolCallback
import android.content.Intent
import android.view.View
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
import androidx.fragment.app.Fragment
import com.atak.plugins.impl.PluginLayoutInflater

/**
 * A simple [Fragment] subclass.
 * Use the [pluginMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class pluginMainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plugin_main, container, false)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment pluginMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): pluginMainFragment? {
            val fragment = pluginMainFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}