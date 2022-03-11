package ru.sorokin.kontur.internship.chartographer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.ImageChartaMock;
import ru.sorokin.kontur.internship.chartographer.util.ImageFragment;
import ru.sorokin.kontur.internship.chartographer.util.LoadOpenCv;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
public class GetFragmentServiceTest {
    @Autowired
    private GetFragmentService getFragmentService;
    @Autowired
    private InsertFragmentService insertFragmentService;
    @MockBean
    private ImageChartaRepo imageChartaRepo;

    static {
        LoadOpenCv.loadOpenCVNativeLibrary();
    }

    @Test
    void getFragmentTest() {
        int x = 0;
        int y = 0;
        Assertions.assertDoesNotThrow(() -> {
            ImageCharta image = ImageChartaMock.getImage();
            image.setId(1L);
            Mockito.doReturn(image).when(imageChartaRepo).getImageById(image.getId());
            insertFragment(x, y, image.getId());
            byte[] fragmentExpected = ImageFragment.getBufferedImageByteArray();
            byte[] fragmentActual = getFragmentService.getFragment(
                    image.getId(),
                    x,
                    y,
                    ImageFragment.getWidth(),
                    ImageFragment.getHeight());
            assertArrayEquals(fragmentExpected, fragmentActual);
        });
    }

    @Test
    void insertAndGetFragmentCrossingBorderImageTest() {
        Assertions.assertDoesNotThrow(() -> {
            ImageCharta image = ImageChartaMock.getImage();
            image.setId(1L);
            ImageCharta fragment = ImageFragment.getFragment();
            ImageFragment.getMatByteArray();
            int x = image.width() - fragment.width() / 2;
            int y = (image.height() - fragment.height()) / 2;
            byte[] byteArrayExpectedFragment = ImageFragment.getBufferedImageByteArrayFragmentSplit();
            Mockito.doReturn(image).when(imageChartaRepo).getImageById(image.getId());
            insertFragment(x, y, image.getId());
            byte[] resultFragment = getFragmentService.getFragment(
                    image.getId(),
                    x,
                    y,
                    fragment.width(),
                    fragment.height());
            Assertions.assertArrayEquals(byteArrayExpectedFragment, resultFragment);
            ImageFragment.resetSides();
        });
    }

    private void insertFragment(int x, int y, Long id) throws Exception {
        insertFragmentService.insertFragment(
                id,
                x,
                y,
                ImageFragment.getWidth(),
                ImageFragment.getHeight(),
                ImageFragment.getBufferedImageByteArray());
    }
}
