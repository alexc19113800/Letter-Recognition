import java.util.ArrayList;

public class FreeBoard extends Board {
    double side;
    boolean printedAns;

    public FreeBoard(int w, int h) {
        super();
        inputScreen = new int[h][w];
        height = inputScreen.length;
        width = inputScreen[0].length;
        side = 560.0 / height;
        printedAns = false;
    }

    public FreeBoard(int w, int h, Handler han) {
        this(w, h);
        handler = han;
        answers = handler.getAnswers();
    }

    public void draw() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int color = inputScreen[r][c];
                if (color >= 0 && color <= 255) {
                    StdDraw.setPenColor(color, color, color);
                } else if (color > 255) {
                    StdDraw.setPenColor(0, 0, 255);
                } else if (color < 0 && color >= -255) {
                    StdDraw.setPenColor(Math.abs(color), 0, 0);
                } else {
                    StdDraw.setPenColor(StdDraw.MAGENTA);
                }
                StdDraw.filledRectangle((c + 0.75) * side, (r * -side) + (560 - 0.75*side), side, side);
            }
        }
        StdDraw.setPenColor(StdDraw.RED);
        for (int i = 0; i < sectionX.size(); i++) {
            double xP = sectionX.get(i) * side - side / 2;
            double yP = (sectionY.get(i) * -side) + side / 2 + 560.0;
            double widthB = sectionW.get(i) * side;
            double heightB = sectionH.get(i) * -side;
            StdDraw.line(xP, yP, xP + widthB, yP);
            StdDraw.line(xP, yP, xP, yP + heightB);
            StdDraw.line(xP, yP + heightB, xP + widthB, yP + heightB);
            StdDraw.line(xP + widthB, yP, xP + widthB, yP + heightB);
        }

        for (int i = 0; i < buttonX.length; i++) {
            if (onButton[i]) {
                StdDraw.setPenColor(100, 100, 100);
                if (onButtonCt[i] > 0)
                    onButtonCt[i] /= 3;
            } else {
                int color = 100 + onButtonCt[i];
                StdDraw.setPenColor(color, color, color);
                if (onButtonCt[i] < 60) {
                    onButtonCt[i] += 5;
                }
            }
            StdDraw.filledRectangle(buttonX[i], buttonY[i], buttonW[i], buttonH[i]);
        }
        StdDraw.setPenColor(0, 0, 0);
        for (int i = 0; i < buttonX.length; i++) {
            StdDraw.text(buttonX[i], buttonY[i], buttonText[i]);
        }

        StdDraw.setFont(big);
        if (!printedAns) {
            if (answer >= 0) {
                System.out.println("Got: " + answers[answer]);
                printedAns = true;
            } else if (answer == -2) {//multiple answer
                String output = "";
                for (int i = 0; i < answerInArray.length; i++) {
                    output += answers[answerInArray[i]];
                }
                System.out.println("Got: " + output);
                printedAns = true;
            } else if (answer == -10) {
                System.out.println("Got: Error");
                printedAns = true;
            }
        } else {
        }

        StdDraw.setFont(small);
    }

    public void window() {
        super.window();
        buttonX = new double[]{80, 240, 400, 560,
                80, 240, 400, 560};
        buttonY = new double[]{670, 670, 670, 670,
                620, 620, 620, 620};
        buttonW = new double[]{80, 80, 80, 80,
                80, 80, 80, 80};
        buttonH = new double[]{25, 25, 25, 25,
                25, 25, 25, 25};
        onButtonCt = new int[]{0, 0, 0, 0,
                0, 0, 0, 0};
        buttonText = new String[]{"Test", "Selection", "Blur", "Resize",
                "Next", "Clear", "Run Samples", "Exit"};
        onButton = new boolean[]{false, false, false, false,
                false, false, false, false};
    }

    public void buttonPressed(int but) {
        sectionX.clear();
        sectionW.clear();
        sectionY.clear();
        sectionH.clear();
        switch (but) {
            case 0:
                //get multiple figures
                printedAns = false;//print answer again
                ArrayList<int[][]> sec = isolateFigures();
                int[][][] fin = new int[sec.size()][28][28];
                for (int i = 0; i < sec.size(); i++) {
                    fin[i] = (ImageEditor.edit(sec.get(i), 28));
                }
                //System.out.println(sections.size());

                if (fin.length >= 1) {
                    handler.test(fin);
                }
                break;
            case 1:
                //single out letters
                ArrayList<int[][]> sections = isolateFigures();
                int[][][] num = new int[sections.size()][height][width];
                for (int i = 0; i < sections.size(); i++) {
                    num[i] = (ImageEditor.edit(sections.get(i), 28));
                }
                break;
            case 2:
                //blur with 5*5 Gaussian Blur kernel
                int[][] output = new int[height][width];
                int[][] kernel = new int[][]
                        {{1, 4, 6, 4, 1},
                                {4, 16, 24, 16, 4},
                                {6, 24, 36, 24, 6},
                                {4, 16, 24, 16, 4},
                                {1, 4, 6, 4, 1}};
                int maxi = -1000;
                for (int r = 0; r < height; r++) {
                    for (int c = 0; c < width; c++) {
                        for (int i = -2; i < 3; i++) {
                            for (int j = -2; j < 3; j++) {
                                if (r + i >= 0 && r + i < output.length && c + j >= 0 && c + j < output[0].length) {
                                    output[r + i][c + j] += inputScreen[r][c] * kernel[i + 2][j + 2];
                                }
                            }
                        }
                    }
                }
                for (int r = 0; r < height; r++) {
                    for (int c = 0; c < width; c++) {
                        if (maxi < output[r][c]) {
                            maxi = output[r][c];
                        }
                    }
                }
                for (int r = 0; r < height; r++) {
                    for (int c = 0; c < width; c++) {
                        output[r][c] *= 255;
                        output[r][c] /= maxi;
                        if (output[r][c] >= 255) {
                            output[r][c] = 255;
                        } else if (output[r][c] < 0) {
                            output[r][c] = 0;
                        }
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
                for (int i = 0; i < ss.size(); i++) {
                    nn[i] = (ImageEditor.edit(ss.get(i), 28));
                }
                if (nn.length >= 1) {
                    inputScreen = new int[height][width];
                    setInputScreen(nn[0]);
                }
                sectionX.clear();
                sectionW.clear();
                sectionY.clear();
                sectionH.clear();
                break;
            case 4:
                //get next input
                handler.next();
                buttonPressed(0);
                break;

            case 5:
                //clear screen
                inputScreen = new int[height][width];
                break;
            case 6:
                //run 1000 tests
                handler.clearData();
                handler.next(1000);
                handler.test();
                break;
            case 7:
                //close program
                System.exit(0);
        }
    }

    public void listenMouse() {
        x = StdDraw.mouseX();
        y = StdDraw.mouseY();
        bX = (int) (x / side);
        bY = (int) ((560 - y) / side);
        for (int i = 0; i < buttonX.length; i++) {
            if (x >= buttonX[i] - buttonW[i] && x <= buttonX[i] + buttonW[i]
                    && y >= buttonY[i] - buttonH[i] && y <= buttonY[i] + buttonH[i]) {
                onButton[i] = true;
            } else {
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
                        if (inputScreen[bY - 1][bX] <= pen / 2) {
                            inputScreen[bY - 1][bX] = pen / 2;
                        }
                        if (bX > 0) {
                            if (inputScreen[bY - 1][bX - 1] < pen / 3) {
                                inputScreen[bY - 1][bX - 1] = pen / 3;
                            }
                        }
                        if (bX < 27) {
                            if (inputScreen[bY - 1][bX + 1] < pen / 3) {
                                inputScreen[bY - 1][bX + 1] = pen / 3;
                            }
                        }
                    }
                    if (bY < 27) {
                        if (inputScreen[bY + 1][bX] < pen / 2) {
                            inputScreen[bY + 1][bX] = pen / 2;
                        }
                        if (bX > 0) {
                            if (inputScreen[bY + 1][bX - 1] < pen / 3) {
                                inputScreen[bY + 1][bX - 1] = pen / 3;
                            }
                        }
                        if (bX < 27) {
                            if (inputScreen[bY + 1][bX + 1] < pen / 3) {
                                inputScreen[bY + 1][bX + 1] = pen / 3;
                            }
                        }
                    }
                    if (bX > 0) {
                        if (inputScreen[bY][bX - 1] < pen / 2) {
                            inputScreen[bY][bX - 1] = pen / 2;
                        }
                    }
                    if (bX < 27) {
                        if (inputScreen[bY][bX + 1] < pen / 2) {
                            inputScreen[bY][bX + 1] = pen / 2;
                        }
                    }
                } else {

                }
                for (int i = 0; i < buttonX.length; i++) {
                    if (onButton[i]) {
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
}