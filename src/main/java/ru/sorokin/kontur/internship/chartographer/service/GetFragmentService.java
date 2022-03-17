package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import java.io.IOException;

/**
 * Сервис для получения фрагмента изображения
 */
public interface GetFragmentService {

    /**
     * Возвращает фрагмент изображения по заданным координатам и размерам
     *
     * @param id     Id изображения
     * @param x      Координата х
     * @param y      Координата у
     * @param width  Ширина фрагмента. Максимальная ширина (в пикселях) 5000
     * @param height Высота фрагмента. Максимальная высота (в пикселях) 5000
     * @return фрагмент в виде массива byte[]
     * @throws CoordinateOutOfRangeException в случае если координаты выходят за пределы основного изображения
     * @throws SidesOutOfBoundsException в случае если размеры фрагмента выходят за пределы допустимых
     * @throws ImageChartaNotFoundException в случае если изображение с таким Id нет
     */
    byte[] getFragment(Long id, int x, int y, int width, int height)
            throws CoordinateOutOfRangeException, SidesOutOfBoundsException, ImageChartaNotFoundException, IOException;
}
