package main.java.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import main.java.model.Plugin;


public class FileCreator {

	private File file;
	// private static final String path = "C:\\Users\\Pauline\\Desktop\\";
	private static final String path = "";
	private static final String filename = "Dockerfile";

	private static String Docker_from = "FROM java:7";
	private static String Docker_maintainer = "MAINTAINER ";
	private static String Docker_volume = "VOLUME /volume/"; // attach volume
	private static String Docker_run = "RUN \\";
	private static String Docker_env = "ENV";
	private static String Docker_cmd = "CMD [\"/bin/bash\"]";

	private String volume;
	private List<Plugin> plugins;

	public FileCreator(List<Plugin> plugins) {
		this.file = new File(path + filename);
		this.plugins = plugins;
		
		try {
			if (file.exists()) {
				file.delete();
			}

			if (this.file.createNewFile()) {
				System.out.println("Dockerfile created at : " + path + filename);
			} else {
				System.out.println("Unable to create the Dockerfile.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param urlSources le chemin vers les sources du projet
	 * @param pathToPom le chemin vers le pom depuis la racine du projet
	 * @param filename le nom du fichier genere (artifactId)
	 * @param version la version du fichier genere
	 * @param typeProject le type de projet (war ou jar)
	 * @param mainClass la classe Main du projet
	 * @return
	 */
	public Boolean createDockerfile(String urlSources, String pathToPom, String filename, String version, String typeProject, String mainClass) {
		String projectName = filename;

		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(this.file));

			output.write(getHeader());

			output.write(createBaseImage());
			output.write(newLine());
			output.write(createMaintainerInfos("Pauline", "pauline@test.com"));
			output.write(newLine());
			output.write(createUpdateAndInstallCommand());
			output.write(newLine());
//			output.write(Docker_env);
			output.write(newLine());
			output.write(createVolume("wd"));
			output.write(newLine());
			output.write(createWorkingDirectory());
			output.write(newLine());
			output.write(copySourcesCommand(urlSources, projectName));
			output.write(newLine());

			if (typeProject.equals("war")) {
				output.write(generateWar(projectName, pathToPom, filename));
			} else if (typeProject.equals("jar")) {
				output.write(generateJar(projectName, pathToPom, filename, version, mainClass));
			} else {
				System.out.println("Unknown format (accepted : .war and .jar files).");
			}

			output.write(newLine());
			output.write(createCmd());

			output.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getHeader() {
		Date date = new Date();
		return "# This Dockerfile was generated by the DockerFileGenerator project." 
				+ "\n# Created on "	+ date.toString() + "\n\n";
	}

	private String newLine() {
		return "\r\n\n";
	}

	/*
	 * *********************************************************************************************
	 * CREATE DOCKER'S COMMANDS
	 *********************************************************************************************/

	private String createBaseImage() {
		return "# Pull base image" + "\n" + Docker_from;
	}

	private String createMaintainerInfos(String name, String mail) {
		if (name != null && name != "" && mail != null && mail != "") {
			return Docker_maintainer + name + " \"" + mail + "\"";
		}
		return "";
	}

	private String createUpdateAndInstallCommand() {
		String command = "# Update and install\n"
				+ Docker_run 
				+ "\n  export DEBIAN_FRONTEND=noninteractive && \\" // avoid to enter password
				+ "\n  apt-get update && \\" 
				+ "\n  apt-get -y upgrade && \\"
				+ "\n  apt-get install -y vim wget curl git maven "; // install tools
		
		if (!this.plugins.isEmpty()) {
			for (Plugin plugin : this.plugins) {
				String artifactId = plugin.getArtifactId();
				if (artifactId.toLowerCase().contains("tomcat")) {
					command += artifactId.toLowerCase().split("-")[0] + " ";
				}
			}
		}
		
		return command;
	}

	private String createVolume(String volume) {
		if (volume != null && volume != "") {
			this.volume = volume;
			return "# Attach volume"
					+ "\n" + Docker_volume + this.volume;
		}
		return "";
	}

	private String createWorkingDirectory() {
		return "# Create working directory" 
				+ "\nRUN mkdir -p /local/" + this.volume 
				+ "\nWORKDIR /local/" + this.volume;
	}

	private String copySourcesCommand(String url, String projectName) {
		return "#Copy sources\n"
				+ "COPY " + url + " /" + projectName;
	}

	private String generateWar(String projectName, String pathToPom, String warFileName) {
		String command = "# Generate and copy war\n" + "RUN ";
		if (pathToPom != null || pathToPom != "") {
			command += "cd "+ projectName + "/" + pathToPom + " && ";
		}
		command += "mvn clean package -DskipTests && "
				+ "cp target/" + warFileName + ".war ~/../../var/lib/tomcat7/webapps/";
		return command;
	}

	private String generateJar(String projectName, String pathToPom, String jarFileName, String version, String mainClass) {
		String command = "# Generate JAR file\n"
				+ "RUN ";
		if (pathToPom != null || pathToPom != "") {
			command += "cd "+ projectName + "/" + pathToPom + " && ";
		}
		command += "mvn clean install -DskipTests && java -jar " + jarFileName + "-" + version + ".jar";
		return command;
	}

	private String createCmd() {
		return "# Run terminal"
				+ "\n" + Docker_cmd;
	}
	
	/*
	 * *********************************************************************************************
	 *  GETTERS & SETTERS FOR THE DOCKERFILE'S ARGUMENTS
	 *********************************************************************************************/

	public static String getDocker_from() {
		return Docker_from;
	}

	public static void setDocker_from(String docker_from) {
		Docker_from = docker_from;
	}

	public static String getDocker_maintainer() {
		return Docker_maintainer;
	}

	public static void setDocker_maintainer(String docker_maintainer) {
		Docker_maintainer = docker_maintainer;
	}

	public static String getDocker_run() {
		return Docker_run;
	}

	public static void setDocker_run(String docker_run) {
		Docker_run = docker_run;
	}

	public static String getDocker_env() {
		return Docker_env;
	}

	public static void setDocker_env(String docker_env) {
		Docker_env = docker_env;
	}

	public static String getDocker_cmd() {
		return Docker_cmd;
	}

	public static void setDocker_cmd(String docker_cmd) {
		Docker_cmd = docker_cmd;
	}

}
