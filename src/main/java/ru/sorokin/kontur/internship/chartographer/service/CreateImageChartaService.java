package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

/**
 * Сервис создания изображения
 */
public interface CreateImageChartaService {

    /**
     * Создаёт изображение по заданным параметрам ширины и высоты
     *
     * @param width  Ширина изображения. Максимальная ширина (в пикселях) 20000
     * @param height Высота изображения. Максимальная высота (в пикселях) 50000
     * @return Id созданного изображения
     * @throws SidesOutOfBoundsException в случае если один из параметров выходит за пределы допустимых
     */
    Long createImage(int width, int height) throws SidesOutOfBoundsException;
}
