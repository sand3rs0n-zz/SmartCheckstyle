import java.io.IOException;
import java.util.Math;

public class UnusedChecker2 {
    private String test;
    private int testNumber;

    public static void main(String[] args){
        IOException ex = new IOException();
        callSum();
        System.out.println(test);
        System.out.println(testNumber);
    }

    public static void callSum() {
        System.out.println(Math.pow(2, 3));
    }

}