package com.ben.chat.client.screensharing;


import com.ben.chat.client.ErrorLogger.ErrorLogger;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenCapture {

    private static BufferedImage screenCapture;
    private  BufferedImage resizedCapture;

    public ScreenCapture(){
    }

    public Image toGrayScale(){
            BufferedImage image = new BufferedImage(resizedCapture.getWidth(), resizedCapture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = image.getGraphics();
            g.drawImage(resizedCapture, 0, 0, null);
            return image;
    }

    public void resizeImage(double Scale) {
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage image = toCompatibleImage(screenCapture, gc);
        final double SCALE = Scale;
        int w = (int) (SCALE * image.getWidth());
        int h = (int) (SCALE * image.getHeight());
        resizedCapture = getScaledInstance(image, w, h, gc);
    }

    public void captureScreen() {
        try {
            screenCapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (AWTException ex) {
            ErrorLogger.getLogger().logError("Screen capture", ex);
        }
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    private static BufferedImage toCompatibleImage(BufferedImage image, GraphicsConfiguration gc) {
        if (gc == null)
            gc = getDefaultConfiguration();
        int w = image.getWidth();
        int h = image.getHeight();
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = gc.createCompatibleImage(w, h, transparency);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }

    private static BufferedImage copy(BufferedImage source, BufferedImage target) {
        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scalex = (double) target.getWidth() / source.getWidth();
        double scaley = (double) target.getHeight() / source.getHeight();
        AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
        g2.drawRenderedImage(source, xform);
        g2.dispose();
        return target;
    }

    private static BufferedImage getScaledInstance(BufferedImage image, int width, int height, GraphicsConfiguration gc) {
        if (gc == null)
            gc = getDefaultConfiguration();
        int transparency = image.getColorModel().getTransparency();
        return copy(image, gc.createCompatibleImage(width, height, transparency));
    }

    public  Image getImage(){
        return resizedCapture;
    }
}