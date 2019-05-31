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

    public String getPackageName() {
        return this.packageName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public String getErrMessage() { return this.errMessage; }
    
    public String getIssueType() { return this.issueType; }
    
    @Override
    public String toString() {
        return packageName + " | " + fileName + " | " + "line: " + lineNumber
                + " | " + issueType + " | "
                + errMessage.replaceAll("\\n", "").trim();
    }
}
