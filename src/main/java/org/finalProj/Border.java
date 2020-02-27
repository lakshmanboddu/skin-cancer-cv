package org.finalProj;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.RotatedRect;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_photo.INPAINT_TELEA;
import static org.bytedeco.opencv.global.opencv_photo.inpaint;
//https://github.com/bytedeco/javacv/blob/master/samples/ImageSegmentation.java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Border {
    Border(){
        Mat img = imread("src/resources/output.jpg");
        Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);
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

        RotatedRect rect = minAreaRect(maxContour);
        RotatedRect box = new RotatedRect();
        boxPoints(box, maxContour);

//        extLeft = (maxContour[maxContour[:, :, 0].argMin()][0])
//        extRight = (maxContour[maxContour[:, :, 0].argMax()][0])
//        extTop = (maxContour[maxContour[:, :, 1].argMin()][0])
//        extBot = (maxContour[maxContour[:, :, 1].argMax()][0])

    }
}
