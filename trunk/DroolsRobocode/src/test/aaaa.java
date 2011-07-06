package test;

public class aaaa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
System.out.println(aaaa.getBotNameWithNoNumber("Droid Bot (1)"));
	}
	
	static public String getBotNameWithNoNumber(String name) {
     	String n = "0";
     	int hi = name.indexOf("(")+1;
     	if (hi >= 0) { 
     		n = name.substring(0, hi - 1); 
     	}
     	return n;
    }

}
