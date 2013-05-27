package com.focused.projectf.utilities.random;

import java.util.Random;


public class PerlinNoiseGenerator {
	// Constants for setting up the Perlin-1 noise functions
	private static final int B = 0x1000;
	private static final int BM = 0xff;

	private static final int N = 0x1000;

	/** Default seed to use for the random number generation */
	private static final int DEFAULT_SEED = 100;

	/** Default sample size to work with */
	private static final int DEFAULT_SAMPLE_SIZE = 256;

	/** Permutation array for the improved noise function */
	private static int[] p_imp;

	/** P array for perline 1 noise */
	private static int[] p;
	private static float[][] g3;
	private static float[][] g2;
	private static float[] g1;

	public static void init() {
		init(DEFAULT_SEED);
	}

	public static void init(int seed) {
		p_imp = new int[DEFAULT_SAMPLE_SIZE << 1];

		int i, j, k;
		Random rand = new Random(seed);

		// Calculate the table of psuedo-random coefficients.
		for(i = 0; i < DEFAULT_SAMPLE_SIZE; i++)
			p_imp[i] = i;

		// generate the psuedo-random permutation table.
		while(--i > 0)  {
			k = p_imp[i];
			j = (int)(rand.nextLong() & DEFAULT_SAMPLE_SIZE);
			p_imp[i] = p_imp[j];
			p_imp[j] = k;
		}

		initPerlin1();
	}

	public static double improvedNoise(double x, double y, double z) {
		// Constraint the point to a unit cube
		int uc_x = (int)Math.floor(x) & 255;
		int uc_y = (int)Math.floor(y) & 255;
		int uc_z = (int)Math.floor(z) & 255;

		// Relative location of the point in the unit cube
		double xo = x - Math.floor(x);
		double yo = y - Math.floor(y);
		double zo = z - Math.floor(z);

		// Fade curves for x, y and z
		double u = fade(xo);
		double v = fade(yo);
		double w = fade(zo);

		// Generate a hash for each coordinate to find out where in the cube
		// it lies.
		int a =  p_imp[uc_x] + uc_y;
		int aa = p_imp[a] + uc_z;
		int ab = p_imp[a + 1] + uc_z;

		int b =  p_imp[uc_x + 1] + uc_y;
		int ba = p_imp[b] + uc_z;
		int bb = p_imp[b + 1] + uc_z;

		// blend results from the 8 corners based on the noise function
		double c1 = grad(p_imp[aa], xo, yo, zo);
		double c2 = grad(p_imp[ba], xo - 1, yo, zo);
		double c3 = grad(p_imp[ab], xo, yo - 1, zo);
		double c4 = grad(p_imp[bb], xo - 1, yo - 1, zo);
		double c5 = grad(p_imp[aa + 1], xo, yo, zo - 1);
		double c6 = grad(p_imp[ba + 1], xo - 1, yo, zo - 1);
		double c7 = grad(p_imp[ab + 1], xo, yo - 1, zo - 1);
		double c8 = grad(p_imp[bb + 1], xo - 1, yo - 1, zo - 1);

		return lerp(w, lerp(v, lerp(u, c1, c2), lerp(u, c3, c4)),
				lerp(v, lerp(u, c5, c6), lerp(u, c7, c8)));
	}

	public static float noise1(float x) {
		float t = x + N;
		int bx0 = ((int) t) & BM;
		int bx1 = (bx0 + 1) & BM;
		float rx0 = t - (int) t;
		float rx1 = rx0 - 1;

		float sx = sCurve(rx0);

		float u = rx0 * g1[p[bx0]];
		float v = rx1 * g1[p[bx1]];

		return lerp(sx, u, v);
	}

	public static float noise2(float x, float y) {
		float t = x + N;
		int bx0 = ((int)t) & BM;
		int bx1 = (bx0 + 1) & BM;
		float rx0 = t - ((int)t);
		float rx1 = rx0 - 1;

		t = y + N;
		int by0 = ((int)t) & BM;
		int by1 = (by0 + 1) & BM;
		float ry0 = t - (int)t;
		float ry1 = ry0 - 1;

		int i = p[bx0];
		int j = p[bx1];

		int b00 = p[i + by0];
		int b10 = p[j + by0];
		int b01 = p[i + by1];
		int b11 = p[j + by1];

		float sx = sCurve(rx0);
		float sy = sCurve(ry0);

		float[] q = g2[b00];
		float u = rx0 * q[0] + ry0 * q[1];
		q = g2[b10];
		float v = rx1 * q[0] + ry0 * q[1];
		float a = lerp(sx, u, v);

		q = g2[b01];
		u = rx0 * q[0] + ry1 * q[1];
		q = g2[b11];
		v = rx1 * q[0] + ry1 * q[1];
		float b = lerp(sx, u, v);

		return lerp(sy, a, b);
	}

	public static float noise3(float x, float y, float z) {
		float t = x + (float)N;
		int bx0 = ((int)t) & BM;
		int bx1 = (bx0 + 1) & BM;
		float rx0 = (float)(t - (int)t);
		float rx1 = rx0 - 1;

		t = y + (float)N;
		int by0 = ((int)t) & BM;
		int by1 = (by0 + 1) & BM;
		float ry0 = (float)(t - (int)t);
		float ry1 = ry0 - 1;

		t = z + (float)N;
		int bz0 = ((int)t) & BM;
		int bz1 = (bz0 + 1) & BM;
		float rz0 = (float)(t - (int)t);
		float rz1 = rz0 - 1;

		int i = p[bx0];
		int j = p[bx1];

		int b00 = p[i + by0];
		int b10 = p[j + by0];
		int b01 = p[i + by1];
		int b11 = p[j + by1];

		t  = sCurve(rx0);
		float sy = sCurve(ry0);
		float sz = sCurve(rz0);

		float[] q = g3[b00 + bz0];
		float u = (rx0 * q[0] + ry0 * q[1] + rz0 * q[2]);
		q = g3[b10 + bz0];
		float v = (rx1 * q[0] + ry0 * q[1] + rz0 * q[2]);
		float a = lerp(t, u, v);

		q = g3[b01 + bz0];
		u = (rx0 * q[0] + ry1 * q[1] + rz0 * q[2]);
		q = g3[b11 + bz0];
		v = (rx1 * q[0] + ry1 * q[1] + rz0 * q[2]);
		float b = lerp(t, u, v);

		float c = lerp(sy, a, b);

		q = g3[b00 + bz1];
		u = (rx0 * q[0] + ry0 * q[1] + rz1 * q[2]);
		q = g3[b10 + bz1];
		v = (rx1 * q[0] + ry0 * q[1] + rz1 * q[2]);
		a = lerp(t, u, v);

		q = g3[b01 + bz1];
		u = (rx0 * q[0] + ry1 * q[1] + rz1 * q[2]);
		q = g3[b11 + bz1];
		v = (rx1 * q[0] + ry1 * q[1] + rz1 * q[2]);
		b = lerp(t, u, v);

		float d = lerp(sy, a, b);

		return lerp(sz, c, d);
	}

	/**
	 * Create a turbulent noise output based on the core noise function. This
	 * uses the noise as a base function and is suitable for creating clouds,
	 * marble and explosion effects. For example, a typical marble effect would
	 * set the colour to be:
	 * <pre>
	 *    sin(point + turbulence(point) * point.x);
	 * </pre>
	 */
	public static double imporvedTurbulence(double x, double y, double z, float loF, float hiF) {
		double p_x = x + 123.456f;
		double p_y = y;
		double p_z = z;
		double t = 0;
		double f;

		for(f = loF; f < hiF; f *= 2) {
			t += Math.abs(improvedNoise(p_x, p_y, p_z)) / f;

			p_x *= 2;
			p_y *= 2;
			p_z *= 2;
		}

		return t - 0.3;
	}

	/**
	 * Create a turbulance function in 2D using the original perlin noise
	 * function.
	 *
	 * @param x The X coordinate of the location to sample
	 * @param y The Y coordinate of the location to sample
	 * @param freq The frequency of the turbluance to create
	 * @return The value at the given coordinates
	 */
	public static float turbulence2(float x, float y, float freq) {
		float t = 0;

		do {
			t += noise2(freq * x, freq * y) / freq;
			freq *= 0.5f;
		} while (freq >= 1);

		return t;
	}

	/**
	 * Create a 2D tileable noise function for the given width and height.
	 */
	public static float tileableNoise2(float x, float y, float w, float h) {
		
		float n0 = noise2(x,     y)     * (w - x) * (h - y);
		float n1 = noise2(x - w, y)     *      x  * (h - y);
		float n2 = noise2(x,     y - h) * (w - x) *      y;
		float n3 = noise2(x - w, y - h) *      x  *      y;
		float o = (n0 + n1 + n2 + n3) / (w * h);
		return o;
	}


	/**
	 * Create a turbulance function that can be tiled across a surface in 2D.
	 */
	public static float tileableTurbulence2(float x, float y, float w, float h, float freq) {
		float t = 0;

		do {
			t += tileableNoise2(freq * x, freq * y, w * freq, h * freq) / freq;
			freq *= 0.5f;
		} while (freq >= 1);

		return t;
	}


	private static double lerp(double t, double a, double b) {
		return a + t * (b - a);
	} 
	
	private static float lerp(float t, float a, float b) {
		return a + t * (b - a);
	}

	private static double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private static double grad(int hash, double x, double y, double z) {
		// Convert low 4 bits of hash code into 12 gradient directions.
		int h = hash & 15;
		double u = (h < 8 || h == 12 || h == 13) ? x : y;
		double v = (h < 4 || h == 12 || h == 13) ? y : z;

		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private static float sCurve(float t) {
		return (t * t * (3 - 2 * t));
	}

	private static void normalize2(float[] v) {
		float s = (float)(1 / Math.sqrt(v[0] * v[0] + v[1] * v[1]));
		v[0] *= s;
		v[1] *= s;
	}

	/**
	 * 3D-vector normalisation function.
	 */
	private static void normalize3(float[] v)
	{
		float s = (float)(1 / Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
		v[0] *= s;
		v[1] *= s;
		v[2] *= s;
	}

	/**
	 * Initialise the lookup arrays used by Perlin 1 function.
	 */
	private static void initPerlin1()
	{
		p = new int[B + B + 2];
		g3 = new float[B + B + 2][3];
		g2 = new float[B + B + 2][2];
		g1 = new float[B + B + 2];
		int i, j, k;

		for(i = 0; i < B; i++)
		{
			p[i] = i;

			g1[i] = (float)(((Math.random() * Integer.MAX_VALUE) % (B + B)) - B) / B;

			for(j = 0; j < 2; j++)
				g2[i][j] = (float)(((Math.random() * Integer.MAX_VALUE) % (B + B)) - B) / B;
			normalize2(g2[i]);

			for(j = 0; j < 3; j++)
				g3[i][j] = (float)(((Math.random() * Integer.MAX_VALUE) % (B + B)) - B) / B;
			normalize3(g3[i]);
		}

		while(--i > 0)
		{
			k = p[i];
			j = (int)((Math.random() * Integer.MAX_VALUE) % B);
			p[i] = p[j];
			p[j] = k;
		}

		for(i = 0; i < B + 2; i++)
		{
			p[B + i] = p[i];
			g1[B + i] = g1[i];
			for(j = 0; j < 2; j++)
				g2[B + i][j] = g2[i][j];
			for(j = 0; j < 3; j++)
				g3[B + i][j] = g3[i][j];
		}
	}
}
