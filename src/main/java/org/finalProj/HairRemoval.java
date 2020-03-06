package org.finalProj;


import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.MORPH_BLACKHAT;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.morphologyEx;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;
import static org.bytedeco.opencv.global.opencv_photo.INPAINT_TELEA;
import static org.bytedeco.opencv.global.opencv_photo.inpaint;

public class HairRemoval {
    public static Mat removeHair(String source){
        //Read image to object of Mat Class
        //Mat object is an n-dimensional matrix object to store images
        Mat src = imread(source);
//        Display.display(src,"Input");

        // Convert the original image to grayscale
        Mat grayScaleImg = new Mat();
        cvtColor(src, grayScaleImg, COLOR_BGR2GRAY);
        imwrite("src/resources/grayScaleSample1.jpg", grayScaleImg);
//        Display.display(grayScaleImg, "Gray Scale Image");

        // Perform the blackHat filtering on the grayscale image to find the hair countours
        Mat kernel = Mat.ones(5,5, CV_32F).asMat();
        Mat blackHat = new Mat();
        morphologyEx(grayScaleImg,blackHat, MORPH_BLACKHAT,kernel);
        imwrite("src/resources/blackHat-output.jpg", blackHat);
//        Display.display(blackHat, "Blackhat image");

        //Intensifying the hair countours in preparation for the inpainting algorithm
        Mat thresholdedImage = new Mat();
        threshold(blackHat, thresholdedImage, 10, 255, THRESH_BINARY);
        imwrite("src/resources/thresholded-output.jpg", thresholdedImage);
//        Display.display(thresholdedImage, "Thresholded Image");

        Mat output = new Mat();
        inpaint(src, thresholdedImage, output, 1, INPAINT_TELEA);
        imwrite("src/resources/output.jpg", output);
//        Display.display(output, "Output");
        return output;
    }

//    public static void main(String[] args) {
//        HairRemoval hr= new HairRemoval();
//        hr.removeHair();
//    }
}
