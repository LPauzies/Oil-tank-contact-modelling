package src.debugger;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DebugLog {
	
	private static int counter;
	
	public static void log(Object message) {
		counter++;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		System.out.println("[" + dateFormat.format(date).toString() + "][Instance " + counter + "] : " + message.toString());
	}
	
	public static void printArray(Object[] array) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		System.out.println("[" + dateFormat.format(date).toString() + "]");
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i].toString());
		}
	}
}
