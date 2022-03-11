package ru.sorokin.kontur.internship.chartographer.service;

import org.opencv.core.*;
import org.springframework.stereotype.Service;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.util.Util;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class GetFragmentService {
    private final GetImageService getImageService;

    public GetFragmentService(GetImageService getImageService) {
        this.getImageService = getImageService;
    }

    public byte[] getFragment(Long id, int x, int y, int width, int height) throws Exception {
        if (Util.checkSizeFragment(width, height)) {
            throw new SidesOutOfBoundsException("Размеры фрагмента заданны не верно. Максимальный размер(в пикселях) 5000*5000");
        }
        ImageCharta source = getImageService.getImage(id);
        Util.checkRange(x, y, source);
        Mat fragment = getFragmentFromFullImage(source, x, y, width, height);
        byte[] byteArray = convertImageFragmentToByteArray(fragment);
        fragment.release();
        if (Util.checkSizeForSplit(source.width(), source.height())) {
            source.release();
        }
        return byteArray;
    }

    public Mat getFragmentFromFullImage(Mat source, int x, int y, int width, int height) {
        Range colRange = new Range(x, source.cols());
        Range rowRange = new Range(y, source.rows());
        Mat submat = source.submat(rowRange, colRange);
        Mat fullFragment = new Mat(width, height, CvType.CV_8UC3, new Scalar(0, 0, 0));
        if (submat.width() > width && submat.height() < height) {
            Mat fragmentCut = source.submat(y, source.rows(), x, width + x);
            Util.copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        } else if (submat.width() < width && submat.height() > height) {
            Mat fragmentCut = source.submat(y, height + y, x, source.cols());
            Util.copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        } else if (submat.width() < width && submat.height() < height) {
            Mat fragmentCut = source.submat(y, source.rows(), x, source.cols());
            Util.copyToMat(fullFragment, fragmentCut, 0, 0);
            return fullFragment;
        }
        submat.release();
        fullFragment.release();
        return source.submat(new Rect(x, y, width, height));
    }

    private byte[] convertImageFragmentToByteArray(Mat fragment) throws IOException {
        byte[] byteArray = new byte[fragment.channels() * fragment.cols() * fragment.rows()];
        fragment.get(0, 0, byteArray);
        return convertMatToBufferedImageByteArray(fragment, byteArray);
    }

    private byte[] convertMatToBufferedImageByteArray(Mat fragment, byte[] array) throws IOException {
        BufferedImage image = new BufferedImage(fragment.cols(), fragment.rows(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(array, 0, data, 0, array.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", stream);
        stream.flush();
        image.getGraphics().dispose();
        return stream.toByteArray();
    }
}
