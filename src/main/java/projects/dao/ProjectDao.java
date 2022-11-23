package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase{
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
			+"Insert into " + PROJECT_TABLE
			+ " (project_name, estimated_hours, actual_hours, difficulty, notes)"
			+ " values"
			+ " (?, ?, ?, ?, ?)";
		// @formatter:on
		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				System.out.println(stmt.toString());
				stmt.executeUpdate();
				Integer projectid = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				project.setProjectId(projectid);
				return project;
			}catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		
	}

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM "+ PROJECT_TABLE+" ORDER BY project_name";
		try(Connection conn = DbConnection.getConnection()){
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet SQLresult = stmt.executeQuery()){
					List<Project> result = new LinkedList<>();
					while(SQLresult.next()) {
						result.add(extract(SQLresult, Project.class));
					}
					return result;
				}catch(Exception e) {
					throw new DbException(e);
				}
			}catch(Exception e) {
				//rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectID) {
		String sql = "Select * from "+PROJECT_TABLE +" where project_id = ?";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try{
				Project project = null;
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectID, Integer.class);
					try(ResultSet rs = stmt.executeQuery()){
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if(Objects.nonNull(project)) {
				project.getMaterials().addAll(fetchMaterialsForProject(conn, projectID));
				project.getSteps().addAll(fetchStepsForProject(conn, projectID));
				project.getCategories().addAll(fetchCategoriesForProject(conn, projectID));
				}
				commitTransaction(conn);
				return Optional.ofNullable(project);
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, int projectID) throws SQLException{
		String sql = "Select * from " + MATERIAL_TABLE + " where project_id = ?";
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectID, Integer.class);
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<>();
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
					
				}
				return materials;
			}
		}
	}
	private List<Step> fetchStepsForProject(Connection conn, int projectID) throws SQLException{
		String sql = "Select * from " + STEP_TABLE + " where project_id = ?";
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectID, Integer.class);
			try(ResultSet rs = stmt.executeQuery()){
				List<Step> steps = new LinkedList<>();
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
					
				}
				return steps;
			}
		}
	}
	private List<Category> fetchCategoriesForProject(Connection conn, int projectID) throws SQLException{
		String sql = "Select c.* from " + CATEGORY_TABLE + " c Inner join " + PROJECT_CATEGORY_TABLE +" pc on c.category_id = pc.category_id where pc.project_id = ?";
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectID, Integer.class);
			try(ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<>();
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
					
				}
				return categories;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		String sql = "Update project set project_name = ?, estimated_hours = ?, actual_hours = ?, difficulty = ?, notes = ? where project_id = ?";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				boolean result = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return result;
				
			}catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException(e);
		}
		
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "Delete from project where project_id = ?";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				boolean result = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return result;
				
			}catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
