package mutation;

public class Commit {
    
    private final String commitId;
    private final int commitTime;
    
    public Commit(String commitId, int commitTime) {
        this.commitId = commitId;
        this.commitTime = commitTime;
    }
    
    public String getCommitId() { return this.commitId; }
    
    public int getCommitTime() { return this.commitTime; }
    
}
