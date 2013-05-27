package com.focused.projectf.utilities;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import com.focused.projectf.Point;


/**
 * A grouping of less precise but much faster to run methods from <code>java.lang.Math</code>
 * and some other nice mathy utilities
 */
public class FMath {

	public static final float PI 						= 3.1415926535f;
	public static final float PI_SQARED 					= PI * PI;
	public static final float TWO_PI 					= PI * 2;
	public static final float HALF_PI 					= PI / 2f;
	public static final float QUARTER_PI 				= PI / 4f;

	private static final float NEG_4_OVER_PI_SQUARED 	= -4f / PI_SQARED;
	private static final float FOUR_OVER_PI				= 4f / PI;

	private static final float threehalfs 				= 1.5F;


	/**
	 * Solves sin(x) about 5 times as fast as Math.sin(x). The trade off is accuracy 
	 * (max error is ~5.6%) Only works on a range of [-pi, pi], so wrap your vars
	 */
	public static float sin(float x) {
		float y = FOUR_OVER_PI * x + NEG_4_OVER_PI_SQUARED * x * abs(x);
		return 0.225f * (y * abs(y) - y) + y;
	}

	/**
	 * Solves cos(x) about 4 times as fast as Math.cos(x). The trade off is accuracy 
	 * (max error is ~5.6%) Only works on a range of [-pi, pi], so wrap your vars
	 */
	public static float cos(float x) {
		x += HALF_PI;
		if(x > PI)	x -= TWO_PI;
		float y = FOUR_OVER_PI * x + NEG_4_OVER_PI_SQUARED * x * abs(x);
		return 0.225f * (y * abs(y) - y) + y;
	}

	/**
	 * Solves cos(x) about 5 times as fast as Math.cos(x). The trade off is accuracy 
	 * (max error is ~5.6%) Only works on a range of [-3/2 pi, 1/2 pi], so wrap your vars
	 */
	public static float cosF(float x) {
		x += HALF_PI;
		float y = FOUR_OVER_PI * x + NEG_4_OVER_PI_SQUARED * x * abs(x);
		return 0.225f * (y * abs(y) - y) + y;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static float tan(float x) {
		System.out.println("Running quick tan");
		final float xValue = x;
		RunnableFuture cosValue = new FutureTask(new Callable<Float>() {
			float cosValue;
			@Override
			public Float call() throws Exception {
				return cosValue;
			}
			@SuppressWarnings("unused")
			public void run() {
				cosValue =  FMath.cos(xValue);
			}
		});
		RunnableFuture sinValue = new FutureTask(new Callable<Float>() {
			float sinValue;
			@Override
			public Float call() throws Exception {
				return sinValue;
			}
			@SuppressWarnings("unused")
			public void run() {
				sinValue =  FMath.sin(xValue);
			}
		});
		// start the thread to execute it (you may also use an Executor)
		new Thread(cosValue).start();
		new Thread(sinValue).start();
		// get the result
		float cos = 0, sin = 0;
		try {
			cos = (Float) cosValue.get();
			sin = (Float) sinValue.get();
		}
		catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sin / cos; // TODO: spread out for efficency
	}

	/** 
	 * returns a point with a length of one that points in the direction of x radians rotated
	 * counterclockwise from the 3 o'clock position. x must be in the range [-pi, pi]
	 */
	public static Point sinCos(float x) {
		return new Point(sin(x), cos(x));
	}	
	/** 
	 * returns a point with a given length  that points in the direction of x radians rotated
	 * counterclockwise from the 3 o'clock position. x must be in the range [-pi, pi]
	 */
	public static Point sinCos(float x, float length) {
		return new Point(sin(x) * length, cos(x) * length);
	}

	public static float abs(float x) {
		return (x >= 0) ? x : -x;
	}

	public static float wrap(float val, float min, float max) {
		val -= min;
		val %= max - min;
		if(val < 0) val += max - min;
		return val + min;
	}
	public static int wrap(int val, int min, int max) {
		val -= min;
		val %= max - min;
		if(val < 0) val += max - min;
		return val + min;
	}

	public static float invSqrt(float number) {
		int i;
		float x2, y;

		x2 = number * 0.5f;
		y  = number;
		i  = (int) y;                       // evil floating point bit level hacking
		i  = 0x5f3759df - ( i >> 1 );               // what the fuck?
		y  = (float)i;
		y  = y * ( threehalfs - ( x2 * y * y ) );   // 1st iteration
		//y  = y * ( threehalfs - ( x2 * y * y ) );   // 2nd iteration, this can be removed

		return y;
	}

	/** 
	 * Code stolen from Doom. 
	 * Ya.... 
	 */
	public static float sqrt(float number) {
		return (float)Math.sqrt(number);
		/*
		int i;
		float x2, y;

		x2 = number * 0.5f;
		y = number;
		i = (int) y;								// evil floating point bit level hacking
		i = 0x5f3759df - (i >> 1);					// what the fuck?
		y = (float)i;
		y = y * (threehalfs - (x2 * y * y));		// 1st iteration
		//y = y * (threehalfs - (x2 * y * y));		// 2nd iteration, this can be removed

		return 1 / y;
		 */
	}

	public static float min(float a, float b) { return (a > b) ? b : a; }
	public static int min(int a, int b) { return (a > b) ? b : a; }
	public static long min(long a, long b) { return (a > b) ? b : a; }

	public static float max(float a, float b) { return (a < b) ? b : a; }
	public static int max(int a, int b) { return (a < b) ? b : a; }
	public static long max(long a, long b) { return (a < b) ? b : a; }
	/** returns the point with the shortest lengthSq() */
	public static Point min(Point a, Point b) {
		if(a.length() < b.length()) 
			return a;
		return b;
	}

	public static int clamp(int val, int max, int min) {
		return max(min, min(max, val));
	}
	public static float clamp(float val, float max, float min) {
		return max(min, min(max, val));
	}

	public static float atan2(float x, float y) {
		return (float) FMath.atan2(x, y);
	}

	public static float cosW(float angle) {
		return cosF(wrap(angle, -HALF_PI * 3, HALF_PI));
	}
	public static float sinW(float angle) {
		return sin(wrap(angle, -PI, PI));
	}

	public static int nextPowerOf2(final int a) {
		int b = 1;
		while (b < a) 
			b = b << 1;
		return b;
	}

	public static Point closestPointToLine(Point A, Point B, Point P) {
		Point AP = P.minus(A);
		Point AB = B.minus(A);
		float ab2 = AB.X * AB.X + AB.Y * AB.Y;
		float ap_ab = AP.X * AB.X + AP.Y * AB.Y;
		float t = ap_ab / ab2;

		return AB.timesEquals(t).plusEquals(A);
	}
	/*
	public static Point closestPointToLineSegmentOld(Point A, Point B, Point P) {
		Point AP = P.minus(A);
		Point AB = B.minus(A);
		float ab2 = AB.X * AB.X + AB.Y * AB.Y;
		float ap_ab = AP.X * AB.X + AP.Y * AB.Y;
		float t = ap_ab / ab2;
		t = FMath.clamp(t, 1, 0);
		return AB.times(t).plus(A);
	}
	/* */
	public static Point closestPointToLineSegment(Point A, Point B, Point P) {
		float px = B.X - A.X;
		float py = B.Y - A.Y;
		float pDistSq = px * px + py * py;
		float u = ((P.X - A.X) * px + (P.Y - A.Y) * py) / pDistSq; 
		if(u > 1) 
			u = 1;
		else if(u < 0) 
			u = 0;
		return new Point(A.X + u * px, A.Y + u * py);
	}
}

