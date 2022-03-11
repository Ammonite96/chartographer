package ru.sorokin.kontur.internship.chartographer.service;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.Constant;
import ru.sorokin.kontur.internship.chartographer.util.Util;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

@Service
public class GetImageService {
    private final ImageChartaRepo imageChartaRepo;

    public GetImageService(ImageChartaRepo imageChartaRepo) {
        this.imageChartaRepo = imageChartaRepo;
    }

    public ImageCharta getImage(Long id) throws ImageChartaNotFoundException {
        ImageCharta imageById = imageChartaRepo.getImageById(id);
        if (imageById.total() == 0) {
            ImageCharta imageCharta = new ImageCharta(
                    imageById.getId(),
                    imageById.getAbsolutPath(),
                    imageById.getHeight(),
                    imageById.getWidth(),
                    imageById.getType());
            if (Util.checkSizeForSplit(imageCharta.width(), imageCharta.height())) {
                joinImageAfterSplit(imageCharta);
                imageById.release();
                return imageCharta;
            }
        }
        return imageById;
    }

    /**
     * Вызывается, при вставке и при получении фрагмента в методе {@link GetImageService#getImage(Long)},
     * если размер изображения превышает {@link Constant#MAX_SIZE_FOR_SPLIT_IMAGE_CHARTA()}
     */
    private void joinImageAfterSplit(ImageCharta image) {
        Mat imageFragment;
        for (int i = 0; i < image.getAbsolutPath().length; i++) {
            imageFragment = Imgcodecs.imread(image.getAbsolutPath()[i]);
            Mat matFr = Util.subMat(i, image.width(), image.height(), image);
            imageFragment.copyTo(matFr);
            matFr.release();
            imageFragment.release();
        }
    }
}
