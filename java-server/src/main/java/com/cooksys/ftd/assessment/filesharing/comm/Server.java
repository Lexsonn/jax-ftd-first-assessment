package com.cooksys.ftd.assessment.filesharing.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;

public class Server implements Runnable {
	private Logger log = LoggerFactory.getLogger(Server.class);

	private ExecutorService executor;
	private ServerSocket serverSocket;

	private FileDDao fileDDao;
	private UserDao userDao;
	private UserFileDao userFileDao;
	
	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = this.serverSocket.accept();
				ClientHandler handler = this.createClientHandler(socket);
				this.executor.execute(handler);
			}
		} catch (IOException e) {
			this.log.error("The server has failed to accept a client.", e);
		}
	}

	public ClientHandler createClientHandler(Socket socket) throws IOException {
		ClientHandler handler = new ClientHandler();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		handler.setReader(reader);
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		handler.setWriter(writer);
		
		handler.setUserDao(userDao);
		handler.setFileDDao(fileDDao);
		handler.setUserFileDao(userFileDao);
		
		return handler;
	}
	
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setFileDDao(FileDDao fileDDao) {
		this.fileDDao = fileDDao;
	}
	
	public void setUserFileDao(UserFileDao userFileDao) {
		this.userFileDao = userFileDao;
	}
}
