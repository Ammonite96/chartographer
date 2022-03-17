package ru.sorokin.kontur.internship.chartographer.util.exception;

/**
 * Сообщает о том, что изображение не найдено
 */
public class ImageChartaNotFoundException extends Exception{

    public ImageChartaNotFoundException(String message) {
        super(message);
    }
}
