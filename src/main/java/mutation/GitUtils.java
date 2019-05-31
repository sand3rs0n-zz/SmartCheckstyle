package mutation;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class GitUtils {
    
    public static Commit getRecentCommitId(String pathName) {
        File dir = new File(pathName);
        Commit commit = null;
        try {
            Git git = Git.open(dir);
            ObjectId recentCommit = git.getRepository().resolve(Constants.HEAD);
            RevWalk revWalk = new RevWalk(git.getRepository());
            RevCommit revCommit = revWalk.parseCommit(recentCommit);
            commit = new Commit(recentCommit.getName(), revCommit.getCommitTime());
        } catch (IOException e) {
            System.out.println("Failed to get HEAD commit id.");
        }
        return commit;
    }
    
    public static boolean isGitRepo(String rootPath) {
        
        File dir = new File(rootPath);
        if (dir.isFile()) {
            return false;
        }
        
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            builder.setGitDir(new File(rootPath))
                    .readEnvironment()
                    .findGitDir().build();
    
        } catch (IOException e) {
            System.out.println("Not a git repository.");
            return false;
        }
        return true;
    }
    
    private static void checkStyleOnTags(File dir) throws  IOException {
        
        Git git = Git.open(dir);
        
        List<Ref> tags = null;
        try {
            tags = git.tagList().call();
            System.out.println("Found " + tags.size() + " tag(s).");
        } catch (GitAPIException e) {
            System.out.println("Failed to check styles");
        }
        System.out.println("Current: " + git.log().setMaxCount(1).toString());
        if (tags != null) {
            for (Ref tag : tags) {
                System.out.println("SWITCHING TO: " + tag.getName());
                try {
                    git.checkout().setName(tag.getName()).call();
                    System.out.println("Current: " + git.log().setMaxCount(1).toString());
                } catch (GitAPIException e) {
                    System.out.println("Failed to checkout " + tag.getName());
                    System.out.println(e.getMessage());
                }
                
            }
        }
        
        System.out.println("Current: " + git.log().setMaxCount(1).toString());
    }
    
    
    public static void main(String[] args) throws IOException {
        File filePath = new File(args[0]);
        GitUtils.checkStyleOnTags(filePath);
    }
}
