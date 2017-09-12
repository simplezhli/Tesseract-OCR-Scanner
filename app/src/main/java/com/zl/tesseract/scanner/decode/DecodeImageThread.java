package com.zl.tesseract.scanner.decode;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.Result;
import com.zl.tesseract.scanner.utils.QrUtils;

/**
 *
 * 解析图像二维码线程
 */
public class DecodeImageThread implements Runnable {
    private static final int MAX_PICTURE_PIXEL = 256;
    private byte[] mData;
    private int mWidth;
    private int mHeight;
    private String mImgPath;
    private DecodeImageCallback mCallback;

    public DecodeImageThread(String imgPath, DecodeImageCallback callback) {
        this.mImgPath = imgPath;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        if (null == mData) {
            if (!TextUtils.isEmpty(mImgPath)) {
                Bitmap bitmap = QrUtils.decodeSampledBitmapFromFile(mImgPath, MAX_PICTURE_PIXEL, MAX_PICTURE_PIXEL);
                this.mData = QrUtils.getYUV420sp(bitmap.getWidth(), bitmap.getHeight(), bitmap);
                this.mWidth = bitmap.getWidth();
                this.mHeight = bitmap.getHeight();
            }
        }

        if (mData == null || mData.length == 0 || mWidth == 0 || mHeight == 0) {
            if (null != mCallback) {
                mCallback.decodeFail(0, "No image data");
            }
            return;
        }

        final Result result = QrUtils.decodeImage(mData, mWidth, mHeight);

        if (null != mCallback) {
            if (null != result) {
                mCallback.decodeSucceed(result);
            } else {
                mCallback.decodeFail(0, "Decode image failed.");
            }
        }
    }
}
