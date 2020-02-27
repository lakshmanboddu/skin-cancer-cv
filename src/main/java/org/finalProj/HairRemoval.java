package org.finalProj;


import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_photo.INPAINT_TELEA;
import static org.bytedeco.opencv.global.opencv_photo.inpaint;

public class HairRemoval {
    public static void removeHair(){
        //Read image to object of Mat Class
        //Mat object is an n-dimensional matrix object to store images
        Mat src = imread("src/resources/Melanoma-2.jpg");
        Display.display(src,"Input");

        // Convert the original image to grayscale
        Mat grayScaleImg = new Mat();
        cvtColor(src, grayScaleImg, COLOR_BGR2GRAY);
        imwrite("src/resources/grayScaleSample1.jpg", grayScaleImg);
        Display.display(grayScaleImg, "Gray Scale Image");

        // Perform the blackHat filtering on the grayscale image to find the hair countours
        Mat kernel = Mat.ones(5,5, CV_32F).asMat();
        Mat blackHat = new Mat();
        morphologyEx(grayScaleImg,blackHat, MORPH_BLACKHAT,kernel);
        imwrite("src/resources/blackHat-output.jpg", blackHat);

        //Intensifying the hair countours in preparation for the inpainting algorithm
        Mat thresholdedImage = new Mat();
        threshold(blackHat, thresholdedImage, 10, 255, THRESH_BINARY);
        imwrite("src/resources/thresholded-output.jpg", thresholdedImage);
        Display.display(thresholdedImage, "Thresholded Image");

        Mat output = new Mat();
        inpaint(src, thresholdedImage, output, 1, INPAINT_TELEA);
        imwrite("src/resources/output.jpg", output);
        Display.display(output, "Output");
    }

//    public static void main(String[] args) {
//        HairRemoval hr= new HairRemoval();
//        hr.removeHair();
//    }
}
