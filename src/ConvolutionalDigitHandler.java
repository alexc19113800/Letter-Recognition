import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.NeuralNetwork.*;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.ConvolutionalNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.comp.Kernel;
import org.neuroph.nnet.comp.layer.*;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;

public class ConvolutionalDigitHandler extends DigitHandler {

    ConvolutionalNetwork myMlPerceptron;
    ConvolutionalBackpropagation rule;
    public ConvolutionalDigitHandler()
    {
        ins = new Board(this);
        ins.window();

        dataAL = new ArrayList<Double[]>();

        // Create Learning Rule
        rule = new ConvolutionalBackpropagation();
        rule.setMaxError (Math.pow(10,-10));  // Change this value to show my Students iteration and Max error 
        rule.setMaxIterations(5);
        rule.setLearningRate(2.0);

        // create training set (logical XOR function)
        trainingSet = new DataSet(28*28, 10);

        // create multi layer perceptron
        Layer2D.Dimensions inputDim = new Layer2D.Dimensions(28,28);
        InputMapsLayer iL = new InputMapsLayer(inputDim,1);
        ConvolutionalLayer L1 = new ConvolutionalLayer(iL, new Kernel(5,5));
        PoolingLayer L2 = new PoolingLayer(L1,new Kernel(2,2));
        ConvolutionalLayer L3 = new ConvolutionalLayer(L2, new Kernel(5,5));
        PoolingLayer L4 = new PoolingLayer(L3,new Kernel(2,2));
        ConvolutionalLayer L5 = new ConvolutionalLayer(L4, new Kernel(5,5));
        myMlPerceptron = new ConvolutionalNetwork();
        myMlPerceptron.addLayer(iL);
        myMlPerceptron.addLayer(L1);
        myMlPerceptron.addLayer(L2);
        myMlPerceptron.addLayer(L3);
        myMlPerceptron.addLayer(L4);
        myMlPerceptron.addLayer(L5);
        myMlPerceptron.setOutputNeurons(new Neuron[10]);

    }
    public ConvolutionalDigitHandler(String address)
    {
        this();
        // create multi layer perceptron
        NeuralNetwork load = NeuralNetwork.createFromFile(address);
        if(load instanceof ConvolutionalNetwork){
            myMlPerceptron = (ConvolutionalNetwork) load;
        }else{
            System.out.println("Not an instance of ConvolutionalNetwork");
            System.exit(0);
        }
    }

    public ConvolutionalDigitHandler(MnistReader s)
    {
        this();
        source = s;
    }

    public ConvolutionalDigitHandler(MnistReader s, String address){
        this(address);
        source = s;
    }
    public void save(){
        myMlPerceptron.save("quickSaveConvDig");
    }
    public void save(String address){
        myMlPerceptron.save(address);
    }
}
