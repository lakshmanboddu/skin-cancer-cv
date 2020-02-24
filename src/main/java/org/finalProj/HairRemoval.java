package org.finalProj;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

public class HairRemoval {
    public void removeHair(){
        // Load OPEN CV CORE
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Read image to object of Mat Class
        //Mat object is an n-dimensional matrix object to store images
        Mat src = Imgcodecs.imread("Melanoma-2.jpg");

        // Convert the original image to grayscale
        Mat grayScaleImg = new Mat();
        Imgproc.cvtColor(src, grayScaleImg, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite("grayScaleSample1.jpg", grayScaleImg);

        // Perform the blackHat filtering on the grayscale image to find the hair countours
        Mat kernel = Mat.ones(5,5, CvType.CV_32F);
        Mat blackHat = new Mat();
        Imgproc.morphologyEx(src,blackHat, Imgproc.MORPH_BLACKHAT,kernel);
        Imgcodecs.imwrite("blackHat-output.jpg", blackHat);

        //Intensifying the hair countours in preparation for the inpainting algorithm
        Mat thresholdedImage = new Mat();
        Imgproc.threshold(src, thresholdedImage, 10, 255, Imgproc.THRESH_BINARY);
        Imgcodecs.imwrite("thresholded-output.jpg", thresholdedImage);

        Mat output = new Mat();
        Photo.inpaint(src, thresholdedImage, output, 1, Photo.INPAINT_TELEA);
        Imgcodecs.imwrite("output.jpg", output);

    }
}
