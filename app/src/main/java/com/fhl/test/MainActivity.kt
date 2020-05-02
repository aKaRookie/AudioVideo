package  com.fhl.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.system.Os.close
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.fhl.test.audio.IMediaPlayer
import com.fhl.test.audio.IMediaStatusListener
import com.fhl.test.audio.MediaPlayerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

const val TAG = "MainActivity"

class MainActivity : DialogActivity(), ServiceConnection, Runnable,
    HorzTextProgressView1.OnProgressChangeListener {
    override fun onStartTracking() {
        mService?.let {
            mProgressHandler.removeCallbacks(this@MainActivity)
        }
    }

    override fun onStopTracking(progress: Double) {
        mService?.let {
            Log.i(TAG, "更新进度至:position= $progress")
            it.seekTo(progress.toLong())
            mProgressHandler.removeCallbacks(this@MainActivity)
            mProgressHandler.post(this@MainActivity)
        }

    }

    //当前MediaPlayer播放状态
    private var mCurStatus = -1

    private inner class MyCallBack : IMediaStatusListener.Stub(), IMediaStatusListener {
        override fun onUpdateStatus(status: Int) {
            Log.i(TAG, "更新状态! status: $status")
            when (status) {
                3 -> {
                    //播放
                    mProgressHandler.removeCallbacks(this@MainActivity)
                    mProgressHandler.post(this@MainActivity)
                }
                5 -> {
                    //播放完成
                    tv.text = "播放"
                    home_iv_media_spectrogram.pause()
                    mProgressHandler.removeCallbacksAndMessages(null)
                }
                else -> mProgressHandler.removeCallbacksAndMessages(null)
            }
            mCurStatus = status
        }

    }

    override fun getDialogRes(): Int = R.layout.item_dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("123")
        val visView = findViewById<VisualizerView>(R.id.home_iv_media_spectrogram)

        tv.setOnClickListener {
            Log.i("MainActivity", "onclick")
            //testSoundTouch()
            mService?.let { stub ->
                if (stub.isPlaying) {
                    tv.text = "播放"
                    stub.pause()
                } else {
                    tv.text = "暂停"
                    stub.play()
                }
                if (visView.isRunning) {
                    visView.stop()
                } else {
                    visView.play()
                }
            }
        }
        tv_stop.setOnClickListener {
            mService?.let { stub ->
                stub.stop()
            }
        }
        live_progress.setOnProgressChangedListener(this)
        initWave()
        initData()
    }

    private val scheduledExecutor by lazy { Executors.newSingleThreadScheduledExecutor() }
    private var count = 0
    private fun initWave() {
        scheduledExecutor.scheduleAtFixedRate({
            val random = (0..7).random()
            home_record_wave.addLine(random)
            count += 100
            if (count >= 15_000) {
                if (scheduledExecutor.isShutdown.not()) {
                    scheduledExecutor.shutdown()
                    count = 0
                    return@scheduleAtFixedRate
                }
            }
        }, 10, 150, TimeUnit.MILLISECONDS)
    }

    private fun setDialogContent(content: String, id: Int) {
        val view = getDialogLayout()
        view?.run {
            val tv = findViewById<TextView>(R.id.dialog_tv_content)
            tv.text = content
            val progressBar = findViewById<ProgressBar>(R.id.progressbar)
            val d: BitmapDrawable = resources.getDrawable(id) as BitmapDrawable
            val dimen = resources.getDimension(R.dimen.dp_56)
            val left = (dimen - d.bitmap.width).toInt() / 2
            val top = (dimen - d.bitmap.height).toInt() / 2
            val right = left + d.bitmap.width
            val bottom = top + d.bitmap.height
            d.setBounds(
                left.absoluteValue,
                top.absoluteValue,
                right.absoluteValue,
                bottom.absoluteValue
            )
            progressBar.indeterminateDrawable = d
            progressBar.isIndeterminate = true

        }
    }

    override fun onStop() {
        dismissDialog()
        super.onStop()
    }

    override fun onDestroy() {
        destoryDialog()
        super.onDestroy()
    }

    private var mService: IMediaPlayer.Stub? = null
    private fun initData() {

        live_progress.setMaxProgress(10000.0)
        startService()

    }



    private fun startService() {
        val intent = Intent(this, MediaPlayerService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(intent)

    }

/* override fun onBindingDied(name: ComponentName?) {
super.onBindingDied(name)
Log.i("MainActivity", "onBindingDied")
}*/

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.i("MainActivity", "onServiceDisconnected")
    }

    //val url = "http://broad-video.zhidaohulian.com/audio/LVDRyGnINa/1574734440239_16000.wav"
//val url = "https://front-dev.zhidaohulian.com/music/kc.mp3"
//var url = "http://broad-video.zhidaohulian.com/audio/栏目2.m4a"
    var url = "/mnt/sdcard/myaudio/guofeng.wav"
    var mDuration = 0
    var mPostion = 0
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.i("MainActivity", "onServiceConnected")
        mService = service as IMediaPlayer.Stub
        mService!!.init()

        mService!!.setUrl(url)
        mService!!.setOnUpdateStatus(MyCallBack())
        mDuration = mService!!.duration
        mPostion = mService!!.currentPosition
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        mProgressHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        if (mCurStatus == 2 || mCurStatus == 3) {
            mProgressHandler.post(this)
        }
    }

    private fun updateUI() {
        live_progress.setCurrentProgress(mPostion.toDouble())
        live_progress.setMaxProgress(mDuration.toDouble())
    }

    private val mProgressHandler: Handler = Handler()
    override fun run() {
        mService?.let {
            mPostion = it.currentPosition
            mDuration = it.duration
//it.seekTo(3L*mPostion)
        }
        Log.i(TAG, "更新进度! Position: $mPostion")
        updateUI()
        mProgressHandler.postDelayed(this, 1000)
    }
}
