package ru.sorokin.kontur.internship.chartographer.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sorokin.kontur.internship.chartographer.model.ImageCharta;
import ru.sorokin.kontur.internship.chartographer.repository.ImageChartaRepo;
import ru.sorokin.kontur.internship.chartographer.util.ImageChartaMock;
import ru.sorokin.kontur.internship.chartographer.util.LoadOpenCv;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeleteImageServiceTest {
    @Autowired
    private DeleteImageService deleteImageService;

    static {
        LoadOpenCv.loadOpenCVNativeLibrary();
    }

    @MockBean
    private ImageChartaRepo imageChartaRepo;

    @Test
    void deleteByIdTest() {
        assertDoesNotThrow(() -> {
            ImageCharta imageOriginal = ImageChartaMock.getImage();
            ImageCharta imageDelete = ImageChartaMock.getImage();
            imageDelete.setId(1L);
            Mockito.doReturn(imageDelete).when(imageChartaRepo).getImageById(imageDelete.getId());
            deleteImageService.deleteById(imageDelete.getId());
            Mockito.verify(imageChartaRepo, Mockito.times(1)).deleteById(imageDelete.getId());
            assertNotEquals(imageOriginal, imageDelete);
        });
    }

    @Test
    void deleteIfImageNotFoundTest() {
        long imageIdNotFound = 0;
        assertThrows(ImageChartaNotFoundException.class, () -> {
            Mockito.doThrow(ImageChartaNotFoundException.class).when(imageChartaRepo).getImageById(imageIdNotFound);
            deleteImageService.deleteById(imageIdNotFound);
        });
    }
}
