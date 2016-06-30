package com.cooksys.ftd.assessment.filesharing.comm;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;

public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;

	private UserDao userDao;
	private FileDDao fileDDao;
	private UserFileDao userFileDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	@Override
	public void run() {
		// read commands and data from the client then send back a Response<T> object
		// rinse and repeat
		while (true) {
			
		}
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
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
	
}
