package billboards.shared;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
/**
 * Utility methods
 * @author  Jack Nielsen
 */
public class Utility {
    /**
     * Prints 2d arrays, useful for debug
     * @param arr
     */
    public static void print2dArray(String[][] arr){
        for(int i = 0; i < arr.length; i ++){
            System.out.println(Arrays.toString(arr[i]));
        }
    }
}
