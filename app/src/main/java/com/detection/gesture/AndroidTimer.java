package com.detection.gesture;

import android.os.Handler;

public class AndroidTimer {

    public interface TickListener {

        void onTick();
    }

    private Handler mHandler = new Handler();

    private final TickListener mTickListener;

    private final long mDelay;

    private boolean mIsRunning;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                mTickListener.onTick();
                mHandler.postDelayed(mRunnable, mDelay);
            }
        }
    };

    public AndroidTimer(final TickListener tickListener, final int fps) {
        mTickListener = tickListener;
        mDelay = 1000 / fps;
        mIsRunning = false;
    }

    public void start() {
        mIsRunning = true;
        mHandler.postDelayed(mRunnable, 0);
    }

    public void stop() {
        mIsRunning = false;
    }
}
