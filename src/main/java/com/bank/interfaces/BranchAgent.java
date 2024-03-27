package com.bank.interfaces;

import com.bank.custom.exceptions.PersistenceException;
import com.bank.pojo.Branch;

public interface BranchAgent {

	void addBranch(Branch branch) throws PersistenceException;

	boolean isIFSCPresent(String iFSC) throws PersistenceException;

	boolean isBranchIdPresent(long branchID) throws PersistenceException;

}
