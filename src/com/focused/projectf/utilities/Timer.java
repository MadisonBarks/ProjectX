package com.focused.projectf.utilities;


/** 
 * A lightweight class for tracking how long methods take to execute.
 */
public class Timer {
	
	private long nano;
	
	public Timer() {
		nano = System.nanoTime();
	}
	
	public long getTime() {
		return System.nanoTime() - nano;
	}
	
	public float getSeconds() {
		return (System.nanoTime() - nano) / 1e9f;
	}
	
	public void logTimeNS(String header) {
		long dif = System.nanoTime() - nano;
		System.out.append(header);
		System.out.println(dif);
	}
	
	public void logTimeMS(String header) {
		float dif = (System.nanoTime() - nano) / 1e6f;
		System.out.append(header);
		System.out.println("\t" + dif);
	}
	
	public void group(String header) {
		//logTimeMS(header);
		restartClock();
	}
	
	public void restartClock() { 
		nano = System.nanoTime();
	}
}
