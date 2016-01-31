package engine;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contain the methods that analyse the code from a project
 * @author Ana Gissel
 *
 */
public class SourceAnalyser {

	private String pomPath;
	
	public SourceAnalyser(){
		pomPath ="";
	}
	
	/**
	 * Verifies if the project is a Maven project
	 * @return true if it is.
	 */
	public Boolean isMavenProject(){
		String localPath = System.getProperty("user.dir")+"\\test";
		Boolean isMaven = false;
		
		//look for pom.xml file
		getPomFile(new File(localPath));
		if(!pomPath.equals(""))
			isMaven=true;
		
		return isMaven;
	}
	
	/**
	 * Read all the dependencies from a pom.xml
	 */
	public void getDependencies(){
		//read dependencies from pom.xml
		 try {
			File fXmlFile = new File(pomPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());					
			NodeList nList = doc.getElementsByTagName("dependency");
					
			System.out.println("----------------------------");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);						
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("groupId : " + eElement.getElementsByTagName("groupId").item(0).getTextContent());
					System.out.println("artifactId : " + eElement.getElementsByTagName("artifactId").item(0).getTextContent());
					System.out.println("version : " + eElement.getElementsByTagName("version").item(0).getTextContent());					
				}
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
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
	            	break;
	            }
	            else if (listFile[i].isDirectory())
	            	getPomFile(listFile[i]);
	        }
	    }
	}
}
