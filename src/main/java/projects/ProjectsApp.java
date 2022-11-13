package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private ProjectService projectService = new ProjectService();
	private Project currentProject;
	private void processUserSelections() {
		boolean done = false; 
		while(!done) {
			try {
				int selection = getUserSelection();
				switch(selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
			
				default:
					System.out.println("\n "+selection+" is not a valid selection. Try Again.");
				
				}
			}catch (Exception e) {
				String exc = "Error: ";
				exc+=e;
				System.out.println(exc);
			}
		}
	}

		private void selectProject() {
		listProjects();
		Integer projectID = getIntInput("Enter a project ID to select a project");
		currentProject = null;
		currentProject = projectService.fetchProjectByID(projectID);
	//	if(Objects.isNull(currentProject)) {
	//		System.out.println("Invalid project Id Selected.");
	//	}
	}



		private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		projects.forEach(project -> System.out.println(" "+project.getProjectId()+": "+project.getProjectName()));

		
	}

		private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: "+dbProject);
	}

		private BigDecimal getDecimalInput(String prompt) {
			String input = getStringInput(prompt);
			if (Objects.isNull(input)) return null;
			else {
				try {
					return new BigDecimal(input).setScale(2);
				} catch (NumberFormatException e) {
					throw new DbException(input +" is not a valid decimal number.");
					
				}
			}
		}

		private boolean exitMenu() {
			
		System.out.println("Exiting the menu.");	
		return true;
		}

		private int getUserSelection(){
			printOperations();
			Integer input = getIntInput("Enter a menu selection");
			return Objects.isNull(input)? -1: input;
		}

	private Integer getIntInput(String prompt) {
			String input = getStringInput(prompt);
			if (Objects.isNull(input)) return null;
			else {
				try {
					return Integer.valueOf(input);
				} catch (NumberFormatException e) {
					throw new DbException(input +" is not a valid number, try again.");
					
				}
			}
		}



	private String getStringInput(String prompt) {
		System.out.print(prompt +": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
			System.out.println("\nThese are the available selections. Press the enter key to quit:");
			for(String operation : operations) {
				System.out.println("   "+operation	);
			}
			if(Objects.isNull(currentProject)) {
				System.out.println("\nYou are not working with a project.");
			}else {
				System.out.println("\nYou are working with a project: " + currentProject);
			}
		}

	//@formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List all projects",
			"3) Select a project"
	);
	//@formatter:on	
	private Scanner scanner = new Scanner(System.in);	
			
			
  public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}
 
}

