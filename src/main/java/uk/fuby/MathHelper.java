package uk.fuby;

public class MathHelper {
	
	public static long lfloor(double x) {
		long long3 = (long)x;
		return (x < long3) ? (long3 - 1L) : long3;
	}
	
	public static double clamp(double x, double min, double max) {
		if (x < min) {
			return min;
		}
		if (x > max) {
			return max;
		}
		return x;
	}
}
