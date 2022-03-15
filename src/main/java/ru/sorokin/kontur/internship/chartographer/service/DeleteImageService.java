package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

public interface DeleteImageService {
    void deleteById(Long id) throws ImageChartaNotFoundException;
}
