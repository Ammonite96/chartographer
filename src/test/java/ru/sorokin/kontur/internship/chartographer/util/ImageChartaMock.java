package ru.sorokin.kontur.internship.chartographer.util;

import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;

public class ImageChartaMock {
    private static int width = 10000;
    private static int height = 10000;

    public static ImageCharta getImage() {
        return new ImageCharta(getHeight(), getWidth(), CvType.CV_8UC3, new Scalar(0, 0, 0));
    }
    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        ImageChartaMock.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        ImageChartaMock.height = height;
    }
}
