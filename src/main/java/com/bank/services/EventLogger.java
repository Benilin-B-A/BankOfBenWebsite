package com.bank.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.LogsAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Event;
import com.bank.util.LogHandler;

public class EventLogger {
	
	private static ExecutorService exService = Executors.newFixedThreadPool(10);
	
	private static LogsAgent logsAgent = PersistenceObj.getLogsAgent();
	
	private static Logger logger = LogHandler.getLogger(EventLogger.class.getName(), "EventLogger.txt");
	
	protected static void log(Event event) {
		Runnable runnableTask = () -> {
			try {
				logsAgent.log(event);
			} catch (PersistenceException exception) {
				logger.log(Level.SEVERE, "Couldn't log event",exception);
				exception.printStackTrace();
			}
		};
		exService.execute(runnableTask);
	}
}
