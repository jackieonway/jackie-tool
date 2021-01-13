package com.github.jackieonway.util;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class ImageText {

    public static void main(String[] args) {
        File file = new File("C:\\Users\\heyu\\Desktop\\other\\img");
        File[] f = file.listFiles();
        assert f != null;
        for (File file2 : f) {
            writerText(file2,"asd");
            //logo(file2);

        }
    }

    //水印图标
    public static void logo(File file) {

        BufferedImage logo;
        try {
            //File file2 = new File("C:/Users/Administrator/Desktop/65724_1408730_93.png");
            File file2 = new File("C:\\Users\\xxx\\Desktop\\other\\U{YDGYDL(PKR)_8JXKEV}MM.png");
            logo = ImageIO.read(file2);
            int lw = logo.getWidth();
            int lh = logo.getHeight();

            BufferedImage dst = ImageIO.read(file);
            Graphics g = dst.getGraphics();
            int dw = dst.getWidth();
            int dh = dst.getHeight();
            int x = dw - lw - 10;
            int y = dh - lh - 100;

            //功能是logo图像，在a.jpg的xy坐标写入
            g.drawImage(logo, x, y, null);
            g.dispose();
            ImageIO.write(dst, "jpg", file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    // 添加外部字体
    public static Font loadFont(String fontFileName, float fontSize) { // 第一个参数是外部字体名，第二个是字体大小
        try {
            File file = new File(fontFileName);
            FileInputStream aixing = new FileInputStream(file);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
            Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
            aixing.close();
            return dynamicFontPt;
        } catch (Exception e) {// 异常处理
            return new Font("宋体", Font.PLAIN, 50);
        }
    }


    //水印文字
    public static void writerText(File file, String text) {
        if (file.exists()) {
            try {
                // 获取读到的图片
                BufferedImage image = ImageIO.read(file);
                //
                int w = image.getWidth();
                int h = image.getHeight();
                // name 文件路径 w 文件宽 h 文件高
                Random random = new Random();
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setColor(new Color(random.nextInt(255), random.nextInt(255),
                        random.nextInt(255), random.nextInt(150) + 100));
                //外部字体
                Font font = new Font("宋体", Font.BOLD, 40);
                g.setFont(font);

                FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
                Rectangle rec = font.getStringBounds(text, frc).getBounds();
                double tw = rec.getWidth();
                double th = rec.getHeight();

                double x = w - tw - 10;
                double y = h - 10;
                // 1.编写文字 2.文字显示横坐标 3.文字显示纵坐标
                g.drawString(text, (int) x, (int) y);
                // 1.图片对象 2.图片扩展名 3.读取的文件
                ImageIO.write(image, "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}