import org.neuroph.nnet.learning.ConvolutionalBackpropagation;

import java.io.*;
import java.util.*;
import java.net.*;
public class Runner{
    public static void main(String[] args){
        testMaxPoolLetter();
    }

    public static void testDigits(){
        try{

            MnistReader ins = new MnistReader(new File("emnist-digits-train-labels-idx1-ubyte"),
                    new File("emnist-digits-train-images-idx3-ubyte"));

            ins.getNumberOfItems();

            DigitHandler test = new DigitHandler(ins, "digit10k.nnet");
            test.window(56,28);
            /*for(int i = 0; i < 1; i++){
                ins.handleSome(1000,test);
                System.out.println("Finished importing data.");
                test.train(100);
                test.test();
                test.clearData();
            }
            test.clearData();
            ins.handleSome(1000,test);
            System.out.println("Finished importing data.");
            test.test();*/
            test.ins.refresh();
            //test.save("digit10k.nnet");
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IO");
        }
    }

    public static void testLetters(){
        try{
            MnistReader ins = new MnistReader(new File("emnist-letters-train-labels-idx1-ubyte"),
                    new File("emnist-letters-train-images-idx3-ubyte"));
            ins.getNumberOfItems();
            LetterHandler test = new LetterHandler(ins, "letter10k.nnet");
            test.window(56,28);/*
            for(int i = 0; i < 1; i++) {
                ins.handleSome(10000, test);

                test.train(100);
                test.test();

                test.clearData();
            }*/
            //ins.handleSome(10000,test);
            //test.test();
            //test.save("letter10k.nnet");
            test.ins.refresh();
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IOERROR");
        }

    }

    public static void testMaxPoolDigit(){
        try{

            MnistReader ins = new MnistReader(new File("emnist-digits-train-labels-idx1-ubyte"),
                    new File("emnist-digits-train-images-idx3-ubyte"));

            ins.getNumberOfItems();

            MaxPoolDigitHandler test = new MaxPoolDigitHandler(ins, "maxPool60k.nnet");
            /*for(int i = 0; i < 1; i++){
                ins.handleSome(60000,test);
                System.out.println("Finished importing data.");
                test.train(100);
                test.test();
                test.clearData();
            }
            test.clearData();*/
            //test.save("maxPool60k.nnet");
            //ins.handleSome(100000,test);
            //System.out.println("Finished importing data.");
            //test.test();
            test.ins.refresh();
            //test.save("digit10k.nnet");
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IO");
        }
    }

    public static void testMaxPoolLetter(){
        try{

            MnistReader ins = new MnistReader(new File("emnist-letters-train-labels-idx1-ubyte"),
                    new File("emnist-letters-train-images-idx3-ubyte"));

            ins.getNumberOfItems();

            MaxPoolLetterHandler test = new MaxPoolLetterHandler(ins,"maxPoolLetter10k.nnet");
            test.window(56,28);
            /*for(int i = 0; i < 1; i++){
                ins.handleSome(10000,test);
                System.out.println("Finished importing data.");
                test.train(100);
                test.test();
                test.clearData();
            }
            test.clearData();
            test.save("maxPoolLetter10k.nnet");*/
            //ins.handleSome(10000,test);
            //System.out.println("Finished importing data.");
            //test.test();
            test.ins.refresh();
            //test.save("maxPoolLetter10k.nnet");
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IO");
        }
    }

    public static void testConvolution(){
        try{
            MnistReader ins = new MnistReader(new File("emnist-digits-train-labels-idx1-ubyte"),
                    new File("emnist-digits-train-images-idx3-ubyte"));

            ins.getNumberOfItems();

            ConvolutionalDigitHandler test = new ConvolutionalDigitHandler(ins,"Convolution10k.nnet");
            test.window(56,28);
            /*for(int i = 0; i < 1; i++){
                ins.handleSome(10000,test);
                System.out.println("Finished importing data.");
                test.train(100);
                test.test();
                test.clearData();
            }
            test.clearData();
            test.save("Convolution10k.nnet");
            ins.handleSome(100,test);
            //System.out.println("Finished importing data.");
            test.test();*/
            test.ins.refresh();
            //test.save("maxPoolLetter10k.nnet");
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IO");
        }
    }

    public static void testConvolutionLetter(){
        try{
            MnistReader ins = new MnistReader(new File("emnist-letters-train-labels-idx1-ubyte"),
                    new File("emnist-letters-train-images-idx3-ubyte"));

            ins.getNumberOfItems();

            ConvolutionalLetterHandler test = new ConvolutionalLetterHandler(ins,"ConvolutionLetter10k.nnet");
            test.window(56,28);
            for(int i = 0; i < 1; i++){
                ins.handleSome(10000,test);
                System.out.println("Finished importing data.");
                test.train(100);
                test.test();
                test.clearData();
            }
            test.clearData();
            test.save("ConvolutionLetter10k.nnet");
            ins.handleSome(100,test);
            //System.out.println("Finished importing data.");
            test.test();
            test.ins.refresh();
            //test.save("maxPoolLetter10k.nnet");
            while(1==1){
                test.ins.listenMouse();
                test.ins.listenKey();
                test.ins.refresh();
            }
        }catch(java.io.IOException e){
            System.out.println("IO");
        }

    }


    public static void testWindow(){
        FreeBoard ins = new FreeBoard(56,28);
        ins.window();
        ins.refresh();
        while(1==1){
            ins.listenMouse();
            ins.listenKey();
            ins.refresh();
        }
    }

    public static void generate(){
        int[] set = new int[26];
        for(int i = 0; i < 26; i++){
            set[i] = 1;
            System.out.println("double[] o" + i + " = " + Arrays.toString(set));
            set = new int[26];
        }

    }
}