package com.atakmap.android.plugintemplate

import android.app.Activity
import android.graphics.drawable.Drawable
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.NetworkOnMainThreadException
import android.view.View
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.dropdown.DropDown.OnStateListener
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.maps.MapView
import com.atakmap.android.plugintemplate.plugin.R
import com.atakmap.coremap.log.Log

import java.net.URL
import android.webkit.WebView
import android.widget.*
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import com.atakmap.android.user.PlacePointTool
import com.atakmap.coremap.maps.coords.GeoPoint
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import com.atakmap.android.cot.CotMapComponent
import com.atakmap.android.cot.detail.CotDetailManager
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.video.VideoDropDownReceiver

import com.atakmap.coremap.filesystem.FileSystemUtils
import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.coremap.maps.time.CoordinatedTime
import java.text.SimpleDateFormat
import java.util.*
import java.io.*
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Handler
import android.os.SystemClock.sleep
import android.view.SurfaceView
import com.atakmap.android.ipc.AtakBroadcast
import org.json.JSONObject

import kotlin.coroutines.*
import android.view.SurfaceHolder
import com.ekito.simpleKML.model.Object
import java.util.concurrent.locks.ReentrantLock


class PluginTemplateDropDownReceiver(mapView: MapView?,
                                     private val pluginContext: Context?) : DropDownReceiver(mapView), OnStateListener, MapEventDispatcher.MapEventDispatchListener, SurfaceHolder.Callback {
    // Define plugin layouts
    private val templateView: View?
    private val mainFragment: View?
    private val videoFragment: View?
    private val webFragment: View?
    //private val listenFragment: View?
    private var showingPlug: Boolean = false
    private var jimThread: JIMThread = JIMThread()
    private var image: Drawable? = null
    private var imagePlace: ImageView? = null

    // Initialize webView
    var inflater = LayoutInflater.from(pluginContext)
    private val ll: LinearLayout? = inflater.inflate(R.layout.web_layout, null) as LinearLayout
    private val htmlViewer: WebView? = WebView(mapView!!.context)

    private var wasCalled: Boolean = false
    private var hasActiveHolder: Boolean = false

    /**************************** PUBLIC METHODS  */
    public override fun disposeImpl() {}

    /**************************** INHERITED METHODS  */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getAction() ?: return

        if (action == SHOW_PLUGIN) {
            Log.d(TAG, "showing plugin drop down")
            showDropDown(videoFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this)

            val dispatcher = mapView.mapEventDispatcher
            dispatcher.addMapEventListener(MapEvent.ITEM_CLICK, this)

            showingPlug = true
        }

        if (!jimThread.isAlive) {
            jimThread.start()
        }
        jimThread.setPlugin(showingPlug)

        // Define video buttons
        val tryWebBut = videoFragment?.findViewById(R.id.tryWeb) as Button
        val sendImageBut = videoFragment?.findViewById(R.id.sendImage) as Button
        val getCOTImage = videoFragment?.findViewById(R.id.getCOTImage) as Button
        var stopPosition: Int = 0

        // Try to stream video
        val vid = videoFragment?.findViewById(R.id.videoView) as VideoView
        //val vidThread = VideoThread(vid)
        //vidThread.start()
        //vidThread.join()
        val videoMediaController = MediaController(pluginContext)
        videoMediaController.setAnchorView(vid)

        val vidDir: File = FileSystemUtils.getItem("tools")
        val vidURI = Uri.parse(vidDir.absolutePath + "/videos/result2.mp4")
        vid.setVideoURI(vidURI)
        //vid.setVideoURI(Uri.parse("aaaaaaaaaaaaaaaaaaaaaaa"))
        vid.setOnPreparedListener(OnPreparedListener { mp ->
            mp.isLooping = true
            vid.start()
        })

//        val mediaPlayer = MediaPlayer().apply {
//            setDataSource(context!!.applicationContext, vidURI)
//            setDisplay(vid.holder)
//            prepare()
//            start()
//        }

//        val player = MediaPlayer()
//        val sh = vid.holder
//        sh.addCallback(this)
//        player.setDataSource(vidDir.absolutePath + "/videos/result2.mp4")
//        synchronized(this) {
//            while (!hasActiveHolder) {
//                try {
//                    val ac = this as Activity
//
//                } catch (e: InterruptedException) {
//                    //Print something
//                }
//            }
//            player.setDisplay(sh)
//            player.prepare()
//        }

        //player.setDisplay(sh)
        //player.prepare()
        //player.start()

        vid.setOnClickListener(View.OnClickListener {
            if (vid.isPlaying) {
                stopPosition = vid.currentPosition
                vid.pause()
            } else {
                vid.seekTo(stopPosition)
                vid.start()
            }
        })



        // If "send image" button is pressed move to image template
        sendImageBut.setOnClickListener() {
            showDropDown(mainFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                HALF_HEIGHT, false, this)

            // Define image buttons and intialise image variable
            //val broadcastImage = mainFragment?.findViewById(R.id.Broadcast) as Button
            val sendImage = mainFragment?.findViewById(R.id.sendImage) as Button
            imagePlace = mainFragment.findViewById(R.id.imageView) as ImageView

            // Try getting image from url and set it in image place
            val thread = ImageThread("http://10.14.55.27:8080/image", this)
            thread.start()

            // If user chooses to send image, open contact/groups page
            sendImage.setOnClickListener(){
                jimThread!!.setPlugin(false)
                val gp: GeoPoint? = jimThread!!.getJimPoint() //mapView.selfMarker.point
                val uid: String = UUID.randomUUID().toString()
                PlacePointTool.MarkerCreator(gp).setUid(uid).setType("b-i-x-i").placePoint()

                // This is how to get a drop down menu that's already in ATAK, don't currently need it for plugin
                //AtakBroadcast.getInstance().sendBroadcast(Intent(ContactPresenceDropdown.SEND_LIST).putExtra("targetUID", mapView.selfMarker.uid))

                val attachDir: File = FileSystemUtils.getItem("attachments")
                val imageFolder: File = File(attachDir.absolutePath, uid)
                imageFolder.mkdirs()

                val fileName: String = SimpleDateFormat("yyyyMMdd_HHmmss'.jpg'").format(Date())
                val imageFile: File = File(imageFolder.absolutePath, fileName)
                val bitImage: Bitmap = image!!.toBitmap(image!!.intrinsicWidth, image!!.intrinsicHeight, null)

                val out = FileOutputStream(imageFile)
                bitImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            }

//            broadcastImage.setOnClickListener() {
//                showDropDown(listenFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
//                    HALF_HEIGHT, false, this)
//                val textbox = listenFragment?.findViewById(R.id.cotInfo) as TextView
//                val see = listenFragment?.findViewById(R.id.seeText) as Button
//                val send = listenFragment?.findViewById(R.id.sendCOT) as Button
//                val testImg = listenFragment?.findViewById(R.id.testImage) as ImageView
//
//                val handler = cotImageHandler()
//                CotDetailManager.getInstance().registerHandler(handler)
//
//                see.setOnClickListener(){
//                    if (handler.getString() != null) {
//                        textbox.setText(handler.getString())
//
//                        //val byteArray = Base64.getDecoder().decode(handler.toString())
//                        //textbox.setText(byteArray.toString())
//                        //val img: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                        //testImg.setImageBitmap(img)
//
//                    } else {
//                        textbox.setText("nothing")
//                    }
//                }
//
//                send.setOnClickListener(){
//                    //initialise an ATAK COT object
//                    val  generatedCOT: CotEvent = CotEvent()
//
//                    //Generate initialise time
//                    val time = CoordinatedTime()
//                    generatedCOT.uid = "sendTest"
//                    generatedCOT.type = "b-i-x-i"
//                    generatedCOT.time = time
//                    generatedCOT.start = time
//                    generatedCOT.stale = time.addMinutes(10)
//                    generatedCOT.how = "h-e"
//
//                    //set the Geo Co-ords of the COT event
//                    generatedCOT.setPoint(CotPoint(20.0,0.0,0.0, 0.0,0.0))
//                    val detail = CotDetail("detail")
//                    generatedCOT.detail = detail
//
//                    // Convert image to byteArray, which can be turned into string
//                    val stream = ByteArrayOutputStream()
//                    val bitImage: Bitmap = image!!.toBitmap(image!!.intrinsicWidth, image.intrinsicHeight, null)
//                    bitImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    val byteArray: ByteArray = stream.toByteArray()
//                    val imageString: String = Base64.getEncoder().encodeToString(byteArray)
//
//                    val imageCOT: CotDetail = CotDetail("__image")
//                    imageCOT.setAttribute("imageBit", imageString)
//                    detail.addChild(imageCOT)
//
//                    CotMapComponent.getInternalDispatcher().dispatch(generatedCOT)
//                    CotMapComponent.getExternalDispatcher().dispatch(generatedCOT)
//                }
//            }
        }

        tryWebBut.setOnClickListener() {
            // Set up webView options for display
            htmlViewer?.isVerticalScrollBarEnabled = true
            htmlViewer?.isHorizontalScrollBarEnabled = true
            val webSettings: WebSettings = htmlViewer!!.getSettings()
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.allowContentAccess = true
            webSettings.databaseEnabled = true
            htmlViewer.webChromeClient = WebChromeClient()

            // Define webView size and place it in layout
            htmlViewer.layoutParams = LinearLayout.LayoutParams(
                (LinearLayout.LayoutParams.WRAP_CONTENT),
                (LinearLayout.LayoutParams.WRAP_CONTENT)
            )
            ll?.addView(htmlViewer)

            // Display layout with webView and go to website
            showDropDown(ll, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                HALF_HEIGHT, false, this)

            htmlViewer.loadUrl("about:blank")
            htmlViewer.loadUrl("http://10.14.55.5:8080/index.html")
        }

        getCOTImage.setOnClickListener() {

        }
    }

    override fun onBackButtonPressed(): Boolean {
        closeDropDown()
        jimThread!!.setPlugin(false)
        return super.onBackButtonPressed()
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
        mainFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.image_layout, null)
        videoFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.video_layout, null)
        webFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.web_layout, null)
        //listenFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.listen_layout, null)
    }

    override fun onMapEvent(event: MapEvent?) {
        val type: String? = event?.getType()
        if ((type.equals(MapEvent.ITEM_CLICK))) {
            wasCalled = true
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        synchronized (this) {
            hasActiveHolder = true;
        }

    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        sleep(2)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        synchronized (this) {
            hasActiveHolder = false;
            //this.notifyAll();
        }
    }

    fun imageLoaded(im: Drawable?) {
        val handler = Handler(pluginContext!!.mainLooper)
        handler.post(Runnable {
            image = im
            imagePlace!!.setImageDrawable(im)
        })
    }
}

class ImageThread(urlAddress: String, private val cla: PluginTemplateDropDownReceiver): Thread() {
    private var image: Drawable? = null
    private var url: String = urlAddress

    public override fun run() {
        try  {
            //Put your code that you want to run in here.
            val u = URL(url)
            val s: InputStream = u.openStream()
            val d = Drawable.createFromStream(s, "jim.jpg")

            cla.imageLoaded(d)
            this.setImage(d)
//            this.interrupt()
////            val lock = ReentrantLock()
////            val condition = lock.newCondition()
////            condition.signalAll()

        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun setImage(d: Drawable) {
        image = d
    }

    public fun getImage(): Drawable? {
        return image
    }
}

class VideoThread(vid: VideoView): Thread() {
    private var image: Drawable? = null
    private var video: VideoView? = vid

    public override fun run() {
        try  {
            //Put your code that you want to run in here.
            val uri: Uri = Uri.parse("http://10.14.55.5:8080/playlist.m3u8")
            video?.setVideoURI(uri)
            video?.start()
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }
}

class JIMThread: Thread() {
    private var jimGeo: GeoPoint? = null
    private var showingPlugin: Boolean = false
    private var uid: String = ""

    public override fun run() {
        try  {
            //Put your code that you want to run in here.
            val u = URL("http://10.14.55.27:8080/own-position")

            while(true) {
                while (u.readText().isEmpty()) {
                    val u = URL("http://10.14.55.27:8080/own-position")
                }
                var json: JSONObject = JSONObject()
                var jsonText: String = ""
                jsonText = u.readText()
                json = JSONObject(jsonText)
                uid = json.getString("source")
                val sensor = sensorMark()
                if (!json.isNull("coord")) {
                    jimGeo = sensor.drawCotLocal(json)
                }

                if (showingPlugin) {
                    // set up the Intent to "focus" on the map item
                    val focusIntent = Intent()
                    focusIntent.action = "com.atakmap.android.maps.FOCUS"
                    focusIntent.putExtra("uid", uid)
                    // The zoom distance is defined by ATAK using the map resolution and scale, exact number can't be used
                    //focusIntent.putExtra("useTightZoom", true)
                    // broadcast
                    AtakBroadcast.getInstance().sendBroadcast(focusIntent)
                    //this.setPlugin(false)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    public fun getJimPoint(): GeoPoint? {
        return jimGeo
    }

    public fun setPlugin(torF: Boolean) {
        this.showingPlugin = torF
        if (!torF) {
            val focusIntent = Intent()
            focusIntent.action = "com.atakmap.android.maps.UNFOCUS"
            focusIntent.putExtra("uid", uid)
            AtakBroadcast.getInstance().sendBroadcast(focusIntent)
        }
    }

}



