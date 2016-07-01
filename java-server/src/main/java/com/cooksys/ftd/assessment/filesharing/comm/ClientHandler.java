package com.cooksys.ftd.assessment.filesharing.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.User;
import com.cooksys.ftd.assessment.filesharing.model.api.AbstractCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.ClientMessage;
import com.cooksys.ftd.assessment.filesharing.model.api.DownloadCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.ListUserFilesCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.LoginCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.RegisterCommand;
import com.cooksys.ftd.assessment.filesharing.model.api.Response;
import com.cooksys.ftd.assessment.filesharing.model.api.UploadCommand;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	
	Map<String, Object> properties = new HashMap<String, Object>();
	
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
		properties.put("eclipselink.media-type", "application/json");

		String input = "";
		try {
			log.info("Waiting for client input...");
			
			ClientMessage clientMessage = getClientMessage();
			
			switch (clientMessage.getMessage()) {
			case "register": registerUser(clientMessage); break;
			case "login": loginUser(clientMessage); break;
			default: // Functions allowed only while logged in
				int delimIndex = clientMessage.getMessage().indexOf('*');
				if (delimIndex == -1) {
					log.info("Session IDs do not match.");
					writer.write("{\"response\":{\"message\":\"*error*Login credentials are incorrect\"}}");
					writer.flush();
				} else {
					String connSessionID = clientMessage.getMessage().substring(delimIndex + 1);
					if (connSessionID.equals(sessionID))
						log.info("Session IDs match!");
					String newMessage = clientMessage.getMessage().substring(0, delimIndex);
					switch (newMessage) {
					case "upload": uploadFileD(connSessionID, clientMessage.getData()); break;
					case "download": downloadFileD(connSessionID, clientMessage.getData()); break;
					case "files": listUserFiles(connSessionID, clientMessage.getData()); break;
					default:
						log.warn("Invalid message type: {}", newMessage);
						writer.write("{\"response\":{\"message\":\"*error*Error in handling command.\"}}");
						writer.flush();
					}
					
				}
			}
		} catch (IOException | JAXBException e) {
			log.error("Error processing user input " + input, e);
			writer.write("{\"response\":{\"message\":\"*error*Server error in processing input\"}}");
		} catch (SQLException e) {
			log.error("Error retreiving information from SQL database.", e);
			writer.write("{\"response\":{\"message\":\"*error*Server error when accessing database\"}}");
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
		log.info("Registering user...");
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
		}
		else {
			if (newUser.getUserId() == -1) {
				String message = "*error*Username already exists.";
				response.setMessage(message);
			}
		}
		
		sendResponse(response);
	}
	
	public void loginUser(ClientMessage clientMessage) throws JAXBException, SQLException, IOException {
		log.info("Loging in user...");
		
		Response<String> response = new Response<>();
		response.setMessage("*loginError*Login credentials are incorrect.");
		response.setData("invalid");
		
		AbstractCommand logCmd = new LoginCommand();
		logCmd.setUserDao(userDao);
		logCmd.executeCommand(clientMessage.getData(), properties);
		User newUser = logCmd.getUser();
		
		if (newUser.getUserId() == -1) {
			sendResponse(response);
			return;
		} 
		
		Response<User> checkPwd = new Response<>();
		checkPwd.setMessage("*checkPass*checkPass");
		checkPwd.setData(newUser);
		sendResponse(checkPwd);	// Send user/hashed pass to check with client side pass
		
		ClientMessage passwordCheckMessage = getClientMessage();
		
		if (passwordCheckMessage.getMessage().equals("success")) {
			generateSessionID(newUser.getUsername());
			response.setMessage("*login*Login successful!");
			response.setData(sessionID);
		}
		
		sendResponse(response);
	}
	
	public void uploadFileD(String connID, String data) throws JAXBException, SQLException {
		Response<FileD> response = new Response<>();
		response.setData(new FileD(-1, "invalid", "invalid"));
		
		int delim = connID.indexOf('*');
		if (delim == -1) {
			response.setMessage("*error*Invalid session ID detected");
			sendResponse(response);
			return;
		}
		
		String username = connID.substring(0, delim);
		User tempUser = userDao.getUserByUsername(username);
		
		AbstractCommand upCmd = new UploadCommand();
		upCmd.setUser(tempUser);
		upCmd.setUserFileDao(userFileDao);
		upCmd.setFileDDao(fileDDao);
		upCmd.executeCommand(data, properties);
		
		if (upCmd.getFileD() == null)
			response.setMessage("*error*Error when reading file.");
		else if (upCmd.getFileD().getFileId() == -1)
			response.setMessage("*error*Error when storing file to database.");
		else if (upCmd.getUserFile() == null)
			response.setMessage("*error*Error when setting up user file relashionship.");
		else if (upCmd.getUserFile().getUserId() == -1 || upCmd.getUserFile().getFileId() == -1)
			response.setMessage("*error*Error when writing user file relashionship.");
		else {
			response.setData(upCmd.getFileD());
			response.setMessage("*uploadSuccess*File has been sucessfully written.");
		}
		sendResponse(response);
	}
	
	public void downloadFileD(String connID, String data) throws JAXBException, SQLException {
		Response<FileD> response = new Response<>();
		response.setData(new FileD(-1, "invalid", "invalid"));
		
		int delim = connID.indexOf('*');
		if (delim == -1) {
			response.setMessage("*error*Invalid session ID detected");
			sendResponse(response);
			return;
		}
		
		String username = connID.substring(0, delim);
		User tempUser = userDao.getUserByUsername(username);
		
		AbstractCommand downCmd = new DownloadCommand();
		downCmd.setUser(tempUser);
		downCmd.setUserFileDao(userFileDao);
		downCmd.setFileDDao(fileDDao);
		downCmd.executeCommand(data, properties);
		
		FileD resultFileD = downCmd.getFileD();
		
		if (resultFileD == null) {
			response.setMessage("*error*File does not exist");
			response.setData(new FileD(-1, "invalid", "invalid"));
		} else {
			response.setData(resultFileD);
			if (resultFileD.getFileId() == -1) {
				response.setMessage("*error*You do not have access to this file.");
			} else {
				response.setMessage("*downloadSuccess*File found! Donwloading...");
			}
		}
		
		sendResponse(response);
	}
	
	public void listUserFiles(String connID, String data) throws JAXBException, SQLException {
		Response<String> response = new Response<>();
		response.setData("None found.");
		
		int delim = connID.indexOf('*');
		if (delim == -1) {
			response.setMessage("*error*Invalid session ID detected");
			sendResponse(response);
			return;
		}
		
		String username = connID.substring(0, delim);
		User tempUser = userDao.getUserByUsername(username);
		
		AbstractCommand listCmd = new ListUserFilesCommand();
		listCmd.setUser(tempUser);
		listCmd.setFileDDao(fileDDao);
		listCmd.setUserFileDao(userFileDao);
		listCmd.executeCommand(data, properties);
		
		List<String> fileList = listCmd.getFileList();
		
		if (fileList.isEmpty()) {
			response.setMessage("*error*No files found for current user.");
			sendResponse(response);
			return;
		}
		
		String responseData = "";
		for (String filename : fileList) {
			responseData += filename + '\n';
		}
		response.setMessage("*filelistSuccess*FILES STORED ON DATABASE:");
		response.setData(responseData);
		
		sendResponse(response);
	}
	
	public void sendResponse(Response<?> response) throws JAXBException {
		log.info("Response: {}", response.getMessage());
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Response.class }, properties);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		
		marshaller.marshal(response, writer);
		writer.flush();
	}
	
	public void generateSessionID(String username) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`-=~!@#$%^&_+|;':,.<>?".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		sessionID = username + '*' + sb.toString();
		log.info("Generated session id: {}", sessionID);
	}
}
