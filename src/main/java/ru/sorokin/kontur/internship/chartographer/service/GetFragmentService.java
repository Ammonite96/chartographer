package ru.sorokin.kontur.internship.chartographer.service;

public interface GetFragmentService {
    byte[] getFragment(Long id, int x, int y, int width, int height) throws Exception;
}
