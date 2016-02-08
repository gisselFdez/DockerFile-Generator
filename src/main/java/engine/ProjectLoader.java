package main.java.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import main.java.util.PathLocation;

/**
 * 
 * @author Ana Gissel
 *
 */
public class ProjectLoader {

	private String remotePath;
    private Repository localRepo;
    private Git git;
    
    /**
     * Get the sources from a git repository
     * @param remotePath
     * @return true if succed
     */
	public Boolean getGitSources(String remotePath){
		try {
        	setLocalPath(); 
        	this.remotePath = remotePath;
			localRepo = new FileRepository(PathLocation.location + "/.git");
			git = new Git(localRepo);
			
			//delete the directory if it exists already
			deleteDirectory(new File(PathLocation.location));
			
			//clone the repository
			Git.cloneRepository().setURI(remotePath)
            .setDirectory(new File(PathLocation.location)).call();
			return true;
			
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the sources from a local path
	 * @param remotePath
	 * @return
	 */
	public Boolean getLocalSources(String remotePath){		
		setLocalPath(remotePath); 
		//verify if the directory exists
		File f = new File(PathLocation.location);
		if (f.exists() && f.isDirectory()) 
		   return true;
		else 
			return false;
     }
	
	/**
	 * Sets the local path where the git repository will be clone
	 * @return
	 */
	private void setLocalPath(){
		PathLocation.location = System.getProperty("user.dir")+"/test";
	}
	
	/**
	 * Sets the local path where the local sources are placed
	 */
	private void setLocalPath(String localPath){
		PathLocation.location = localPath;
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
