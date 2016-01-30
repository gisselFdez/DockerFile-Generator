package engine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class GitLoader {

	private String localPath, remotePath;
    private Repository localRepo;
    private Git git;
    
	public Boolean getSources(String remotePath){
		try {
        	this.localPath = getLocalPath(); 
        	this.remotePath = remotePath;
			localRepo = new FileRepository(localPath + "/.git");
			git = new Git(localRepo);
			
			Git.cloneRepository().setURI(remotePath)
            .setDirectory(new File(localPath)).call();
			return true;
			
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private String getLocalPath(){
		String localPath = System.getProperty("user.dir")+"\\test";
		return localPath;
	}
}
