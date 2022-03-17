package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

/**
 * Сервис для удаления изображения
 */
public interface DeleteImageService {

    /**
     * Удаляет изображение по его Id
     *
     * @param id Id изображения
     * @throws ImageChartaNotFoundException в случае если такого изображения нет
     */
    void deleteById(Long id) throws ImageChartaNotFoundException;
}
