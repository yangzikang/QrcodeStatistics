package edu.yzk.qrcodestatistics.qrcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import edu.yzk.qrcodestatistics.R;

import java.util.Collection;
import java.util.HashSet;

public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int angleColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private boolean oneDBarCode;
    private boolean oneDBarCodeVertical = false;// 条形码扫描纵向
    private Context context;

    // scan line
    private boolean isFirst;
    private Rect tipFrame;
    private Rect frame;


    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();


        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        angleColor = resources.getColor(R.color.viewfinder_angle);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
        oneDBarCode = false;
        this.context = context;
        tipFrame = new Rect();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (oneDBarCode) {
            if (oneDBarCodeVertical) {
                frame = CameraManager.get().getFramingRectOneDVertical();
            } else {
                frame = CameraManager.get().getFramingRectOneD();
            }
        } else {
            frame = CameraManager.get().getFramingRect();
        }
        if (frame == null) {
            return;
        }
        if (oneDBarCodeVertical) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            //WalletLogger.d("canvas width=" + width + " height=" + height);
            //WalletLogger.d("frame width=" + frame.width() + "height=" + frame.height());
            //int angleLength = (frame.right - frame.left) / 6;
            // Draw the exterior (i.e. outside the framing rect) darkened
            paint.setColor(resultBitmap != null ? resultColor : maskColor);
            canvas.drawRect(0, 0, width, frame.top, paint);
            canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
            canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
            canvas.drawRect(0, frame.bottom + 1, width, height, paint);
            if (resultBitmap != null) {
                // Draw the opaque result bitmap over the scanning rectangle
                paint.setAlpha(OPAQUE);
                canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
            } else {
                // Draw the angle
                paint.setColor(angleColor);
                int tipLength = frame.width();
                int tipAngleLenght = tipLength / 40 * 3;
                int offset = (frame.width() - tipLength) / 2;
                int left = frame.left + offset;
                int right = frame.right - offset;
                int top = frame.top + offset;
                int bottom = frame.bottom - offset;
                tipFrame.set(left, top, right, bottom);
                canvas.drawRect(tipFrame.left, tipFrame.top, tipFrame.left + 4, tipFrame.top + tipAngleLenght, paint);
                canvas.drawRect(tipFrame.left, tipFrame.top, tipFrame.left + tipAngleLenght, tipFrame.top + 4, paint);
                canvas.drawRect(tipFrame.left, tipFrame.bottom + 1 - tipAngleLenght, tipFrame.left + 4, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.left, tipFrame.bottom - 3, tipFrame.left + tipAngleLenght, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.right - tipAngleLenght, tipFrame.top, tipFrame.right + 1, tipFrame.top + 4, paint);
                canvas.drawRect(tipFrame.right - 3, tipFrame.top, tipFrame.right + 1, tipFrame.top + tipAngleLenght, paint);
                canvas.drawRect(tipFrame.right + 1 - tipAngleLenght, tipFrame.bottom - 3, tipFrame.right + 1, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.right - 3, tipFrame.bottom + 1 - tipAngleLenght, tipFrame.right + 1, tipFrame.bottom + 1, paint);
                // Draw a red "laser scanner" line through the middle to show decoding is active
                paint.setColor(laserColor);
                paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
                scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//      int middle = frame.height() / 2 + frame.top;
//      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
                Collection<ResultPoint> currentPossible = possibleResultPoints;
                Collection<ResultPoint> currentLast = lastPossibleResultPoints;
                if (currentPossible.isEmpty()) {
                    lastPossibleResultPoints = null;
                } else {
//                    possibleResultPoints = new HashSet<ResultPoint>(5);
                    possibleResultPoints.clear();
                    lastPossibleResultPoints = currentPossible;
                    paint.setAlpha(OPAQUE);
                    paint.setColor(resultPointColor);
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                    }
                }
                if (currentLast != null) {
                    paint.setAlpha(OPAQUE / 2);
                    paint.setColor(resultPointColor);
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                    }
                }
                // Request another update at the animation interval, but only repaint the laser line,
                // not the entire viewfinder mask.
                postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
            }
        } else {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            //WalletLogger.d("canvas width=" + width + " height=" + height);
            //WalletLogger.d("frame width=" + frame.width() + "height=" + frame.height());
            //int angleLength = (frame.right - frame.left) / 6;
            // Draw the exterior (i.e. outside the framing rect) darkened
            paint.setColor(resultBitmap != null ? resultColor : maskColor);
            canvas.drawRect(0, 0, width, frame.top, paint);
            canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
            canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
            canvas.drawRect(0, frame.bottom + 1, width, height, paint);
            if (resultBitmap != null) {
                // Draw the opaque result bitmap over the scanning rectangle
                paint.setAlpha(OPAQUE);
                canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
            } else {
                // Draw the angle
                paint.setColor(angleColor);
                int tipLength = frame.width();
                int tipAngleLenght = tipLength / 40 * 3;
                int offset = (frame.width() - tipLength) / 2;
                int left = frame.left + offset;
                int right = frame.right - offset;
                int top = frame.top + offset;
                int bottom = frame.bottom - offset;
                tipFrame.set(left, top, right, bottom);
                canvas.drawRect(tipFrame.left, tipFrame.top, tipFrame.left + 4, tipFrame.top + tipAngleLenght, paint);
                canvas.drawRect(tipFrame.left, tipFrame.top, tipFrame.left + tipAngleLenght, tipFrame.top + 4, paint);
                canvas.drawRect(tipFrame.left, tipFrame.bottom + 1 - tipAngleLenght, tipFrame.left + 4, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.left, tipFrame.bottom - 3, tipFrame.left + tipAngleLenght, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.right - tipAngleLenght, tipFrame.top, tipFrame.right + 1, tipFrame.top + 4, paint);
                canvas.drawRect(tipFrame.right - 3, tipFrame.top, tipFrame.right + 1, tipFrame.top + tipAngleLenght, paint);
                canvas.drawRect(tipFrame.right + 1 - tipAngleLenght, tipFrame.bottom - 3, tipFrame.right + 1, tipFrame.bottom + 1, paint);
                canvas.drawRect(tipFrame.right - 3, tipFrame.bottom + 1 - tipAngleLenght, tipFrame.right + 1, tipFrame.bottom + 1, paint);

                // Draw a red "laser scanner" line through the middle to show decoding is active
                paint.setColor(laserColor);
                paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
                scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
                //      int middle = frame.height() / 2 + frame.top;
                //      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

                Collection<ResultPoint> currentPossible = possibleResultPoints;
                Collection<ResultPoint> currentLast = lastPossibleResultPoints;
                if (currentPossible.isEmpty()) {
                    lastPossibleResultPoints = null;
                } else {
//                    possibleResultPoints = new HashSet<ResultPoint>(5);
                    possibleResultPoints.clear();
                    lastPossibleResultPoints = currentPossible;
                    paint.setAlpha(OPAQUE);
                    paint.setColor(resultPointColor);
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                    }
                }
                if (currentLast != null) {
                    paint.setAlpha(OPAQUE / 2);
                    paint.setColor(resultPointColor);
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                    }
                }
                // Request another update at the animation interval, but only repaint the laser line,
                // not the entire viewfinder mask.
                postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
            }
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    /**
     * 扫描二维码or条形码
     *
     * @param oneDBarCode
     */
    public void setOneDBarCode(boolean oneDBarCode) {
        this.oneDBarCode = oneDBarCode;
    }

    /**
     * 设置条形码扫描框是否纵向
     *
     * @param oneDBarCodeVertical
     */
    public void setOneDBarCodeVertical(boolean oneDBarCodeVertical) {
        this.oneDBarCodeVertical = oneDBarCodeVertical;
        isFirst = false;
        invalidate();
    }

    public boolean getOneDBarCodeVertical() {
        return oneDBarCodeVertical;
    }

    public Rect getFrame(){
        return this.frame;
    }
}
