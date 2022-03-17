package ru.sorokin.kontur.internship.chartographer.service.impl;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.service.GetImageService;
import ru.sorokin.kontur.internship.chartographer.util.Constant;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

import static ru.sorokin.kontur.internship.chartographer.util.Util.checkSizeForSplit;
import static ru.sorokin.kontur.internship.chartographer.util.Util.subMat;

@Service
public class GetImageServiceImpl implements GetImageService {
    private final ImageChartaRepo imageChartaRepo;

    public GetImageServiceImpl(ImageChartaRepo imageChartaRepo) {
        this.imageChartaRepo = imageChartaRepo;
    }

    @Override
    public ImageCharta getImage(Long id) throws ImageChartaNotFoundException {
        ImageCharta imageById = imageChartaRepo.getImageById(id);
        if (imageById.total() == 0) {
            ImageCharta imageCharta = new ImageCharta(
                    imageById.getId(),
                    imageById.getAbsolutPath(),
                    imageById.getHeight(),
                    imageById.getWidth(),
                    imageById.getType());
            if (checkSizeForSplit(imageCharta.width(), imageCharta.height())) {
                joinImageAfterSplit(imageCharta);
                imageById.release();
                return imageCharta;
            }
        }
        return imageById;
    }

    /**
     * Вызывается, при вставке и при получении фрагмента в методе {@link GetImageService#getImage(Long)},
     * если размер изображения превышает {@link Constant#MAX_SIZE_FOR_SPLIT_IMAGE_CHARTA}
     * Данный метод получает абсолютный путь к файлам изображения, и собирает эти изображения в одно целое.
     */
    private void joinImageAfterSplit(ImageCharta source) {
        Mat imageFragment;
        for (int i = 0; i < source.getAbsolutPath().length; i++) {
            imageFragment = Imgcodecs.imread(source.getAbsolutPath()[i]);
            Mat matFr = subMat(i, source.width(), source.height(), source);
            imageFragment.copyTo(matFr);
            matFr.release();
            imageFragment.release();
        }
    }
}
