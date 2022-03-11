package ru.sorokin.kontur.internship.chartographer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sorokin.kontur.internship.chartographer.service.CreateImageChartaService;
import ru.sorokin.kontur.internship.chartographer.service.DeleteImageService;
import ru.sorokin.kontur.internship.chartographer.service.GetFragmentService;
import ru.sorokin.kontur.internship.chartographer.service.InsertFragmentService;
import ru.sorokin.kontur.internship.chartographer.util.exception.CoordinateOutOfRangeException;
import ru.sorokin.kontur.internship.chartographer.util.exception.ImageChartaNotFoundException;
import ru.sorokin.kontur.internship.chartographer.util.exception.SidesOutOfBoundsException;


@Controller
@RequestMapping("/chartas")
public class ImageController {
    private final CreateImageChartaService createImageChartaService;
    private final InsertFragmentService insertFragmentService;
    private final GetFragmentService getFragmentService;
    private final DeleteImageService deleteImageService;
    private static final String MIME_BMP = "image/bmp";

    public ImageController(CreateImageChartaService createImageChartaService,
                           InsertFragmentService insertFragmentService,
                           GetFragmentService getFragmentService,
                           DeleteImageService deleteImageService) {
        this.createImageChartaService = createImageChartaService;
        this.insertFragmentService = insertFragmentService;
        this.getFragmentService = getFragmentService;
        this.deleteImageService = deleteImageService;
    }

    @ExceptionHandler(ImageChartaNotFoundException.class)
    public ResponseEntity<?> imageChartaNotFoundException() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CoordinateOutOfRangeException.class)
    public ResponseEntity<?> coordinateNotRangeException() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SidesOutOfBoundsException.class)
    public ResponseEntity<?> sidesOutOfBoundException(){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping()
    public ResponseEntity<Long> createImage(@RequestParam(value = "width") Integer width,
                                              @RequestParam(value = "height") Integer height) throws SidesOutOfBoundsException {
        Long id = createImageChartaService.createImage(width, height);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PostMapping("{id}")
    public ResponseEntity<?> insertFragment(@PathVariable(value = "id") Long id,
                                            @RequestParam(value = "x") Integer x,
                                            @RequestParam(value = "y") Integer y,
                                            @RequestParam(value = "width") Integer width,
                                            @RequestParam(value = "height") Integer height,
                                            @RequestBody() byte[] imageFragment) throws Exception {
        insertFragmentService.insertFragment(id, x, y, width, height, imageFragment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{id}", produces = MIME_BMP)
    public ResponseEntity<byte[]> getFragment(@PathVariable(value = "id") Long id,
                                                     @RequestParam(value = "x") Integer x,
                                                     @RequestParam(value = "y") Integer y,
                                                     @RequestParam(value = "width") Integer width,
                                                     @RequestParam(value = "height") Integer height) throws Exception {

        byte[] fragment = getFragmentService.getFragment(id, x, y, width, height);
        return new ResponseEntity<>(fragment, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws ImageChartaNotFoundException {
        deleteImageService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
