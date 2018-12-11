package com.zl.tesseract.scanner.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Utility methods for configuring the Android camera.
 *
 * @author Sean Owen
 */
@SuppressWarnings("deprecation") // camera APIs
public final class CameraConfigurationUtils {

    private static final String TAG = "CameraConfiguration";

    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {

        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size size : rawSupportedSizes) {
                previewSizesString.append(size.width).append('x').append(size.height).append(' ');
            }
            Log.i(TAG, "Supported preview sizes: " + previewSizesString);
        }

//        double screenAspectRatio = screenResolution.x / (double) screenResolution.y;

        // Find a suitable size, with max resolution
//        int maxResolution = 0;
        Camera.Size maxResPreviewSize = null;
        int diff = Integer.MAX_VALUE;
        for (Camera.Size size : rawSupportedSizes) {
            int realWidth = size.width;
            int realHeight = size.height;
            int resolution = realWidth * realHeight;
            if (resolution < MIN_PREVIEW_PIXELS) {
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
//            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
//            double distortion = Math.abs(aspectRatio - screenAspectRatio);
//            if (distortion > MAX_ASPECT_DISTORTION) {
//                continue;
//            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }

            int newDiff = Math.abs(maybeFlippedWidth - screenResolution.x) + Math.abs(maybeFlippedHeight - screenResolution.y);
            if (newDiff < diff) {
                maxResPreviewSize = size;
                diff = newDiff;
            }

            // Resolution is suitable; record the one with max resolution
//            if (resolution > maxResolution) {
//                maxResolution = resolution;
//                maxResPreviewSize = size;
//            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (maxResPreviewSize != null) {
            Point largestSize = new Point(maxResPreviewSize.width, maxResPreviewSize.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }


}