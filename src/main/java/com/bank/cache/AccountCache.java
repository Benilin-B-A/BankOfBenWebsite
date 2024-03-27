package com.bank.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.custom.exceptions.BankingException;
import com.bank.custom.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.CacheAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Account;
import com.bank.util.LogHandler;

public class AccountCache extends LinkedHashMap<Long, Account> implements CacheAgent{
   
	private static final long serialVersionUID = -5217631433928774784L;
	private final int CAPACITY;
	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static Logger logger = LogHandler.getLogger(AccountCache.class.getName(), "AccountsCache.txt");

    private AccountCache(int capacity) {
        super(capacity, 0.75f, true);
        this.CAPACITY = capacity;
    }
    
    static class AccCacheHolder{
    	private static final AccountCache accCache = new AccountCache(50);
    }
    
    public static AccountCache getInstance() {
        return AccCacheHolder.accCache;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Account> eldest) {
        return size() > CAPACITY;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Account get(long key) throws BankingException {
    	Account acc = super.get(key);
    	if(acc == null) {
    		try {
				acc = accAgent.getAccount(key);
			} catch (PersistenceException exception) {
				logger.log(Level.INFO, "Couldn't fetch account", exception);
				throw new BankingException("Couldn't fetch Account for account number : " + key, exception);
			}
			put(key, acc);
    	}
    	return acc;
    }
    
    
}

