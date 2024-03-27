package com.bank.db.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.custom.exceptions.PersistenceException;
import com.bank.db.queries.AccountsTableQuery;
import com.bank.enums.AccountType;
import com.bank.enums.Status;
import com.bank.interfaces.AccountsAgent;
import com.bank.pojo.Account;

public class AccountsDBAgent implements AccountsAgent {
	
	private AccountsDBAgent() {
	}
	
	private static class AccountsDBAgentHolder{
		private final static AccountsDBAgent accAgent = new AccountsDBAgent(); 
	}
	
	public static AccountsDBAgent getAccountsDBAgent() {
		return AccountsDBAgentHolder.accAgent;
	}

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(ConnectionHandler.getURL(), ConnectionHandler.getUser(),
				ConnectionHandler.getPassword());
	}

	@Override
	public long getBalance(long userId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getBalance)) {
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getLong(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch password", exception);
		}
	}

	@Override
	public long getPrimaryAcc(long userId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getPrimaryAccount)) {
			st.setLong(1, userId);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getLong("ACC_NUMBER");
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Error in fetching primary account", exception);
		}
	}

	@Override
	public boolean isAccountPresent(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.isAccountPresent)) {
			st.setLong(1, accNum);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getBoolean(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Error in checking account", exception);
		}
	}

	@Override
	public long getCustomerId(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getCustomerId)) {
			st.setLong(1, accNum);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getLong(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Error fetching customerId", exception);
		}
	}


	@Override
	public void switchPrimary(long userId, long currentAcc, long newAccNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.changePrimaryAccount)) {
			st.setLong(1, newAccNum);
			st.setLong(2, currentAcc);
			st.executeUpdate();
		} catch (SQLException exception) {
			throw new PersistenceException("Error in fetching Accounts", exception);
		}

	}

	@Override
	public Account getAccount(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getAccountDetails)) {
			st.setLong(1, accNum);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				Account acc = new Account();
				acc.setUId(set.getLong(1));
				acc.setAccNum(set.getLong(2));
				acc.setBalance(set.getLong(3));
				acc.setType(AccountType.getTypeByValue(set.getInt(4)));
				acc.setBranchId(set.getInt(5));
				acc.setOpenedOn(set.getLong(6));
				acc.setStatus(Status.getStatusByState(set.getInt(8)));
				return acc;
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Error in fetching Accounts", exception);
		}
	}

	@Override
	public Map<Long, Account> getAccounts(long customerId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getCustomerAccounts)) {
			st.setLong(1, customerId);
			Map<Long, Account> map = new HashMap<>();
			try (ResultSet set = st.executeQuery()) {
				while (set.next()) {
					long accNum = set.getLong("ACC_NUMBER");
					Account acc = getAccount(accNum);
					map.put(accNum, acc);
				}
				return map;
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Error in fetching Accounts", exception);
		}
	}

	@Override
	public int getAccStatus(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getAccountStatus)) {
			st.setLong(1, accNum);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getInt(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch status", exception);
		}
	}

	@Override
	public void setAccStatus(long accNum, Status status) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.setAccountStatus)) {
			st.setInt(1, status.getState());
			st.setLong(2, accNum);
			st.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't update status", exception);
		}
	}

	@Override
	public void closeAccount(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st1 = connection.prepareStatement(AccountsTableQuery.moveToClosedAccounts);
				PreparedStatement st2 = connection.prepareStatement(AccountsTableQuery.closeAccount)) {
			st1.setLong(1, accNum);
			st2.setLong(1, accNum);
			st1.execute();
			st2.execute();
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't delete account", exception);
		}
	}

	@Override
	public long getBranchId(long accNum) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getBranchId)) {
			st.setLong(1, accNum);
			try (ResultSet set = st.executeQuery()) {
				set.next();
				return set.getLong(1);
			}
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch branch Id", exception);
		}
	}

	@Override
	public List<Long> getAccountList(long customerId) throws PersistenceException {
		try (Connection connection = connect();
				PreparedStatement st = connection.prepareStatement(AccountsTableQuery.getAccounts)) {
			st.setLong(1, customerId);
			List<Long> list = new ArrayList<>();
			try (ResultSet set = st.executeQuery()) {
				while(set.next()) {
					list.add(set.getLong(1));
				}
			}
			return list;
		} catch (SQLException exception) {
			throw new PersistenceException("Couldn't fetch AccountList", exception);
		}
	}
}
