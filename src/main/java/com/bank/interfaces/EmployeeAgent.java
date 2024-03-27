package com.bank.interfaces;

import com.bank.custom.exceptions.PersistenceException;
import com.bank.pojo.Employee;

public interface EmployeeAgent {

	public long getBranchId(long userId) throws PersistenceException;

	public Employee getEmployee(long userId) throws PersistenceException;

	boolean isEmployeePresent(long userId) throws PersistenceException;

}
