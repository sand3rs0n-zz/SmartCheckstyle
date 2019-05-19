import java.io.IOException;
import java.util.*;

public class UnusedChecker2 {
    private String test;
    private int testNumber;

    public static void main(String[] args){
        IOException ex = new IOException();
        callSum();
        System.out.println(test);
        System.out.println(testNumber);
    }

    public static void callSum(){
        System.out.println(1+2);
    }

}