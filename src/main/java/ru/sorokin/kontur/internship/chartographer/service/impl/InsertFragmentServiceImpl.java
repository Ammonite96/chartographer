package ru.sorokin.kontur.internship.chartographer.service.impl;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.service.InsertFragmentService;
import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static ru.sorokin.kontur.internship.chartographer.util.Util.*;

@Service
public class InsertFragmentServiceImpl implements InsertFragmentService {
    private final GetImageServiceImpl getImageService;

    public InsertFragmentServiceImpl(GetImageServiceImpl getImageService) {
        this.getImageService = getImageService;
    }

    @Override
    public void insertFragment(Long id, int x, int y, int width, int height, byte[] fragmentImage)
            throws CoordinateOutOfRangeException, SidesOutOfBoundsException, ImageChartaNotFoundException, IOException {
        if (checkSizeFragment(width, height)) {
            throw new SidesOutOfBoundsException("Размеры фрагмента заданны не верно. Максимальный размер(в пикселях) 5000*5000");
        }
        ImageCharta source = getImageService.getImage(id);
        checkRange(x, y, source);
        ImageCharta fragment = convertByteArrayToMatImage(width, height, fragmentImage);
        insert(x, y, source, fragment);
        if (checkSizeForSplit(source.width(), source.height())) {
            splitImage(source);
            source.release();
        }
        fragment.release();
    }

    /**
     * Преобразует массив byte[] в матрицу Mat библиотеки OpenCv, используя BufferedImage
     *
     * @param width         Ширина
     * @param height        Высота
     * @param fragmentImage фрагмент в виде массива byte[]
     * @return Объект фрагмента в виде матрицы
     */
    private ImageCharta convertByteArrayToMatImage(int width, int height, byte[] fragmentImage) throws IOException {
        ImageCharta fragment = new ImageCharta(height, width, CvType.CV_8UC3);
        BufferedImage image = byteArrayToBufferedImage(fragmentImage);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] buf = Arrays.copyOf(data, data.length);
        image.getGraphics().dispose();
        fragment.put(0, 0, buf);
        return fragment;
    }

    private BufferedImage byteArrayToBufferedImage(byte[] array) throws IOException {
        InputStream in = new ByteArrayInputStream(array);
        return ImageIO.read(in);
    }

    /**
     * Вставляет распознанный фрагмент в определённую область изображения по указанным координатам.
     * В том случае, если фрагмент пересекает границы исходного изображения, фрагмент обрезается по
     * границам.
     *
     * @param x        Координата Х
     * @param y        Координата У
     * @param source   Исходное изображение (Папирус)
     * @param fragment Восстановленный фрагмент
     */
    private void insert(int x, int y, ImageCharta source, ImageCharta fragment) {
        if (x == 0 && y == 0 || x <= fragment.cols() && y <= fragment.rows()) {
            if (fragment.total() > source.total()) {
                ranges(x, y, source.cols(), source.rows(), source, fragment);
            } else {
                copyToMat(source, fragment, x, y);
            }
        } else if (x == 0) {
            ranges(x, y, fragment.cols(), source.rows(), source, fragment);
        } else if (y == 0) {
            ranges(x, y, source.cols(), fragment.rows(), source, fragment);
        } else if (x > fragment.cols() || y > fragment.rows()) {
            ranges(x, y, source.cols(), source.rows(), source, fragment);
        }
    }

    /**
     * Определяет область в исходном изображении в зависимости от координат и размера папируса
     *
     * @param x               Координата Х
     * @param y               Координата У
     * @param paramSideFirst  Сторона изображения в зависимости от координаты Х
     * @param paramSideSecond Сторона изображения в зависимости от координаты У
     * @param source          Исходное изображение
     * @param fragment        Восстановленный фрагмент
     */
    private void ranges(int x, int y, int paramSideFirst, int paramSideSecond, ImageCharta source, ImageCharta fragment) {
        Range colRange = new Range(x, paramSideFirst);
        Range rowRange = new Range(y, paramSideSecond);
        cutFragment(x, y, colRange, rowRange, source, fragment);
    }

    /**
     * Определяет зависимость размеров области вставки с размерами фрагмента.
     *
     * @param x        Координата Х
     * @param y        Координата У
     * @param colRange Область исходного изображения по ширине
     * @param rowRange Область исходного изображения по высоте
     * @param source   Исходное изображение (папирус)
     * @param fragment Распознанный фрагмент
     */
    private void cutFragment(int x, int y, Range colRange, Range rowRange, ImageCharta source, ImageCharta fragment) {
        Mat sourceArea = source.submat(rowRange, colRange);
        Mat fragmentCut;
        if (sourceArea.total() > fragment.total()
                || sourceArea.width() > fragment.width()
                || sourceArea.height() > fragment.height()) {
            fragmentCut = cutFragmentOutside(fragment, sourceArea);
        } else {
            fragmentCut = fragment.submat(0, sourceArea.rows(), 0, sourceArea.cols());
        }
        copyToMat(source, fragmentCut, x, y);
        sourceArea.release();
        fragmentCut.release();
    }

    /**
     * Обрезает фрагмент по ширине или высоте в зависимости от того какая сторона фрагмента ближе к
     * границам исходного изображения и пересекает их
     *
     * @param fragment Распознанный фрагмент
     * @param sourceArea Область вставки фрагмента,
     *                   определяется в {@link InsertFragmentServiceImpl#cutFragment(int, int, Range, Range, ImageCharta, ImageCharta)}
     * @return Объект фрагмента в виде матрицы Mat библиотеки OpenCv.
     */
    private Mat cutFragmentOutside(ImageCharta fragment, Mat sourceArea) {
        Mat fragmentArea = new Mat();
        if (sourceArea.width() > fragment.width() && sourceArea.height() < fragment.height()) {
            fragmentArea = fragment.submat(
                    0,
                    fragment.height() - (fragment.height() - sourceArea.height()),
                    0,
                    fragment.width());
        } else if (sourceArea.width() < fragment.width() && sourceArea.height() > fragment.height()) {
            fragmentArea = fragment.submat(
                    0,
                    fragment.height(),
                    0,
                    fragment.width() - (fragment.width() - sourceArea.width()));
        }
        return fragmentArea;
    }
}
