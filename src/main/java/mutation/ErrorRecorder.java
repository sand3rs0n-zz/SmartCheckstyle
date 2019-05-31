package mutation;

import models.Issue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ErrorRecorder {

    public static void appendSummary(String reportFilePath, List<Issue> issues, Commit commit)
            throws IOException {
        Map<String, Long> errorCntsByType = issues.stream().collect(
                Collectors.groupingBy(Issue::getIssueType, Collectors.counting()));
    
        StringJoiner logHeader = new StringJoiner(",");
        logHeader.add("Git Repo.");
        logHeader.add("Commit");
        logHeader.add("Timestamp");
        
        StringJoiner logRecord = new StringJoiner(",");
        logRecord.add(commit.getRepoName());
        logRecord.add(commit.getCommitId());
        logRecord.add(String.valueOf(commit.getCommitTime()));
        
        errorCntsByType.forEach((k, v) -> {
                    logHeader.add(k);
                    logRecord.add(v.toString());
                }
             );
        logHeader.add("\n");
        logRecord.add("\n");

        Path path = Paths.get(reportFilePath);
        if (!Files.exists(path)) {
            Files.write(path, logHeader.toString().getBytes(), StandardOpenOption.CREATE);
            Files.write(path, logRecord.toString().getBytes(), StandardOpenOption.APPEND);
        } else {
            Files.write(path, logRecord.toString().getBytes(), StandardOpenOption.APPEND);
        }
        System.out.println("UPDATED: Error Summary: " + new File(reportFilePath).getAbsolutePath());

    }

    public static void main(String[ ] args) throws IOException {
        String reportFilePath = "./report.csv";
        List<Issue> issues = new ArrayList<>();
        String[] issueTypes = {"JAVA_DOC", "METHOD", "WHITESPACE", "JAVA_DOC", "METHOD",
                "JAVA_DOC"};
        for (String issueType: issueTypes) {
            issues.add(new Issue("", "", 0, issueType, ""));
        }
        appendSummary(reportFilePath, issues,
                new Commit("GIT_HASH_A", new Date().toString(), "banana.git"));
        appendSummary(reportFilePath, issues,
                new Commit("GIT_HASH_B", new Date().toString(), "apple.git"));


    }
}
