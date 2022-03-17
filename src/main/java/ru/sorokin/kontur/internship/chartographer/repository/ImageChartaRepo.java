package ru.sorokin.kontur.internship.chartographer.repository;

import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

public interface ImageChartaRepo {

    /**
     * Сохраняет изображение, и присваивает Id
     * @param image сохраняемый объект харты
     */
    void save(ImageCharta image);

    /**
     * Получает изображение по его Id
     * @param id Id изображения в хранилище
     * @return Объект с заданным Id
     * @throws ImageChartaNotFoundException в случае если такого изображения нет
     */
    ImageCharta getImageById(Long id) throws ImageChartaNotFoundException;

    /**
     * Удаляет изображение по его Id
     * @param id Id изображения
     */
    void deleteById(Long id);
}
