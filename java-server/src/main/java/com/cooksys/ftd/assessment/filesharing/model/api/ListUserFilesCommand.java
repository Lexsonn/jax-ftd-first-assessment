package com.cooksys.ftd.assessment.filesharing.model.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cooksys.ftd.assessment.filesharing.model.FileD;
import com.cooksys.ftd.assessment.filesharing.model.UserFile;

public class ListUserFilesCommand extends AbstractCommand {
	
	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {
		this.fileList = new ArrayList<>();
		List<UserFile> userFiles = userFileDao.getUserFileList(user);
		
		for (UserFile uf : userFiles) {
			FileD tempFileD = fileDDao.getFileById(uf.getFileId());
			if (tempFileD.getFileId() != -1)
				fileList.add("ID: " + uf.getFileId() + ", Server filepath: " + tempFileD.getFilepath());
		}
	}
}
