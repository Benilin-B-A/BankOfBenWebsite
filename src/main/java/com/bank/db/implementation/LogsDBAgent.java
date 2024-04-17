package com.bank.db.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.bank.db.queries.LogsTableQuery;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.LogsAgent;
import com.bank.pojo.Event;

public class LogsDBAgent implements LogsAgent{
	
	private LogsDBAgent() {
	}
	
	private static class LogsDBAgentHolder{
		private final static LogsDBAgent logsAgent = new LogsDBAgent(); 
	}
	
	public static LogsDBAgent getLogsDBAgent() {
		return LogsDBAgentHolder.logsAgent;
	}
	
	private Connection connect() throws SQLException {
		return DriverManager.getConnection(ConnectionHandler.getURL(), ConnectionHandler.getUser(),
				ConnectionHandler.getPassword());
	}

	@Override
	public void log(Event event) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(LogsTableQuery.logEvent)){
			st.setString(1, event.getEvent());
			st.setLong(2, event.getUserId());
			st.setLong(3, event.getTargetUserId());
			st.setString(4, event.getDescription());
			st.setLong(5, event.getTime());
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't log event", exception);
		}
	}

}
