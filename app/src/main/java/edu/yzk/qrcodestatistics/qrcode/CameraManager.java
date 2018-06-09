package edu.yzk.qrcodestatistics.qrcode;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

public final class CameraManager {
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1280;
    private static final int MAX_FRAME_HEIGHT = 1080;
    public static CameraManager cameraManager;
    private final CameraConfigurationManager configManager;
    private Camera camera;
    private Rect framingRect;
    private Rect framingRectOneD;
    private Rect framingRectOneDVertical;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;

    public static final int orientationStateDefault = 0x01;
    public static final int orientationStateChangToLandScape = 0x02;
    public static final int orientationStateChangeToPortrait = 0x03;
    private int orientationState = orientationStateDefault;

    public static void init() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
    }

    public void setFramingRectInPreview() {
        this.framingRectInPreview = null;
    }

    public void setOrientationState(int orientationState) {
        this.orientationState = orientationState;
    }

    public int getOrientationState() {
        return orientationState;
    }

    public static CameraManager get() {
        init();
        return cameraManager;
    }


    private CameraManager() {
        this.configManager = new CameraConfigurationManager();
        previewCallback = new PreviewCallback(configManager);
        autoFocusCallback = new AutoFocusCallback();
    }

    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        openDriver(holder, false);
    }

    /**
     * 根据横屏 竖屏初始化相机
     *
     * @param holder
     * @param isHorizontal 是否是横屏拍摄
     * @throws IOException
     */
    public synchronized void openDriver(SurfaceHolder holder, boolean isHorizontal) throws IOException {
        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                throw new IOException();
            }
            camera.setPreviewDisplay(holder);

            if (!initialized) {
                initialized = true;
                configManager.initFromCameraParameters(camera);
            }
            if (!isHorizontal) {// 竖屏旋转 90°
                configManager.setDesiredCameraParameters(camera);
            }
        }
    }

    public synchronized void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public synchronized void startPreview() {
        if (camera != null && !previewing) {
            try {
                camera.startPreview();
                previewing = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stopPreview() {
        if (camera != null && previewing) {
            camera.setPreviewCallback(null);//必须置空再stop
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public synchronized void requestPreviewFrame(Handler[] handlers, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handlers, message);
            camera.setPreviewCallback(previewCallback);//非oneshot方式会尽力进行图像的Preview
        }
    }

    public synchronized void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
//            camera.autoFocus(autoFocusCallback);
        }
    }

    /**
     * 二维码扫描框
     *
     * @return
     */
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int width = screenResolution.x * 558 / 750;
            int height = screenResolution.y * 558 / 1334;

            if (width < height) {
                height = width;
            } else {
                width = height;
            }

            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = screenResolution.y * 388 / 1334;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
        return framingRect;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = resolution / 9 * 7; // Target 50% of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    /**
     * 条形码扫描框
     *
     * @return
     */
    public synchronized Rect getFramingRectOneD() {
        if (framingRectOneD == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);
            if (width < height) {
                height = width;
            } else {
                width = height;
            }
//	      width = width * 6 / 5;
            height = height * 1 / 2;
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRectOneD = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            //Log.d(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRectOneD;
    }

    /**
     * 条形码扫描框 纵向
     *
     * @return
     */
    public synchronized Rect getFramingRectOneDVertical() {
        if (framingRectOneDVertical == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);
            if (width < height) {
                height = width;
            } else {
                width = height;
            }
            width = width * 1 / 2;
            height = height * 3 / 2;
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            // 上移20px
            framingRectOneDVertical = new Rect(leftOffset, topOffset - 20, leftOffset + width, topOffset + height - 20);
            //Log.d(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRectOneDVertical;
    }

    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();

            if (orientationState == orientationStateChangToLandScape) {
                rect.left = rect.left * cameraResolution.x / screenResolution.x;
                rect.right = rect.right * cameraResolution.x / screenResolution.x;
                rect.top = rect.top * cameraResolution.y / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            } else {
                rect.left = rect.left * cameraResolution.y / screenResolution.x;
                rect.right = rect.right * cameraResolution.y / screenResolution.x;
                rect.top = rect.top * cameraResolution.x / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            }


            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    /**
     * 构建LuminanceSource 数据源
     * @param data  包含二维码的图像
     * @param width 图像宽
     * @param height 图像高
     * @param notRotaion90 是否旋转90，截取rect扫码框中的大小，构造的数据源
     * @return
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height, boolean notRotaion90) {
        Rect rect = getFramingRectInPreview();
        int previewFormat = configManager.getPreviewFormat();
        String previewFormatString = configManager.getPreviewFormatString();

        switch (previewFormat) {
            case PixelFormat.YCbCr_420_SP:
            case PixelFormat.YCbCr_422_SP:
                if (notRotaion90) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height());
                } else {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.top, rect.left,
                            rect.height(), rect.width()); //横向图片裁剪，上边距和左变局对换，宽高对换
                }

            default:
                if ("yuv420p".equals(previewFormatString)) {
                    if (notRotaion90) {
                        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                                rect.width(), rect.height());
                    } else {
                        return new PlanarYUVLuminanceSource(data, width, height, rect.top, rect.left,
                                rect.height(), rect.width());
                    }
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }

    public synchronized void setTorch(boolean newSetting) {
        if (camera != null && configManager != null) {
            if (newSetting != configManager.getTorchState(camera)) {
                configManager.setTorch(camera, newSetting);
            }
        }
    }

    public synchronized void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, final Camera.PictureCallback jpeg) {
        if (camera != null) {
            camera.takePicture(shutterCallback, pictureCallback, jpeg);
        }
    }

    public synchronized void setPictureSize(int screenW, int screenH) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size previewSize = getOptimalPreviewSize(supportedPreviewSizes, screenW, screenH);
            parameters.setPreviewSize(previewSize.width, previewSize.height);

            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            Camera.Size pictureSize = getOptimalPreviewSize(supportedPictureSizes, screenW, screenH);
            parameters.setPictureSize(pictureSize.width, pictureSize.height);

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            try {
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
