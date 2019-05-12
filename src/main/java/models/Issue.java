package models;

public class Issue {

    private String packageName;
    private String fileName;
    private int lineNumber;
    private String issueType;
    private String errMessage;
    
    public Issue(String packageName, String fileName, int lineNumber, String issueType,
                 String errMessage) {
        this.packageName = packageName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.issueType = issueType;
        this.errMessage = errMessage;
        
    }
    
    @Override
    public String toString() {
        return packageName + " | " + fileName + " | " + "line: " + lineNumber
                + " | " + issueType + " | "
                + errMessage.replaceAll("\\n", "").trim();
    }
}
