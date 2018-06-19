package edu.yzk.qrcodestatistics.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import edu.yzk.qrcodestatistics.R;
import edu.yzk.qrcodestatistics.form.FormActivity;

import java.io.IOException;
import java.util.Vector;

public class GeneralCaptureActivity extends Activity implements Callback {

    private GeneralCaptureActivityHandler handler;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;

    private boolean decodeOneD = false;
    private boolean isPermissionGranted = true; //相机相关权限是否被允许

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        viewfinderView = findViewById(R.id.viewfinder_view);
        surfaceView = findViewById(R.id.preview_view);

        surfaceView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        viewfinderView.setOneDBarCode(decodeOneD);
        CameraManager.init();
        hasSurface = false;

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface && isPermissionGranted) {
            initCamera(surfaceHolder);
        }
    }

    private void closeScanActvity() {
        GeneralCaptureActivity.this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        CameraManager.get().setOrientationState(CameraManager.orientationStateDefault);
        CameraManager.get().setFramingRectInPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        String resultString = result.getText();
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra("result", resultString);
        startActivity(intent);
        //Toast.makeText(this,resultString,Toast.LENGTH_LONG).show();
    }



    private void initCamera(final SurfaceHolder surfaceHolder) {
        //6.0 camera运行时权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);//申请WRITE_EXTERNAL_STORAGE权限
        }

        try {
            CameraManager.get().openDriver(surfaceHolder);//开启相机，会自动开始Preview
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (handler == null) {
            handler = new GeneralCaptureActivityHandler(GeneralCaptureActivity.this, decodeFormats, null);//创建解析线程调度线程
        }

    }

    public boolean isDecodeOneD() {
        return decodeOneD;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

}
