package models;

public class Metrics {
    
    private int numClasses;
    private int numMethods;
    private int numLines;
    private long analysisInMilliseconds;
    
    
    public int getNumClasses() {
        return numClasses;
    }
    
    public void addNumClass(int numClass) {
        this.numClasses += numClass;
    }
    
    public int getNumMethods() {
        return numMethods;
    }
    
    public void addtNumMethods(int numMethods) {
        this.numMethods += numMethods;
    }
    
    public int getNumLines() {
        return numLines;
    }
    
    public void addNumLines(int numLines) {
        this.numLines += numLines;
    }
    
    public long getAnalysisInMilliseconds() {
        return analysisInMilliseconds;
    }
    
    public void setAnalysisInMilliseconds(long analysisInMilliseconds){
        this.analysisInMilliseconds = analysisInMilliseconds;
    }
    
    @Override
    public String toString() {
        return "Total - NumClasses: " + numClasses + " | NumMethods: " + numMethods
                + " | NumLines: " + numLines + " | TimeToAnalysis: " + analysisInMilliseconds;
    }
    
}
