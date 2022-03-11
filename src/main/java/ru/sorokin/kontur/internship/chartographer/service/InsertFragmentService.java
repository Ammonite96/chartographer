package ru.sorokin.kontur.internship.chartographer.service;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.Util;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class InsertFragmentService {
    private final GetImageService getImageService;

    public InsertFragmentService(GetImageService getImageService) {
        this.getImageService = getImageService;
    }

    public void insertFragment(Long id, int x, int y, int width, int height, byte[] fragmentImage) throws Exception {
        if (Util.checkSizeFragment(width, height)) {
            throw new SidesOutOfBoundsException("Размеры фрагмента заданны не верно. Максимальный размер(в пикселях) 5000*5000");
        }
        ImageCharta source = getImageService.getImage(id);
        Util.checkRange(x, y, source);
        ImageCharta fragment = convertByteArrayToMatImage(width, height, fragmentImage);
        insert(x, y, source, fragment);
        if (Util.checkSizeForSplit(source.width(), source.height())) {
            Util.splitImage(source);
            source.release();
        }
        fragment.release();
    }

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

    private void insert(int x, int y, ImageCharta source, ImageCharta fragment) {
        if (x == 0 && y == 0 || x <= fragment.cols() && y <= fragment.rows()) {
            if (fragment.total() > source.total()) {
                ranges(x, y, source.cols(), source.rows(), source, fragment);
            } else {
                Util.copyToMat(source, fragment, x, y);
            }
        } else if (x == 0) {
            ranges(x, y, fragment.cols(), source.rows(), source, fragment);
        } else if (y == 0) {
            ranges(x, y, source.cols(), fragment.rows(), source, fragment);
        } else if (x > fragment.cols() || y > fragment.rows()) {
            ranges(x, y, source.cols(), source.rows(), source, fragment);
        }
    }

    private void ranges(int x, int y, int paramSideFirst, int paramSideSecond, ImageCharta source, ImageCharta fragment) {
        Range colRange = new Range(x, paramSideFirst);
        Range rowRange = new Range(y, paramSideSecond);
        cutFragment(x, y, colRange, rowRange, source, fragment);
    }

    private void cutFragment(int x, int y, Range colRange, Range rowRange, ImageCharta source, ImageCharta fragment) {
        Mat sourceSubmatRange = source.submat(rowRange, colRange);
        Mat fragmentSubmat;
        if (sourceSubmatRange.total() > fragment.total()
                || sourceSubmatRange.width() > fragment.width()
                || sourceSubmatRange.height() > fragment.height())
            fragmentSubmat = cutFragmentOutside(x, y, source, fragment, sourceSubmatRange);
        else fragmentSubmat = fragment.submat(0, sourceSubmatRange.rows(), 0, sourceSubmatRange.cols());
        Util.copyToMat(source, fragmentSubmat, x, y);
        sourceSubmatRange.release();
        fragmentSubmat.release();
    }

    private Mat cutFragmentOutside(int x, int y, ImageCharta source, ImageCharta fragment, Mat sourceSubmatRange) {
        Mat fragmentSubmat;
        if (sourceSubmatRange.width() > fragment.width() && sourceSubmatRange.height() < fragment.height()) {
            fragmentSubmat = fragment.submat(
                    0,
                    fragment.height() - (fragment.height() - sourceSubmatRange.height()),
                    0,
                    fragment.width());
            return fragmentSubmat;
        } else if (sourceSubmatRange.width() < fragment.width() && sourceSubmatRange.height() > fragment.height()) {
            fragmentSubmat = fragment.submat(
                    0,
                    fragment.height(),
                    0,
                    fragment.width() - (fragment.width() - sourceSubmatRange.width()));
            return fragmentSubmat;
        } else {
            fragmentSubmat = source.submat(new Rect(x, y, fragment.width(), fragment.height()));
            fragment.copyTo(fragmentSubmat);
            return fragmentSubmat;
        }
    }
}
