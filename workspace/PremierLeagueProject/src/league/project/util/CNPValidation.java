package league.project.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class CNPValidation {

	private static int[] cnpArray;

	private static int[] keyArray = { 2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9 };

	private static Date birthDay = null;

	public static boolean validate(String cnp) {
		cnpArray = new int[13];
		/* entered CNP is a number */
		try {
			Long.parseLong(cnp);
		} catch (NumberFormatException e) {
			return false;
		}

		if (cnp.length() != 13) {
			return false;
		}

		/* convert the String into an int array for easier manipulation */
		cnpArray = convertStringToIntArray(cnp);

		/* first digit tells us if it is Romanian citizen [1..6] or not[7..9] */
		int firstChar = cnpArray[0];
		if (firstChar == 0) {
			return false;
		}

		/* digits in positions [2-7] gives us a valid Birthdate */
		if (null == getDateFromCNP(cnp)) {
			return false;
		}

		/* last digit is the control digit that validate the CNP algorithm */
		if (getControlNumber(cnpArray) != cnpArray[12]) {
			return false;
		}

		return true;

	}

	private static int[] convertStringToIntArray(String cnp) {
		int[] output = new int[13];
		for (int i = 0; i < cnp.length(); i++) {
			output[i] = Integer.parseInt(cnp.substring(i, i + 1));
		}
		return output;
	}

	private static int getControlNumber(int[] cnpArray) {
		int sumTest = 0;

		for (int i = 0; i < keyArray.length; i++) {
			sumTest += cnpArray[i] * keyArray[i];
		}
		int testRest = sumTest % 11;
		if (testRest == 10) {
			testRest = 1;
		}

		return testRest;
	}

	public static Date getDateFromCNP(String cnp) {
		Calendar cal = Calendar.getInstance();
		switch (Integer.valueOf(cnp.substring(0, 1)).intValue()) {
			case 7:
				;
			case 8:
				;
			case 9:
				if (Integer.valueOf(cnp.substring(1, 3)).intValue() > cal.get(Calendar.YEAR) % 100) {
					cal.set(Calendar.YEAR, 1900 + Integer.valueOf(cnp.substring(1, 3)).intValue());
				} else {
					cal.set(Calendar.YEAR, 2000 + Integer.valueOf(cnp.substring(1, 3)).intValue());
				}
				break;
			case 1:
				;
			case 2:
				cal.set(Calendar.YEAR, 1900 + Integer.valueOf(cnp.substring(1, 3)).intValue());
				break;
			case 3:
				;
			case 4:
				cal.set(Calendar.YEAR, 1800 + Integer.valueOf(cnp.substring(1, 3)).intValue());
				break;
			case 5:
				;
			case 6:
				cal.set(Calendar.YEAR, 2000 + Integer.valueOf(cnp.substring(1, 3)).intValue());
				break;
		}
		int month = Integer.valueOf(cnp.substring(3, 5)).intValue() - 1;
		if (month < 0 || month > 11) {
			return null;
		}
		cal.set(Calendar.MONTH, month);
		int day = Integer.valueOf(cnp.substring(5, 7)).intValue();
		if (day < 1 || day > 31) {
			return null;
		}
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(cnp.substring(5, 7)).intValue());
		birthDay = cal.getTime();
		return birthDay;
	}

	public static boolean validateCNPwithBirthdate(String cnp, Date birthDate) {
		if (null == birthDate) {
			return false;
		}
		if (!validate(cnp)) {
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		Date cnpBirthDay = CNPValidation.getDateFromCNP(cnp);
		if (cnpBirthDay == null) {
			return false;
		}
		Calendar cnpCalendar = Calendar.getInstance();
		cnpCalendar.setTime(cnpBirthDay);
		if (cnpCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
			return false;
		}
		if (cnpCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
			return false;
		}
		if (cnpCalendar.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}
		return true;
	}

	public static boolean validateCNPwithBirthdate(String cnp, String birthDate) {
		Date birthDateD;
		String[] pattern = { "dd.MM.yyyy" };
		try {
			birthDateD = DateUtils.parseDate(birthDate, pattern);
		} catch (ParseException e) {
			return false;
		}
		return validateCNPwithBirthdate(cnp, birthDateD);
	}
}
