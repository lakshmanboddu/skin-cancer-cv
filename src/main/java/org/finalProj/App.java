package org.finalProj;


import org.apache.log4j.BasicConfigurator;
import org.bytedeco.opencv.opencv_core.Mat;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static String fileChose() {
        JFileChooser fc = new JFileChooser();
        int ret = fc.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {


        int height = 75;    // height of the picture in px
        int width = 100;     // width of the picture in px
        int channels = 3;
        BasicConfigurator.configure(); //http://logging.apache.org/log4j/1.2/manual.html


//        {
//        ModelTrainTest.trainTestModel();
//            System.out.println("Train Done");
//            System.exit(0);
//        }

        String filechose = fileChose();
        if(filechose == null){
            System.out.println("No file is chosen");
            System.exit(0);
        }
        File modelLocation = new File("src/resources/Model/model.zip");
        // Check for presence of saved model
        if (modelLocation.exists()) {
            LOGGER.info("Saved Model Found!");
        } else {
            LOGGER.error("Saved Model Not found!");
            LOGGER.error("This example depends on running ModelTrainTest, run that first");
            System.exit(0);
        }

        MultiLayerNetwork model = MultiLayerNetwork.load(modelLocation, true);

        LOGGER.info("TEST YOUR IMAGE AGAINST SAVED NETWORK");
        // FileChose is a string we will need a file
        File file = new File(Objects.requireNonNull(filechose));
        Mat hairRemoved = HairRemoval.removeHair(file.toString());
        // Use NativeImageLoader to convert to numerical matrix
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);

        // Get the image into an INDarray
        INDArray image = loader.asMatrix(hairRemoved);

        // 0-255
        // 0-1
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.transform(image);

        // Pass through to neural Net
        INDArray output = model.output(image);

        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, "Actinic keratoses");
        map.put(1, "Basal cell carcinoma");
        map.put(2, "Benign keratosis-like lesions");
        map.put(3, "Dermatofibroma");
        map.put(4, "Melanoma");
        map.put(5, "Melanocytic nevi");
        map.put(6, "Vascular lesions");


        LOGGER.info("The file chosen was " + filechose);
        LOGGER.info("The neural nets prediction (list of probabilities per label)");
        //log.info("## List of Labels in Order## ");
        // In new versions labels are always in order
        LOGGER.info(output.toString());

        String s= output.toString();
        String s1 = s.replaceAll("[^.,0-9]", "");
//        s.replaceAll("]]", "");
        String str[] = s1.split(",");
        double max= Double.parseDouble(str[0]);
        int key=0;
        for (int j=1; j<str.length; j++ ){
            double d1 = Double.parseDouble(str[j]);
            if(d1>max ){
                max= d1;
                key=j;
            }
        }

        if(max<0.50){
            System.out.println("The Disease cannot be predicted");
        }
        else {
            System.out.println("The Predicted disease is " + map.get(key) + " with " + new DecimalFormat("#.##").format(max * 100) + "% probaility");
        }
    }

}


