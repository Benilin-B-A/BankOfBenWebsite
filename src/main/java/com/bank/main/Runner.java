package com.bank.main;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.custom.exceptions.BankingException;
import com.bank.services.AdminServices;
import com.bank.services.AuthServices;
import com.bank.services.CustomerServices;
import com.bank.services.EmployeeServices;
import com.bank.util.LogHandler;

public class Runner {
	
	private static Scanner scan = new Scanner(System.in);
	private static Logger logger = LogHandler.getLogger(Runner.class.getName(), "MainLogs.txt");
	private static AuthServices auth = new AuthServices();

	public static void main(String... args) {
		System.out.println("Welcome to Bank of Ben");
		Runner main = new Runner();
		main.startApp();
		scan.close();
	}
	
	private void startApp() {
		
		boolean continueService = true;
		
		while(continueService) {
			
			int userChoice = MainUtil.getInt("1) Login\n2) Exit\nEnter Choice : ", 2);
			
			switch(userChoice) {
			
			case 1:
				long userId = login();
				System.out.println("Login Successful : Welcome");
				Object user = null;
				try {
					user = AuthServices.getServiceObj(userId);
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				if (user instanceof AdminServices) {
					AdminRunner.run((AdminServices) user);
				}

				else if (user instanceof EmployeeServices) {
					EmployeeRunner.run((EmployeeServices) user);
				}

				else if (user instanceof CustomerServices) {
					CustomerRunner.run((CustomerServices) user);
				}
				break;
				
			case 2:
				
				continueService = false;
			}
		}
	}

	static long login() {
		long userId = MainUtil.getLong("Enter UserId : ");
		String password = MainUtil.getString("Enter Password : ");
		try {
			auth.login(userId, password);
		} catch (BankingException exception) {
			System.out.println(exception.getMessage());
			logger.log(Level.SEVERE, "Login Error", exception);
			login();
		}
		return userId;
	}
}