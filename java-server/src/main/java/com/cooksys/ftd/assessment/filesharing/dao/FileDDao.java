package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.User;

public class FileDDao extends AbstractDao {
	
	public FileD createFile(FileD fileD) throws SQLException {
		String findFileD = "SELECT * FROM file "
						 + "WHERE filepath LIKE ? ";
		
		// Check if file exists first. If so update it.
		PreparedStatement stmt = conn.prepareStatement(findFileD, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, fileD.getFilepath());
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) { // File already exists... update it.
			int fileId = rs.getInt("file_id");
			fileD.setFileId(fileId);
			String updateFileD = "UPDATE file "
							   + "SET filepath = ?, data = ? "
							   + "WHERE file_id = ? ";
			PreparedStatement updateStmt = conn.prepareStatement(updateFileD);
			updateStmt.setString(1, fileD.getFilepath());
			updateStmt.setString(2, fileD.getFile());
			updateStmt.setInt(3, fileD.getFileId());
			updateStmt.executeUpdate();
		} else { // File does not exist... Create it.
			String createFileD = "INSERT INTO file (filepath, data) "
					   		   + "VALUES ( ?, ? ) ";
			PreparedStatement createStmt = conn.prepareStatement(createFileD, Statement.RETURN_GENERATED_KEYS );
			createStmt.setString(1, fileD.getFilepath());
			createStmt.setString(2, fileD.getFile());
			int result = createStmt.executeUpdate();
			ResultSet createRs = createStmt.getGeneratedKeys();
			if (result == 0 || !createRs.next())
				return null;
			fileD.setFileId(createRs.getInt(1));
		}
		
		return fileD;
	}
	
	public FileD getFileFromPath(String filepath) throws SQLException {
		String findFileD = "SELECT * FROM file "
				 	     + "WHERE filepath LIKE ? ";

		// Check if file exists first. If so update it.
		PreparedStatement stmt = conn.prepareStatement(findFileD, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, filepath);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) { // File exists in the database.
			int fileId = rs.getInt("file_id");
			String path = rs.getString("filepath");
			String file = rs.getString("data");
			return new FileD(fileId, path, file);
		}
		
		return new FileD(-1, "invalid", "invalid");
	}

	public FileD getFileById(int fileId) throws SQLException {
		if (fileId == -1) 
			return new FileD(-1, "invalid", "invalid");
		String findFileD = "SELECT * FROM file "
	 			 		 + "WHERE file_id = ? ";
		
		PreparedStatement stmt = conn.prepareStatement(findFileD, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, fileId);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			int id = rs.getInt("file_id");
			String filepath = rs.getString("filepath");
			String file = rs.getString("data");
			return new FileD(id, filepath, file);
		}
		
		return null;
	}
}
