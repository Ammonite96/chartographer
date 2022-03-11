package ru.sorokin.kontur.internship.chartographer.service;

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
import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InsertFragmentServiceTest {
    @Autowired
    private InsertFragmentService insertFragmentService;
    @MockBean
    ImageChartaRepo imageChartaRepo;

    static {
        LoadOpenCv.loadOpenCVNativeLibrary();
    }


    @Test
    void insertFragmentTest() {
        int x = 0;
        int y = 0;
        assertDoesNotThrow(() -> {
            ImageCharta imageOriginal = ImageChartaMock.getImage();
            ImageCharta imageAfterInsertFragment = ImageChartaMock.getImage();
            imageOriginal.setId(1L);
            Mockito.doReturn(imageOriginal).when(imageChartaRepo).getImageById(imageOriginal.getId());
            insertFragment(x, y, imageOriginal.getId());
            assertNotEquals(imageOriginal, imageAfterInsertFragment);
        });
    }

    @Test
    void insertFragmentWhenImageNotFoundTest() {
        int x = 0;
        int y = 0;
        long imageIdNotFound = 0;
        assertThrows(ImageChartaNotFoundException.class, () -> {
            Mockito.doThrow(ImageChartaNotFoundException.class).when(imageChartaRepo).getImageById(imageIdNotFound);
            insertFragment(x, y, imageIdNotFound);
        });
    }

    @Test
    void insertFragmentWhenCoordinatesOutOfBoundsRangeImageTest() {
        int x = ImageChartaMock.getWidth() + 1;
        int y = ImageChartaMock.getHeight() + 1;

        assertThrows(CoordinateOutOfRangeException.class, () -> {
            ImageCharta imageOriginal = ImageChartaMock.getImage();
            ImageCharta imageAfterInsertFragment = ImageChartaMock.getImage();
            imageOriginal.setId(1L);
            Mockito.doReturn(imageOriginal).when(imageChartaRepo).getImageById(imageOriginal.getId());
            insertFragment(x, y, imageOriginal.getId());
            assertEquals(imageOriginal, imageAfterInsertFragment);
        });
    }

    @Test
    void insertFragmentWhenSidesOutOfBoundsFragmentImageTest() {
        int x = 0;
        int y = 0;
        assertThrows(SidesOutOfBoundsException.class, () -> {
            ImageCharta imageOriginal = ImageChartaMock.getImage();
            ImageCharta imageAfterInsertFragment = ImageChartaMock.getImage();
            imageOriginal.setId(1L);
            Mockito.doReturn(imageAfterInsertFragment).when(imageChartaRepo).getImageById(imageOriginal.getId());
            ImageFragment.setWidth(ImageFragment.getWidth() + 1);
            ImageFragment.setHeight(ImageFragment.getHeight() + 1);
            insertFragment(x, y, imageOriginal.getId());
            assertEquals(imageOriginal, imageAfterInsertFragment);
        });
        ImageFragment.resetSides();
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
