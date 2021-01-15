package com.github.jackieonway.util.qrcode;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jackie
 */
public enum QrCodeUtils {
    /**
     * QrCodeUtils 实例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeUtils.class);
    private static final int QRCODE_DEFAULT_HEIGHT = 220;

    private static final int QRCODE_DEFAULT_WIDTH = 220;

    private static final int BLACK = 0x000000;
    private static final int WHITE = 0xFFFFFF;


    /**
     * @param data     二维码内容
     * @param path     二维码中心logo文件地址带名字
     * @param response
     */
    public static void outputQrCodeImage(String data, String path, HttpServletResponse response) {
        BufferedImage image = null;
        if (StringUtils.isEmpty(path)) {
            image = createQrCode(data);
        } else {
            File file = new File(path);
            image = createQrCodeWithLogo(data,  file);
        }
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("qrcode error",e);
        }
    }


    /**
     * @param data     二维码内容
     * @param path     二维码中心logo文件地址带名字
     * @param width    二维码宽度
     * @param height   二维码高度
     * @param response
     */
    public static void outputQrCodeImage(String data, String path, int width, int height, HttpServletResponse response) {
        BufferedImage image = null;
        if (StringUtils.isEmpty(path)) {
            image = createQrCode(data, width, height);
        } else {
            File file = new File(path);
            image = createQrCodeWithLogo(data, width, height, file);
        }
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("qrcode error",e);
        }
    }

    /**
     * Create qrcode with default settings
     *
     * @param data
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCode(String data) {
        return createQrCode(data, QRCODE_DEFAULT_WIDTH, QRCODE_DEFAULT_HEIGHT);
    }

    /**
     * Create qrcode with default charset
     *
     * @param data content
     * @param width width
     * @param height height
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCode(String data, int width, int height) {
        return createQrCode(data, StandardCharsets.UTF_8.displayName(), width, height);
    }

    /**
     * Create qrcode with specified charset
     *
     * @param data content
     * @param charset charset
     * @param width width
     * @param height height
     * @return
     * @author jakcie
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BufferedImage createQrCode(String data, String charset, int width, int height) {
        Map hint = new HashMap();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.CHARACTER_SET, charset);

        return createQrCode(data, charset, hint, width, height);
    }

    /**
     * Create qrcode with specified hint
     *
     * @param data content
     * @param charset charset
     * @param hint hint
     * @param width width
     * @param height height
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCode(String data, String charset, Map<EncodeHintType, ?> hint, int width,
                                             int height) {
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE,
                    width, height, hint);
            return toBufferedImage(matrix);
        } catch (Exception e) {
            LOGGER.error("qrcode error",e);
            return null;
        }
    }

    /**
     * Create qrcode with specified hint
     *
     * @param data content
     * @param charset charset
     * @param hint hint
     * @param width width
     * @param height height
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCodeWithColor(String data, String charset, Map<EncodeHintType, ?> hint,
                                                      int width,
                                                      int height, int color) {
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE,
                    width, height, hint);
            return toBufferedImageWithColor(matrix, color);
        } catch (Exception e) {
            LOGGER.error("qrcode error",e);
            return null;
        }
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    private static BufferedImage toBufferedImageWithColor(BitMatrix matrix, int color) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    /**
     * Create qrcode with default settings and logo
     *
     * @param data content
     * @param logoFile logo file
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCodeWithLogo(String data, File logoFile) {
        return createQrCodeWithLogo(data, QRCODE_DEFAULT_WIDTH, QRCODE_DEFAULT_HEIGHT, logoFile);
    }

    /**
     * Create qrcode with default charset and logo
     *
     * @param data content
     * @param width width
     * @param height height
     * @param logoFile logo file 
     * @return 
     * @author jakcie
     */
    private static BufferedImage createQrCodeWithLogo(String data, int width, int height, File logoFile) {
        return createQrCodeWithLogo(data, StandardCharsets.UTF_8.displayName(), width, height, logoFile);
    }

    /**
     * Create qrcode with specified charset and logo
     *
     * @param data content
     * @param charset charset
     * @param width width
     * @param height height
     * @param logoFile logo file
     * @return
     * @author jakcie
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BufferedImage createQrCodeWithLogo(String data, String charset, int width, int height,
                                                     File logoFile) {
        Map hint = new HashMap();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.CHARACTER_SET, charset);

        return createQrCodeWithLogo(data, charset, hint, width, height, logoFile);
    }

    /**
     * Create qrcode with specified hint and logo
     *
     * @param data content
     * @param charset charset
     * @param hint hint
     * @param width width
     * @param height height
     * @param logoFile logo File
     * @return
     * @author jakcie
     */
    private static BufferedImage createQrCodeWithLogo(String data, String charset, Map<EncodeHintType, ?> hint,
                                                     int width, int height, File logoFile) {
        try {
            BufferedImage qrcode = createQrCode(data, charset, hint, width, height);
            BufferedImage logo = ImageIO.read(logoFile);
            /*
             * 设置logo的大小,设置为二维码图片的20%,因为过大会盖掉二维码
             */
            int widthLogo = Math.min(logo.getWidth(null), qrcode.getWidth() / 5);
            int heightLogo = logo.getHeight(null) > qrcode.getHeight() / 5 ? (qrcode.getHeight() / 5) : logo.getWidth(null);
            /*
             * logo放在中心
             */
            int x = (qrcode.getWidth() - widthLogo) / 2;
            int y = (qrcode.getHeight() - heightLogo) / 2;

            //开始绘制图片
            Graphics2D g = qrcode.createGraphics();
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.dispose();
            return qrcode;
        } catch (Exception e) {
            LOGGER.error("qrcode error",e);
            return null;
        }
    }

    /**
     * Return base64 for image
     *
     * @param image buffer image
     * @return
     * @author jakcie
     */
    public static String getImageBase64String(BufferedImage image) {
        String result = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStream b64 = new Base64OutputStream(os);
            ImageIO.write(image, "png", b64);
            result = os.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            LOGGER.error("qrcode error",e);
        }
        return result;
    }

    /**
     * Decode the base64Image data to image
     *
     * @param base64ImageString
     * @param file
     * @author jakcie
     */
    public static void convertBase64StringToImage(String base64ImageString, File file) {
        FileOutputStream os;
        try {
            Base64 d = new Base64();
            byte[] bs = d.decode(base64ImageString);
            os = new FileOutputStream(file.getAbsolutePath());
            os.write(bs);
            os.close();
        } catch (Exception e) {
            LOGGER.error("qrcode error",e);
        }
    }


}
