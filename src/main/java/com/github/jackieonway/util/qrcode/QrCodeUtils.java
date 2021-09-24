/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.qrcode;

import com.github.jackieonway.util.StringUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jackie
 * @version $id: QrCodeUtils.java v 1.0.2 2021-09-07 9:15 Jackie Exp $$
 */
public enum  QrCodeUtils {

    /**
     * qrcode utils instance
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeUtils.class);
    private static final String ENCODE_BARCODE_ERROR = "encode barcode error";

    private static final int BLACK = 0x000000;
    private static final int WHITE = 0xFFFFFF;

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

    private static void generateCodeToFile(String contents, int width, int height, String imgPath,
                                           Map<EncodeHintType, Object> hints, BarcodeFormat qrCode, String s) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, qrCode, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(imgPath));
        } catch (Exception e) {
            LOGGER.error(s, e);
        }
    }

    private static void createFolder(String imgPath) {
        final String filePath = imgPath.substring(0, imgPath.lastIndexOf("\\"));
        final File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    private static int getCodeWidth(int width) {
        // start guard
        int codeWidth = 3 +
                // left bars
                (7 * 6) +
                // middle guard
                5 +
                // right bars
                (7 * 6) +
                // end guard
                3;
        codeWidth = Math.max(codeWidth, width);
        return codeWidth;
    }

    private static String decodeCodeFromFIle(String imgPath, String s, Map<DecodeHintType, Object> o, String s2) {
        BufferedImage image;
        Result result;
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                LOGGER.error(s);
                return null;
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            result = new MultiFormatReader().decode(bitmap, o);
            return result.getText();
        } catch (Exception e) {
            LOGGER.error(s2, e);
        }
        return null;
    }
    public enum  QrCode{
        /**
         * qr code instance
         */
        INSTANCE;

        private static final int QRCODE_DEFAULT_HEIGHT = 220;

        private static final int QRCODE_DEFAULT_WIDTH = 220;

        /**
         * encode qr code
         * @param contents qr code contents
         * @param width qr code width
         * @param height qr code height
         * @param imgPath images Path
         * @author  Jackie
         * @since 1.0.2
         */
        public static void encode(String contents, int width, int height, String imgPath) {
            createFolder(imgPath);
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            width = width < 1 ? QRCODE_DEFAULT_WIDTH : width;
            height = height < 1 ? QRCODE_DEFAULT_HEIGHT : height;
            generateCodeToFile(contents, width, height, imgPath, hints, BarcodeFormat.QR_CODE, "encode qrcode error");
        }


        /**
         * encode qrcode
         * @param contents qrcode contents
         * @param width qrcode width
         * @param height qrcode height
         * @param outputStream qrcode outputStream
         * @author  Jackie
         * @since 1.0.2
         */
        public static void encode(String contents, int width, int height, OutputStream outputStream) {
            if (Objects.isNull(outputStream)){
                throw new NullPointerException("output stream is null");
            }
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            try {
                width = width < 1 ? QRCODE_DEFAULT_WIDTH : width;
                height = height < 1 ? QRCODE_DEFAULT_HEIGHT : height;
                BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
                MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
            } catch (Exception e) {
                LOGGER.error("encode qrcode error", e);
            }
        }

        /**
         * encode qrcode
         * @param contents qrcode contents
         * @param width qrcode width
         * @param height qrcode height
         * @return bufferedImage
         * @author  Jackie
         * @since 1.0.2
         */
        public static BufferedImage encode(String contents, int width, int height) {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            try {
                width = width < 1 ? QRCODE_DEFAULT_WIDTH : width;
                height = height < 1 ? QRCODE_DEFAULT_HEIGHT : height;
                BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
                return toBufferedImage(bitMatrix);
            } catch (Exception e) {
                LOGGER.error(ENCODE_BARCODE_ERROR, e);
            }
            return null;
        }

        /**
         * decode qrcode from path
         * @param imgPath images path
         * @return  contents
         * @author  Jackie
         * @since 1.0.2
         */
        public static String decode(String imgPath) {
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            return decodeCodeFromFIle(imgPath, "the decode image may be not exit.", hints, "decode qrcode error");
        }

        /**
         * decode qrcode from stream
         * @param inputStream qrcode inputStream
         * @return  contents
         * @author  Jackie
         * @since 1.0.2
         */
        public static String decode(InputStream inputStream) {
            if (Objects.isNull(inputStream)){
                throw new NullPointerException("input stream is null");
            }
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            BufferedImage image;
            Result result;
            try {
                image = ImageIO.read(inputStream);
                if (image == null) {
                    LOGGER.error("the decode image may be not exit.");
                    return null;
                }
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                result = new MultiFormatReader().decode(bitmap, hints);
                return result.getText();
            } catch (Exception e) {
                LOGGER.error("decode qrcode error", e);
            }
            return null;
        }

        /**
         * Create qrcode with specified hint and logo
         *
         * @param contents content
         * @param width width
         * @param height height
         * @param logoIs logo File
         * @return image buffer
         * @author jakcie
         */
        private static BufferedImage encode(String contents, int width, int height, InputStream logoIs) {
            try {
                BufferedImage qrcode = encode(contents,  width, height);
                if (Objects.isNull(qrcode)){
                    throw new NullPointerException("qr code is null");
                }
                BufferedImage logo = ImageIO.read(logoIs);
                /*
                 * set logo size 20%
                 */
                int widthLogo = Math.min(logo.getWidth(null), qrcode.getWidth() / 5);
                int heightLogo = logo.getHeight(null) > qrcode.getHeight() / 5 ? (qrcode.getHeight() / 5) : logo.getWidth(null);
                /*
                 * put logo to center position
                 */
                int x = (qrcode.getWidth() - widthLogo) / 2;
                int y = (qrcode.getHeight() - heightLogo) / 2;

                //start draw image
                Graphics2D g = qrcode.createGraphics();
                g.drawImage(logo, x, y, widthLogo, heightLogo, null);
                g.dispose();
                return qrcode;
            } catch (IOException e) {
                LOGGER.error("qrcode error",e);
            }
            return null;
        }
    }
    public enum  BarCode{
        /**
         * barcode instance
         */
        INSTANCE;
        private static final int BAR_CODE_DEFAULT_HEIGHT = 120;

        private static final int BAR_CODE_DEFAULT_WIDTH = 220;
        public static final String CONTENTS_DO_NOT_PASS_CHECKSUM = "Contents do not pass checksum";

        /**
         * encode barcode
         * @param contents barcode contents
         * @param width barcode width
         * @param height barcode height
         * @param imgPath images Path
         * @author  Jackie
         * @since 1.0.2
         */
        public static void encode(String contents, int width, int height, String imgPath) {
            if (!checkStandardUpceanChecksum(contents)) {
                throw new IllegalArgumentException(CONTENTS_DO_NOT_PASS_CHECKSUM);
            }
            createFolder(imgPath);
            width = width < 1 ? BAR_CODE_DEFAULT_WIDTH : width;
            int codeWidth = getCodeWidth(width);
            height = height < 1 ? BAR_CODE_DEFAULT_HEIGHT : height;
            generateCodeToFile(contents, codeWidth, height, imgPath, null, BarcodeFormat.EAN_13,
                    ENCODE_BARCODE_ERROR);
        }

        /**
         * encode barcode
         * @param contents barcode contents
         * @param width barcode width
         * @param height barcode height
         * @param outputStream barcode outputStream
         * @author  Jackie
         * @since 1.0.2
         */
        public static void encode(String contents, int width, int height, OutputStream outputStream) {
            if (!checkStandardUpceanChecksum(contents)) {
                throw new IllegalArgumentException(CONTENTS_DO_NOT_PASS_CHECKSUM);
            }
            if (Objects.isNull(outputStream)){
                throw new NullPointerException("output stream is null");
            }
            width = width < 1 ? BAR_CODE_DEFAULT_WIDTH : width;
            int codeWidth = getCodeWidth(width);
            height = height < 1 ? BAR_CODE_DEFAULT_HEIGHT : height;
            try {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.EAN_13, codeWidth, height, null);
                MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
            } catch (Exception e) {
                LOGGER.error(ENCODE_BARCODE_ERROR, e);
            }
        }

        /**
         * encode barcode
         * @param contents barcode contents
         * @param width barcode width
         * @param height barcode height
         * @return bufferedImage
         * @author  Jackie
         * @since 1.0.2
         */
        public static BufferedImage encode(String contents, int width, int height) {
            if (!checkStandardUpceanChecksum(contents)) {
                throw new IllegalArgumentException(CONTENTS_DO_NOT_PASS_CHECKSUM);
            }
            width = width < 1 ? BAR_CODE_DEFAULT_WIDTH : width;
            int codeWidth = getCodeWidth(width);
            height = height < 1 ? BAR_CODE_DEFAULT_HEIGHT : height;
            try {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.EAN_13, codeWidth, height, null);
                return toBufferedImage(bitMatrix);
            } catch (Exception e) {
                LOGGER.error(ENCODE_BARCODE_ERROR, e);
            }
            return null;
        }
        /**
         * encode barcode
         * @param contents barcode contents
         * @param width barcode width
         * @param height barcode height
         * @param bufferedImage not use param
         * @return bufferedImage
         * @author  Jackie
         * @since 1.0.2
         */
        private static BufferedImage encode(String contents, int width, int height, BufferedImage bufferedImage) {
            if (!checkStandardUpceanChecksum(contents)) {
                throw new IllegalArgumentException(CONTENTS_DO_NOT_PASS_CHECKSUM);
            }
            BufferedImage barcode = encode(contents,  width , height * 4 / 5);
            if (Objects.isNull(barcode)){
                throw new NullPointerException("bar code is null");
            }
            bufferedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            //start draw image
            Graphics2D g = bufferedImage.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0,0,width,height);
            g.setFont(new Font("微软雅黑", Font.PLAIN, height * 2 / 15));
            g.drawImage(barcode, 0, 0, barcode.getWidth(), barcode.getHeight(), null);
            int strWidth = g.getFontMetrics().stringWidth(contents);
            g.setColor(Color.BLACK);
            g.drawString(contents, (width - strWidth) / 2, barcode.getHeight() + height / 6);
            g.dispose();
            return bufferedImage;
        }

        public static boolean checkStandardUpceanChecksum(CharSequence contents) {
            int length = contents.length();
            if (length == 0) {
                return false;
            }
            int check = Character.digit(contents.charAt(length - 1), 10);
            return getStandardUpceanChecksum(contents.subSequence(0, length - 1)) == check;
        }

        public static String getBarCode(String barCodePrefix){
            if (StringUtils.isEmpty(barCodePrefix)){
                throw new IllegalArgumentException("bar code prefix is null");
            }
            final int length = barCodePrefix.length();
            final int barLength = 12;
            if (length != barLength){
                throw new IllegalArgumentException(
                        "Requested barCodePrefix should be 12 digits long, but got " + length);
            }
            final int checksum = getStandardUpceanChecksum(barCodePrefix);
            return barCodePrefix + checksum;
        }

        public static int getStandardUpceanChecksum(CharSequence contents) {
            int length = contents.length();
            int sum = 0;
            for (int i = length - 1; i >= 0; i -= 2) {
                int digit = contents.charAt(i) - '0';
                if (digit < 0 || digit > 9) {
                    throw new IllegalArgumentException(String.format("illegal digit : %s", digit));
                }
                sum += digit;
            }
            sum *= 3;
            for (int i = length - 2; i >= 0; i -= 2) {
                int digit = contents.charAt(i) - '0';
                if (digit < 0 || digit > 9) {
                    throw new IllegalArgumentException(String.format("illegal digit : %s", digit));
                }
                sum += digit;
            }
            return (1000 - sum) % 10;
        }

        /**
         * decode barcode from path
         * @param imgPath images path
         * @return  contents
         * @author  Jackie
         * @since 1.0.2
         */
        public static String decode(String imgPath) {
            return decodeCodeFromFIle(imgPath, "the decode image may be not exit~", null, "decode barcode " +
                    "error");
        }

        /**
         * decode barcode from stream
         * @param inputStream qrcode inputStream
         * @return  contents
         * @author  Jackie
         * @since 1.0.2
         */
        public static String decode(InputStream  inputStream) {
            if (Objects.isNull(inputStream)){
                throw new NullPointerException("input stream is null");
            }
            BufferedImage image;
            Result result;
            try {
                image = ImageIO.read(inputStream);
                if (image == null) {
                    LOGGER.error("the decode image may be not exit~");
                    return null;
                }
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                result = new MultiFormatReader().decode(bitmap, null);
                return result.getText();
            } catch (Exception e) {
                LOGGER.error("decode barcode error", e);
            }
            return null;
        }
    }
}
