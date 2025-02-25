/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.Common;

import com.arjuna.ats.arjuna.utils.Utility;
import org.jboss.jbossts.qa.ArjunaCore.Utils.qautil;

/**
 * Simple test to see if Properties are being loaded correctly
 */
public class UtilityTest
{
	/**
	 * Global varable for test result
	 */
	private static boolean mCorrect = true;

	/**
	 * Simple test to see if utiity methods are working correctly.
	 */
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			finishTest(false);
			qautil.debug("incorrect number of args");
			System.exit(0);
		}

		if (args[0].equals("int"))
		{
			intTest(args[1]);
		}
		else
		{
			longTest(args[1]);
		}
	}

	/**
	 * Main test bolck for checking function inttohexstring and
	 * back to int.
	 */
	public static void intTest(String intvalue)
	{
		int passedValue = 0;
		String hexReturnValue = "";
		int intReturnValue = 0;

		if (intvalue.equals("max") || intvalue.equals("min"))
		{
			if (intvalue.equals("max"))
			{
				passedValue = Integer.MAX_VALUE;
			}
			else
			{
				passedValue = Integer.MIN_VALUE;
			}
		}
		else
		{
			try
			{
				passedValue = Integer.parseInt(intvalue);
			}
			catch (NumberFormatException nfe)
			{
				mCorrect = false;
				qautil.debug("Exception in parseInt: ", nfe);
			}
		}

		hexReturnValue = Utility.intToHexString(passedValue);

		qautil.qadebug("Hex String = " + hexReturnValue);

		intReturnValue = Utility.hexStringToInt(hexReturnValue);

		qautil.qadebug("Int from Hex = " + intReturnValue);

		if (intReturnValue != passedValue)
		{
			mCorrect = false;
			qautil.debug("Final value does not equal original value");
		}

		finishTest(mCorrect);
	}

	/**
	 * Main test bolck for checking function longtohexstring and
	 * back to long.
	 */
	public static void longTest(String longvalue)
	{
		long passedValue = 0;
		String hexReturnValue = "";
		long longReturnValue = 0;

		if (longvalue.equals("max") || longvalue.equals("min"))
		{
			if (longvalue.equals("max"))
			{
				passedValue = Long.MAX_VALUE;
			}
			else
			{
				passedValue = Long.MIN_VALUE;
			}
		}
		else
		{
			try
			{
				passedValue = Long.parseLong(longvalue);
			}
			catch (NumberFormatException nfe)
			{
				mCorrect = false;
				qautil.debug("Exception in parseInt: ", nfe);
			}
		}

		hexReturnValue = Utility.longToHexString(passedValue);

		qautil.qadebug("Hex String = " + hexReturnValue);

		longReturnValue = Utility.hexStringToLong(hexReturnValue);

		qautil.qadebug("Long from Hex = " + longReturnValue);

		if (longReturnValue != passedValue)
		{
			mCorrect = false;
			qautil.debug("Final value does not equal original value");
		}

		finishTest(mCorrect);
	}

	/**
	 * Simple method for printing result.
	 */
	public static void finishTest(boolean result)
	{
		if (result)
		{
			System.out.println("Passed");
		}
		else
		{
			System.out.println("Failed");
		}
	}
}