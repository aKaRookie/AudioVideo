package com.fhl.test.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

public class ZDMediaPlayer {
    private static final String TAG = ZDMediaPlayer.class.getSimpleName();
    private static final int ERROR_PROGRESS = -1;
    // settable by the client
    private Uri mUri;
    private Map<String, String> mHeaders;
    private int mDuration;
    // the progress while suspend
    private int mProgress = ERROR_PROGRESS;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a player object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the player object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mPrepareState = STATE_PREPARED;

    private MediaPlayer mMediaPlayer = null;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private int mCurrentBufferPercentage;
    private int mCurrentQuality;//当前播放歌曲的音质

    public Context context;

    public ZDMediaPlayer(Context context) {
        this.context = context;
        initPlayer();
    }

    private Context getContext() {
        return context;
    }

    private void initPlayer() {
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setAudioPath(String path) {
        try {
            setAudioURI(Uri.parse(path));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void setAudioURI(Uri uri) {
        setAudioURI(uri, null);
    }

    /**
     * @hide
     */
    public void setAudioURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openAudio();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAudio() {
        if (mUri == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // Tell the music playback service to pause
       /* Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "stop");
        i.putExtra("from", "audio");
        getContext().sendBroadcast(i);*/

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(getContext(), mUri, mHeaders);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            mPrepareState = STATE_PREPARING;

        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }


    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            mPrepareState = STATE_PREPARED;

            Log.d(TAG, "OnPreparedListener: mTargetState:" + mTargetState + ",mSeekWhenPrepared:" + mSeekWhenPrepared);
//            isFocusLoss = !requestAudioFocus();
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;

                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener =
            new MediaPlayer.OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
                }
            };
    private MediaPlayer.OnErrorListener mErrorListener =
            new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    mPrepareState = STATE_PREPARED;


                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                    return true;
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }


    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }


    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, player will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /*
     * release the media player in any state
     */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mPrepareState = STATE_PREPARED;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    public boolean isPrepared() {
        return mPrepareState == STATE_PREPARED;
    }

    public void start() {
        if (mMediaPlayer != null && isInPlaybackState()) {
            Log.e(TAG, "start 1");
            try {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;

                mPrepareState = STATE_PREPARED;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mTargetState = STATE_PLAYING;
        }
    }


    public void pause() {
        if (isInPlaybackState()) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mCurrentState = STATE_PAUSED;
                    mProgress = getCurrentPosition();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        if (mMediaPlayer != null)
            mProgress = getCurrentPosition();
        release(false);
        Log.d(TAG, "suspend" + mProgress);
    }

    public void resume() {
        Log.d(TAG, "resume" + mProgress);//播放本地音乐，点静音后，此时是没有声音输出的    2.再操作下拉中的关机，关机后，再开机起来时瞬间有声音输出，音乐是暂停播放的（其他模式起来 都会在播放）
        /*
    	if (mProgress >= 0 && requestAudioFocus() && mMediaPlayer == null){
    		openAudio();
    		if (mProgress >= 0 ){
	    		seekTo(mProgress);
	    		mProgress = ERROR_PROGRESS;
    		}
    	}
        */
        if (isPrepared()) {
            start();
        }
    }

    public boolean isPause() {
        try {
            return /*isInPlaybackState()*/mCurrentState == STATE_PAUSED && mMediaPlayer != null && mMediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // cache duration as mDuration for faster access
    public int getDuration() throws IllegalStateException {
        if (isInPlaybackState()) {
            if (mDuration > 0) {
                return mDuration;
            }

            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() throws IllegalStateException {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return ERROR_PROGRESS;
    }

    public void seekTo(int msec) {
        if (isInPlaybackState() && (msec >= 0 && msec <= getDuration())) {
            try {
                mMediaPlayer.seekTo(msec);
                mSeekWhenPrepared = 0;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            mSeekWhenPrepared = msec;
        }

        mProgress = msec;
    }

    public boolean isPlaying() {
        try {
            return /*isInPlaybackState()*/ mMediaPlayer != null && mMediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    public String getAudioPath() {
        if (mUri != null) {
            return mUri.toString();
        }
        return null;
    }

    public int getAudioSessionId() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getAudioSessionId();
        return 0;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(leftVolume, rightVolume);

    }

    public boolean isTargetPlaying() {
        return mTargetState == STATE_PLAYING;
    }

    public void setQuality(int quality) {
        mCurrentQuality = quality;
    }

    public int getQuality() {
        return mCurrentQuality;
    }

    public int getCurrentState() {
        return mCurrentState;
    }

}
