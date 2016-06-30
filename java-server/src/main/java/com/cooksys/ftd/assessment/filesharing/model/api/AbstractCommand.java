package com.cooksys.ftd.assessment.filesharing.model.api;

import java.util.List;

import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public abstract class AbstractCommand {
	
	private String filepath;
	private User user;
	private FileD fileD;
	private List<UserFile> userFileList;
	
	public void executeCommand() {
		// implement in all commands
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public FileD getFileD() {
		return fileD;
	}

	public void setFileD(FileD fileD) {
		this.fileD = fileD;
	}

	public List<UserFile> getUserFileList() {
		return userFileList;
	}

	public void setUserFileList(List<UserFile> userFileList) {
		this.userFileList = userFileList;
	}
	
}
