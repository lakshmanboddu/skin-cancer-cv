package org.finalProj;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.Collections;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Diameter {

    public Point midPoint(Point ptA, Point ptB) {
        return new Point((int) ((ptA.x() + ptB.x()) * 0.5), (int) ((ptA.y() + ptB.y()) * 0.5));
    }

    public static void findDiameter() {
        Mat img = imread("src/resources/output.jpg");
        Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);
        Mat blur = new Mat();
        GaussianBlur(gray, blur, new Size(7, 7), 0);

        Mat edged = new Mat();
        Canny(blur, edged, 50, 100);
//        Mat edged1 = new Mat();
        dilate(edged, edged, new Mat());
        erode(edged, edged, new Mat());

        MatVector contours = new MatVector();
        findContours(edged, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

//        List<Mat> contoursArray = contours;
//
//        Collections.sort(new List<Mat>(contours));

    }
}
