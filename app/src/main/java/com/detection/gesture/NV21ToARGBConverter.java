package com.detection.gesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;

public class NV21ToARGBConverter {

    private Bitmap mBitmap;

    private RenderScript script;
    private ScriptIntrinsicYuvToRGB scriptInstrinct;
    private Allocation mIn;
    private Allocation mOut;

    public NV21ToARGBConverter(Context context) {
        script = RenderScript.create(context);
        scriptInstrinct = ScriptIntrinsicYuvToRGB.create(script, Element.U8_4(script));
    }

    public Bitmap getBitmapWithARGB888FromNV21(byte[] yuvByteArray, int width, int height) {
        initIfNeed(yuvByteArray, width, height);

        mIn.copyFrom(yuvByteArray);

        scriptInstrinct.setInput(mIn);
        scriptInstrinct.forEach(mOut);

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        mOut.copyTo(mBitmap);

        return mBitmap;
    }

    private void initIfNeed(byte[] yuvByteArray, int width, int height) {
        if (mIn == null) {
            Type.Builder yuvType = new Type.Builder(script, Element.U8(script)).setX(yuvByteArray.length);
            mIn = Allocation.createTyped(script, yuvType.create(), Allocation.USAGE_SCRIPT);
        }

        if (mOut == null) {
            Type.Builder rgbaType = new Type.Builder(script, Element.RGBA_8888(script)).setX(width).setY(height);
            mOut = Allocation.createTyped(script, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }
    }
}
