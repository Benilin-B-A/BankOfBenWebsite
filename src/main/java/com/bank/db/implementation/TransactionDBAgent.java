package com.bank.db.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.db.queries.TransactionTableQuery;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.TransactionAgent;
import com.bank.pojo.Transaction;


public class TransactionDBAgent implements TransactionAgent {
	
	private TransactionDBAgent() {
	}
	
	private static class TransactionDBAgentHolder{
		private final static TransactionDBAgent accAgent = new TransactionDBAgent(); 
	}
	
	public static TransactionDBAgent getTransactionDBAgent() {
		return TransactionDBAgentHolder.accAgent;
	}

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(ConnectionHandler.getURL(), ConnectionHandler.getUser(),
				ConnectionHandler.getPassword());
	}
	
	@Override
	public void doTransaction(Transaction trans) throws PersistenceException {
		try (Connection connection = connect(); PreparedStatement st = connection.prepareStatement(TransactionTableQuery.transfer)) {
			st.setLong(1, trans.getCustomerId());
			st.setLong(2, trans.getTransactionId());
			st.setLong(3, trans.getAccNumber());
			st.setLong(4, trans.getAmount());
			st.setString(5, trans.getType());
			st.setLong(6, trans.getTransAccNum());
			st.setLong(7, System.currentTimeMillis());
			st.setLong(8, trans.getOpeningBal());
			st.setLong(9, trans.getClosingBal());
//			st.setString(10, trans.getDescription());
			st.executeUpdate();
		} catch (SQLException exception) {
			throw new PersistenceException("Error in transaction", exception);
		}
	}

	@Override
	public long getTransactionBranch(long transId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(TransactionTableQuery.getTransBranch)){
				st.setLong(1, transId);
				try (ResultSet set = st.executeQuery()){
					set.next();
					return set.getLong(1);
				}
		} catch(SQLException exception) {
			throw new PersistenceException("Couldn't get account statement", exception);
		}
	}
	
	private List<Transaction> getTransactionsList(PreparedStatement st) throws SQLException {
		try (Connection connection = connect();
				ResultSet set = st.executeQuery()) {
			List<Transaction> list = new ArrayList<>();
			while (set.next()) {
				Transaction trans = new Transaction();
				trans.setTransactionId(set.getLong(2));
				trans.setAmount(set.getLong(4));
				trans.setType(set.getString(5));
				trans.setTime(set.getLong(7));
				trans.setOpeningBal(set.getLong(8));
				trans.setClosingBal(set.getLong(9));
				trans.setDescription(set.getString(10));
				list.add(trans);
			}
			return list;
		}
	}

	@Override
	public List<Transaction> getAccountStatement(long accNum, int limit, int offset) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(TransactionTableQuery.getAccountStatement)){
				st.setLong(1, accNum);
				st.setInt(2, limit);
				st.setInt(3, offset);
				return getTransactionsList(st);
		} catch(SQLException exception) {
			throw new PersistenceException("Couldn't get account statement", exception);
		}
	}

	@Override
	public List<Transaction> getTransactionStatement(long transId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(TransactionTableQuery.getTransStatement)){
				st.setLong(1, transId);
				return getTransactionsList(st);
		} catch(SQLException exception) {
			throw new PersistenceException("Couldn't get account statement", exception);
		}
	}
}
