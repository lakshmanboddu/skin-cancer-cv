package org.finalProj;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.File;
import java.io.IOException;

public class ModelPredict {
    ModelPredict() throws IOException {
        File file = new File("src/resources/Model/model.json");
        MultiLayerNetwork model = MultiLayerNetwork.load(file, true);


    }
}
