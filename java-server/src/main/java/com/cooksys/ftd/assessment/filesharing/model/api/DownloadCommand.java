package com.cooksys.ftd.assessment.filesharing.model.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class DownloadCommand extends AbstractCommand {
	
	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {
		message = message.replace("\"", "");
		
		int searchFileId = Integer.parseInt(message);
		
		fileD = new FileD(-1, "invalid", "invalid");
		
		FileD existsFileD = fileDDao.getFileById(searchFileId);
		if (existsFileD.getFileId() == -1) {
			fileD = null;
			return;
		}
		
		List<UserFile> userFiles = userFileDao.getUserFileList(user);
		for (UserFile uf : userFiles) {
			if (uf.getFileId() == searchFileId) {
				fileD.setFileId(searchFileId);
			}
		}
		
		fileD = fileDDao.getFileById(fileD.getFileId());
	}
}
