package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class UserFileDao extends AbstractDao {
	
	public UserFile createUserFile(UserFile userFile) throws SQLException {
		String findFileD = "SELECT * FROM user_file "
				 		 + "WHERE file_id = ? ";
		
		PreparedStatement stmt = conn.prepareStatement(findFileD, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, userFile.getFileId());
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			int userId = rs.getInt("user_id");
			int fileId = rs.getInt("file_id");
			return new UserFile(userId, fileId);
		}
		
		String createUserFile = "INSERT INTO user_file (user_id, file_id) "
							  + "VALUES ( ? , ? ) ";
		
		PreparedStatement createStmt = conn.prepareStatement(createUserFile, Statement.RETURN_GENERATED_KEYS );
		createStmt.setInt(1, userFile.getUserId());
		createStmt.setInt(2, userFile.getFileId());
		createStmt.executeUpdate();
		
		return userFile;
	}
	
	public List<UserFile> getUserFileList(User user) throws SQLException {
		List<UserFile> userFiles = new ArrayList<UserFile>();
		
		String findUserFiles = "SELECT * FROM user_file "
		 		 			 + "WHERE user_id = ? ";
		
		PreparedStatement stmt = conn.prepareStatement(findUserFiles, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, user.getUserId());
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			int userId = rs.getInt("user_id");
			int fileId = rs.getInt("file_id");
			userFiles.add(new UserFile(userId, fileId));
		}

		return userFiles;
	}
	
	public List<String> getUserFileIds(User user) {
		return null; //TODO
	}
}
