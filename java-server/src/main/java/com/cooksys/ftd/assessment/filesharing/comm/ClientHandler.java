package com.cooksys.ftd.assessment.filesharing.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;
import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.api.AbstractCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.ClientMessage;
import com.cooksys.ftd.assessment.filesharing.model.api.RegisterCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.Response;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	
	private String sessionID;
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
		String input = "";
		while (true) {
			try {
				input = reader.readLine();
				JAXBContext jc = JAXBContext.newInstance(ClientMessage.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				unmarshaller.setProperty("eclipselink.media-type", "application/json");	
				
				ClientMessage clientMessage = (ClientMessage)unmarshaller.unmarshal(new StringReader(input));
				
				switch (clientMessage.getCommand()) {
				case "register": 
					registerUser(clientMessage);
					break;
				case "login": break;
				default:
				}
				
			} catch (IOException | JAXBException e) {
				log.error("Error processing user input " + input + ".", e);
			} catch (SQLException e) {
				log.error("Error retreiving information from SQL database.", e);
			}
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
	
	public void registerUser(ClientMessage clientMessage) throws JAXBException, SQLException {
		Response<User> response = new Response<>();
		response.setError(false);
		response.setMessage("User has been sucessfully registered!");
		AbstractCommand regCmd = new RegisterCommand();
		regCmd.setUserDao(userDao);
		regCmd.executeCommand(clientMessage.getMessage());
		User newUser = regCmd.getUser();
		response.setData(newUser);
		if (newUser == null) {
			response.setError(true);
			String message = "Unable to enter user into database.";
			response.setMessage(message);
			log.error(message);
		}
		else {
			if (newUser.getUserId() == -1) {
				response.setError(true);
				String message = "Username already exists.";
				response.setMessage(message);
				log.info(message);
			} else
				log.info("User {} has been sucessfully registered!", newUser.getUsername());
		}
		
		JAXBContext jc = JAXBContext.newInstance(Response.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty("eclipselink.media-type", "application/json");
		
		marshaller.marshal(response, writer);
	}
	
	public void generateSessionID() {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`-=~!@#$%^&*()_+[]\\{}|;':\",./<>?".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		sessionID = sb.toString();
		log.info("Generated session id: {}", sessionID);
	}
}
