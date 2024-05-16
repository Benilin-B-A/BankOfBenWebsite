package test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.bank.exceptions.BankingException;
import com.bank.services.AuthServices;

public class AuthenticationTester {

	static AuthServices service = new AuthServices();

	@Test(dataProvider = "loginDataProvider", expectedExceptions= {BankingException.class})
	public void loginTest(long userId, String password) throws BankingException {
		service.login(userId, password);
	}

	@DataProvider(name = "loginDataProvider")
	public Object[][] loginData() {
		return new Object[][] { { 1, "Ben@0000" }, { 1, "ehyvgfhef" }, { 1, null } };
	}
}
