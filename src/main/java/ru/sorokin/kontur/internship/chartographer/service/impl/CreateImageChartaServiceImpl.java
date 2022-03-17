package ru.sorokin.kontur.internship.chartographer.service.impl;

import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.service.CreateImageChartaService;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import static ru.sorokin.kontur.internship.chartographer.util.Util.*;

@Service
public class CreateImageChartaServiceImpl implements CreateImageChartaService {
    private final ImageChartaRepo imageChartaRepo;

    public CreateImageChartaServiceImpl(ImageChartaRepo imageChartaRepo) {
        this.imageChartaRepo = imageChartaRepo;
    }

    @Override
    public Long createImage(int width, int height) throws SidesOutOfBoundsException {
        if (checkSizeImage(width, height)) {
            throw new SidesOutOfBoundsException("Размеры изображения заданны не верно. Максимальный размер(в пикселях) 20000*50000");
        }
        ImageCharta image = new ImageCharta(height, width, CvType.CV_8UC3, new Scalar(0, 0, 0));
        imageChartaRepo.save(image);
        if (checkSizeForSplit(width, height)) {
            splitImage(image);
            image.release();
        }
        return image.getId();
    }
}
