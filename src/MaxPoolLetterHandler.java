import org.neuroph.core.*;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;

public class MaxPoolLetterHandler extends LetterHandler {
    public MaxPoolLetterHandler(){
        ins = new Board(this);
        ins.window();

        dataAL = new ArrayList<Double[]>();

        // Create Learning Rule
        rule = new MomentumBackpropagation();
        rule.setMaxError (Math.pow(10,-10));  // Change this value to show my Students iteration and Max error
        rule.setMaxIterations(5);
        rule.setLearningRate(0.05);

        // create training set (logical XOR function)
        trainingSet = new DataSet(14*14, 26);

        // create multi layer perceptron
        myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 14*14, 7*7, 26);
    }
    public MaxPoolLetterHandler(String address){
        this();
        // create multi layer perceptron
        NeuralNetwork load = NeuralNetwork.createFromFile(address);
        if(load instanceof MultiLayerPerceptron){
            myMlPerceptron = (MultiLayerPerceptron)load;
        }else{
            System.out.println("Not an instance of MultiLayerPerceptron");
            System.exit(0);
        }
    }

    public MaxPoolLetterHandler(MnistReader s){
        this();
        source = s;
    }

    public MaxPoolLetterHandler(MnistReader s, String address){
        this(address);
        source = s;
    }

    public void handle( long index, byte[] data, final byte item){
        //System.out.println(digits[item]);
        ins.setInputScreen(data);
        Double[] temp = new Double[14*14+1];
        double[] dat = new double[data.length];
        for(int i = 0; i < data.length; i++){
            if(data[i]<0){
                dat[i] = (((double)data[i])+256)/255.0;
            }else {
                dat[i] = ((double)data[i]) / 255.0;
            }
        }
        dat = maxPoolByTwo(dat);
        for(int i = 0; i < dat.length; i++){
            temp[i] = dat[i];
        }
        temp[temp.length-1] = new Double(item-1);
        dataAL.add(temp);
        trainingSet.addRow(new DataSetRow(dat, outputs[item-1]));

        StdDraw.show();
    }

    public double[] maxPoolByTwo(double[] input){
        double[][] prep = new double[28][28];
        for(int c = 0; c < 28; c++){
            for(int r = 0; r < 28; r++){
                prep[r][c] = input[c*28+r];
            }
        }
        prep = maxPoolByTwo(prep);
        input = new double[14*14];
        for(int c = 0; c < 14; c++){
            for(int r = 0; r < 14; r++){
                input[c*14+r] = prep[r][c];
            }
        }
        return input;
    }

    public double[][] maxPoolByTwo(double[][] ori){
        double max = 0;
        double[][] ret = new double[ori.length/2][ori.length/2];
        for(int r = 0; r < ori.length-1; r+=2){
            for(int c = 0; c < ori[r].length-1; c+=2){
                max = Math.max(Math.max(ori[r][c],ori[r][c+1]),Math.max(ori[r+1][c],ori[r+1][c+1]));
                ret[r/2][c/2] = max;
            }
        }
        return ret;
    }



    public double[][] getWeightMap(int digit){
        Layer[] all = myMlPerceptron.getLayers();
        Neuron[] temp = all[1].getNeurons();
        Neuron target = all[2].getNeuronAt(digit);
        double[] twoDMap = new double[28*28];
        for(int i = 0; i < 7*7; i++){
            Weight[] tem = temp[i].getWeights();
            double mod = target.getWeights()[i].getValue();
            for(int j = 0; j < 14*14; j++){
                twoDMap[j] += tem[j].getValue()*mod;
            }
        }
        double[][] output = new double[28][28];
        double[][] temper = new double[14][14];
        for(int r = 0; r < 14; r++){
            for(int c =0; c < 14; c++) {
                temper[r][c] = twoDMap[r*14+c];
            }
        }
        for(int r = 0; r < 28; r+=2){
            for(int c = 0; c < 28; c+=2){
                output[r][c] = temper[r/2][c/2];
                output[r+1][c] = temper[r/2][c/2];
                output[r][c+1] = temper[r/2][c/2];
                output[r+1][c+1] = temper[r/2][c/2];
            }
        }
        return output;
    }

    public void train(int iterations){
        // learn the training set
        rule.setMaxIterations(iterations);
        myMlPerceptron.learn(trainingSet,rule);

        System.out.println("Error = " + myMlPerceptron.getLearningRule().getTotalNetworkError());

        // test perceptron
        //testNeuralNetwork(myMlPerceptron, trainingSet);

        // save trained neural network
        myMlPerceptron.save("quickSaveMaxPoolLetters.nnet");
    }

    public void test(){
        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("save.nnet");
        int success = 0;
        int fail = 0;
        for(int i = 0; i < dataAL.size(); i++){
            double[] temp = new double[14*14];
            for(int j = 0; j < dataAL.get(i).length-1;j++){
                temp[j] = dataAL.get(i)[j];
            }
            int corAns = dataAL.get(i)[dataAL.get(i).length-1].intValue();
            myMlPerceptron.setInput(temp);
            myMlPerceptron.calculate();
            double[] ans = myMlPerceptron.getOutput();
            int ct = 0;
            int pos = -1;
            double max = -1000;
            for(int j = 0; j < ans.length; j++){
                if(ans[j] > max){
                    max = ans[j];
                    pos = j;
                }
                if(ans[j] > 0.5){
                    ct += 1;
                }
            }
            //System.out.println("Answer: " + digits[corAns]);

            //System.out.println("Got: " + digits[pos]);

            if(corAns == pos){
                //System.out.println("Got: " + digits[pos]);
                displayAns(pos,ans);
                success += 1;
            }else{
                //System.out.println("Got: ERROR");
                //System.out.println("A:" + Arrays.toString(ans));
                displayAns(-10,ans);
                fail += 1;
            }
        }
        System.out.println("Success: " + success + "\nFail: " + fail);
    }

    public void test(double[] image){
        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("save.nnet");
        int success = 0;
        int fail = 0;
        image = maxPoolByTwo(image);
        myMlPerceptron.setInput(image);
        myMlPerceptron.calculate();
        double[] ans = myMlPerceptron.getOutput();
        int ct = 0;
        int pos = -1;
        double max = -1000;
        for(int j = 0; j < ans.length; j++){
            if(ans[j] > max){
                max = ans[j];
                pos = j;
            }
            if(ans[j] > 0.5){
                ct += 1;
            }
        }
        if(pos >= 0){
            displayAns(pos,ans);
        }else{
            displayAns(-10, ans);
        }
        lastAnswer = pos;
        lastWeight = ans;
    }
}