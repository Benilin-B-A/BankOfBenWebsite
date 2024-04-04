package com.bank.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.exceptions.BankingException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.CacheAgent;
import com.bank.interfaces.CustomerAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Customer;
import com.bank.util.LogHandler;

public class ProfileCache extends LinkedHashMap<Long, Customer> implements CacheAgent {

	private static final long serialVersionUID = -5217631433928774784L;
	private final int CAPACITY;
	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();
	private static Logger logger = LogHandler.getLogger(AccountCache.class.getName(), "AccountsCache.txt");

	private ProfileCache(int capacity) {
		super(capacity, 0.75f, true);
		this.CAPACITY = capacity;
	}

	static class ProCacheHolder {
		private static final ProfileCache accCache = new ProfileCache(50);
	}

	public static ProfileCache getInstance() {
		return ProCacheHolder.accCache;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<Long, Customer> eldest) {
		return size() > CAPACITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Customer get(long key) throws BankingException {
		Customer cus = super.get(key);
		if (cus == null) {
			try {
				cus = cusAgent.getCustomer(key);
			} catch (PersistenceException exception) {
				logger.log(Level.INFO, "Couldn't fetch account", exception);
				throw new BankingException("Couldn't fetch Account for account number : " + key, exception);
			}
			put(key, cus);
		}
		return cus;
	}

}
