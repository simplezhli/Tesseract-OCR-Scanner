/*
 * Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.zl.tesseract.scanner.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The implementation
 * encapsulates the steps needed to take preview-sized images, which are used for both preview and decoding.
 */
public final class CameraManager {

    private static CameraManager sCameraManager;

    private final CameraConfigurationManager mConfigManager;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to clear the handler so
     * it will only receive one message.
     */
    private final PreviewCallback mPreviewCallback;
    /** Auto-focus callbacks arrive here, and are dispatched to the Handler which requested them. */
    private final AutoFocusCallback mAutoFocusCallback;
    private Camera mCamera;
    private boolean mInitialized;
    private boolean mPreviewing;
    private boolean useAutoFocus;

    private CameraManager() {
        this.mConfigManager = new CameraConfigurationManager();
        mPreviewCallback = new PreviewCallback(mConfigManager);
        mAutoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Initializes this static object with the Context of the calling Activity.
     */
    public static void init() {
        if (sCameraManager == null) {
            sCameraManager = new CameraManager();
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return sCameraManager;
    }

    /**
     * Opens the mCamera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the mCamera will draw preview frames into.
     * @throws IOException Indicates the mCamera driver failed to open.
     */
    public boolean openDriver(SurfaceHolder holder) throws IOException {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                if (mCamera != null) {
                    // setParameters 是针对魅族MX5做的。MX5通过Camera.open()拿到的Camera 对象不为null
                    Camera.Parameters mParameters = mCamera.getParameters();
                    mCamera.setParameters(mParameters);
                    mCamera.setPreviewDisplay(holder);

                    String currentFocusMode = mCamera.getParameters().getFocusMode();
                    useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);

                    if (!mInitialized) {
                        mInitialized = true;
                        mConfigManager.initFromCameraParameters(mCamera);
                    }
                    mConfigManager.setDesiredCameraParameters(mCamera);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public boolean closeDriver() {
        if (mCamera != null) {
            try {
                mCamera.release();
                mInitialized = false;
                mPreviewing = false;
                mCamera = null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 打开或关闭闪光灯
     *
     * @param open 控制是否打开
     * @return 打开或关闭失败，则返回false。
     */
    public boolean setFlashLight(boolean open) {
        if (mCamera == null || !mPreviewing) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return false;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (null == flashModes || 0 == flashModes.size()) {
            // Use the screen as a flashlight (next best thing)
            return false;
        }
        String flashMode = parameters.getFlashMode();
        if (open) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        } else {
            if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Asks the mCamera hardware to begin drawing preview frames to the screen.
     */
    public boolean startPreview() {
        if (mCamera != null && !mPreviewing) {
            try {
                mCamera.startPreview();
                mPreviewing = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Tells the mCamera to stop drawing preview frames.
     */
    public boolean stopPreview() {
        if (mCamera != null && mPreviewing) {
            try {
                // 停止预览时把callback移除.
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
                mPreviewCallback.setHandler(null, 0);
                mAutoFocusCallback.setHandler(null, 0);
                mPreviewing = false;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[] in the
     * message.obj field, with width and height encoded as message.arg1 and message.arg2, respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            mPreviewCallback.setHandler(handler, message);
            mCamera.setOneShotPreviewCallback(mPreviewCallback);
        }
    }

    /**
     * Asks the mCamera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            mAutoFocusCallback.setHandler(handler, message);
            // Log.d(TAG, "Requesting auto-focus callback");
            if (useAutoFocus) {
                try {
                    mCamera.autoFocus(mAutoFocusCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){

        mCamera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
    }

    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = new ArrayList<String>(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }
}
