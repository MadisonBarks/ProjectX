package com.focused.projectf.utilities.random;

import java.util.Random;

public class Chance {
	public static final Random rnd = new Random();
	
	/** provides a 'trues' in 'total' chance of returning true. Otherwise, false is returned. */
	public static boolean chance(int trues, int total) {
		return rnd.nextInt(total) > total - trues - 1;
	}
	
	/** 
	 * returns one of the provided objects at random. The chance of each object being returned is determined
	 *  by its respective value in weights. Higher weights values equals more likely to be returned.
	 */
	public static <T> T random(T[] returns, int[] weights) {
		if(returns.length != weights.length)
			throw new Error("Both arrays must be of equal size");
		
		int total = 0;
		for(int i = 0; i < weights.length; i++) 
			total += weights[i];		
		int r = rnd.nextInt(total);
		int index = 0;
		while(r > weights[index]) {
			r -= weights[index];
			index++;
		}
		return returns[index];
	}

	public static int random(int[] returns, int[] weights) {
		if(returns.length != weights.length)
			throw new Error("Both arrays must be of equal size");
		
		int total = 0;
		for(int i = 0; i < weights.length; i++) 
			total += weights[i];		
		int r = rnd.nextInt(total);
		int index = 0;
		while(r > weights[index]) {
			r -= weights[index];
			index++;
		}
		return returns[index];
	}
	
	public static <T> T random(T[] returns, float[] weights) {
		if(returns.length != weights.length)
			throw new Error("Both arrays must be of equal size");
		
		float total = 0;
		for(int i = 0; i < weights.length; i++) 
			total += weights[i];		
		float r = rnd.nextFloat() * total;
		int index = 0;
		while(r > weights[index]) {
			r -= weights[index];
			index++;
		}
		return returns[index];
	}
	
	/**
	 * returns one of the provided objects at random
	 */
	public static <T> T random(T[] returns) {
		return returns[rnd.nextInt(returns.length)];
	}
	public static int random(int[] returns) {
		return returns[rnd.nextInt(returns.length)];
	}

	public static int nextInt(int n) { 
		return rnd.nextInt(n); 
	}
	
	public static int randomInRange(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}
	
	public static double nextDouble() { 
		return rnd.nextDouble(); 
	}
	public static long nextLong() { 
		return rnd.nextLong(); 
	}
}
