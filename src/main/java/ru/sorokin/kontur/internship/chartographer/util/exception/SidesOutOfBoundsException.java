package ru.sorokin.kontur.internship.chartographer.util.exception;

/**
 * Сообщает о том, что размеры изображений выходят за пределы допустимых
 */
public class SidesOutOfBoundsException extends Exception {

    public SidesOutOfBoundsException(String message) {
        super(message);
    }
}
