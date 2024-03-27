package com.bank.persistence.util;

import com.bank.db.implementation.AccountsDBAgent;
import com.bank.db.implementation.BranchDBAgent;
import com.bank.db.implementation.CustomerDBAgent;
import com.bank.db.implementation.EmployeeDBAgent;
import com.bank.db.implementation.TransacOperations;
import com.bank.db.implementation.TransactionDBAgent;
import com.bank.db.implementation.UserDBAgent;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.BranchAgent;
import com.bank.interfaces.CustomerAgent;
import com.bank.interfaces.EmployeeAgent;
import com.bank.interfaces.TransacAgent;
import com.bank.interfaces.TransactionAgent;
import com.bank.interfaces.UserAgent;

public class PersistenceObj {

	private PersistenceObj() {
	}

	private final static UserAgent userAgent = UserDBAgent.getUserDBAgent();
	private final static AccountsAgent accAgent = AccountsDBAgent.getAccountsDBAgent();
	private final static EmployeeAgent empAgent = EmployeeDBAgent.getEmployeeDBAgent();
	private final static CustomerAgent cusAgent = CustomerDBAgent.getCustomerDBAgent();
	private final static TransactionAgent tranAgent = TransactionDBAgent.getTransactionDBAgent();
	private final static BranchAgent branchAgent = BranchDBAgent.getBranchDBAgent();
	private final static TransacAgent trAgent = TransacOperations.getTransacDBAgent();
	
	
	public static TransacAgent getTransacAgent() {
		return trAgent;
	}

	public static UserAgent getUserAgent() {
		return userAgent;
	}

	public static AccountsAgent getAccountsAgent() {
		return accAgent;
	}

	public static EmployeeAgent getEmployeeAgent() {
		return empAgent;
	}

	public static CustomerAgent getCustmomerAgent() {
		return cusAgent;
	}

	public static TransactionAgent getTransactionAgent() {
		return tranAgent;
	}

	public static BranchAgent getBranchAgent() {
		return branchAgent;
	}
}
