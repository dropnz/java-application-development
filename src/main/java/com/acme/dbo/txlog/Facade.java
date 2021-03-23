package com.acme.dbo.txlog;

public class Facade {
    private static final String PREFIX_PRIMITIVE = "primitive: ";
    private static final String PREFIX_CHAR = "char: ";
    private static final String PREFIX_STRING = "string: ";
    private static final String PREFIX_REFERENCE = "reference: ";
    private static Object accumulatedValue = null;
    private static int stringDuplicates = 0;
    private static String MODE_INT = "MODE_INT";
    private static String MODE_BYTE = "MODE_BYTE";
    private static String MODE_BOOLEAN = "MODE_BOOLEAN";
    private static String MODE_CHARACTER = "MODE_CHARACTER";
    private static String MODE_OBJECT = "MODE_OBJECT";
    private static String MODE_STRING = "MODE_STRING";
    private static String MODE_NONE = "MODE_NONE";
    private static String accumulationMode = MODE_NONE;

    public static void log(int n) {
	if (accumulationMode.equals(MODE_INT)) {
	    boolean overflow = (Integer)accumulatedValue > 0 &&  n > 0 &&  (Integer)accumulatedValue + n < 0;
	    if (overflow) {
		flush();
		accumulatedValue = (Integer)n;
	    } else {
		accumulatedValue = (Integer)accumulatedValue + n;
	    }
	} else {
	    flush();
	    accumulatedValue = n;
	}
	accumulationMode = MODE_INT;
    }

    public static void log(byte n) {
	if (accumulationMode.equals(MODE_BYTE)) {
	    boolean overflow = (Byte)accumulatedValue > 0 &&  n > 0 &&  (byte)((Byte)accumulatedValue + n) < 0;
	    if (overflow) {
		flush();
		accumulatedValue = (Byte)n;
	    } else {
		accumulatedValue = (Byte)accumulatedValue + n;
	    }
	} else {
	    flush();
	    accumulatedValue = n;
	}
	accumulationMode = MODE_BYTE;
    }

    public static void log(boolean b) {
	flush();
	accumulatedValue = (Boolean)b;
	accumulationMode = MODE_BOOLEAN;
    }

    public static void log(char c) {
	flush();
	accumulatedValue = (Character)c;
	accumulationMode = MODE_CHARACTER;
    }

    public static void log(String s) {
	if (accumulationMode.equals(MODE_STRING)) {
	    if (accumulatedValue.equals(s)) {
		stringDuplicates++;
	    } else {
		flush();
		accumulatedValue = s;
	    }
	} else {
	    flush();
	    accumulatedValue = s;
	}
	accumulationMode = MODE_STRING;
    }

    public static void log(Object obj) {
	flush();
	accumulatedValue = obj;
	accumulationMode = MODE_OBJECT;
    }

    private static String decorateBuffer() {
	if (accumulationMode.equals(MODE_INT)) {
	    return PREFIX_PRIMITIVE + accumulatedValue;
	} else if (accumulationMode.equals(MODE_BYTE)) {
	    return PREFIX_PRIMITIVE + accumulatedValue;
	} else if (accumulationMode.equals(MODE_BOOLEAN)) {
	    return PREFIX_PRIMITIVE + accumulatedValue;
	} else if (accumulationMode.equals(MODE_STRING)) {
	    if (stringDuplicates > 0) {
		return PREFIX_STRING + accumulatedValue + " (x" + (stringDuplicates+1) + ")";
	    } else {
		return PREFIX_STRING + accumulatedValue;
	    }
	} else if (accumulationMode.equals(MODE_OBJECT)) {
	    return PREFIX_REFERENCE + accumulatedValue;
	} else if (accumulationMode.equals(MODE_CHARACTER)) {
	    return PREFIX_CHAR + accumulatedValue;
	}
	throw new RuntimeException("Unknown mode: " + accumulationMode);
    }

    public static void flush() {
	if (accumulationMode == MODE_NONE) {
	    return;
	}
	System.out.println(decorateBuffer());
	accumulationMode = MODE_NONE;
	stringDuplicates = 0;
    }
}
