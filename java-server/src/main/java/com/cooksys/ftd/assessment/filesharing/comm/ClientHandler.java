package com.cooksys.ftd.assessment.filesharing.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;
import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.api.AbstractCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.ClientMessage;
import com.cooksys.ftd.assessment.filesharing.model.api.LoginCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.RegisterCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.Response;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	
	Map<String, Object> properties = new HashMap<String, Object>();
	
	private String sessionID;
	private BufferedReader reader;
	private PrintWriter writer;

	private UserDao userDao;
	private FileDDao fileDDao;
	private UserFileDao userFileDao;
	
	private boolean closed;
	
	public UserDao getUserDao() {
		return userDao;
	}

	@Override
	public void run() {
		properties.put("eclipselink.media-type", "application/json");
		generateSessionID();
		
		log.info("Session ID={}", sessionID);
		closed = false;
		// read commands and data from the client then send back a Response<T> object
		// rinse and repeat
		String input = "";
		try {
			while (!closed) {
				log.info("Waiting for client input...");
				
				ClientMessage clientMessage = getClientMessage();
				
				log.info("clientMessage: data={}, message={}", clientMessage.getData(), clientMessage.getMessage());
				
				switch (clientMessage.getMessage()) {
				case "register": registerUser(clientMessage); break;
				case "login": loginUser(clientMessage); break;
				default: 
				}
			}
		} catch (IOException | JAXBException e) {
			log.error("Error processing user input " + input + ".", e);
			writer.write("{\"response\":{\"message\":\"*error*error\"}}");
			closed = true;
		} catch (SQLException e) {
			log.error("Error retreiving information from SQL database.", e);
			writer.write("{\"response\":{\"message\":\"*error*error\"}}");
			closed = true;
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
	
	public ClientMessage getClientMessage() throws IOException, JAXBException {
		String input = reader.readLine();
		log.info("Input: {}", input);
		JAXBContext jc = JAXBContext.newInstance(new Class[] { ClientMessage.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		
		return (ClientMessage)unmarshaller.unmarshal(new StringReader(input));
	}
	
	public void registerUser(ClientMessage clientMessage) throws JAXBException, SQLException {
		Response<User> response = new Response<>();
		response.setMessage("*user*User has been sucessfully registered!");
		
		AbstractCommand regCmd = new RegisterCommand();
		regCmd.setUserDao(userDao);
		regCmd.executeCommand(clientMessage.getData(), properties);
		User newUser = regCmd.getUser();
		response.setData(newUser);
		
		if (newUser == null) {
			String message = "*error*Unable to enter user into database.";
			response.setMessage(message);
			log.error(message);
		}
		else {
			if (newUser.getUserId() == -1) {
				String message = "*error*Username already exists.";
				response.setMessage(message);
				log.info(message);
			} else
				log.info("User {} has been sucessfully registered!", newUser.getUsername());
		}
		
		sendResponse(response);
	}
	
	public void loginUser(ClientMessage clientMessage) throws JAXBException, SQLException, IOException {
		generateSessionID();
		
		Response<String> response = new Response<>();
		String message = "*error*Login credentials are incorrect.";
		response.setMessage(message);
		response.setData("invalid");
		
		AbstractCommand logCmd = new LoginCommand();
		logCmd.setUserDao(userDao);
		logCmd.executeCommand(clientMessage.getData(), properties);
		User newUser = logCmd.getUser();
		
		if (newUser.getUserId() == -1) {
			log.info(message);
			sendResponse(response);
			return;
		} 
		
		Response<User> checkPwd = new Response<>();
		checkPwd.setMessage("*checkPass*checkPass");
		checkPwd.setData(newUser);
		sendResponse(checkPwd);	// Send user/hashed pass to check with client side pass
		
		ClientMessage passwordCheckMessage = getClientMessage();
		
		if (passwordCheckMessage.getMessage().equals("success")) {
			message = "*login*Login successful!";
			response.setMessage(message);
			response.setData(sessionID);
			log.info(message);
		}
		
		sendResponse(response);
	}
	
	public void sendResponse(Response<?> response) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Response.class }, properties);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		
		marshaller.marshal(response, writer);
		writer.flush();
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
