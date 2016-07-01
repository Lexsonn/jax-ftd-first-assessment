package com.cooksys.ftd.assessment.filesharing.model.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class DownloadCommand extends AbstractCommand {
	
	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {
		int searchFileId = Integer.parseInt(message);
		boolean fileExists = false;
		
		fileD = null;
		
		List<UserFile> userFiles = userFileDao.getUserFileList(user);
		
		for (UserFile uf : userFiles) {
			if (uf.getFileId() == searchFileId) {
				fileExists = true;
			}
		}
		
		if (!fileExists) 
			return;
		
		fileD = fileDDao.getFileById(searchFileId);
	}
}
