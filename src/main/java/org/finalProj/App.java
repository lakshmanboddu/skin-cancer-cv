package org.finalProj;


import org.apache.log4j.BasicConfigurator;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTrainTest.class);

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

//        HairRemoval.removeHair();
//        AsymmetryDetection obj = new AsymmetryDetection();
//        System.out.println(obj.symmetry);
//        Border border = new Border();
//        ColorProperty colorProperty = new ColorProperty();
//        Diameter d = new Diameter();
        int height = 75;    // height of the picture in px
        int width = 100;     // width of the picture in px
        int channels = 3;
        BasicConfigurator.configure(); //http://logging.apache.org/log4j/1.2/manual.html


        ModelTrainTest.trainTestModel();

        String filechose = fileChose();

        File locationToSave = new File("src/resources/Model/model.zip");
        // Check for presence of saved model
        if (locationToSave.exists()) {
            LOGGER.info("Saved Model Found!");
        } else {
            LOGGER.error("File not found!");
            LOGGER.error("This example depends on running ModelTrainTest, run that example first");
            System.exit(0);
        }

        MultiLayerNetwork model = MultiLayerNetwork.load(locationToSave, true);

        LOGGER.info("TEST YOUR IMAGE AGAINST SAVED NETWORK");
        // FileChose is a string we will need a file
        File file = new File(Objects.requireNonNull(filechose));

        // Use NativeImageLoader to convert to numerical matrix
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);

        // Get the image into an INDarray
        INDArray image = loader.asMatrix(file);

        // 0-255
        // 0-1
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.transform(image);

        // Pass through to neural Net
        INDArray output = model.output(image);

        HashMap<String, String> map = new HashMap<>();
        map.put("nv", "Melanocytic nevi");
        map.put("mel", "Melanoma");
        map.put("bkl", "Benign keratosis-like lesions");
        map.put("bcc", "Basal cell carcinoma");
        map.put("akiec", "Actinic keratoses");
        map.put("vasc", "Vascular lesions");
        map.put("df", "Dermatofibroma");

        LOGGER.info("The file chosen was " + filechose);
        LOGGER.info("The neural nets prediction (list of probabilities per label)");
        //log.info("## List of Labels in Order## ");
        // In new versions labels are always in order
        LOGGER.info(output.toString());
    }

}


