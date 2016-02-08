package main.java.main;

import java.util.ArrayList;
import java.util.List;

import main.java.generator.FileCreator;
import main.java.model.Plugin;
import main.java.view.View;


public class GeneratorMain {	
    
	public static void main(String[] args) {	   
		
		//Create view
		View view = new View();
		view.run();
		
		/*List<Plugin> plugins = new ArrayList<Plugin>();
		String gitURL = "https://github.com/gisselFdez/ICSE-2013-TestEvol.git";
		String pathToPom = "tool/"; // where is the pom.xml file ?
		String war = "testevol"; // the name of the war file generated
		
		FileCreator fileCreator = new FileCreator(plugins);
		fileCreator.createDockerfile(gitURL, pathToPom, war);*/
	}	
}
