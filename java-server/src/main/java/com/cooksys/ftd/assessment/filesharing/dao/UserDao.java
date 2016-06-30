package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.model.User;

public class UserDao extends AbstractDao {
	private Logger log = LoggerFactory.getLogger(UserDao.class);
	
	public User createUser(User user) throws SQLException {
		User invalidUser = new User(-1, "undefined", "undefined");
		String findUser = "SELECT * FROM user "
						+ "WHERE username LIKE ? ";
		
		PreparedStatement stmt = conn.prepareStatement(findUser);
		stmt.setString(1, user.getUsername());
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next())
			return invalidUser;
		
		String createUser = "INSERT INTO user (username, password) "
						  + "VALUES( ? , ? ) ";
		
		PreparedStatement createStmt = conn.prepareStatement(createUser, Statement.RETURN_GENERATED_KEYS);
		createStmt.setString(1, user.getUsername());
		createStmt.setString(2, user.getPassword());
		int result = createStmt.executeUpdate();
		ResultSet createRs = createStmt.getGeneratedKeys();
		if (result == 0 || !createRs.next())
			return null;
		
		user.setUserId(createRs.getInt(1));
		
		return user;
	}
	
	public User getUserByUsername(String username) throws SQLException {
		User user = new User(-1, "undefined", "undefined");
		String findUser = "SELECT * FROM user "
						+ "WHERE username LIKE ? ";
		PreparedStatement stmt = conn.prepareStatement(findUser);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			String name = rs.getString("username");
			String pass = rs.getString("password");
			int id = rs.getInt("user_id");
			
			user = new User(id, name, pass);
			log.info("User=id: {}, username: {}, password: {}", id, name, pass);
		}
		
		return user;
	}
}