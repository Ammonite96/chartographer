package ru.sorokin.kontur.internship.chartographer.repository.impl;

import org.opencv.core.CvType;
import org.springframework.stereotype.Repository;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ImageChartaRepoImpl implements ImageChartaRepo {
    private final List<ImageCharta> collectImageCharta = new ArrayList<>();
    private static Long ID_IMAGE = 0L;

    @Override
    public void save(ImageCharta image) {
        image.setId(++ID_IMAGE);
        image.setType(CvType.CV_8UC3);
        image.setWidth(image.width());
        image.setHeight(image.height());
        collectImageCharta.add(image);
    }

    @Override
    public ImageCharta getImageById(Long id) throws ImageChartaNotFoundException {
        return collectImageCharta.stream()
                .filter(image -> Objects.equals(image.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ImageChartaNotFoundException("Charta with this id does not exist"));
    }

    @Override
    public void deleteById(Long id) {
        collectImageCharta.removeIf(imageCharta -> {
            imageCharta.release();
            return Objects.equals(imageCharta.getId(), id);
        });
    }
}
