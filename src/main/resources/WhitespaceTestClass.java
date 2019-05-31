package me.tomassetti.examples;

import checkers.WhitespaceChecker;import com.github.javaparser.JavaParser;import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;
import models.Issue;

public class WhitespaceTestClass {

    /**
     * The first and last name of this student.
     */

    // age
    private int age;

    public int getAbs(int x) {
        if  (x < 0){
            x *= -1;
        }

        return x;
    }

    public int printN(int N) {
        for    (int i=0; i < N; i++) {
            try {
                System.out.println(i);
            } catch (Exception ex) {
                // do nothing.
            }
        }
    }
}

