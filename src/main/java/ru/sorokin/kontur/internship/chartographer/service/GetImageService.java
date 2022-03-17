package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

/**
 * Вспомогательный сервис
 */
public interface GetImageService {

    /**
     * Получает объект из репозитория, и в случае необходимости устанавливает некоторые параметры.
     *
     * @param id Id изображения
     * @return Объект с заданным Id
     * @throws ImageChartaNotFoundException если изображения с таким id нет
     */
    ImageCharta getImage(Long id) throws ImageChartaNotFoundException;
}
