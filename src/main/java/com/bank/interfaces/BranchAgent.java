package com.bank.interfaces;

import java.util.List;

import com.bank.exceptions.PersistenceException;
import com.bank.pojo.Branch;

public interface BranchAgent {

	void addBranch(Branch branch) throws PersistenceException;

	boolean isIFSCPresent(String iFSC) throws PersistenceException;

	boolean isBranchIdPresent(long branchID) throws PersistenceException;

	List<Branch> getBranches() throws PersistenceException;

}
