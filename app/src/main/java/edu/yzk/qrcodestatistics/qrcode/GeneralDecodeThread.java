package edu.yzk.qrcodestatistics.qrcode;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 */
final class GeneralDecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final GeneralCaptureActivity activity;
    private final Hashtable<DecodeHintType, Object> hints;
    private Handler handler;
    private boolean isOneD;
    private final CountDownLatch handlerInitLatch;

    GeneralDecodeThread(GeneralCaptureActivity activity,
                        Vector<BarcodeFormat> decodeFormats,
                        String characterSet,
                        ResultPointCallback resultPointCallback, boolean isOneD) {

        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
        this.isOneD = isOneD;
        hints = new Hashtable<DecodeHintType, Object>(3);

        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new GeneralDecodeHandler(activity, hints, isOneD);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
