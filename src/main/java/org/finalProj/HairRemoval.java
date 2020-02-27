package org.finalProj;


import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_photo.INPAINT_TELEA;
import static org.bytedeco.opencv.global.opencv_photo.inpaint;

public class HairRemoval {
    public void removeHair(){
        //Read image to object of Mat Class
        //Mat object is an n-dimensional matrix object to store images
        Mat src = imread("Melanoma-2.jpg");

        // Convert the original image to grayscale
        Mat grayScaleImg = new Mat();
        cvtColor(src, grayScaleImg, COLOR_RGB2GRAY);
        imwrite("grayScaleSample1.jpg", grayScaleImg);

        // Perform the blackHat filtering on the grayscale image to find the hair countours
        Mat kernel = Mat.ones(5,5, CV_32F).asMat();
        Mat blackHat = new Mat();
        morphologyEx(src,blackHat, MORPH_BLACKHAT,kernel);
        imwrite("blackHat-output.jpg", blackHat);

        //Intensifying the hair countours in preparation for the inpainting algorithm
        Mat thresholdedImage = new Mat();
        threshold(src, thresholdedImage, 10, 255, THRESH_BINARY);
        imwrite("thresholded-output.jpg", thresholdedImage);

        Mat output = new Mat();
        inpaint(src, thresholdedImage, output, 1, INPAINT_TELEA);
        imwrite("output.jpg", output);
    }
}
