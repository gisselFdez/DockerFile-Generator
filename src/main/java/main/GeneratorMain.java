package main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import engine.ProjectLoader;
import view.View;
import engine.ProjectAnalyser;


public class GeneratorMain {	
    
	public static void main(String[] args) {	   
		
		//Create view
		View view = new View();
		view.run();
		
		FileCreator fileCreator = new FileCreator();
		fileCreator.createDockerfile();
	}	
}
