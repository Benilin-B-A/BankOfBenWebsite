package com.bank.interfaces;

import com.bank.enums.Status;
import com.bank.exceptions.PersistenceException;

public interface UserAgent {

	public String getPassword(long userId) throws PersistenceException;

	public boolean isUserPresent(long uId) throws PersistenceException;

	public int getAttempt(long uId) throws PersistenceException;

	public void setAttempt(long uId, int attempt) throws PersistenceException;

	public int getStatus(long uId) throws PersistenceException;

	public void setStatus(long uId, Status status)throws PersistenceException;

	public int getUserLevel(long userId) throws PersistenceException;

	public void changePassword(long userId, String hashPassword) throws PersistenceException;

}
