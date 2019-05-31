package mutation;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class GitUtils {
    
    public static Commit getRecentCommitId(String pathName, int hashLength) {
        
        Commit commit = null;
        try {
            Repository repository = getGitRepo(pathName);
            String commitId = getHeadHashFromLast(repository, hashLength);
            String commitTime = getHeadCommitTime(repository);
            String repoName = repository.toString();
            commit = new Commit(commitId, commitTime, repoName);
            
        } catch (IOException e) {
            System.out.println("Failed to get HEAD commit id.");
        }
        return commit;
    }
    
    public static boolean isGitRepo(String pathName) {
    
        if (getGitRepo(pathName) == null) {
            return false;
        }
        return true;
    }
    
    public static String getHeadHashFromLast(Repository gitRepo, Integer hashLength)
            throws IOException {
        
        String objectId = gitRepo.resolve(Constants.HEAD).toObjectId().getName();
        String objectIdFromLast;
        if (hashLength >= 40) {
            objectIdFromLast = objectId;
        } else {
            objectIdFromLast = objectId.substring(objectId.length() - hashLength);
        }
        
        return objectIdFromLast;
    }
    
    public static String getHeadCommitTime(Repository gitRepo) throws IOException {
        ObjectId objectId = gitRepo.resolve(Constants.HEAD);
        RevWalk walk = new RevWalk(gitRepo);
        RevCommit revCommit = walk.parseCommit(objectId);
        int commitTime = revCommit.getCommitTime();
        return new Date(commitTime).toString();
    }
    
    public static Repository getGitRepo(String pathName) {
        File dir = new File(pathName);
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.addCeilingDirectory(new File("~"));
        Repository repository = null;
        try {
            repository = repositoryBuilder.findGitDir(dir).build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return repository;
    }
    
    public static void main(String[] args) throws IOException {
    
        String pathName = args[0];
        Repository gitRepo = getGitRepo(pathName);
        
        // Case 1 - Get HEAD hash with repository
        System.out.println(getHeadHashFromLast(gitRepo, 10));
        
        // Case 2 - get HEAD commitTime with repository
        System.out.println(getHeadCommitTime(gitRepo));
    }
    
}
