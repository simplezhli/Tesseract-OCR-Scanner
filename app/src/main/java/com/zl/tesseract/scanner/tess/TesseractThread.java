package com.zl.tesseract.scanner.tess;

import android.graphics.Bitmap;

/**
 *
 * 解析拍照数字线程
 */
public class TesseractThread implements Runnable {

    private Bitmap mBitmap;
    private TesseractCallback mCallback;

    public TesseractThread(Bitmap mBitmap, TesseractCallback callback) {
        this.mBitmap = mBitmap;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        if (mBitmap == null && null != mCallback) {
            mCallback.fail();
            return;
        }
        mCallback.succeed(TessEngine.Generate().detectText(mBitmap));
    }
}
