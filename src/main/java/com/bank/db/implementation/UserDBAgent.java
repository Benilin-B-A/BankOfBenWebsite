package com.bank.db.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bank.db.queries.UserTableQuery;
import com.bank.enums.Status;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.UserAgent;

public class UserDBAgent implements UserAgent{
	
	private UserDBAgent() {
	}
	
	private static class UserDBAgentHolder{
		private final static UserDBAgent accAgent = new UserDBAgent(); 
	}
	
	public static UserDBAgent getUserDBAgent() {
		return UserDBAgentHolder.accAgent;
	}

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(ConnectionHandler.getURL(), ConnectionHandler.getUser(),
				ConnectionHandler.getPassword());
	}

	@Override
	public  String getPassword(long userId) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.getPassword)){
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getString(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch password",exception);
		}
	}

	@Override
	public boolean isUserPresent(long userId) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.isUserPresent)){
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getBoolean(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't validate user",exception);
		}
	}

	@Override
	public int getAttempt(long userId) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.getAttempts)){
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getInt(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch attempts",exception);
		}
	}

	@Override
	public void setAttempt(long userId, int attempt) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.setAttempts)){
			st.setInt(1, attempt);
			st.setLong(2, userId);
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't update attempts",exception);
		}
	}

	@Override
	public int getStatus(long userId) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.getStatus)){
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getInt(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch status",exception);
		}
	}

	@Override
	public void setStatus(long userId, Status status) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.setStatus)){
			st.setInt(1, status.getState());
			st.setLong(2, userId);
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't update status",exception);
		}
	}
	
	@Override
	public int getUserLevel(long userId) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.getUserLevel)){
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getInt(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch user type",exception);
		}
	}

	@Override
	public void changePassword(long userId, String password) throws PersistenceException {
		try (Connection connection = connect(); 
				PreparedStatement st = connection.prepareStatement(UserTableQuery.changePassword)){
			st.setString(1, password);
			st.setLong(2, userId);
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't update status",exception);
		}
	}
	
}
