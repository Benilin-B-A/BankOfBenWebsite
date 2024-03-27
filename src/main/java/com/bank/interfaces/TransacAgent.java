package com.bank.interfaces;

import com.bank.custom.exceptions.PersistenceException;
import com.bank.pojo.Account;
import com.bank.pojo.Customer;
import com.bank.pojo.Employee;

public interface TransacAgent {
	
	public long addCustomer(Customer customer, Account account, String password) throws PersistenceException ;
		
	public long addEmployee(Employee employee, String password) throws PersistenceException ;

	public void addAccount(Account account) throws PersistenceException;
}
