package com.cooksys.ftd.assessment.filesharing.model.api;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class UploadCommand extends AbstractCommand {

	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {
		JAXBContext jc = JAXBContext.newInstance(new Class[] { FileD.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		message = "{\"fileD\":" + message + "}";
		
		FileD newFileD = (FileD)unmarshaller.unmarshal(new StringReader(message));
		
		if (user.getUserId() == -1 || newFileD.getFile() == null) {
			fileD = null;
			return;
		}
		
		String serverPath = generateFilePath(newFileD);
		newFileD.setFilepath(serverPath);
		
		fileD = fileDDao.createFile(newFileD);
		if (fileD.getFileId() == -1) {
			fileD = new FileD(-1, "invalid", "invalid");
			return;
		}
		
		userFile = userFileDao.createUserFile(new UserFile(user.getUserId(), fileD.getFileId()));
	}
	
	private String generateFilePath(FileD newFileD) {
		String filepath = newFileD.getFilepath();
		
		int delim = getDelimiter(filepath);
		if (delim == -1)
			return "invalid";
		
		while (filepath.indexOf(':') < filepath.indexOf(delim) && filepath.indexOf(':') != -1) {
			filepath = filepath.substring(filepath.indexOf(delim));
		}
		
		return "C:/" + user.getUsername() + filepath;
	}
	
	private int getDelimiter(String filepath) {
		if (filepath == null)
			return -1;
		int delim = '/';
		if (filepath.indexOf('/') == -1)
			delim = '\\';
		return delim;
	}
}
