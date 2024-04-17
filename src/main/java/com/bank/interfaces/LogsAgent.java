package com.bank.interfaces;

import com.bank.exceptions.PersistenceException;
import com.bank.pojo.Event;

public interface LogsAgent {
	public void log(Event event) throws PersistenceException;
}
