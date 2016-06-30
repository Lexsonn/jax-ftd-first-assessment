package com.cooksys.ftd.assessment.filesharing;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.ftd.assessment.filesharing.comm.Server;
import com.cooksys.ftd.assessment.filesharing.dao.FileDDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserDao;
import com.cooksys.ftd.assessment.filesharing.dao.UserFileDao;

public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/user_file_database";
	private static String username = "root";
	private static String password = "bondstone";
	private static int port = 667;
	
	public static void main(String[] args) throws ClassNotFoundException {
		
		Class.forName(driver); 
		ExecutorService executor = Executors.newCachedThreadPool();
		
		try (Connection conn = DriverManager.getConnection(url, username, password)) {

			Server server = new Server(); // init server
			
			server.setServerSocket(new ServerSocket(port));
			
			server.setExecutor(executor);

			UserDao userDao = new UserDao();
			userDao.setConn(conn);
			FileDDao fileDDao = new FileDDao();
			fileDDao.setConn(conn);
			UserFileDao userFileDao = new UserFileDao();
			userFileDao.setConn(conn);
			
			server.setUserDao(userDao);
			server.setFileDDao(fileDDao);
			server.setUserFileDao(userFileDao);
			
			Future<?> serverFuture = executor.submit(server); // start server
															  // (asynchronously)

			serverFuture.get();

		} catch (SQLException | InterruptedException | ExecutionException e) {
			log.error("An error occurred during server startup. Shutting down after error log.", e);
		} catch (IOException e) {
			log.error("An error occurred when opening the server socket.", e);
		} finally {
			executor.shutdown(); // shutdown thread pool (see inside of try
								 // block for blocking call)
		}
	}
}
