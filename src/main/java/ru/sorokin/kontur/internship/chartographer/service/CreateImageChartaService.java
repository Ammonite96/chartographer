package ru.sorokin.kontur.internship.chartographer.service;

import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

public interface CreateImageChartaService {
    Long createImage(int width, int height) throws SidesOutOfBoundsException;
}
