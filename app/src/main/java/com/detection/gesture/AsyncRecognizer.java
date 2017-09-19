package com.detection.gesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class AsyncRecognizer {

    public interface TextListener {

        @MainThread
        void onTextReceived(@NonNull final String text);
    }

    private final TextListener mTextListener;

    private String mString = "";
    private int mInt = 0;

    private final Handler mMainThreadHandler;
    private final Handler mBackgroundHandler;
    private NV21ToARGBConverter mNV21ToARGBConverter;

    public AsyncRecognizer(@NonNull final Context context, final TextListener textListener) {
        mNV21ToARGBConverter = new NV21ToARGBConverter(context);
        mMainThreadHandler = new Handler();

        HandlerThread handlerThread = new HandlerThread("Background handler thread");
        handlerThread.start();
        mBackgroundHandler = new Handler(handlerThread.getLooper());

        mTextListener = textListener;
    }

    /**
     * Вызывается при нажатии на кнопку play.
     */
    public void startRecognition() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                // Это происходит в фоне
                // TODO: отправка на сервер события начала распознавания
            }
        });
    }

    /**
     * Вызывается при нажатии на кнопку stop.
     */
    public void stopRecognition() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                // Это происходит в фоне
                // TODO: отправка на сервер события конца распознавания
            }
        });
    }

    /**
     * Отправка frame.
     */
    public void sendFrame(final byte[] frame, final int width, final int height) {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                // Это происходит в фоне

                // Конвертируем из NV21 в нормальный RGB
                Bitmap bitmap = mNV21ToARGBConverter.getBitmapWithARGB888FromNV21(frame, width, height);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Теперь в стриме лежат байты jpg
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                // TODO: отправка на сервер байтов jpg

                // Получение текущего распознанного текста
                // Здесь должен получатся накопленный за предыдущие фотки текст
                // Текущая фотка, скорее всего, ещё не успеет обработаться
                final String text = getRecognizedText();
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextListener.onTextReceived(text);
                    }
                });
            }
        });
    }

    private String getRecognizedText() {
        // TODO: получения текущего распознанного текста с сервера

        // Сейчас просто заглушка
        mString = mInt++ + "\n" + mString;
        return mString;
    }
}
