package com.fhl.test.audio

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.fhl.test.audio.MediaCacheProxy

private const val WHAT_INIT: Int = 0X8000
private const val WHAT_PLAY = 0X8001
private const val WHAT_PAUSE = 0X8002
private const val WHAT_STOP = 0X8003
private const val WHAT_RELEASE = 0X8004
private const val WHAT_IS_PLAYING = 0X8005
private const val WHAT_SEEK = 0X8006
private const val WHAT_SET_URL = 0X8007


const val TAG = "MediaPlayerService"

class MediaPlayerService : IntentService("MediaPlayerService"),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnErrorListener {
    override fun onPrepared(mp: MediaPlayer?) {
        Log.i(TAG, "onPrepared")
        mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.i(TAG, "onCompletion")
        mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        Log.i(TAG, "onSeekComplete: prosition = ${mp?.currentPosition}")
        mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.w(TAG, "onError")
        mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
        return false
    }

    private val mBinder = MediaPlayerBinder()
    override fun onHandleIntent(intent: Intent?) {}
    private var mStatusListener: IMediaStatusListener? = null

    private val mHandler: Handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            //  WHAT_INIT ->
            WHAT_IS_PLAYING -> mMediaPlayer.isPlaying
            WHAT_PLAY -> {
                mMediaPlayer.start()
                mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
            }
            WHAT_PAUSE -> {
                mMediaPlayer.pause()
                mStatusListener?.onUpdateStatus(mMediaPlayer.currentState)
            }
            WHAT_STOP -> {
                mMediaPlayer.stopPlayback()
            }
            WHAT_SEEK -> {
                Log.i(TAG,"server: 更新进度: ${it.arg1}")
                mMediaPlayer.seekTo(it.arg1)
            }
            WHAT_SET_URL -> {
                mMediaPlayer.stopPlayback()
                val url = it.obj
                if (url is String) {
                    if (url.startsWith("http")) {
                        //mMediaPlayer.audioPath =
                        //    MediaCacheProxy.getInstance().getCachePatch(MediaCacheProxy.getInstance().path)

                        mMediaPlayer.audioPath =
                            MediaCacheProxy.getInstance().getCachePatch(url)
                            //MediaCacheProxy.getInstance().getCachePatch( URLEncoder.encode(url,"utf-8"))
                    } else {
                        mMediaPlayer.setAudioURI(Uri.parse(url))
                    }
                }
            }

        }
        false
    }


    private lateinit var mMediaPlayer: ZDMediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return this.mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mMediaPlayer.stopPlayback()
        mHandler.removeCallbacksAndMessages(null)
        return super.onUnbind(intent)
    }

    private inner class MediaPlayerBinder : IMediaPlayer.Stub() {
        override fun init() {
            Log.i(TAG, "media init !")
            mHandler.obtainMessage(WHAT_INIT).sendToTarget()
        }

        override fun play() {
            Log.i(TAG, "media play !")
            mHandler.obtainMessage(WHAT_PLAY).sendToTarget()
        }

        override fun pause() {
            Log.i(TAG, "media pause !")
            mHandler.obtainMessage(WHAT_PAUSE).sendToTarget()
        }

        override fun stop() {
            Log.i(TAG, "media stop !")
            mHandler.obtainMessage(WHAT_STOP).sendToTarget()
        }

        override fun release() {
            Log.i(TAG, "media release !")
            mHandler.obtainMessage(WHAT_RELEASE).sendToTarget()
        }

        override fun isPlaying(): Boolean {
            Log.i(TAG, "media isPlaying !")
            // mHandler.obtainMessage(WHAT_IS_PLAYING).sendToTarget()

            return mMediaPlayer.isPlaying
        }

        override fun setUrl(url: String?) {
            mHandler.obtainMessage(WHAT_SET_URL, url).sendToTarget()
        }

        override fun seekTo(progress: Long) {
            mHandler.obtainMessage(WHAT_SEEK, progress.toInt(),0).sendToTarget()
        }

        override fun getCurrentPosition(): Int {
            return mMediaPlayer.currentPosition
        }

        override fun getDuration(): Int {
            return mMediaPlayer.duration
        }

        override fun setOnUpdateStatus(url: IMediaStatusListener?) {
            mStatusListener = url
        }
    }

    override fun onCreate() {
        super.onCreate()
        initPlayer()
    }

    private fun initPlayer() {
        mMediaPlayer = ZDMediaPlayer(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnErrorListener(this)
        mMediaPlayer.setOnSeekCompleteListener(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

}
