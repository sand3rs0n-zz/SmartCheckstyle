package mutation;

public class Commit {
    
    private final String commitId;
    private final String commitTime;
    private final String repoName;
    
    public Commit(String commitId, String commitTime, String repoName) {
        this.commitId = commitId;
        this.commitTime = commitTime;
        this.repoName = repoName;
    }
    
    public String getCommitId() { return this.commitId; }
    
    public String getCommitTime() { return this.commitTime; }
    
    public String getRepoName() { return this.repoName; }
    
}
