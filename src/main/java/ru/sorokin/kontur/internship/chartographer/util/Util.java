package ru.sorokin.kontur.internship.chartographer.util;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.service.GetImageService;
import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;

import java.io.File;
import java.util.UUID;

import static ru.sorokin.kontur.internship.chartographer.util.Constant.*;

public class Util {
    public static void loadOpenCVNativeLibrary() {
        nu.pattern.OpenCV.loadLocally();
    }

    /**
     * Делит изображение на 4 части и сохраняет на диск
     *
     * @param fullImage Изображение папируса
     */
    public static void splitImage(ImageCharta fullImage) {
        int width = fullImage.width();
        int height = fullImage.height();
        int countSplitImage = 4;
        Mat mat;
        if (fullImage.getAbsolutPath() == null) {
            generateFileName(fullImage, countSplitImage);
        }
        for (int i = 0; i < countSplitImage; i++) {
            mat = subMat(i, width, height, fullImage);
            Imgcodecs.imwrite(fullImage.getAbsolutPath()[i], mat);
            mat.release();
        }
        fullImage.release();
    }

    /**
     * Генерирует абсолютный путь к файлам исходного изображения
     *
     * @param image           Исходное изображение
     * @param countSplitImage кол.во частей на которое надо разделить исходное изображение
     */
    private static void generateFileName(ImageCharta image, int countSplitImage) {
        String pathDir = PathDir.getPathDir();
        String uuidFile = UUID.randomUUID().toString();
        String[] result = new String[countSplitImage];
        for (int i = 0; i < countSplitImage; i++) {
            result[i] = String.format("%s%s%d_%d_%s.bmp", pathDir, File.separator, image.getId(), i, uuidFile);
        }
        image.setAbsolutPath(result);
    }

    /**
     * Делит изображение на 4 части по высоте,
     * каждую часть возвращает в {@link Util#splitImage(ImageCharta)} для записи на диск.
     * Так же данный метод используется в {@link GetImageService}
     * при вставке распознанного фрагмента, когда исходное изображение хранится на диске
     *
     * @param i      Итератор
     * @param width  Ширина изображения
     * @param height Высота изображения
     * @param source Исходное изображение
     */
    public static Mat subMat(int i, int width, int height, ImageCharta source) {
        switch (i) {
            case 0:
                return source.submat(0, height / 4, 0, width);
            case 1:
                return source.submat(height / 4, height / 2, 0, width);
            case 2:
                return source.submat(height / 2, height - height / 4, 0, width);
            case 3:
                return source.submat(height - height / 4, height, 0, width);
            default:
                return new Mat();
        }
    }

    /**
     * Копирует фрагмент в определённую область исходного изображения
     *
     * @param source   Исходное изображение
     * @param fragment Фрагмент для копирования
     * @param x        Координата X
     * @param y        Координата У
     */
    public static void copyToMat(Mat source, Mat fragment, int x, int y) {
        Mat area = source.submat(new Rect(x, y, fragment.width(), fragment.height()));
        fragment.copyTo(area);
        area.release();
    }

    /**
     * Проверяет превышение размера изображения к {@link Constant#MAX_SIZE_FOR_SPLIT_IMAGE_CHARTA}.
     *
     * @param wight  Ширина изображения
     * @param height Высота изображения
     *               Если размер изображение больше {@link Constant#MAX_SIZE_FOR_SPLIT_IMAGE_CHARTA},
     *               то вызывается метод {@link Util#splitImage(ImageCharta)}.
     */
    public static boolean checkSizeForSplit(int wight, int height) {
        return (wight * height) >= MAX_SIZE_FOR_SPLIT_IMAGE_CHARTA;
    }

    /**
     * Проверяет размеры Исходного изображения (папируса)
     *
     * @param width  Ширина изображения
     * @param height Высота Изображения
     */
    public static boolean checkSizeImage(int width, int height) {
        return (width * height) > MAX_SIZE_IMAGE_CHARTA
                || width > MAX_WIDTH_IMAGE_CHARTA
                || height > MAX_HEIGHT_IMAGE_CHARTA
                || width == 0
                || height == 0;
    }

    /**
     * Проверяет размеры распознанного фрагмента
     *
     * @param width  Ширина фрагмента
     * @param height Высота фрагмента
     */
    public static boolean checkSizeFragment(int width, int height) {
        return (width * height) > MAX_SIZE_IMAGE_FRAGMENT
                || width > MAX_SIDES_IMAGE_FRAGMENT
                || height > MAX_SIDES_IMAGE_FRAGMENT
                || width == 0
                || height == 0;
    }

    /**
     * Проверяет, находятся ли координаты в диапазоне изображения (папируса)
     *
     * @param x      Координата Х
     * @param y      Координата Н
     * @param source Исходное изображение (папирус)
     * @throws CoordinateOutOfRangeException в случае если координаты не в диапазоне изображения
     */
    public static void checkRange(int x, int y, ImageCharta source) throws CoordinateOutOfRangeException {
        if (x > source.width() && y > source.height()) {
            String format = String.format(
                    "Координата x = %s и координата y = %s не в диапазоне, размер изображения %d * %d ",
                    x,
                    y,
                    source.width(),
                    source.height());
            throw new CoordinateOutOfRangeException(format);
        } else if (x > source.width()) {
            String format = String.format(
                    "Координата x = %s не в диапазоне, размер изображения по x = %d",
                    x,
                    source.width());
            throw new CoordinateOutOfRangeException(format);
        } else if (y > source.height()) {
            String format = String.format(
                    "Координата y = %s не в диапазоне, размер изображения по y = %d",
                    y,
                    source.height());
            throw new CoordinateOutOfRangeException(format);
        }
    }
}
