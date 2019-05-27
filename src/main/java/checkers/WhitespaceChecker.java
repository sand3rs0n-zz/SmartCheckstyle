package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.Issue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitespaceChecker extends VoidVisitorAdapter<List<Issue>> {
    private String packageName;
    private String fileName;
    private String ISSUE_TYPE = "WHITESPACE";
    private List<Integer> ifStmtLineNumbers = new ArrayList<>();
    private List<Integer> forStmtLineNumbers = new ArrayList<>();
    private List<Integer> catchStmtLineNumbers = new ArrayList<>();

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

        int nextIfLineIdx = 0, nextForLineIdx = 0, nextCatchIdx = 0;
        int curLineNumber = 1;
        try (BufferedReader br = new BufferedReader(new FileReader("source_to_parse/checkers/"+this.fileName))) {
            while (br.ready()
                    && (nextIfLineIdx < ifStmtLineNumbers.size()
                        || nextForLineIdx < forStmtLineNumbers.size()
                        || nextCatchIdx < catchStmtLineNumbers.size())) {
                String line = br.readLine();
                if (nextIfLineIdx < ifStmtLineNumbers.size() && curLineNumber ==  ifStmtLineNumbers.get(nextIfLineIdx)) {
                    validateIfStmt(line, curLineNumber, issues);
                    nextIfLineIdx++;
                } else if (nextForLineIdx < forStmtLineNumbers.size() && curLineNumber ==  forStmtLineNumbers.get(nextForLineIdx)) {
                    validateForStmt(line, curLineNumber, issues);
                    nextForLineIdx++;
                } else if (nextCatchIdx < catchStmtLineNumbers.size() && curLineNumber ==  catchStmtLineNumbers.get(nextCatchIdx)) {
                    validateCatchStmt(line, curLineNumber, issues);
                    nextCatchIdx++;
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

    @Override
    public void visit(ForStmt n, List<Issue> issues) {
        super.visit(n, issues);
        forStmtLineNumbers.add(n.getRange().get().begin.line);
    }

    @Override
    public void visit(CatchClause n, List<Issue> issues) {
        super.visit(n, issues);
        catchStmtLineNumbers.add(n.getRange().get().begin.line);
    }

    private void validateIfStmt(String ifBlock, int line, List<Issue> issues) {
        Pattern whitespace = Pattern.compile(".*if\\s[(].*");
        Matcher matcher = whitespace.matcher(ifBlock);
        if (matcher.find()) {
            return;
        }

        Pattern multiWhitespace = Pattern.compile(".*if\\s\\s+[(].*");
        matcher = multiWhitespace.matcher(ifBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line,"] if statement has multiple whitespace before (."));
            return;
        }

        issues.add(generateIssue(line,"] if statement is missing whitespace before (."));
    }

    private void validateForStmt(String forBlock, int line, List<Issue> issues) {
        Pattern whitespace = Pattern.compile(".*for\\s[(].*");
        Matcher matcher = whitespace.matcher(forBlock);
        if (matcher.find()) {
            return;
        }

        Pattern multiWhitespace = Pattern.compile(".*for\\s\\s+[(].*");
        matcher = multiWhitespace.matcher(forBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line,"] for statement has multiple whitespace before (."));
            return;
        }

        issues.add(generateIssue(line,"] for statement is missing whitespace before (."));
    }

    private void validateCatchStmt(String catchBlock, int line, List<Issue> issues) {
        Pattern whitespace = Pattern.compile(".*\\}\\scatch\\s[(].*");
        Matcher matcher = whitespace.matcher(catchBlock);
        if (matcher.find()) {
            return;
        }

        Pattern multiWhitespace = Pattern.compile(".*catch\\s\\s+[(].*");
        matcher = multiWhitespace.matcher(catchBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line,"] catch statement has multiple whitespace before (."));
            return;
        }

        Pattern missingWhitespace = Pattern.compile(".*catch[(].*");
        matcher = missingWhitespace.matcher(catchBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line, "] catch statement is missing whitespace before (."));
            return;
        }

        Pattern missingWhitespaceAtStart = Pattern.compile(".*\\}[catch(].*");
        matcher = missingWhitespaceAtStart.matcher(catchBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line, "] catch statement is missing whitespace after {."));
            return;
        }

        Pattern multiWhitespaceAtStart = Pattern.compile(".*\\}\\s\\s+[catch(].*");
        matcher = multiWhitespaceAtStart.matcher(catchBlock);
        if (matcher.find()) {
            issues.add(generateIssue(line, "catch statement has multiple whitespace after {."));
            return;
        }
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
