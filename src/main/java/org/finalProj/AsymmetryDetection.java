package org.finalProj;


import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_photo.INPAINT_TELEA;
import static org.bytedeco.opencv.global.opencv_photo.inpaint;
//https://github.com/bytedeco/javacv/blob/master/samples/ImageSegmentation.java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AsymmetryDetection {

    public String symmetry;
    AsymmetryDetection(){
        Mat img = imread("src/resources/Melanoma-2.jpg");
        Mat gray = new Mat();
        cvtColor(img,gray, COLOR_RGB2GRAY);
        Mat blur = new Mat();
        GaussianBlur(gray, blur, new Size(5,5),0);
        Mat thresh =  new Mat();
        threshold(blur, thresh, 70, 255, THRESH_BINARY_INV + THRESH_OTSU);
        MatVector contours =  new MatVector();
        findContours(thresh, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
//https://stackoverflow.com/questions/10668573/find-contours-in-javacv-or-opencv
//        double max_cnt =  Imgproc.contourArea(contours.get(0));
        double maxArea = 0;
        Mat maxContour = new Mat();
//        https://www.programcreek.com/java-api-examples/?class=org.opencv.imgproc.Imgproc&method=contourArea
        Mat[] contoursArray = contours.get();
        for (Mat contour : contoursArray) {
            double area = contourArea( contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }

        // max_contour is a MatOfPoint object with int values
        //fitEllipse takes MatOfPoint2f which has float values
        //https://stackoverflow.com/questions/11273588/how-to-convert-matofpoint-to-matofpoint2f-in-opencv-java-api
//        Mat floatMaxContour = new Mat();
//        maxContour.convertTo(floatMaxContour, CV_32F);

//        RotatedRect ellipse1 = fitEllipse(floatMaxContour);
        RotatedRect ellipse1 = fitEllipse(maxContour);


        Point2dVector ellipsePoints = new Point2dVector();
        ellipse2Poly( new Point2d( ellipse1.center().x(), ellipse1.center().y()), new Size2d(ellipse1.size()),(int)ellipse1.angle(), 0,360, 1, ellipsePoints);
//        ellipse2Poly
        Mat ellipsePts = new Mat(ellipsePoints);
        double comparisonValue = matchShapes(maxContour, ellipsePts, 1, 0.0);
        if (comparisonValue < 0.099){
            this.symmetry = "Asymmetric";
            System.out.println("Asymmetric");
        }
        else {
            this.symmetry = "Symmetric";
            System.out.println("Symmetric");
        }
    }

//    public static void main(String[] args) {
//        AsymmetryDetection obj = new AsymmetryDetection();
//    }
}
