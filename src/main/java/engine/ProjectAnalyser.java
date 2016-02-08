package main.java.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import main.java.model.Dependency;
import main.java.model.Plugin;
import main.java.util.PathLocation;

/**
 * This class contain the methods that analyse the code from a project
 * @author Ana Gissel
 *
 */
public class ProjectAnalyser {

	private String pomPath;
	private List<Plugin> plugins;
	private List<List<Plugin>> repeatedPlugins;
	private static Boolean hasRepeatedPlugins = false;
	private String mainClass;
	
	public ProjectAnalyser(){
		pomPath ="";
		plugins = new ArrayList<Plugin>();
		repeatedPlugins = new ArrayList<List<Plugin>>();
	}
			
	public List<Plugin> getPluginsList() {
		return this.plugins;
	}
	
	public List<List<Plugin>> getRepeatedPluginsList() {
		return this.repeatedPlugins;
	}
	
	public String getMainClassName(){
		return this.mainClass;
	}

	/**
	 * Verifies if the project is a Maven project
	 * @return true if it is.
	 */
	public Boolean isMavenProject(){
		Boolean isMaven = false;
		
		//look for pom.xml file
		getPomFile(new File(PathLocation.location));
		if(!pomPath.equals(""))
			isMaven=true;
		
		return isMaven;
	}
	
	/**
	 * Read all the dependencies from a pom.xml
	 */
	public List<Dependency> getDependencies(){
		List<Dependency> dependencies = new ArrayList<Dependency>();
		
		//read dependencies from pom.xml
		 try {
			File fXmlFile = new File(pomPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();
					
			NodeList nList = doc.getElementsByTagName("dependency");
					
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Dependency dependency = new Dependency();
				
				Node nNode = nList.item(temp);						
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					dependency.setGroupId(eElement.getElementsByTagName("groupId").item(0).getTextContent());
					dependency.setArtifactId(eElement.getElementsByTagName("artifactId").item(0).getTextContent());
					dependency.setVersion(eElement.getElementsByTagName("version").item(0).getTextContent());
				}
				dependencies.add(dependency);
			}
			return dependencies;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	/**
	 * Get the list of pugins in the pom.xml file
	 * @return a list of repeated plugins
	 */
	private List<Plugin> getPlugins(){		
		//read dependencies from pom.xml
		 try {
			File fXmlFile = new File(pomPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();					
			NodeList nList = doc.getElementsByTagName("plugin");
					
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);						
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Plugin plugin = new Plugin();
					
					Element eElement = (Element) nNode;					
					plugin.setGroupId(eElement.getElementsByTagName("groupId").item(0).getTextContent());
					plugin.setArtifactId(eElement.getElementsByTagName("artifactId").item(0).getTextContent());
					if(eElement.getElementsByTagName("version").getLength()!=0){
						plugin.setVersion(eElement.getElementsByTagName("version").item(0).getTextContent());
					}						
					if(eElement.getElementsByTagName("configuration").getLength()!=0){
						plugin.setConfiguration(eElement.getElementsByTagName("configuration").item(0).getTextContent());
					}					
					this.plugins.add(plugin);
				}
			}
			return this.plugins;
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return this.plugins;
	    }
	}
	
	/**
	 * Get the main class specified in the pom.xml file
	 * @return
	 */
	public String getMainClass(){
		this.mainClass="";
		//read the main class from pom.xml
		 try {			 
			File fXmlFile = new File(pomPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
				
			if(doc.getElementsByTagName("mainClass").getLength()!=0)
				this.mainClass = doc.getElementsByTagName("mainClass").item(0).getTextContent();
			
			return this.mainClass;
			
	    } catch (NullPointerException | ParserConfigurationException | SAXException | IOException e) {
	    	e.printStackTrace();
	    	return mainClass;
	    }
	}
	
	/**
	 * Get the main class specified in the pom.xml file
	 * @return
	 */
	public String getArtifactId(){
		String artifact="";
		//read the main class from pom.xml
		 try {			 
			File fXmlFile = new File(pomPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
				
			if(doc.getElementsByTagName("artifactId").getLength()!=0)
				artifact = doc.getElementsByTagName("artifactId").item(0).getTextContent();
			
			return artifact;
			
	    } catch (NullPointerException | ParserConfigurationException | SAXException | IOException e) {
	    	e.printStackTrace();
	    	return artifact;
	    }
	}
	
	/**
	 * Verify if a plugin is repeated
	 * @return the list of repeated plugins
	 */
	public List<List<Plugin>> getRepeatedPlugins(){	
		getPlugins();
		List<String> repeatedGroup = new ArrayList<String>();
		
		for(int i =0;i<this.plugins.size();i++){
			Plugin plugin = this.plugins.get(i);
			String groupId = plugin.getGroupId();
			List<Plugin> repeated = new ArrayList<Plugin>();
			Boolean isRepeted = false;
			for(int j=0;j<this.plugins.size();j++){
				Plugin plug = this.plugins.get(j);
				String group = plug.getGroupId();
				if(groupId.equals(group) && i!=j){
					isRepeted = true;
					repeated.add(plug);
					repeated.add(plugin);
				}					
			}
			
			if(isRepeted && !repeatedGroup.contains(groupId)){
				hasRepeatedPlugins =true;
				repeatedGroup.add(groupId);
				this.repeatedPlugins.add(repeated);
			}
		}
		return this.repeatedPlugins;
	}
	
	/**
	 * Look for the pom.xml file in a project
	 * @param localPath
	 */
	private void getPomFile(File localPath){
		File listFile[] = localPath.listFiles();
	    if (listFile != null) {
	        for (int i = 0; i < listFile.length; i++) {
	            if (!listFile[i].isDirectory() && listFile[i].toString().contains("pom.xml")) {
	            	pomPath = listFile[i].getPath();
	            	PathLocation.pomLocation = listFile[i].getPath().replace("pom.xml", "");
	            	break;
	            }
	            else if (listFile[i].isDirectory())
	            	getPomFile(listFile[i]);
	        }
	    }
	}
}
