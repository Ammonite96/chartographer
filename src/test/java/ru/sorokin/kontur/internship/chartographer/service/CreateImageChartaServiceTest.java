package ru.sorokin.kontur.internship.chartographer.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sorokin.kontur.internship.chartographer.util.Constant;
import ru.sorokin.kontur.internship.chartographer.util.LoadOpenCv;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateImageChartaServiceTest {
    @Autowired
    private CreateImageChartaService createImageChartaService;

    static {
        LoadOpenCv.loadOpenCVNativeLibrary();
    }

    @Test
    void createImageTest() {
        assertDoesNotThrow(() -> {
            createImageChartaService.createImage(1000, 1000);
        });
    }

    @Test
    void createImageWhenSidesEqualsZeroTest() {
        int width = 0;
        int height = 0;
        assertThrows(SidesOutOfBoundsException.class, () -> createImageChartaService.createImage(width, height));
    }

    @Test
    void createImageSidesOfBoundTest() {
        int width = Constant.MAX_WIDTH_IMAGE_CHARTA() + 1;
        int height = Constant.MAX_HEIGHT_IMAGE_CHARTA() + 1;
        assertThrows(SidesOutOfBoundsException.class, () -> createImageChartaService.createImage(width, height));
    }

}
