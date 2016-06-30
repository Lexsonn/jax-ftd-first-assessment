package com.cooksys.ftd.assessment.filesharing.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.cooksys.ftd.assessment.filesharing.model.User;

public class UserDao extends AbstractDao {

	public User createUser(User user) throws SQLException {
		User invalidUser = new User(-1, "undefined", "undefined");
		String findUser = "SELECT username FROM user"
						+ "WHERE username like '" + user.getUsername() + "'";
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(findUser);
		
		if (rs.next())
			return invalidUser;
		
		String createUser = "INSERT INTO user (username, password)"
						  + "VALUES( '" + user.getUsername() + ", '" + user.getPassword() + "')";
		
		Statement createStmt = conn.createStatement();
		int result = stmt.executeUpdate(createUser, Statement.RETURN_GENERATED_KEYS);
		ResultSet createRs = createStmt.getGeneratedKeys();
		if (result == 0 || !createRs.next())
			return null;
		user.setUserId(createRs.getInt(1));
		
		return user;
	}
	
	public Optional<User> getUserByUsername(String username) {
		return null; // TODO
	}
}