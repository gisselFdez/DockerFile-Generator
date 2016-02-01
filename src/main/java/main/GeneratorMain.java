package main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import engine.ProjectLoader;
import engine.ProjectAnalyser;


public class GeneratorMain {	
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Get repository sources
		ProjectLoader git = new ProjectLoader();
        //if(git.getSources("https://github.com/gisselFdez/TestEvol.git")){
        	ProjectAnalyser analyser = new ProjectAnalyser();
        	//verify if is a maven project
        	if(analyser.isMavenProject()){
        		System.out.println("is mvn");
        		//get dependencies from pom file
        		analyser.getDependencies();
        		//get plugins from pom file
        		analyser.getPlugins();
        	}      	
        //}	    
	}	
}
