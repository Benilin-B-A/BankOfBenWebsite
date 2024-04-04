package com.bank.db.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.db.queries.BranchTableQuery;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.BranchAgent;
import com.bank.pojo.Branch;

public class BranchDBAgent implements BranchAgent{
	
	private BranchDBAgent() {
	}
	
	private static class BranchDBAgentHolder{
		private final static BranchDBAgent accAgent = new BranchDBAgent(); 
	}
	
	public static BranchDBAgent getBranchDBAgent() {
		return BranchDBAgentHolder.accAgent;
	}
	
	private Connection connect() throws SQLException {
		return DriverManager.getConnection(ConnectionHandler.getURL(), ConnectionHandler.getUser(),
				ConnectionHandler.getPassword());
	}
	
	@Override
	public void addBranch(Branch branch) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(BranchTableQuery.addBranch)) {
			st.setString(1, branch.getBranchName());
			st.setString(2, branch.getIFSC());
			st.setString(3, branch.getAddress());
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't add Branch", exception);
		}

	}

	@Override
	public boolean isIFSCPresent(String iFSC) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(BranchTableQuery.isIFSCPresent)) {
			st.setString(1, iFSC);
			try(ResultSet set = st.executeQuery()){
				set.next();
				return set.getBoolean(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't add Branch", exception);
		}
	}

	@Override
	public boolean isBranchIdPresent(long branchID) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(BranchTableQuery.isBranchPresent)) {
			st.setLong(1, branchID);
			try(ResultSet set = st.executeQuery()){
				set.next();
				return set.getBoolean(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't add Branch", exception);
		}
	}

	@Override
	public List<Branch> getBranches() throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(BranchTableQuery.getAllBranches)) {
			List<Branch> branches = new ArrayList<>();
			try(ResultSet set = st.executeQuery()){
				while (set.next()) {
					Branch branch = new Branch();
					branch.setBranchName(set.getString(1));
					branch.setId(set.getLong(2));
					branches.add(branch);
				}
				return branches;
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't add Branch", exception);
		}
	}
}
