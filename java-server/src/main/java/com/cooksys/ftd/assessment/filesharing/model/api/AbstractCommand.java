package com.cooksys.ftd.assessment.filesharing.model.api;

import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;
import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public abstract class AbstractCommand {
	
	protected String filepath;
	protected String username;
	protected UserDao userDao;
	protected FileDDao fileDDao;
	protected UserFileDao userFileDao;
	
	// Variables affected by individual execute commands
	protected User user;
	protected FileD fileD;
	protected List<FileD> userFiles;
	
	public AbstractCommand() {
		super();
	}
	
	public void executeCommand(String message) throws JAXBException, SQLException {
		// implement in all commands
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public FileDDao getFileDDao() {
		return fileDDao;
	}

	public void setFileDDao(FileDDao fileDDao) {
		this.fileDDao = fileDDao;
	}

	public UserFileDao getUserFileDao() {
		return userFileDao;
	}

	public void setUserFileDao(UserFileDao userFileDao) {
		this.userFileDao = userFileDao;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public FileD getFileD() {
		return this.fileD;
	}
	
	public List<FileD> getUserFile() {
		return this.userFiles;
	}
	
}
