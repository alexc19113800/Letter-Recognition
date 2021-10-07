import java.util.*;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.MomentumBackpropagation;
public abstract class Handler{
    Board ins;

    MomentumBackpropagation rule;
    DataSet trainingSet;
    MultiLayerPerceptron myMlPerceptron;


    double[][] output;
    ArrayList<Double[]> dataAL;
    public Handler(){
        
    }

    public abstract int getPosAnsLen();

    public void handle( long index, byte[] data, final byte item){
        
    }

    public void train(int iterations){
        
    }

    public void test(){
        
    }

    public void test(int[][] input){

    }

    public void test(double[] image){
        
    }

    public abstract double[][] getWeightMap(int digit);

    public void test(int[][][] input){}

    public void clearData(){
        
    }
    public abstract void next();

    public abstract void next(int count);

    public abstract void save(String address);

    public abstract char getAnswerAt(int pos);
    public abstract char[] getAnswers();
}
