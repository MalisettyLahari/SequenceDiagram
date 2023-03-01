package com.contiautomotive.cidl.utils;

public class CidlUtils {
	 private CidlUtils() {
		    throw new IllegalStateException("Utility class");
		  }
	/**
	 * Checks whether the type is a void type.
	 * @param type_name Name of the type
	 */
	public static boolean isVoidType(String type_name) {

		return (type_name.equalsIgnoreCase("void"));
	}
}
