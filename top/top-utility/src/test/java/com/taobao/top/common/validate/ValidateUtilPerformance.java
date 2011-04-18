package com.taobao.top.common.validate;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @version 2009-8-6
 * @author <a href="mailto:haishi@taobao.com">haishi</a>
 *
 */
public class ValidateUtilPerformance {

	@Ignore
	public void testIsUnsignedNumber() {
		////////////
		// Positive
		long start = System.currentTimeMillis();
		// 100 length number.
		for (int i = 0; i < 1000; i ++) {
			ValidateUtil.isUnsignedNumber("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		}
		long spent = System.currentTimeMillis() - start;
		
		System.out.println("Spent " + spent / 1000.0 + " ms for each ValidateUtil.isUnsignedNumber() - positive");
		assertTrue(spent <= 20); // 20 millisecond for 1000 times = 0.02 ms for each invocation.
		
		///////////
		// Negative
		start = System.currentTimeMillis();
		// 100 length malformat number.
		for (int i = 0; i < 1000; i ++) {
			ValidateUtil.isUnsignedNumber("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789A");
		}
		spent = System.currentTimeMillis() - start;
		
		System.out.println("Spent " + spent / 1000.0 + " ms for each ValidateUtil.isUnsignedNumber() - negative");
		assertTrue(spent <= 5); // 5 millisecond for 1000 times = 0.005 ms for each invocation.
	}
	

	@Test
	public void testIsNumberInRange() {
		////////////
		// Positive 
		long start = System.currentTimeMillis();
		// 100 length number.
		for (int i = 0; i < 1000; i ++) {
			ValidateUtil.isNumberInRange("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", new BigInteger("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567889"), new BigInteger("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567891"));
		}
		long spent = System.currentTimeMillis() - start;
		
		System.out.println("Spent " + spent / 1000.0 + " ms for each ValidateUtil.isNumberInRange() - positive");
		assertTrue(spent <= 200); // 20 millisecond for 1000 times = 0.02 ms for each invocation.

		///////////
		// Positive small number no range constraint.
		start = System.currentTimeMillis();
		// 100 length malformat number.
		for (int i = 0; i < 1000; i ++) {
			ValidateUtil.isNumberInRange("1", null, null);
		}
		spent = System.currentTimeMillis() - start;
		
		System.out.println("Spent " + spent / 1000.0 + " ms for each ValidateUtil.isNumberInRange() - positive (small number, no range constraint)");
		assertTrue(spent <= 20); // 20 millisecond for 1000 times = 0.02 ms for each invocation.

		
		///////////
		// Negative
		start = System.currentTimeMillis();
		// 100 length malformat number.
		for (int i = 0; i < 1000; i ++) {
			ValidateUtil.isNumberInRange("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", new BigInteger("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567891"), new BigInteger("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567889"));
		}
		spent = System.currentTimeMillis() - start;
		
		System.out.println("Spent " + spent / 1000.0 + " ms for each ValidateUtil.isNumberInRange() - negative");
		assertTrue(spent <= 200); // 20 millisecond for 1000 times = 0.02 ms for each invocation.

	}


}
