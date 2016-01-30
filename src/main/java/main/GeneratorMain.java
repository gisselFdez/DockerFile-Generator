package main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import engine.GitLoader;

public class GeneratorMain {

	
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Get repository sources
		GitLoader git = new GitLoader();
        git.getSources("https://github.com/gisselFdez/PFE-M2IAGL.git");
	    
	}

}
