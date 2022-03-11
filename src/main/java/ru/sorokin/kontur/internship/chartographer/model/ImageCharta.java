package ru.sorokin.kontur.internship.chartographer.model;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ImageCharta extends Mat {
    private Long id;
    private String[] absolutPath;
    private int width;
    private int height;
    private int type;

    public ImageCharta(int height, int width, int type, Scalar scalar) {
        super(height, width, type, scalar);
    }

    public ImageCharta(int height, int width, int type) {
        super(height, width, type);
    }

    public ImageCharta(Long id, String[] absolutPath, int height, int width, int type) {
        super(height,width,type);
        this.id = id;
        this.absolutPath = absolutPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getAbsolutPath() {
        return absolutPath;
    }

    public void setAbsolutPath(String[] absolutPath) {
        this.absolutPath = absolutPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
