package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class View {
	public static JFrame frame = new JFrame("Dockerfile Generator");
	
	public void run(){
		EventQueue.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        try {
	        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | 
	        		UnsupportedLookAndFeelException ex) {
	        }
	        //initialize the main panel
	        Panel controlPanel = new Panel();
	        //JFrame frame = new JFrame("Dockerfile Generator");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLayout(new BorderLayout());
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
	        panel.add(controlPanel, BorderLayout.CENTER);
	        //panel.setBackground(new Color(205,239,255));
	        frame.add(panel);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	        frame.repaint();
	      }
		});
	}
}
