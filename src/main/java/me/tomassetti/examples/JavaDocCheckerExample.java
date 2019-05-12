package me.tomassetti.examples;

import checkers.JavaDocChecker;
import com.github.javaparser.JavaParser;
import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;

import java.io.File;
import java.io.IOException;

public class JavaDocCheckerExample {

    public static void checkDocuments(File projectDir) {

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);

            try {
                JavaDocChecker dChecker = new JavaDocChecker();
                dChecker.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
            System.out.println(Strings.repeat("=", path.length()));
        }).explore(projectDir);
    }

    public static void main(String[] args) {
        File projectDir = new File("source_to_parse/checkers");
        checkDocuments(projectDir);
    }
}

