package edu.yzk.qrcodestatistics.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import edu.yzk.qrcodestatistics.R;

import java.util.Vector;

public final class GeneralCaptureActivityHandler extends Handler {

    private static final String TAG = GeneralCaptureActivityHandler.class.getSimpleName();
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final GeneralCaptureActivity activity;
    private final GeneralDecodeThread decodeThread[] = new GeneralDecodeThread[2];
    private State state;
    private boolean isDone = false;
    private Handler handlers[] = new Handler[2];

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public GeneralCaptureActivityHandler(GeneralCaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
                                         String characterSet) {
        this.activity = activity;
        decodeThread[0] = new GeneralDecodeThread(activity, DecodeFormatManager.ONE_D_FORMATS, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()), true); //只做条形码水平扫
        decodeThread[0].start();
        handlers[0] = decodeThread[0].getHandler();
        if (!activity.isDecodeOneD()) { //只扫条形码时关闭
            decodeThread[1] = new GeneralDecodeThread(activity, decodeFormats, characterSet,
                    new ViewfinderResultPointCallback(activity.getViewfinderView()), false); //二维码+条形码竖直扫
            decodeThread[1].start();
            handlers[1] = decodeThread[1].getHandler();
        }

        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
                break;
            case R.id.restart_preview:
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
                state = State.SUCCESS;
                if (!isDone) {
                    isDone = true; //保证只有一个结果
                    Bundle bundle = message.getData();
                    Bitmap barcode = bundle == null ? null :
                            (Bitmap) bundle.getParcelable(BARCODE_BITMAP);
                    activity.handleDecode((Result) message.obj, barcode);
                }
                break;
            case R.id.decode_failed:
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(handlers, R.id.decode);
                break;
            case R.id.return_scan_result:
                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();
                break;
            case R.id.launch_product_query:
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                activity.startActivity(intent);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(R.id.decode_succeeded);
        Message quit = Message.obtain(decodeThread[0].getHandler(), R.id.quit);
        quit.sendToTarget();
        if (!activity.isDecodeOneD()) {
            quit = Message.obtain(decodeThread[1].getHandler(), R.id.quit);
            quit.sendToTarget();
        }
        try {
            decodeThread[0].join();
            if (!activity.isDecodeOneD()) {
                decodeThread[1].join();
            }
        } catch (InterruptedException e) {
            // continue
        }
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(handlers, R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            activity.drawViewfinder();
        }
    }

}
