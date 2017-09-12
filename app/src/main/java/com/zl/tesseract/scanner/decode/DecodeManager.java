package com.zl.tesseract.scanner.decode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.zl.tesseract.R;

/**
 * 二维码解析管理。
 */
public class DecodeManager {

    public void showCouldNotReadQrCodeFromScanner(Context context, final OnRefreshCameraListener listener) {
        new AlertDialog.Builder(context).setTitle(R.string.notification)
                .setMessage(R.string.could_not_read_qr_code_from_scanner)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.refresh();
                        }
                    }
                })
                .show();
    }

    public interface OnRefreshCameraListener {
        void refresh();
    }
}
