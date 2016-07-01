package com.cooksys.ftd.assessment.filesharing.model.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class DownloadCommand extends AbstractCommand {
	private Logger log = LoggerFactory.getLogger(DownloadCommand.class);
	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {

		message = message.replace("\"", "");
		
		log.info("message: {}", message);
		
		int searchFileId = Integer.parseInt(message);
		boolean fileExists = false;
		
		log.info("file id: {}", searchFileId);
		
		fileD = null;
		
		List<UserFile> userFiles = userFileDao.getUserFileList(user);
		
		for (UserFile uf : userFiles) {
			log.info("uf file: {}, user: {}", uf.getFileId(), uf.getUserId());
			if (uf.getFileId() == searchFileId) {
				fileExists = true;
			}
		}
		
		if (!fileExists) 
			return;
		
		fileD = fileDDao.getFileById(searchFileId);
	}
}
