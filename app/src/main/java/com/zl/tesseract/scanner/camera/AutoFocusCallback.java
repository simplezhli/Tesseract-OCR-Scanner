/*
 * Copyright (C) 2010 ZXing authors
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
import android.os.Message;
import android.util.Log;

final class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final String TAG = AutoFocusCallback.class.getName();
    private static final long AUTO_FOCUS_INTERVAL_MS = 1300L; //自动对焦时间

    private Handler mAutoFocusHandler;
    private int mAutoFocusMessage;

    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.mAutoFocusHandler = autoFocusHandler;
        this.mAutoFocusMessage = autoFocusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (mAutoFocusHandler != null) {
            Message message = mAutoFocusHandler.obtainMessage(mAutoFocusMessage, success);
            mAutoFocusHandler.sendMessageDelayed(message, AUTO_FOCUS_INTERVAL_MS);
            mAutoFocusHandler = null;
        } else {
            Log.v(TAG, "Got auto-focus callback, but no handler for it");
        }
    }
}
