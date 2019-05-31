package mutation;

import models.Metrics;

public class Commit {
    
    private final String commitId;
    private final String commitTime;
    private final String repoName;
    private Metrics metrics;
    
    public Commit(String commitId, String commitTime, String repoName) {
        this.commitId = commitId;
        this.commitTime = commitTime;
        this.repoName = repoName;
    }
    
    public String getCommitId() { return this.commitId; }
    
    public String getCommitTime() { return this.commitTime; }
    
    public String getRepoName() { return this.repoName; }
    
    
    public Metrics getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }
    
}
