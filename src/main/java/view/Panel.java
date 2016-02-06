package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import engine.ProjectAnalyser;
import engine.ProjectLoader;
import generator.FileCreator;
import model.Dependency;
import model.Plugin;
import processors.ClassProcessor;
import spoon.Launcher;
import util.PathLocation;

public class Panel extends JPanel {

	ProjectAnalyser analyser;
	ProjectLoader loader;
	JPanel projectPanel;
	JPanel pluginPanel;
	JPanel mainsPanel;
	JPanel resultsPanel;
	
	public Panel() {
		this.loader = new ProjectLoader();
		this.analyser = new ProjectAnalyser();
	    init();
	 }
	
	/**
	 * Initialize the main panel
	 */
	private void init() {
	    this.setLayout(new FlowLayout());
	    this.setPreferredSize(new Dimension(550, 450));
	    
	    initProjectPanel();
	    this.add(this.projectPanel);
	}
	
	/**
	 * Initialize the ProjectPanel 
	 */
	private void initProjectPanel(){
		this.projectPanel = new JPanel();
		this.projectPanel.setPreferredSize(new Dimension(550,130));
		this.projectPanel.setBorder(new TitledBorder("Project Information"));
		this.projectPanel.setLayout(null);
		
		//initialize components
		JLabel lblProject = new JLabel("Project location");
		lblProject.setBounds(20, 30, 100, 20);
		JTextField txtProjectpPath = new JTextField("");
		txtProjectpPath.setPreferredSize(new Dimension(250,20));
		txtProjectpPath.setBounds(110, 30, 430, 20);
		JLabel lblType = new JLabel("Type of Project");
		lblType.setBounds(20, 60, 100, 20);
		JComboBox cmbType = new JComboBox(new String[]{"Git", "Local project" });
		cmbType.setBounds(110, 60, 100, 20);
		JLabel lblError = new JLabel("");
		lblError.setForeground(Color.red);
		lblError.setBounds(220, 60, 250, 20);
		JButton btnLoad = new JButton("Load Project");
		btnLoad.setBounds(110, 90, 100, 30);
		btnLoad.addActionListener(new ActionListener() {			 
            public void actionPerformed(ActionEvent e)
            {            	
            	Boolean loadedSources = false;
            	ProjectLoader loader = new ProjectLoader();
            	//get type of sources
            	if(cmbType.getSelectedItem().toString().equals("Git")){
            		//load sources from git
            		PathLocation.gitLocation = txtProjectpPath.getText();
            		loadedSources = loader.getGitSources(txtProjectpPath.getText());
            	}
            	else{
            		//load local sources
            		loadedSources = loader.getLocalSources(txtProjectpPath.getText());
            	}
            		
                //load sources            	
            	if(loadedSources){   
            		btnLoad.setEnabled(false);
                	//verify if is a maven project
                	if(analyser.isMavenProject()){
                		initDockerFileConfiguration();                		
                	} 
                	else{
                		lblError.setText("The project selected is not a Maven project.");
                	}
            	} 
            	else{
            		lblError.setText("The sources couldn't be loaded.");
            	}
            }
        });
		
		//add components
		this.projectPanel.add(lblProject);
		this.projectPanel.add(txtProjectpPath);
		this.projectPanel.add(lblType);
		this.projectPanel.add(cmbType);
		this.projectPanel.add(btnLoad);
		this.projectPanel.add(lblError);
	}
	
	/**
	 * Call all the method to initialize the docker file configuration panels
	 */
	private void initDockerFileConfiguration(){		
		initPluginPanel();
		initMainsPanel();
		initResultsPanel();
		View.frame.revalidate();
	}
	
	/**
	 * Initialize the panel to generate the results
	 */
	private void initResultsPanel(){
		this.resultsPanel = new JPanel();
		this.resultsPanel.setPreferredSize(new Dimension(550,50));
		this.resultsPanel.setLayout(null);
		
		//initialize components
		JButton btnGenerate = new JButton("Generate Dockerfile");
		btnGenerate.setBounds(200, 20, 150, 30);
		btnGenerate.addActionListener(new ActionListener() {			 
            public void actionPerformed(ActionEvent e)
            {
                //get dependencies
            	List<Dependency>  dependencies = analyser.getDependencies();
            	
            	//get plugins list            	
            	List<Plugin> plugins = getPlugins();
            	
            	//get main
            	String main = getMain();
            	
            	//generate docker file
        		String gitURL = PathLocation.gitLocation;//PathLocation.location;//"https://github.com/gisselFdez/ICSE-2013-TestEvol.git";
        		String pathToPom = PathLocation.pomLocation;// where is the pom.xml file ?
        		String war = analyser.getArtifactId(); // the name of the war file generated
        		
        		FileCreator fileCreator = new FileCreator(plugins);
        		fileCreator.createDockerfile(gitURL, pathToPom, war);
            }
        });
		JLabel lblResult = new JLabel();
		//add components
		this.resultsPanel.add(btnGenerate);
		this.resultsPanel.add(lblResult);
		this.add(this.resultsPanel);
	}
	
	/**
	 * Initialize the pluginsPanel
	 */
	private void initPluginPanel(){
		//get plugins from pom file
		List<List<Plugin>> repeated = analyser.getRepeatedPlugins();
		
		if(!repeated.isEmpty()){
			this.pluginPanel = new JPanel();
			this.pluginPanel.setLayout(null);
			this.pluginPanel.setBorder(new TitledBorder("Plugins"));
			int posy = 60;
			
			JLabel lblPlugin = new JLabel("Some plugins on the pom.xml have the same groupId, please select the artifactId you want to include.");			
			lblPlugin.setBounds(20, 20, 540, 40);
			this.pluginPanel.add(lblPlugin);
			
			JComboBox cmbPlugin;
			for(List<Plugin> list: repeated){
				cmbPlugin = new JComboBox();
				cmbPlugin.setBounds(110, posy, 250, 20);
				for(Plugin s: list){
					cmbPlugin.addItem(s.getArtifactId());
				}
				this.pluginPanel.add(cmbPlugin);
				posy = posy+30;
			}	
			this.pluginPanel.setPreferredSize(new Dimension(550,posy+10));
			
			this.add(this.pluginPanel);
			this.repaint();
		}		
	}
	
	/**
	 * Initialize the main panel
	 */
	private void initMainsPanel(){
		//get main from pom file
		if(analyser.getMainClass().equals("")){
			//execute Spoon to find a main class			
			List<String> mainsList = runProcessor(); 
			if(!mainsList.isEmpty()){
				this.mainsPanel = new JPanel();
				this.mainsPanel.setPreferredSize(new Dimension(550,100));
				this.mainsPanel.setBorder(new TitledBorder("Main Class"));
				this.mainsPanel.setLayout(null);
				
				//initialize components
				JLabel lblMain = new JLabel("There is no main class specified on the pom.xml file, please select a main class.");			
				lblMain.setBounds(20, 20, 540, 40);
				this.mainsPanel.add(lblMain);
				JComboBox cmbMains = new JComboBox();
				cmbMains.setBounds(110, 60, 250, 20);
				
				for(String main: mainsList){
					cmbMains.addItem(main);
				}
				this.mainsPanel.add(cmbMains);
				this.add(mainsPanel);
			}
		}
	}
	
	/**
	 * Returns the main class specified on the pom.xml file,
	 * if there is no main class specified in the pom.xml class
	 * it will return the selected class from the mainsPanel
	 * 
	 * @return
	 */
	private String getMain(){
		String mainClass = analyser.getMainClass();
		if(!mainClass.equals(""))
			return mainClass;
		else{
			if(this.mainsPanel!=null){
				for(Component c:this.mainsPanel.getComponents()){
					if(c instanceof JComboBox){
						JComboBox cmb = (JComboBox)c;
						mainClass = cmb.getSelectedItem().toString();
					}						
				}
			}
			return mainClass;
		}
	}
	
	/**
	 * Excutes the spoon processor to find a main class
	 * @return
	 */
	private List<String> runProcessor(){
		Launcher launcher = new Launcher();
		ClassProcessor processor = new ClassProcessor();
		launcher.addProcessor(new ClassProcessor());
		String path = System.getProperty("user.dir")+"\\test";
        launcher.run(new String[] {"-i", path, "-x"});
        return processor.getMainClasses();
	}
	
	/**
	 * Returns the list of plugins to add in the dockerfile. This method removes the unselected plugins
	 * (in the repeated plugins panel) from the list of plugins.
	 * @return the list of plugins to add in the dockerfile.
	 */
	private List<Plugin> getPlugins(){
		List<List<Plugin>> repeated = analyser.getRepeatedPluginsList();
		List<Plugin> plugins = analyser.getPluginsList();
		
		if(repeated.isEmpty())
			return plugins;
		else
		{
			//get the selected plugins
			Component[] components = this.pluginPanel.getComponents();
			List<String> pluginsSelected = new ArrayList<String>();
			
			for(int i = 0; i<components.length ;i++){
				if(components[i] instanceof JComboBox){
					JComboBox cmbPlugin = (JComboBox)components[i];
					pluginsSelected.add(cmbPlugin.getSelectedItem().toString());
				}
			}
			
			//get the non selected plugins
			List<Plugin> noSelected = new ArrayList<Plugin>();			
			for(List<Plugin> repPlug: repeated){
				for(Plugin p: repPlug){
					Boolean selected = false;
					for(String s: pluginsSelected){
						if(p.getArtifactId().equals(s))
							selected = true;
					}
					if(!selected)
						noSelected.add(p);
				}
			}
						
			//delete the non selected plugins from the plugins list
			for(Plugin p : noSelected){
				plugins.remove(p);
			}
				
			return plugins;
		}
	}
}
