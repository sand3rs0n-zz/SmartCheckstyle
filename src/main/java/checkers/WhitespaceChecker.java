package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import models.Issue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitespaceChecker extends VoidVisitorAdapter<List<Issue>> {
    private String packageName;
    private String fileName;
    private String issueType = "WHITESPACE";
    private List<Integer> ifStmtLineNumbers = new ArrayList<>();

    public WhitespaceChecker(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void visit(CompilationUnit n, List<Issue> issues) {
        if (n.getPackageDeclaration().isPresent()) {
            this.packageName = n.getPackageDeclaration().toString();
        } else {
            this.packageName = "N/A";
        }

        super.visit(n, issues);

        int nextIfLineIdx = 0;
        int curLineNumber = 1;
        try (BufferedReader br = new BufferedReader(new FileReader("source_to_parse/checkers"+this.fileName))) {
            while (br.ready() && nextIfLineIdx < ifStmtLineNumbers.size()) {
                String line = br.readLine();
                if (curLineNumber ==  ifStmtLineNumbers.get(nextIfLineIdx)) {
                    validateIfStmt(line, curLineNumber);
                    nextIfLineIdx++;
                }

                curLineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(IfStmt n, List<Issue> issues) {
        super.visit(n, issues);
        ifStmtLineNumbers.add(n.getRange().get().begin.line);
    }

    private void validateIfStmt(String ifBlock, int line) {
        int numWhitespaces = 0;
        Pattern whitespace = Pattern.compile(".*if\\s[(].*");
        Matcher matcher = whitespace.matcher(ifBlock);
        if (matcher.find()) {
            numWhitespaces = 1;
            return;
        }

        Pattern multiWhitespace = Pattern.compile(".*if\\s\\s+[(].*");
        matcher = multiWhitespace.matcher(ifBlock);
        if (matcher.find()) {
            System.out.println("[Line: " + line + "] if statement has multiple whitespace.");
            return;
        }

        System.out.println("[Line: " + line + "] if statement is missing whitespace.");
    }
}
