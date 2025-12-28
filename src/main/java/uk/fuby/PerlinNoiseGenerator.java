package uk.fuby;

import java.util.Arrays;
import java.util.Random;

public class PerlinNoiseGenerator {
	private final ImprovedNoiseGenerator[] noiseLevels;
	private final int levels;
	
	public PerlinNoiseGenerator(Random random, int levels) {
		this.levels = levels;
		this.noiseLevels = new ImprovedNoiseGenerator[levels];
		for (int j = 0; j < levels; ++j) {
			this.noiseLevels[j] = new ImprovedNoiseGenerator(random);
		}
	}
	
	public double[] getRegion(double[] values, int x, int y, int z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ) {
		if (values == null) {
			values = new double[sizeX * sizeY * sizeZ];
		}
		else {
			Arrays.fill(values, 0.0);
		}
		double scale = 1.0;
		for (int p = 0; p < this.levels; ++p) {
			double scaledX = x * scale * scaleX;
			double scaledY = y * scale * scaleY;
			double scaledZ = z * scale * scaleZ;
			
			long flooredX = MathHelper.lfloor(scaledX);
			long flooredZ = MathHelper.lfloor(scaledZ);
			
			scaledX -= flooredX;
			scaledZ -= flooredZ;
			flooredX %= 16777216L;
			flooredZ %= 16777216L;
			scaledX += flooredX;
			scaledZ += flooredZ;
			
			this.noiseLevels[p].add(values, scaledX, scaledY, scaledZ, sizeX, sizeY, sizeZ, scaleX * scale, scaleY * scale, scaleZ * scale, scale);
			
			scale /= 2.0;
		}
		return values;
	}
}
