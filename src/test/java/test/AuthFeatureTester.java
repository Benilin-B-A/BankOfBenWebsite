package test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.bank.exceptions.BankingException;
import com.bank.services.AuthServices;

public class AuthFeatureTester {
	
	static AuthServices service = new AuthServices();
	
	@Test
	public void loginTester(String userId, String password) {
		try {
			service.login(Long.parseLong(userId), password);
		} catch (NumberFormatException | BankingException e) {
			e.getMessage();
		}
	}
	
	@DataProvider(name = "loginDataProvider")
	public Object[][] loginData() {
		return new Object[][] { { "1", "Ben@0000" }, { "", "" }, { null, null }, { "1", "ehyvgfhef" },
				{ "Ben", "Ben@0000" } };
	}
}
