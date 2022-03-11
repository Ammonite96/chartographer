package ru.sorokin.kontur.internship.chartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sorokin.kontur.internship.chartographer.util.Util;
import ru.sorokin.kontur.internship.chartographer.util.PathDir;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        Util.loadOpenCVNativeLibrary();
        PathDir.setPathDir(args[0]);
        SpringApplication.run(Application.class, args);
    }
}
