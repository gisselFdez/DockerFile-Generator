package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import engine.ProjectAnalyser;
import engine.ProjectLoader;
import model.Dependency;
import model.Plugin;

public class Panel extends JPanel{

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
	    this.setPreferredSize(new Dimension(600, 600));
	    this.setBackground(Color.white);
	    
	    initProjectPanel();
	    this.add(this.projectPanel);
	}
	
	/**
	 * Initialize the ProjectPanel 
	 */
	private void initProjectPanel(){
		this.projectPanel = new JPanel();
		this.projectPanel.setPreferredSize(new Dimension(600,100));
		
		//initialize components
		JLabel lblProject = new JLabel("Project Information");
		JTextField txtProjectpPath = new JTextField("");
		txtProjectpPath.setPreferredSize(new Dimension(250,20));
		JLabel lblType = new JLabel("Type of Project");
		JComboBox cmbType = new JComboBox(new String[]{"Git", "Local project" });
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {			 
            public void actionPerformed(ActionEvent e)
            {
                //load sources            	
            	//if(git.getSources(txtProjectpPath.getText())){            		            		
                	//verify if is a maven project
                	if(analyser.isMavenProject()){
                		initDockerFileConfiguration();                		
                	} 
            	//}            		
            }
        });
		
		//add components
		this.projectPanel.add(lblProject);
		this.projectPanel.add(txtProjectpPath);
		this.projectPanel.add(lblType);
		this.projectPanel.add(cmbType);
		this.projectPanel.add(btnLoad);
	}
	
	/**
	 * Call all the method to initialize the docker file configuration panels
	 */
	private void initDockerFileConfiguration(){		
		initPluginPanel();
		initMainsPanel();
		initResultsPanel();
		//get dependencies from pom file
		//analyser.getDependencies();		
	}
	
	/**
	 * Initialize the panel to generate the results
	 */
	private void initResultsPanel(){
		this.resultsPanel = new JPanel();
		this.resultsPanel.setPreferredSize(new Dimension(600,100));
		
		//initialize components
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {			 
            public void actionPerformed(ActionEvent e)
            {
                //get dependencies
            	List<Dependency>  dependencies = analyser.getDependencies();
            	
            	//get plugins list            	
            	List<Plugin> plugins = getPlugins();
            	
            	//get main
            	
            	//generate docker file
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
			int height = 30;
					
			JComboBox cmbPlugin;
			for(List<Plugin> list: repeated){
				height += 30;
				cmbPlugin = new JComboBox();
				for(Plugin s: list){
					cmbPlugin.addItem(s.getArtifactId());
				}
				this.pluginPanel.add(cmbPlugin);
			}	
			this.pluginPanel.setPreferredSize(new Dimension(600,height));
			this.add(this.pluginPanel);
			this.repaint();
		}		
	}
	
	/**
	 * Initialize the main panel
	 */
	private void initMainsPanel(){
		//execute Spoon
		List<String> mainsList = null; //call spoon processor
		if(mainsList!=null){
			this.mainsPanel = new JPanel();
			this.mainsPanel.setPreferredSize(new Dimension(600,100));
			
			//initialize components
			JLabel lblMain = new JLabel("Main method");
			JComboBox cmbMains = new JComboBox();
			
			for(String main: mainsList){
				cmbMains.addItem(main);
			}
		}
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
