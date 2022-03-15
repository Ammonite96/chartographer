package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

public interface GetImageService {
    ImageCharta getImage(Long id) throws ImageChartaNotFoundException;
}
