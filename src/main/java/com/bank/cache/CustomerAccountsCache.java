package com.bank.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.custom.exceptions.BankingException;
import com.bank.custom.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.CacheAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.util.LogHandler;

public class CustomerAccountsCache extends LinkedHashMap<Long, List<Long>> implements CacheAgent{
	   
	private static final long serialVersionUID = -5217631433928774784L;
	private final int CAPACITY;
	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static Logger logger = LogHandler.getLogger(CustomerAccountsCache.class.getName(), "AccListCache.txt");

	 private CustomerAccountsCache(int capacity) {
	        super(capacity, 0.75f, true);
	        this.CAPACITY = capacity;
	    }
	    
	    static class AccsCacheHolder{
	    	private static final CustomerAccountsCache cusAccCache = new CustomerAccountsCache(50);
	    }
	    
	    public static CustomerAccountsCache getInstance() {
	        return AccsCacheHolder.cusAccCache;
	    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, List<Long>> eldest) {
        return size() > CAPACITY;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Long> get(long key) throws BankingException {
    	List<Long> accList = super.get(key);
    	if(accList == null) {
    		try {
				accList = new ArrayList<>(accAgent.getAccountList(key));
			} catch (PersistenceException exception) {
				logger.log(Level.INFO, "Couldn't fetch accounts", exception);
				throw new BankingException("Couldn't fetch Accounts for user", exception);
			}
			put(key, accList);
    	}
    	return accList;
    }
}
