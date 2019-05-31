package edu.uw.csep.scs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import checkers.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import models.Issue;
import modifiers.ImportModifier;
import modifiers.JavadocModifier;
import mutation.Commit;
import mutation.ErrorRecorder;
import mutation.GitUtils;
import org.apache.commons.cli.*;


public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        
        Options options = new Options();

        Option help = new Option("h", false, "options");
        options.addOption(help);

        Option input = new Option("i",true,"input root directory or file path");
        options.addOption(input);

        Option modify = new Option("m",false,"modify files");
        options.addOption(modify);

        Option checkJavadoc = new Option("j",false,"check javadoc style");
        options.addOption(checkJavadoc);

        Option checkDeclarations = new Option("d",false,"check declarations style");
        options.addOption(checkDeclarations);

        Option checkImports = new Option("im",false,"check unused imports");
        options.addOption(checkImports);

        Option checkMethods = new Option("md",false,"check unused methods");
        options.addOption(checkMethods);

        Option checkVariables = new Option("va",false,"check unused variables");
        options.addOption(checkVariables);

        Option checkWhitespaces = new Option("ws",false,"check whitespaces");
        options.addOption(checkWhitespaces);

        Option checkNewlines = new Option("n",false,"check new lines");
        options.addOption(checkNewlines);
    
        Option report = new Option("r",true,"a path to report directory");
        options.addOption(report);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            showOptions(options, "");
            return;
        }

        if (!cmd.hasOption("i")) {
            showOptions(options, "Missing input(-i) parameter");
            return;
        }

        List<File> files = generateFileList(cmd.getOptionValue("i"));

        List<Issue> issues = new ArrayList<>();

        for (File file : files) {
            CompilationUnit compilationUnit;
            try {
                compilationUnit = JavaParser.parse(file);
            } catch (Exception e) {
                System.out.println("Parsing exception " + e.getMessage());
                System.out.println(file.getAbsolutePath());
                return;
            }
            
            // javadoc
            if (cmd.hasOption("j")) {
                JavadocChecker javadocChecker = new JavadocChecker(file.getName());
                javadocChecker.visit(compilationUnit, issues);

                if (cmd.hasOption("m")) {
                    List<Issue> javadocIssues = new ArrayList<>();
                    JavadocModifier javadocModifier = new JavadocModifier(file.getName());
                    javadocModifier.visit(compilationUnit, javadocIssues);
                    if (javadocIssues.size() > 0) {
                        Files.write(Paths.get(file.getAbsolutePath()),
                                compilationUnit.toString().getBytes());
                        issues.addAll(javadocIssues);
                    }
                }
            }

            // declarations
            if (cmd.hasOption("d")) {
                DeclarationChecker declarationChecker = new DeclarationChecker(file.getName());
                declarationChecker.visit(compilationUnit, issues);
            }

            // methods
            if (cmd.hasOption("md")) {
                UnusedMethodChecker methodChecker = new UnusedMethodChecker(file.getName());
                List<Issue> methodIssues = new ArrayList<>();
                methodChecker.handleUnusedMethods(compilationUnit, methodIssues, cmd.hasOption("m"));
                issues.addAll(methodIssues);

                if (cmd.hasOption("m") && methodIssues.size() > 0) {
                    Files.write(Paths.get(file.getAbsolutePath()), compilationUnit.toString().getBytes());
                }
            }

            // variables
            if (cmd.hasOption("va")) {
                UnusedVariableChecker variableChecker = new UnusedVariableChecker(file.getName());
                List<Issue> variableIssues = new ArrayList<>();
                variableChecker.checkUnusedVariables(compilationUnit, variableIssues, cmd.hasOption("m"));
                issues.addAll(variableIssues);

                if (cmd.hasOption("m") && variableIssues.size() > 0) {
                    Files.write(Paths.get(file.getAbsolutePath()), compilationUnit.toString().getBytes());
                }
            }

            // imports
            if (cmd.hasOption("im")) {
                UnusedImportChecker importChecker = new UnusedImportChecker(file.getName());
                List<Issue> importIssues = new ArrayList<>();
                importChecker.checkUnusedImports(compilationUnit, importIssues);
                issues.addAll(importIssues);

                if (cmd.hasOption("m") && importIssues.size() > 0) {
                    ImportModifier importModifier = new ImportModifier(file.getName());
                    importModifier.visit(compilationUnit, issues);
                    Files.write(Paths.get(file.getAbsolutePath()), compilationUnit.toString().getBytes());
                }
            }

            // whitespaces
            if (cmd.hasOption("ws")) {
                WhitespaceChecker whitespaceChecker = new WhitespaceChecker(file.getName(), file.getPath());
                List<Issue> whitespaceIssues = new ArrayList<>();
                whitespaceChecker.visit(compilationUnit, whitespaceIssues);
                issues.addAll(whitespaceIssues);
            }

            // new lines
            if (cmd.hasOption("n")) {
                NewLineChecker newLineChecker = new NewLineChecker(file.getName());
                List<Issue> variableIssues = new ArrayList<>();
                newLineChecker.visit(compilationUnit, variableIssues);
                issues.addAll(variableIssues);
            }
        }

        Collections.sort(issues, Comparator.comparing(Issue::getPackageName)
                .thenComparing(Issue::getFileName)
                .thenComparing(Issue::getLineNumber));

        for (Issue issue : issues) {
            System.out.println(issue);
        }
    
        if (cmd.hasOption("r") && GitUtils.isGitRepo(cmd.getOptionValue("i"))) {
            String reportPath = cmd.getOptionValue("r");
            Commit commit = GitUtils.getRecentCommitId(cmd.getOptionValue("i"));
            if (commit == null) {
                System.out.println("Failed to find HEAD on the input directory.");
            } else {
                appendSummary(Paths.get(reportPath, "report.csv").toAbsolutePath().toString(),
                        issues, commit);
            }
            
        }
    }

    private static void appendSummary(String filePath, List<Issue> issues, Commit commit) {
        
        try {
            ErrorRecorder.appendSummary(filePath, issues, commit);
        }
        catch (IOException e) {
            System.out.println("Failed to create summary " + e.getMessage());
            }
        
    }
    
    private static List<File> generateFileList(String rootPath) {
        List<File> files = new ArrayList<>();
        File rootDirFile = new File(rootPath);
        if (rootDirFile.isDirectory()) {
            for (File file : rootDirFile.listFiles()) {
                files.addAll(generateFileList(file.getAbsolutePath()));
            }
        } else if (rootDirFile.isFile() && rootDirFile.getName().endsWith(".java")) {
            files.add(rootDirFile);
        }
        return files;
    }

    private static void showOptions(Options options, String message) {
        PrintWriter printWriter = new PrintWriter(System.out);
        printWriter.println(message);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(printWriter, 80, "ls", "",
                options, 4, 4,"", true);
        printWriter.flush();
    }
}
