package ru.sorokin.kontur.internship.chartographer.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.ImageChartaMock;
import ru.sorokin.kontur.internship.chartographer.util.ImageFragment;
import ru.sorokin.kontur.internship.chartographer.util.LoadOpenCv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ImageControllerTest {

    @Autowired
    private ImageController imageController;
    @MockBean
    private ImageChartaRepo imageChartaRepo;

    static {
        LoadOpenCv.loadOpenCVNativeLibrary();
    }

    @Test
    void createImageTest() {
        assertDoesNotThrow(() -> {
            HttpStatus expectedCode = HttpStatus.CREATED;
            HttpStatus actualCode = imageController.createImage(100, 100).getStatusCode();
            assertEquals(expectedCode, actualCode);
        });
    }

    @Test
    void insertFragmentTest() {
        int x = 0;
        int y = 0;
        ImageCharta image = ImageChartaMock.getImage();
        image.setId(1L);
        HttpStatus expectedCode = HttpStatus.OK;
        assertDoesNotThrow(() -> {
            byte[] fragmentArray = ImageFragment.getBufferedImageByteArray();
            Mockito.doReturn(image).when(imageChartaRepo).getImageById(image.getId());
            HttpStatus actualCode = imageController.insertFragment(
                    image.getId(),
                    x,
                    y,
                    ImageFragment.getWidth(),
                    ImageFragment.getHeight(),
                    fragmentArray).getStatusCode();
            assertEquals(expectedCode, actualCode);
        });

    }

    @Test
    void getFragmentTest() {
        int x = 0;
        int y = 0;
        ImageCharta image = ImageChartaMock.getImage();
        image.setId(1L);
        HttpStatus expectedCode = HttpStatus.OK;
        assertDoesNotThrow(() -> {
            Mockito.doReturn(image).when(imageChartaRepo).getImageById(image.getId());
            HttpStatus actualCode = imageController.getFragment(
                    image.getId(),
                    x,
                    y,
                    ImageFragment.getWidth(),
                    ImageFragment.getHeight()).getStatusCode();
            assertEquals(expectedCode, actualCode);
        });

    }

    @Test
    void deleteTest() {
        ImageCharta image = ImageChartaMock.getImage();
        image.setId(1L);
        HttpStatus expectedCode = HttpStatus.OK;
        assertDoesNotThrow(() -> {
            Mockito.doReturn(image).when(imageChartaRepo).getImageById(image.getId());
            HttpStatus actualCode = imageController.delete(image.getId()).getStatusCode();
            assertEquals(expectedCode, actualCode);
        });
    }
}