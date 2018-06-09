package edu.yzk.qrcodestatistics.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;


public final class EncodingHandler {
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xfffFFFFF;

    /**
     * 根据code生成条形码
     *
     * @param str
     * @param widthAndHeight
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCode(String str, int widthAndHeight)
            throws WriterException {
        return createOverlayQRCode(str, widthAndHeight, null, null);
    }

    /**
     * 根据code生成条形码
     *
     * @param str
     * @param widthAndHeight
     * @param overlay        中间的小图标
     * @param bitmap         用户提供的返回对象
     * @return
     * @throws WriterException
     */
    public static Bitmap createOverlayQRCode(String str, int widthAndHeight, Bitmap overlay, Bitmap bitmap)
            throws WriterException {
        if (str == null || str.length() == 0) {
            return bitmap;
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
        int[] pixels = toBufferedImage(matrix);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        if (bitmap == null || widthAndHeight < width || widthAndHeight < height) {
            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if (overlay != null) {
            int deltaHeight = height - overlay.getHeight();
            int deltaWidth = width - overlay.getWidth();
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(overlay, deltaWidth / 2, deltaHeight / 2, null);
        }
        return bitmap;
    }

    /**
     * Renders a int[] pixels as an image, where "false" bits are rendered
     * as white, and "true" bits are rendered as black.
     */
    public static int[] toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        return pixels;
    }

    /**
     * 根据str生成条形码
     *
     * @param str
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createBarcode(String str, int width, int height)
            throws WriterException {
        return createBarcode(str, width, height, null);
    }

    public static int getBarcodeLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        Code128Writer awriter = new Code128Writer();
        boolean[] result = awriter.encode(str);
        if (result != null) {
            return result.length;
        } else {
            return 0;
        }

    }

    /**
     * 根据str生成条形码
     *
     * @param str
     * @param width
     * @param height
     * @param bitmap 返回结果参数
     * @return
     */
    public static Bitmap createBarcode(String str, int width, int height, Bitmap bitmap)
            throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);

        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.CODE_128, width, height, hints);
        width = matrix.getWidth();
        height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }
}
