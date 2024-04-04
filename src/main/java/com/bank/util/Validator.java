package com.bank.util;

import java.util.regex.Pattern;

import com.bank.exceptions.InvalidInputException;

public class Validator {

	private static Pattern mobilePattern = Pattern.compile("^[7-9]{1}[0-9]{9}$");
	private static Pattern alphaNumeric = Pattern.compile("\\A[0-9A-Z]+\\Z");
	private static Pattern mailPattern = Pattern.compile("\\A[a-zA-Z0-9+-_~]+@[a-zA-Z]+.[a-zA-Z]{2,}");
	private static Pattern passwordPattern = Pattern.compile(
			"^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+]).{8,}$");
	private static Pattern pinPattern = Pattern.compile("[0-9]{4}");
	private static Pattern dOBPattern = Pattern.compile(
			"^(?:\\d{4})-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1][0-9]|[2][0-9]|3[01])$");
	private static Pattern genderPattern = Pattern.compile("^(Male|Female|Other)$");

	public static boolean validateMobile(String num) throws InvalidInputException {
		boolean isValid = validate(mobilePattern, num);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid mobile number");
	}
	
	public static boolean validateGender(String gender) throws InvalidInputException {
		boolean isValid = validate(genderPattern, gender);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid mobile number");
	}
	
	public static boolean validateDOB(String dOB) throws InvalidInputException {
		boolean isValid = validate(dOBPattern, dOB);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid DOB");
	}

	public static boolean validateAlphaNum(String alphaNum) throws InvalidInputException {
		boolean isValid = validate(alphaNumeric, alphaNum);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid IFSC");
	}

	public static boolean validateMail(String mail) throws InvalidInputException {
		boolean isValid = validate(mailPattern, mail);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid E-Mail");
	}

	public static boolean validatePassword(String password) throws InvalidInputException {
		boolean isValid = validate(passwordPattern, password);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Invalid Password");
	}

	private static boolean validate(Pattern pattern, String str) throws InvalidInputException {
		return pattern.matcher(str).matches();

	}
	
	public static void checkNull(Object obj)throws InvalidInputException{
		checkNull(obj,"Invalid Input : The Object is null");
	}
	
	public static void checkNull(Object obj,String message)throws InvalidInputException{
		if (obj==null){
			throw new InvalidInputException(message);
		}
	}

	public static boolean validatePin(String newPin) throws InvalidInputException {
		boolean isValid = validate(pinPattern, newPin);
		if (isValid) {
			return true;
		}
		throw new InvalidInputException("Pin must be a four digit number");
	}
}
