package org.finalProj;

import org.opencv.core.Core;

public class App {

    public static void main(String[] args) {
//        https://www.jetbrains.com/help/idea/content-roots.html#
//        File input = new File("Melanoma-2.jpg");
//
//        //Reading the image
//        BufferedImage image = ImageIO.read(input);
//
//        //Saving the image with a different name
//        File op = new File("sample.jpg");
//        ImageIO.write(image, "jpg", op);
//
//        System.out.println("image Saved");
        // Load OPEN CV CORE
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        AsymmetryDetection obj = new AsymmetryDetection();
        System.out.println(obj.symmetry);

    }

}

