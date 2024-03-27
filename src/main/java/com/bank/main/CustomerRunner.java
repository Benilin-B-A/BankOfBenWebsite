package com.bank.main;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.custom.exceptions.BankingException;
import com.bank.custom.exceptions.InvalidInputException;
import com.bank.custom.exceptions.PinNotSetException;
import com.bank.pojo.Transaction;
import com.bank.services.CustomerServices;

public class CustomerRunner {

	public static void run(CustomerServices user) {

		boolean continueService = true;

		while (continueService) {

			int userChoice = MainUtil.getInt("1) View Balance\n" + "2) Withdraw Money\n" + "3) Deposit Money\n"
					+ "4) Transfer Money\n" + "5) Change Password\n" + "6) Change Pin\n" + "7) Get Statement\n"
					+ "8) Switch Account\n" + "9) View Account Details\n" + "10) Logout\n" + "Enter choice : ", 10);
			switch (userChoice) {

			case 1:
				try {
					long bal = user.getBalance();
					System.out.println(bal);
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				break;

			case 2:
				long amount = 0;
				try {
					amount = MainUtil.getLong("Enter amount to withdraw : ");
					String pin = MainUtil.getString("Enter PIN : ");
					user.withdraw(amount, pin);
					System.out.println("Wihtdrawl successful");
				} catch (PinNotSetException exc) {
					String pin = MainUtil.getString("Enter PIN to set : ");
					try {
						user.setPin(pin);
						user.withdraw(amount, pin);
					} catch (BankingException | InvalidInputException e) {
						System.out.println(e.getMessage());
					}
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
					exception.printStackTrace();
				} catch (InvalidInputException excep) {
					System.out.println(excep.getMessage());
				}
				break;

			case 3:
				try {
					long amt = MainUtil.getLong("Enter amount to deposit : ");
					user.deposit(amt);
					System.out.println("Deposit successful");
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				break;

			case 4:
				Transaction trans = null;
				boolean withinBank = false;
				try {
					String pin = MainUtil.getString("Enter pin : ");
					int transferType = MainUtil.getInt("Is the money transfer within Bank ?  (1-Yes) (2-No): ", 2);
					if (transferType == 1) {
						withinBank = true;
					}
					trans = MainUtil.getTransactionObj(withinBank);
					user.transfer(trans, pin, withinBank);
				} catch (PinNotSetException exc) {
					String pin = MainUtil.getString("Enter PIN to set : ");
					try {
						user.setPin(pin);
						user.transfer(trans, pin, withinBank);
					} catch (BankingException | InvalidInputException e) {
						System.out.println(e.getMessage());
					}
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				} catch (InvalidInputException excep) {
					System.out.println(excep.getMessage());
				}
				break;

			case 5:
				try {
					String oldPass = MainUtil.getString("Enter Current Password : ");
					String newPass = MainUtil.getString("Enter new Password : ");
					user.changePassword(oldPass, newPass);
					System.out.println("Password changed successfully");
					break;
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				break;

			case 6:
				try {
					String pin = MainUtil.getString("Enter existing Pin : ");
					String newPin = MainUtil.getString("Enter new Pin : ");
					user.changePin(pin, newPin);
				} catch (BankingException | InvalidInputException exception) {
					System.out.println(exception.getMessage());
				}

				break;

			case 7:
				try {
					boolean continueStatementView = true;
					JSONArray transactions = user.getAccountStatement();
					while (continueStatementView) {
						MainUtil.printTransactions(transactions);
//						MainUtil.filter(transactions);
						int pageChoice = MainUtil.getInt("Pages(5) : Enter page to view or enter 6 to exit : ", 6);
						if (pageChoice == 6) {
							continueStatementView = false;
						} else {
							transactions = user.getAccountStatement(pageChoice);
						}
					}
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}

				break;

			case 8:
				try {
					System.out.println("Select from below accounts");
					System.out.println(user.getAccounts());
					long accNum = MainUtil.getLong("Enter account number : ");
					user.switchAccount(accNum);
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				break;

			case 9:

				try {
					JSONObject acc = user.getAccount();
					System.out.println(acc);
				} catch (BankingException exception) {
					System.out.println(exception.getMessage());
				}
				break;

			case 10:

				continueService = false;
			}
		}
	}
}
