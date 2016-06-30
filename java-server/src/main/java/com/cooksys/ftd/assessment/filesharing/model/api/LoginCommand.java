package com.cooksys.ftd.assessment.filesharing.model.api;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import com.cooksys.ftd.assessment.filesharing.model.User;

public class LoginCommand extends AbstractCommand {
	
	@Override
	public void executeCommand(String message, Map<String, Object> properties) throws JAXBException, SQLException {
		JAXBContext jc = JAXBContext.newInstance(new Class[] { User.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		message = "{\"user\":" + message + "}";
		
		User newUser = (User)unmarshaller.unmarshal(new StringReader(message));
		
		this.user = userDao.getUserByUsername(newUser.getUsername());
	}
}
