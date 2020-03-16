package org.finalProj;

import org.apache.log4j.BasicConfigurator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
//https://deeplearning4j.org/docs/latest/datavec-overview
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.schedule.MapSchedule;
import org.nd4j.linalg.schedule.ScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.IOException;

public class ModelTrainTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTrainTest.class);


    public static void trainTestModel() throws IOException {
        int height = 75;    // height of the picture in px
        int width = 100;     // width of the picture in px
        int channels = 3;   // single channel for grayscale images
        int outputNum = 7; // 7 digits classification
        int batchSize = 54; // number of samples that will be propagated through the network in each iteration
        int nEpochs = 150;    // number of training epochs

        int seed = 1234;    // number used to initialize a pseudorandom number generator.
        Random randNumGen = new Random(seed);

        LOGGER.info("Data load...");


        File trainData = new File("ham10000/HAM10000_images");
        System.out.println("Data loaded");
        FileSplit trainSplit = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        LabelGenerator labelMaker = new LabelGenerator(); // get labels from CSV File

        ImageRecordReader trainRR = new ImageRecordReader(height, width, channels, labelMaker);
        trainRR.initialize(trainSplit, null);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(trainRR, batchSize, 1, 7);

        // pixel values from 0-255 to 0-1 (min-max scaling)
        DataNormalization imageScaler = new ImagePreProcessingScaler();
        imageScaler.fit(trainIter);
        trainIter.setPreProcessor(imageScaler);

        // vectorization of test data
        File testData = new File("ham10000/HAM10000_images");
        FileSplit testSplit = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        ImageRecordReader testRR = new ImageRecordReader(height, width, channels, labelMaker);
        testRR.initialize(testSplit);
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, batchSize, 1, outputNum);
        imageScaler.fit(testIter);
        testIter.setPreProcessor(imageScaler); // same normalization for better results

        LOGGER.info("Network configuration and training...");
        // reduce the learning rate as the number of training epochs increases
        // iteration #, learning rate
//        Map<Integer, Double> learningRateSchedule = new HashMap<>();
//        learningRateSchedule.put(0, 0.06);
//        learningRateSchedule.put(200, 0.05);
//        learningRateSchedule.put(600, 0.028);
//        learningRateSchedule.put(800, 0.0060);
//        learningRateSchedule.put(1000, 0.001);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .l2(0.0005) // ridge regression value
//                .updater(new Nesterovs(new MapSchedule(ScheduleType.ITERATION, learningRateSchedule)))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .nIn(channels)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .stride(1, 1) // nIn need not specified in later layers
                        .nOut(50)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(500)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(height, width, channels)) // InputType.convolutional for normal image
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
//        model.setListeners(new ScoreIterationListener(10));
//        model.setListeners(new ScoreIterationListener(10), new EvaluativeListener(testIter, 1, InvocationType.EPOCH_END)); //Print score every 10 iterations and evaluate on test set every epoch

        LOGGER.info("Total num of params: {}", model.numParams());

        // evaluation while training (the score should go down)
        for (int i = 0; i < nEpochs; i++) {
//            model.fit(trainIter, nEpochs);
            LOGGER.info("Start epoch {}", i + 1, " of ", nEpochs);

            model.fit(trainIter);
//            LOGGER.info("Completed epoch {}", nEpochs);
            LOGGER.info("Completed epoch {}", i + 1, " of ", nEpochs);
            trainIter.reset();
            testIter.reset();
        }
        Evaluation eval = model.evaluate(testIter);
        LOGGER.info(eval.stats());

//            trainIter.reset();
//            testIter.reset();
//    }

        File modelPath = new File("src/resources/Model/model.zip");
        ModelSerializer.writeModel(model, modelPath, true);
        LOGGER.info("The model has been saved in {}", modelPath.getPath());
    }
}


