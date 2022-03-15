package ru.sorokin.kontur.internship.chartographer.service;

public interface InsertFragmentService {
    void insertFragment(Long id, int x, int y, int width, int height, byte[] fragmentImage) throws Exception;
}
