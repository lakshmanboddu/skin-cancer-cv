package org.finalProj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        File input = new File("Melanoma-2.jpg");

        //Reading the image
        BufferedImage image = ImageIO.read(input);

        //Saving the image with a different name
        File op = new File("sample.jpg");
        ImageIO.write(image, "jpg", op);

        System.out.println("image Saved");
    }

}

