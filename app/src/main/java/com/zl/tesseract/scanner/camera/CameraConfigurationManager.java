/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zl.tesseract.scanner.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.zl.tesseract.scanner.MyApplication;

/**
 * 设置相机的参数信息，获取最佳的预览界面
 * 
 */
public final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";
	// 屏幕分辨率
	private Point screenResolution;
	// 相机分辨率
	private Point cameraResolution;

	public void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) MyApplication.sAppContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
//		Point theScreenResolution = getDisplaySize(display);
//		theScreenResolution = getDisplaySize(display);

		screenResolution = getDisplaySize(display);
		Log.i(TAG, "Screen resolution: " + screenResolution);

		/** 因为换成了竖屏显示，所以不替换屏幕宽高得出的预览图是变形的 */
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;

		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}

		cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
		Log.i(TAG, "Camera resolution x: " + cameraResolution.x);
		Log.i(TAG, "Camera resolution y: " + cameraResolution.y);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private Point getDisplaySize(final Display display) {
		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (NoSuchMethodError ignore) {
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point;
	}

	public void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

//		if (safeMode) {
//			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
//		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if (afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size " + cameraResolution.x + 'x' + cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}

		/** 设置相机预览为竖屏 */
		camera.setDisplayOrientation(90);
	}

	public Point getCameraResolution() {
		return cameraResolution;
	}

	public Point getScreenResolution() {
		return screenResolution;
	}

}
