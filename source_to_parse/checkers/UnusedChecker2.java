import java.io.IOException;

public class UnusedChecker2 {
    private String test;
    private int testNumber;

    public static void main(String[] args){
        IOException ex = new IOException();
        callSum();

    }

    public static void callSum(){
        System.out.println(1+2);
    }

}