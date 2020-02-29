package org.finalProj;


import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_core.cvInRange;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import static org.bytedeco.opencv.global.opencv_imgproc.RETR_EXTERNAL;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY_INV;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_OTSU;
import static org.bytedeco.opencv.global.opencv_imgproc.contourArea;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.drawContours;
import static org.bytedeco.opencv.global.opencv_imgproc.ellipse;
import static org.bytedeco.opencv.global.opencv_imgproc.ellipse2Poly;
import static org.bytedeco.opencv.global.opencv_imgproc.findContours;
import static org.bytedeco.opencv.global.opencv_imgproc.fitEllipse;
import static org.bytedeco.opencv.global.opencv_imgproc.matchShapes;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;

public class ColorProperty {
    ColorProperty() {

//        Mat frame = imread("src/resources/output.jpg");
//        Mat hsv = new Mat();
//        int[] lowerBound = new int[]{9, 0, 178};
//        int[] upperBound = new int[]{255, 77, 255};
//        CvArr mask_blue = new CvArr();
//        cvInRange(new CvArr(hsv), new CvArr(lowerBound), new Scalar(255, 77, 255, 1.0), mask_blue )
    //#We created a mask by providing the hsv image along with lower and upper bound.

//                res=cv2.bitwise_and(frame, frame, mask=mask_blue) #We will perfrom the bitwise_and() operation ands save the result in res variable.


        Mat img = imread("src/resources/output.jpg");
        Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);
        Mat blur = new Mat();
        GaussianBlur(gray, blur, new Size(5, 5), 0);
        Mat thresh = new Mat();
        threshold(blur, thresh, 70, 255, THRESH_BINARY_INV + THRESH_OTSU);
        MatVector contours = new MatVector();
        findContours(thresh, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
//https://stackoverflow.com/questions/10668573/find-contours-in-javacv-or-opencv
//        double max_cnt =  Imgproc.contourArea(contours.get(0));
        double maxArea = 0;
        Mat maxContour = new Mat();
//        https://www.programcreek.com/java-api-examples/?class=org.opencv.imgproc.Imgproc&method=contourArea
        Mat[] contoursArray = contours.get();
        for (Mat contour : contoursArray) {
            double area = contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }
//        https://stackoverflow.com/questions/22849790/opencv-mat-object-get-data-length
        long maxContourLength = maxContour.total() * maxContour.elemSize();

        if (maxContourLength > 4) {
            RotatedRect ellipse1 = fitEllipse(maxContour);
//            Rect obj=boundingRect(maxContour);
//            Mat var = new Mat ((int)(obj.y() + 0.3*obj.height()):)
            Point2dVector ellipsePoints = new Point2dVector();
            ellipse2Poly(new Point2d(ellipse1.center().x(), ellipse1.center().y()), new Size2d(ellipse1.size()), (int) ellipse1.angle(), 0, 360, 1, ellipsePoints);
//        ellipse2Poly
            Mat ellipsePts = new Mat(ellipsePoints);
            double comparisonValue = matchShapes(maxContour, ellipsePts, 1, 0.0);
            System.out.println(comparisonValue);
            ellipse(img, ellipse1, new Scalar(0,255,0,1.0) );
        }

        System.out.println("Number of contours = " + contoursArray.length);

        drawContours(img, new MatVector(maxContour), -1, new Scalar(0, 0, 255, 1.0));
        Display.display(img,"Frame");
        Display.display(gray, "Image GRAY");
    }
}
