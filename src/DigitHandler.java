import java.io.IOException;
import java.util.*;
import org.neuroph.core.*;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.MomentumBackpropagation;
/**
 * Write a description of class LetterHandler here.
 *
 * @author Alex Chiu
 * @version (a version number or a date)
 */
public class DigitHandler extends Handler implements MnistReader.DataArrayImageHandler{
    Board ins;

    MomentumBackpropagation rule;
    DataSet trainingSet;
    MultiLayerPerceptron myMlPerceptron;

    char[] digits = {'0','1','2','3','4','5','6','7','8','9'};

    double[] o0 = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    double[] o1 = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    double[] o2 = {0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
    double[] o3 = {0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
    double[] o4 = {0, 0, 0, 0, 1, 0, 0, 0, 0, 0};
    double[] o5 = {0, 0, 0, 0, 0, 1, 0, 0, 0, 0};
    double[] o6 = {0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
    double[] o7 = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
    double[] o8 = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
    double[] o9 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1};

    double[][] outputs = {o0, o1, o2, o3, o4, o5, o6, o7, o8, o9};
    ArrayList<Double[]> dataAL;
    MnistReader source;
    public DigitHandler()
    {
        dataAL = new ArrayList<Double[]>();

        // Create Learning Rule
        rule = new MomentumBackpropagation ();
        rule.setMaxError (Math.pow(10,-10));  // Change this value to show my Students iteration and Max error 
        rule.setMaxIterations(5);
        rule.setLearningRate(0.05);

        // create training set (logical XOR function)
        trainingSet = new DataSet(28*28, 10);

        // create multi layer perceptron
        myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 28*28, 7*7, 10);
    }
    public DigitHandler(String address)
    {
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

    public void window(){
        ins = new Board(this);
        ins.window();
    }

    public void window(int w, int h){
        ins = new FreeBoard(w,h,this);
        ins.window();
    }

    public DigitHandler(MnistReader s)
    {
        this();
        source = s;
    }

    public DigitHandler(MnistReader s, String address){
        this(address);
        source = s;
    }

    public void handle( long index, byte[] data, final byte item){
        //System.out.println(digits[item]);
        ins.setInputScreen(data);
        Double[] temp = new Double[data.length+1];
        double[] dat = new double[data.length];
        for(int i = 0; i < data.length; i++){
            if(data[i]<0){
                dat[i] = (((double)data[i])+256)/255.0;
            }else {
                dat[i] = ((double)data[i]) / 255.0;
            }
            temp[i] = new Double(dat[i]);
        }
        temp[data.length] = new Double(item);
        dataAL.add(temp);
        trainingSet.addRow(new DataSetRow(dat, outputs[item]));

        StdDraw.show();
    }


    public double[][] getWeightMap(int digit){
        Layer[] all = myMlPerceptron.getLayers();
        Neuron[] temp = all[1].getNeurons();
        Neuron target = all[2].getNeuronAt(digit);
        double[] twoDMap = new double[28*28];
        for(int i = 0; i < 7*7; i++){
            Weight[] tem = temp[i].getWeights();
            double mod = target.getWeights()[i].getValue();
            for(int j = 0; j < 28*28; j++){
                twoDMap[j] += tem[j].getValue()*mod;
            }
        }
        double[][] output = new double[28][28];
        for(int r = 0; r < 28; r++){
            for(int c =0; c < 28; c++) {
                output[r][c] = twoDMap[r*28+c];
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
        myMlPerceptron.save("quickSaveDigits.nnet");
    }

    public void test(){
        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("save.nnet");
        int success = 0;
        int fail = 0;
        for(int i = 0; i < dataAL.size(); i++){
            double[] temp = new double[28*28];
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
            System.out.println("Answer: " + digits[corAns]);

            //System.out.println("Got: " + digits[pos]);

            if(corAns == pos){
                System.out.println("Got: " + digits[pos]);
                success += 1;
            }else{
                //System.out.println("Got: ERROR");
                //System.out.println("A:" + Arrays.toString(ans));
                fail += 1;
            }
        }
        System.out.println("Success: " + success + "\nFail: " + fail);
    }

    int lastAnswer = -1;
    double[] lastWeight;
    public void test(double[] image){
        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("save.nnet");
        int success = 0;
        int fail = 0;
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
        lastWeight = ans;
        if(pos >= 0){
            displayAns(pos,ans);
        }else{
            displayAns(-10, ans);
        }
        lastAnswer = pos;
    }

    public void test(int[][] input){
        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("save.nnet");
        int success = 0;
        int fail = 0;
        double[] image = new double[28*28];
        for(int c = 0; c < 28; c++){
            for(int r = 0; r < 28; r++){
                image[c*28+r] = input[r][c]/255.0;
            }
        }
        test(image);
    }

    public void test(int[][][] input){
        int[] ans = new int[input.length];
        for(int i = 0; i < input.length; i++){
            test(input[i]);
            ans[i] = lastAnswer;
        }
        if(ans.length == 1){
            displayAns(ans[0], lastWeight);
        }else {
            displayAns(ans, new double[10]);
        }
    }

    public void clearData(){
        try{
            dataAL.clear();
            trainingSet.clear();
        }catch(Exception E){
            System.out.println("NOO");
        }
    }

    public void next(){
        if(source != null){
            try {
                source.handleSome(1, this);
            }catch(IOException e){
                System.out.println("IOEXCEPTION");
            }
        }else{
            System.out.println("Not connected to data source");
        }
    }

    public void next(int count){
        if(source != null){
            try {
                source.handleSome(count, this);
            }catch(IOException e){
                System.out.println("IOEXCEPTION");
            }
        }else{
            System.out.println("Not connected to data source");
        }
    }

    public void displayAns(int ans, double[] sim){
        ins.showAnswer(ans);
        ins.showSimilarity(sim);
    }
    public void displayAns(int[] ans, double[] sim){
        ins.showAnswer(ans);
        ins.showSimilarity(sim);
    }
    public int getPosAnsLen(){
        return outputs.length;
    }
    public void setSource(MnistReader s){
        source = s;
    }
    public void save(){
        myMlPerceptron.save("digits.nnet");
    }
    public void save(String address){
        myMlPerceptron.save(address);
    }
    public char getAnswerAt(int pos){
        return digits[pos];
    }
    public char[] getAnswers(){
        return digits;
    }
}