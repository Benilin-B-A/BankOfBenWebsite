package test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.bank.exceptions.BankingException;
import com.bank.services.AdminServices;

public class AdminServiceTester {

	static AdminServices admin = new AdminServices();
	static {
		admin.setUserId(1);
	}

	@Test(dataProvider = "withdrawDataProvider")
	public void withdrawTest(long accountNumber, long amount) {
		try {
			admin.withdraw(accountNumber, amount);
		} catch (BankingException exception) {
			exception.printStackTrace();
		}
	}

	@DataProvider(name = "withdrawDataProvider")
	public Object[][] withdrawData() {
		return new Object[][] { { 1, 1000 }, { 2, 1000 }, { 3, -1000 }, { 3, 0 }, { 3, 10000 } };
	}
}
