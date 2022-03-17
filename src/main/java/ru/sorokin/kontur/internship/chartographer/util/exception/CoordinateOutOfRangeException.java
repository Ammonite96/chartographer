package ru.sorokin.kontur.internship.chartographer.util.exception;

/**
 * Сообщает о том, что координаты находятся в не диапазона изображения
 */
public class CoordinateOutOfRangeException extends Exception{

    public CoordinateOutOfRangeException(String message){
        super(message);
    }
}
