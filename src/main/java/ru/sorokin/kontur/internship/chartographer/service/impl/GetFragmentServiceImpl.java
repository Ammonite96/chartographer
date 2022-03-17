package ru.sorokin.kontur.internship.chartographer.service.impl;

import org.opencv.core.*;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.service.GetFragmentService;
import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static ru.sorokin.kontur.internship.chartographer.util.Util.*;

@Service
public class GetFragmentServiceImpl implements GetFragmentService {
    private final GetImageServiceImpl getImageService;

    public GetFragmentServiceImpl(GetImageServiceImpl getImageService) {
        this.getImageService = getImageService;
    }

    @Override
    public byte[] getFragment(Long id, int x, int y, int width, int height)
            throws CoordinateOutOfRangeException, SidesOutOfBoundsException, ImageChartaNotFoundException, IOException {
        if (checkSizeFragment(width, height)) {
            throw new SidesOutOfBoundsException("Размеры фрагмента заданны не верно. Максимальный размер(в пикселях) 5000*5000");
        }
        ImageCharta source = getImageService.getImage(id);
        checkRange(x, y, source);
        Mat fragment = getFragmentFromFullImage(source, x, y, width, height);
        byte[] byteArray = convertImageFragmentToByteArray(fragment);
        fragment.release();
        if (checkSizeForSplit(source.width(), source.height())) {
            source.release();
        }
        return byteArray;
    }

    /**
     * Получает фрагмент изображения из определённой области исходного изображения (папируса)
     * Если часть фрагмента была за пределами папируса, то эта часть будет закрашена чёрным.
     *
     * @param source Исходное изображение (папирус)
     * @param x      Координата Х
     * @param y      Координата У
     * @param width  Ширина фрагмента
     * @param height Высота фрагмента
     * @return Объект типа Мат библиотеки OpenCv, представляющий собой матрицу фрагмента
     */
    private Mat getFragmentFromFullImage(Mat source, int x, int y, int width, int height) {
        Range colRange = new Range(x, source.cols());
        Range rowRange = new Range(y, source.rows());
        Mat area = source.submat(rowRange, colRange);
        Mat fullFragment = new Mat(width, height, CvType.CV_8UC3, new Scalar(0, 0, 0));
        if (area.width() > width && area.height() < height) {
            Mat fragmentCut = source.submat(y, source.rows(), x, width + x);
            copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        } else if (area.width() < width && area.height() > height) {
            Mat fragmentCut = source.submat(y, height + y, x, source.cols());
            copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        } else if (area.width() < width && area.height() < height) {
            Mat fragmentCut = source.submat(y, source.rows(), x, source.cols());
            copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        }
        area.release();
        fullFragment.release();
        return source.submat(new Rect(x, y, width, height));
    }

    /**
     * Преобразует фрагмент из матрицы Mat в массив byte[] используя BufferedImage
     *
     * @param fragment Фрагмент изображения в виде матрицы Мат библиотеки OpenCv
     * @return фрагмент в виде массива byte[]
     */
    private byte[] convertImageFragmentToByteArray(Mat fragment) throws IOException {
        byte[] byteArray = new byte[fragment.channels() * fragment.cols() * fragment.rows()];
        fragment.get(0, 0, byteArray);
        return convertMatToBufferedImageByteArray(fragment, byteArray);
    }

    private byte[] convertMatToBufferedImageByteArray(Mat fragment, byte[] byteArray) throws IOException {
        BufferedImage image = new BufferedImage(fragment.cols(), fragment.rows(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(byteArray, 0, data, 0, byteArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", stream);
        stream.flush();
        image.getGraphics().dispose();
        return stream.toByteArray();
    }
}
