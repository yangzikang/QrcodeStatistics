package edu.yzk.qrcodestatistics.qrcode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import edu.yzk.qrcodestatistics.R;

import java.util.Hashtable;


final class GeneralDecodeHandler extends Handler {

    private boolean isWorking = false;   //是否正在解析
    private Object synchronizedTag = new Object(); //锁标志
    private final GeneralCaptureActivity activity;
    private final MultiFormatReader multiFormatReader;
    private boolean isBarcodeThread;

    GeneralDecodeHandler(GeneralCaptureActivity activity, Hashtable<DecodeHintType, Object> hints, boolean isBarcodeThread) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.activity = activity;
        this.isBarcodeThread = isBarcodeThread;
    }


    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode:
                setWorking(true);
                decode((byte[]) message.obj, message.arg1, message.arg2);
                setWorking(false);
                break;
            case R.id.quit:
                if (Looper.myLooper() != null) {
                    //noinspection ConstantConditions
                    Looper.myLooper().quit();
                }
                break;
        }
    }

    private void decode(byte[] data, int width, int height) {
        Result rawResult = null;

        //二维码线程的识别和方向无关，不做旋转,只有1Dbarcode才可能旋转
        if (isBarcodeThread) {
            if (CameraManager.get().getOrientationState() != CameraManager.orientationStateChangToLandScape) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                int tmp = width; // Here we are swapping, that's the difference to #11
                width = height;
                height = tmp;
                data = rotatedData;
            }
        }
        //二维码线程不做源图像旋转，但数据源需要做90度旋转后取样
        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height, isBarcodeThread);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            // 采用MultiFormatReader解析图像，可以解析多种数据格式
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            re.printStackTrace();
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            try {
                Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundle.putParcelable(GeneralDecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                message.setData(bundle);
                message.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setWorking(boolean isWorking) {
        synchronized (synchronizedTag) {
            this.isWorking = isWorking;
        }
    }

    public boolean isWorking() {
        synchronized (synchronizedTag) {
            return isWorking;
        }
    }
}
