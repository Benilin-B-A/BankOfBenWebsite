package com.bank.interfaces;

import com.bank.enums.Status;
import com.bank.exceptions.PersistenceException;

public interface UserAgent {

	public String getPassword(long userId) throws PersistenceException;

	public boolean isUserPresent(long userId) throws PersistenceException;

	public int getAttempt(long userId) throws PersistenceException;

	public void setAttempt(long userId, int attempt) throws PersistenceException;

	public int getStatus(long userId) throws PersistenceException;

	public void setStatus(long userId, Status status)throws PersistenceException;

	public int getUserLevel(long userId) throws PersistenceException;

	public void changePassword(long userId, String hashPassword) throws PersistenceException;

	public long getLastPasswordChange(long userId)throws PersistenceException;

	public void updatePasswordChange(long userId, long time) throws PersistenceException;

	public boolean isAlreadyUser(long aadharNumber) throws PersistenceException;

}
