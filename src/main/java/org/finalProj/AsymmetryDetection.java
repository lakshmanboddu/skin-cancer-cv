package org.finalProj;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class AsymmetryDetection {

    public String symmetry;
    AsymmetryDetection(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = Imgcodecs.imread("src/resources/Melanoma-2.jpg");
        Mat gray = new Mat();
        Imgproc.cvtColor(img,gray, Imgproc.COLOR_RGB2GRAY);
        Mat blur = new Mat();
        Imgproc.GaussianBlur(gray, blur, new Size(5,5),0);
        Mat thresh =  new Mat();
        Imgproc.threshold(blur, thresh, 70, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        List<MatOfPoint> contours =  new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//        double max_cnt =  Imgproc.contourArea(contours.get(0));
        double maxArea = 0;
        MatOfPoint maxContour = new MatOfPoint();
//        https://www.programcreek.com/java-api-examples/?class=org.opencv.imgproc.Imgproc&method=contourArea
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }

        // max_contour is a MatOfPoint object with int values
        //fitEllipse takes MatOfPoint2f which has float values
        //https://stackoverflow.com/questions/11273588/how-to-convert-matofpoint-to-matofpoint2f-in-opencv-java-api
        MatOfPoint2f floatMaxContour = new MatOfPoint2f();
        maxContour.convertTo(floatMaxContour, CvType.CV_32F);
        RotatedRect ellipse = Imgproc.fitEllipse(floatMaxContour);

        MatOfPoint ellipsePoints = new MatOfPoint();
        Imgproc.ellipse2Poly(ellipse.center, ellipse.size,(int)ellipse.angle, 0,360, 1, ellipsePoints);

        double comparisonValue = Imgproc.matchShapes(maxContour, ellipsePoints, 1, 0.0);
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
