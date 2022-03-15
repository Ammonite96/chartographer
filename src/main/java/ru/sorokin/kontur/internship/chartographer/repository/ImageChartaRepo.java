package ru.sorokin.kontur.internship.chartographer.repository;

import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

public interface ImageChartaRepo {
    void save(ImageCharta image);
    ImageCharta getImageById(Long id) throws ImageChartaNotFoundException;
    void deleteById(Long id);
}
