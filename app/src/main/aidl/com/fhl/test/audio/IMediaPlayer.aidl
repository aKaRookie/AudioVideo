
package com.fhl.test.audio;

import com.fhl.test.audio.IMediaStatusListener;
interface IMediaPlayer {
    void init();
    void play();
    void pause();
    void stop();
    void release();
    boolean isPlaying();
    void  seekTo(long progress);
    int  getCurrentPosition();
    int  getDuration();
    void  setUrl(String url);
    void  setOnUpdateStatus(IMediaStatusListener url);
}
