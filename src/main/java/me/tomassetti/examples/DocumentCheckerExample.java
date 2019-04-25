package me.tomassetti.examples;

import checkers.DocumentChecker;
import com.github.javaparser.JavaParser;
import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;

import java.io.File;
import java.io.IOException;

public class DocumentCheckerExample {

    public static void checkDocuments(File projectDir) {

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);

            try {
                DocumentChecker dChecker = new DocumentChecker();
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

