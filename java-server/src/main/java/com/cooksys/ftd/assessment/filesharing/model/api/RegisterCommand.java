package com.cooksys.ftd.assessment.filesharing.model.api;

import java.io.StringReader;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.ftd.assessment.filesharing.model.User;

public class RegisterCommand extends AbstractCommand {
	
	@Override
	public void executeCommand(String message) throws JAXBException, SQLException {
		// TODO register user
		JAXBContext jc = JAXBContext.newInstance(User.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		User newUser = (User)unmarshaller.unmarshal(new StringReader(message));
		this.user = userDao.createUser(newUser);
	}
}
