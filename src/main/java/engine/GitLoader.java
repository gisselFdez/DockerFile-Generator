package engine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

/**
 * 
 * @author Ana Gissel
 *
 */
public class GitLoader {

	private String localPath, remotePath;
    private Repository localRepo;
    private Git git;
    
    /**
     * Get the sources from a git repository
     * @param remotePath
     * @return true if succed
     */
	public Boolean getSources(String remotePath){
		try {
        	this.localPath = getLocalPath(); 
        	this.remotePath = remotePath;
			localRepo = new FileRepository(localPath + "/.git");
			git = new Git(localRepo);
			
			//delete the directory if it exists already
			deleteDirectory(new File(localPath));
			
			//clone the repository
			Git.cloneRepository().setURI(remotePath)
            .setDirectory(new File(localPath)).call();
			return true;
			
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Return the local path where the git repository will be clone
	 * @return
	 */
	private String getLocalPath(){
		String localPath = System.getProperty("user.dir")+"\\test";
		return localPath;
	}
	
	/**
	 * Delete a directory with it's content
	 * @param file
	 */
	private void deleteDirectory(File file){
		File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	        	deleteDirectory(f);
	        }
	    }
	    file.delete();
	}
}
