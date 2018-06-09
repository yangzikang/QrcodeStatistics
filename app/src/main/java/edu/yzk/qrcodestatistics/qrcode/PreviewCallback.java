package edu.yzk.qrcodestatistics.qrcode;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

final class PreviewCallback implements Camera.PreviewCallback {

    private final CameraConfigurationManager configManager;
    private Handler previewHandlers[];
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager) {
        this.configManager = configManager;
    }

    void setHandler(Handler[] previewHandlers, int previewMessage) {
        this.previewHandlers = previewHandlers;
        this.previewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = configManager.getCameraResolution();
        if (previewHandlers != null) {
            if (!((GeneralDecodeHandler) previewHandlers[0]).isWorking()) {
                Message message = previewHandlers[0].obtainMessage(previewMessage, cameraResolution.x,
                        cameraResolution.y, data);
                message.sendToTarget();
            }
            if (previewHandlers[1] != null && !((GeneralDecodeHandler) previewHandlers[1]).isWorking()) {
                Message message = previewHandlers[1].obtainMessage(previewMessage, cameraResolution.x,
                        cameraResolution.y, data);
                message.sendToTarget();
            }
        }
    }

}
