package ru.sorokin.kontur.internship.chartographer.service;

import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class DeleteImageService {
    private final ImageChartaRepo imageChartaRepo;
    private final GetImageService getImageService;

    public DeleteImageService(ImageChartaRepo imageChartaRepo, GetImageService getImageService) {
        this.imageChartaRepo = imageChartaRepo;
        this.getImageService = getImageService;
    }

    public void deleteById(Long id) throws ImageChartaNotFoundException {
        ImageCharta image = getImageService.getImage(id);
        if (image.getAbsolutPath() != null) {
            Arrays.stream(image.getAbsolutPath()).map(Path::of).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        image.release();
        imageChartaRepo.deleteById(id);
    }
}
