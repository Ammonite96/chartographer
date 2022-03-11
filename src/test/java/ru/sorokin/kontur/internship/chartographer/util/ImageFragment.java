package ru.sorokin.kontur.internship.chartographer.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageFragment {
    private static int width = 5000;
    private static int height = 5000;

    public static ImageCharta getFragment() {
        return new ImageCharta(getHeight(), getWidth(), CvType.CV_8UC3, new Scalar(255, 0, 0));
    }

    public static ImageCharta getFragmentSplit() {
        ImageCharta fullFragmentSplit = new ImageCharta(getHeight(), getWidth(), CvType.CV_8UC3);
        ImageCharta fragment = getFragment();
        Mat submat = fragment.submat(new Rect(0, 0, width / 2, height));
        Mat roi = fullFragmentSplit.submat(new Rect(0, 0, submat.width(), submat.height()));
        submat.copyTo(roi);
        return fullFragmentSplit;
    }

    public static byte[] getMatByteArray() {
        ImageCharta fragment = ImageFragment.getFragment();
        byte[] byteArray = new byte[fragment.channels() * fragment.cols() * fragment.rows()];
        fragment.get(0, 0, byteArray);
        return byteArray;
    }

    public static byte[] getBufferedImageByteArray() throws IOException {
        ImageCharta fragment = getFragment();
        byte[] matByteArray = getMatByteArray();
        return convertMatToBufferedImageByteArray(fragment, matByteArray);
    }

    public static byte[] getByteArrayFragmentSplit() {
        ImageCharta fragmentSplitVertical = getFragmentSplit();
        byte[] result = new byte[
                fragmentSplitVertical.channels() *
                        fragmentSplitVertical.cols() *
                        fragmentSplitVertical.rows()];
        fragmentSplitVertical.get(0, 0, result);
        return result;
    }

    public static byte[] getBufferedImageByteArrayFragmentSplit() throws IOException {
        ImageCharta fragmentSplit = getFragmentSplit();
        byte[] byteArrayFragmentSplit = getByteArrayFragmentSplit();
        return convertMatToBufferedImageByteArray(fragmentSplit, byteArrayFragmentSplit);
    }

    private static byte[] convertMatToBufferedImageByteArray(Mat fragment, byte[] array) throws IOException {
        BufferedImage image = new BufferedImage(fragment.cols(), fragment.rows(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(array, 0, data, 0, array.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", stream);
        stream.flush();
        return stream.toByteArray();
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        ImageFragment.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        ImageFragment.height = height;
    }

    public static void resetSides() {
        setWidth(5000);
        setHeight(5000);
    }
}
