import java.util.Arrays;

public class ImageEditor {

    public ImageEditor() {

    }

    public static int[][] edit(int[][] image, int preferredDimension) {
        //add buffer to make the image square
        int max = 0;
        int min = 0;
        int trigger = 0;//0-equal, 1-r>c, -1 - c>r
        int[][] output = new int[preferredDimension][preferredDimension];
        if (image.length == image[0].length) {
            max = image.length;
        } else if (image.length > image[0].length) {
            max = image.length;
            min = image[0].length;
            trigger = 1;
        } else {
            max = image[0].length;
            min = image.length;
            trigger = -1;
        }
        int[][] temp = new int[max][max];
        if (trigger == 1) {
            for (int r = 0; r < max; r++) {
                for (int c = 0; c < min; c++) {
                    temp[r][c + (max - min) / 2] = image[r][c];
                }
            }
        } else if (trigger == -1) {
            for (int r = 0; r < min; r++) {
                for (int c = 0; c < max; c++) {
                    temp[r + (max - min) / 2][c] = image[r][c];
                }
            }
        }

        if (temp.length == preferredDimension) {
            return temp;
        } else {
            return resize(temp, preferredDimension);
        }
    }


    //obtained from http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
    public static int[][] resize(int[][] input, int endSize) {
        double px, py;
        int[][] temp = new int[endSize-4][endSize-4];
        double modifier = input.length / ((double)temp.length);
        for (int r = 0; r < temp.length; r++) {
            for (int c = 0; c < temp[r].length; c++) {
                px = Math.floor(c * modifier);
                py = Math.floor(r * modifier);
                temp[r][c] = input[(int) py][(int) px];
            }
        }
        int[][] output = new int[endSize][endSize];
        for(int r = 2; r < endSize-2; r++){
            for(int c = 2; c < endSize-2; c++){
                output[r][c] = temp[r-2][c-2];
            }
        }
        return output;
    }
}
