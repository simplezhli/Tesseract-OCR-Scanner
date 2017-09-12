package com.zl.tesseract.scanner.tess;

/**
 * 图片解析数字回调方法
 */
public interface TesseractCallback {

    void succeed(String result);

    void fail();
}
