package org.finalProj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws Exception {
        new File(WebController.fileBasePath).mkdir();
        SpringApplication.run(App.class, args);

    }
// gmail.com
// gmail.com/
}


