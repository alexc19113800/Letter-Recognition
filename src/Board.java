import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.awt.*;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.BackPropagation;

import javax.swing.text.NumberFormatter;

public class Board {
    int[][] inputScreen;

    double screenXPos, screenYPos;
    int screenSizeX, screenSizeY;

    double x, y;
    int bX, bY;
    int prevX, prevY;

    double holdX, holdY;
    double offsetX, offsetY;

    double oriX, oriY;

    boolean hold;
    boolean pressHold;

    double[] buttonX, buttonY, buttonW, buttonH;
    String[] buttonText;
    boolean[] onButton;
    int[] onButtonCt;

    int pen;
    Handler handler;

    int answer;
    int[] answerInArray;
    double[] similarity;

    int pCt;

    Font big = new Font("Arial", Font.BOLD, 240);
    Font medium = new Font("Arial", Font.PLAIN,30);
    Font small = new Font("Arial", Font.PLAIN,20);

    char[] answers;

    int width;
    int height;

    int tolerance;

    ArrayList<Integer> sectionX, sectionY, sectionW, sectionH;

    public Board() {
        inputScreen = new int[28][28];
        height = inputScreen.length;
        width = inputScreen[0].length;
        pen = 1;
    }

    public Board(Handler in) {
        this();
        handler = in;
        answers = handler.getAnswers();
    }

    public void window() {
        StdDraw.setCanvasSize(1080, 720);
        StdDraw.setXscale(0, 1080);
        StdDraw.setYscale(0, 720);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(small);
        StdDraw.enableDoubleBuffering();
        buttonX = new double[]{80, 240, 400, 560, 720,
                80, 240, 400, 560, 720};
        buttonY = new double[]{670, 670, 670, 670, 670,
                620, 620, 620, 620, 620};
        buttonW = new double[]{80, 80, 80, 80, 80,
                80, 80, 80, 80, 80};
        buttonH = new double[]{25, 25, 25, 25, 25,
                25, 25, 25, 25, 25};
        onButtonCt = new int[]{0, 0, 0, 0, 0,
                0, 0, 0, 0, 0};
        buttonText = new String[]{"Test", "Selection", "Blur", "Resize", "Weight Map",
                "Next", "MultiLetter", "Clear", "Run Samples", "Exit"};
        onButton = new boolean[]{false,false,false,false,false,
                false,false,false,false,false};
        pCt = 0;
        if(similarity != null)
            similarity = new double[answers.length];
        answer = -1;

        sectionX = new ArrayList<Integer>();
        sectionW = new ArrayList<Integer>();
        sectionY = new ArrayList<Integer>();
        sectionH = new ArrayList<Integer>();

        tolerance = 256;
    }

    public void refresh() {
        StdDraw.clear();
        StdDraw.setXscale(0, 1080);
        StdDraw.setYscale(0, 720);
        draw();
        StdDraw.show();
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    DecimalFormat formatter = new DecimalFormat("#0.00");

    public void draw() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int color = inputScreen[r][c];
                if(color >= 0 && color <= 255){
                    StdDraw.setPenColor(color, color, color);
                }else if(color > 255){
                    StdDraw.setPenColor(0,0,255);
                }else if(color < 0&&color >= -255){
                    StdDraw.setPenColor(Math.abs(color),0,0);
                }else{
                    StdDraw.setPenColor(StdDraw.MAGENTA);
                }
                StdDraw.filledRectangle(c * 20 + 10, (r * -20) + 550, 10, 10);
            }
        }
        StdDraw.setPenColor(StdDraw.RED);
        for(int i = 0; i < sectionX.size(); i++){
            double xP = sectionX.get(i)*20.0;
            double yP = (sectionY.get(i)*-20.0) + 560.0;
            double width = sectionW.get(i)*20.0;
            double height = sectionH.get(i)*-20.0;
            StdDraw.line(xP,yP, xP+width, yP);
            StdDraw.line(xP,yP, xP, yP+height);
            StdDraw.line(xP,yP+height, xP+width, yP+height);
            StdDraw.line(xP+width,yP, xP+width, yP+height);
        }

        for (int i = 0; i < buttonX.length; i++) {
            if(onButton[i]){
                StdDraw.setPenColor(100, 100, 100);
                if(onButtonCt[i]>0)
                    onButtonCt[i] /= 3;
            }else{
                int color = 100 + onButtonCt[i];
                StdDraw.setPenColor(color, color, color);
                if(onButtonCt[i] < 60){
                    onButtonCt[i] += 5;
                }
            }
            StdDraw.filledRectangle(buttonX[i], buttonY[i], buttonW[i], buttonH[i]);
        }
        StdDraw.setPenColor(0, 0, 0);
        for (int i = 0; i < buttonX.length; i++) {
            StdDraw.text(buttonX[i], buttonY[i], buttonText[i]);
        }

        StdDraw.text(1000,570,"Similarity:");
        if(answers != null&&similarity != null) {
            if (answers.length > 10) {
                for (int i = 0; i < similarity.length; i++) {
                    StdDraw.text(1000, 520 - 20 * i, answers[i] + ": " + formatter.format(similarity[i]));
                }
            } else {
                for (int i = 0; i < similarity.length; i++) {
                    StdDraw.text(1000, 520 - 50 * i, answers[i] + ": " + formatter.format(similarity[i]));
                }
            }
        }

        StdDraw.setFont(big);
        if(answer>= 0){
            StdDraw.text(780, 300, ""+ answers[answer]);
        }else if(answer == -2){//multiple answer
            String output = "";
            for(int i = 0; i < answerInArray.length;i++){
                output += answers[answerInArray[i]];
            }
            StdDraw.text(780, 300, output);
        }else if(answer == -10){
            StdDraw.text(780, 300, "Err");
        }
        StdDraw.setFont(small);
    }

    public void setInputScreen(double[][] dat) {
        for(int r = 0; r < height; r++){
            for(int c = 0; c < width; c++){
                inputScreen[r][c] = (int)(dat[r][c]*255.0);
            }
        }
    }
    public void setInputScreen(int[][] dat) {
        try {
            for (int r = 0; r < 28; r++) {
                for (int c = 0; c < 28; c++) {
                    inputScreen[r][c] = dat[r][c];
                }
            }
        }catch(Exception E){
            System.out.println("Error at setting input: inputScreen size may be too small.");
        }
    }
    public void setInputScreen(byte[] dat) {
        int[] input = new int[dat.length];
        for (int i = 0; i < dat.length; i++) {
            if (dat[i] < 0) {
                input[i] = 256 + dat[i];
            } else {
                input[i] = dat[i];
            }
            if (input[i] > 255) {
                input[i] = 255;
            }
        }
        inputScreen = new int[height][width];
        int pos = 0;//the data set reads the first column then the next, top to down then left to right
        for (int c = 0; c < 28; c++) {
            for (int r = 0; r < 28; r++) {
                inputScreen[r][c] = input[pos];
                pos++;
            }
        }
        StdDraw.show();
        //sleep(300);
    }

    public void showAnswer(int ans){
        answer = ans;
    }

    public void showAnswer(int[] ans){
        answer = -2;
        answerInArray = ans;
    }

    public void showSimilarity(double[] ans){
        similarity = ans;
    }

    public ArrayList<int[][]> isolateFigures(){
        //identify numbers
        ArrayList<Integer> horizontalSections = new ArrayList<Integer>();
        int ct = 0;
        ArrayList<int[][]> sections = new ArrayList<int[][]>();
        for(int c = 0; c < width; c++){
            int total = 0;
            for(int r = 0; r < height; r++){
                total += inputScreen[r][c];
            }
            if(total < tolerance){
                horizontalSections.add(c);
            }
        }

        //find the image on left it there exists one
        //find images in the middle
        int startPos = -1;
        for(int i = 0; i < horizontalSections.size();i++){
            if(horizontalSections.get(i) > startPos+1){
                sectionX.add(startPos+1);
                sectionW.add(horizontalSections.get(i)-(startPos+1));
                int[][] section = new int[inputScreen[0].length][horizontalSections.get(i)-(startPos+1)];
                for(int r = 0; r < height; r++){
                    for(int c = startPos+1; c < horizontalSections.get(i);c++){
                        section[r][c-(startPos+1)] = inputScreen[r][c];
                    }
                }
                sections.add(section);
            }
            startPos = horizontalSections.get(i);
        }
        //find image at the right if there is one
        if(horizontalSections.size() >= 1) {
            int last = horizontalSections.get(horizontalSections.size() - 1);
            if (last != width-1) {
                sectionX.add(last + 1);
                sectionW.add(width-1 - last);
                int[][] section = new int[inputScreen.length][width-1 - last];
                for (int r = 0; r < inputScreen.length; r++) {
                    for (int c = last + 1; c < inputScreen[r].length; c++) {
                        section[r][c - (last + 1)] = inputScreen[r][c];
                    }
                }
                sections.add(section);
            }
        }

        int pos = 0;
        ArrayList<Integer> removeY = new ArrayList<>();
        for(int i = 0; i < sections.size();i++){
            int[][] section = sections.get(i);
            for(int r = 0; r < section.length; r++){
                int totalVal = 0;
                for(int c = 0; c < section[r].length; c++){
                    totalVal += section[r][c];
                }
                if(totalVal == 0){
                }else{
                    removeY.add(r);
                    break;
                }
            }
            for(int r = section.length-1; r >= 0; r--){
                int totalVal = 0;
                for(int c = 0; c < section[r].length; c++){
                    totalVal += section[r][c];
                }
                if(totalVal == 0){
                }else{
                    removeY.add(r);
                    break;
                }
            }
            int height = 1+removeY.get(1)-removeY.get(0);
            sectionY.add(removeY.get(0));
            sectionH.add(height);
            section = new int[height][section[0].length];
            for(int r = 0; r < height; r++){
                for(int c = 0; c < section[0].length; c++){
                    section[r][c] = sections.get(i)[removeY.get(0)+r][c];
                }
            }
            sections.set(i,section);
            removeY.clear();
        }
        return sections;
    }

    public void buttonPressed(int but){
        sectionX.clear();
        sectionW.clear();
        sectionY.clear();
        sectionH.clear();
        switch (but) {
            case 0:
                //output the inputScreen and let the handler test it
                handler.test(inputScreen);
                break;
            case 1:
                //single out letters
                ArrayList<int[][]> sections = isolateFigures();
                int[][][] num = new int[sections.size()][height][width];
                for(int i = 0; i < sections.size(); i++){
                    num[i] = (ImageEditor.edit(sections.get(i),28));
                }
                break;
            case 2:
                //blur
                int[][] output = new int[height][width];
                int[][] kernel = new int[][]{{1,4,6,4,1},{4,16,24,16,4},{6,24,36,24,6},{4,16,24,16,4},{1,4,6,4,1}};
                int maxi = -1000;
                for(int r = 0; r < output.length; r++){
                    for(int c = 0; c < output[r].length; c++){
                        for(int i = -2; i < 3; i++){
                            for(int j = -2; j < 3; j++){
                                if(r+i >= 0&&r+i < output.length&&c+j >= 0 && c+j < output[0].length){
                                    output[r+i][c+j] += inputScreen[r][c] * kernel[i+2][j+2];
                                }
                            }
                        }
                    }
                }
                for(int r = 0; r < output.length; r++){
                    for(int c = 0; c < output.length; c++){
                        if(maxi < output[r][c]){
                            maxi = output[r][c];
                        }
                    }
                }
                for(int r = 0; r < output.length; r++){
                    for(int c = 0; c < output.length; c++){
                        output[r][c] *= 255;
                        output[r][c] /= maxi;
                    }
                }
                inputScreen = output;
                /*//Add noise
                int ran;
                for(int r = 0; r < inputScreen.length;r++){
                    for(int c = 0; c < inputScreen.length; c++){
                        ran = (int)(Math.random()*100)-50;
                        if(ran > 0){
                            inputScreen[r][c] += ran;
                        }
                        if(inputScreen[r][c] > 255){
                            inputScreen[r][c] = 255;
                        }
                    }
                }*/
                break;
            case 3:
                //enlarge first figure
                ArrayList<int[][]> ss = isolateFigures();
                int[][][] nn = new int[ss.size()][28][28];
                for(int i = 0; i < ss.size(); i++){
                    nn[i] = (ImageEditor.edit(ss.get(i),28));
                }
                if(nn.length>=1) {
                    setInputScreen(nn[0]);
                }
                sectionX.clear();
                sectionW.clear();
                sectionY.clear();
                sectionH.clear();
                break;
            case 4:
                //get weight map
                double[][] map = handler.getWeightMap(pCt);
                int[][] input = new int[28][28];
                double max = -1000.0;
                double temp;
                for (int c = 0; c < 28; c++) {
                    for (int r = 0; r < 28; r++) {
                        temp = map[r][c];
                        if(Math.abs(temp)>max){
                            max = Math.abs(temp);
                        }
                    }
                }
                double modifier = 255/max;
                for (int c = 0; c < 28; c++) {
                    for (int r = 0; r < 28; r++) {
                        input[r][c] = (int) (map[r][c]*modifier);
                    }
                }
                inputScreen = input;
                pCt++;
                if(pCt == handler.getPosAnsLen()){
                    pCt = 0;
                }
                break;
            case 5:
                //get next input
                handler.next();
                buttonPressed(0);
                break;
            case 6:
                //get multiple figures
                ArrayList<int[][]> sec = isolateFigures();
                int[][][] fin = new int[sec.size()][28][28];
                for(int i = 0; i < sec.size(); i++){
                    fin[i] = (ImageEditor.edit(sec.get(i),28));
                }
                //System.out.println(sections.size());

                if(fin.length>=1) {
                    handler.test(fin);
                }
                break;
            case 7:
                //clear screen
                inputScreen = new int[28][28];
                break;
            case 8:
                //run 1000 tests
                handler.clearData();
                handler.next(1000);
                handler.test();
                break;
            case 9:
                //close program
                System.exit(0);
        }
    }

    public void listenMouse() {
        x = StdDraw.mouseX();
        y = StdDraw.mouseY();
        bX = (int) (x / 20.0);
        bY = (int) ((560 - y) / 20.0);
        for(int i = 0; i < buttonX.length; i++){
            if(x >= buttonX[i] - buttonW[i] && x <= buttonX[i] + buttonW[i]
                    && y >= buttonY[i] - buttonH[i] && y <= buttonY[i] + buttonH[i]){
                onButton[i] = true;
            }else{
                onButton[i] = false;
            }
        }
        if (StdDraw.isMousePressed()) {
            if (!hold) {//if on first click
                holdX = x;
                holdY = y;
                if (bX >= 0 && bX < width && bY >= 0 && bY < height) {
                    sectionX.clear();
                    sectionW.clear();
                    sectionY.clear();
                    sectionH.clear();
                    if (inputScreen[bY][bX] <= 128) {
                        pen = 255;
                    } else {
                        pen = 0;
                    }
                    inputScreen[bY][bX] = pen;
                    if (bY > 0) {
                        if(inputScreen[bY - 1][bX] <= pen / 2) {
                            inputScreen[bY - 1][bX] = pen / 2;
                        }
                        if(bX > 0){
                            if(inputScreen[bY - 1][bX-1] < pen / 3){
                                inputScreen[bY - 1][bX-1] = pen / 3;
                            }
                        }
                        if(bX < 27){
                            if(inputScreen[bY - 1][bX+1] < pen / 3){
                                inputScreen[bY - 1][bX+1] = pen / 3;
                            }
                        }
                    }
                    if (bY < 27) {
                        if(inputScreen[bY + 1][bX] < pen / 2) {
                            inputScreen[bY + 1][bX] = pen / 2;
                        }
                        if(bX > 0){
                            if(inputScreen[bY + 1][bX-1] < pen / 3){
                                inputScreen[bY + 1][bX-1] = pen / 3;
                            }
                        }
                        if(bX < 27){
                            if(inputScreen[bY + 1][bX+1] < pen / 3){
                                inputScreen[bY + 1][bX+1] = pen / 3;
                            }
                        }
                    }
                    if (bX > 0) {
                        if(inputScreen[bY][bX - 1] < pen / 2) {
                            inputScreen[bY][bX - 1] = pen / 2;
                        }
                    }
                    if (bX < 27) {
                        if(inputScreen[bY][bX + 1] < pen / 2) {
                            inputScreen[bY][bX + 1] = pen / 2;
                        }
                    }
                } else {

                }
                for(int i = 0; i < buttonX.length; i++){
                    if(onButton[i]){
                        onButtonCt[i] = 0;
                        buttonPressed(i);
                    }
                }
                oriX = screenXPos;
                oriY = screenYPos;
                hold = true;//not on first click
                refresh();
            } else {
                if (bX >= 0 && bX < width && bY >= 0 && bY < height) {
                    inputScreen[bY][bX] = pen;
                    if (bY > 0 && inputScreen[bY - 1][bX] <= pen / 2) {
                        inputScreen[bY - 1][bX] = pen / 2;
                    }
                    if (bY < 27 && inputScreen[bY + 1][bX] <= pen / 2) {
                        inputScreen[bY + 1][bX] = pen / 2;
                    }
                    if (bX > 0 && inputScreen[bY][bX - 1] <= pen / 2) {
                        inputScreen[bY][bX - 1] = pen / 2;
                    }
                    if (bX < 27 && inputScreen[bY][bX + 1] <= pen / 2) {
                        inputScreen[bY][bX + 1] = pen / 2;
                    }
                } else {

                }
                offsetX = (x - holdX) / 2;
                offsetY = (y - holdY) / 2;
                refresh();
            }
        } else {
            hold = false;
            screenXPos += offsetX;
            screenYPos += offsetY;
            offsetX = 0;
            offsetY = 0;
        }
    }

    public void listenKey() {
        if (StdDraw.isKeyPressed(73)) {
            if (!pressHold) {
                screenXPos += 0.25 * screenSizeX;
                screenYPos += 0.25 * screenSizeY;
                screenSizeX /= 2;
                screenSizeY /= 2;
                pressHold = true;
                refresh();
            }
        } else if (StdDraw.isKeyPressed(79)) {
            if (!pressHold) {
                screenSizeX *= 2;
                screenSizeY *= 2;
                screenXPos -= 0.25 * screenSizeX;
                screenYPos -= 0.25 * screenSizeY;
                pressHold = true;
                refresh();
            }
        } else if (StdDraw.isKeyPressed(87)) {
            screenYPos += 10;
            refresh();
        } else if (StdDraw.isKeyPressed(83)) {
            screenYPos -= 10;
            refresh();
        } else if (StdDraw.isKeyPressed(65)) {
            screenXPos -= 10;
            refresh();
        } else if (StdDraw.isKeyPressed(68)) {
            screenXPos += 10;
            refresh();
        } else if (StdDraw.isKeyPressed(82)) {

        } else {
            pressHold = false;
        }
    }
}
