package uk.fuby;

import java.util.Random;

public class ImprovedNoiseGenerator {
	private int[] permutations;
	public double x;
	public double y;
	public double z;
	private static final double[] VEC3_X;
	private static final double[] VEC3_Y;
	private static final double[] VEC3_Z;
	
	public ImprovedNoiseGenerator(Random random) {
		this.permutations = new int[512];
		this.x = random.nextDouble() * 256.0;
		this.y = random.nextDouble() * 256.0;
		this.z = random.nextDouble() * 256.0;
		for (int i = 0; i < 256; ++i) {
			this.permutations[i] = i;
		}
		for (int i = 0; i < 256; ++i) {
			int randomIndex = random.nextInt(256 - i) + i;
			int temp = this.permutations[i];
			this.permutations[i] = this.permutations[randomIndex];
			this.permutations[randomIndex] = temp;
			this.permutations[i + 256] = this.permutations[i];
		}
	}
	
	public final double lerp(double delta, double min, double max) {
		return min + delta * (max - min);
	}
	
	public final double gradient(int hash, double x, double y, double z) {
		int integer9 = hash & 0xF;
		return ImprovedNoiseGenerator.VEC3_X[integer9] * x + ImprovedNoiseGenerator.VEC3_Y[integer9] * y + ImprovedNoiseGenerator.VEC3_Z[integer9] * z;
	}
	
	public void add(double[] values, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ, double scaleExponent) {
		int index1;
		int point1Index1;
		int point1Index2;
		
		int index2;
		int point2Index1;
		int point2Index2;
		
		double xLerp1 = 0.0;
		double xLerp2 = 0.0;
		double xLerp3 = 0.0;
		double xLerp4 = 0.0;
		
		int oldY = -1;
		
		double multiplier = 1.0 / scaleExponent;
		
		int counter = 0;
		for (int sampleX = 0; sampleX < sizeX; ++sampleX) {
			double currentX = x + sampleX * scaleX + this.x;
			
			int roundedX = (int)currentX;
			if (currentX < roundedX) {
				--roundedX;
			}
			
			int truncatedX = roundedX & 0xFF;
			currentX -= roundedX;
			// bicubic interpolation
			double berpX = currentX * currentX * currentX * (currentX * (currentX * 6.0 - 15.0) + 10.0);
			
			for (int sampleZ = 0; sampleZ < sizeZ; ++sampleZ) {
				double currentZ = z + sampleZ * scaleZ + this.z;
				
				int roundedZ = (int)currentZ;
				if (currentZ < roundedZ) {
					--roundedZ;
				}
				
				int truncatedZ = roundedZ & 0xFF;
				currentZ -= roundedZ;
				// bicubic interpolation
				double berpZ = currentZ * currentZ * currentZ * (currentZ * (currentZ * 6.0 - 15.0) + 10.0);
				
				for (int sampleY = 0; sampleY < sizeY; ++sampleY) {
					double currentY = y + sampleY * scaleY + this.y;
					
					int roundedY = (int)currentY;
					if (currentY < roundedY) {
						--roundedY;
					}
					
					int truncatedY = roundedY & 0xFF;
					currentY -= roundedY;
					// bicubic interpolation
					double berpY = currentY * currentY * currentY * (currentY * (currentY * 6.0 - 15.0) + 10.0);
					
					if (sampleY == 0 || truncatedY != oldY) {
						oldY = truncatedY;
						
						index1 = this.permutations[truncatedX] + truncatedY;
						point1Index1 = this.permutations[index1] + truncatedZ;
						point1Index2 = this.permutations[index1 + 1] + truncatedZ;
						
						index2 = this.permutations[truncatedX + 1] + truncatedY;
						point2Index1 = this.permutations[index2] + truncatedZ;
						point2Index2 = this.permutations[index2 + 1] + truncatedZ;
						
						xLerp1 = this.lerp(berpX, this.gradient(this.permutations[point1Index1], currentX, currentY, currentZ), this.gradient(this.permutations[point2Index1], currentX - 1.0, currentY, currentZ));
						xLerp2 = this.lerp(berpX, this.gradient(this.permutations[point1Index2], currentX, currentY - 1.0, currentZ), this.gradient(this.permutations[point2Index2], currentX - 1.0, currentY - 1.0, currentZ));
						xLerp3 = this.lerp(berpX, this.gradient(this.permutations[point1Index1 + 1], currentX, currentY, currentZ - 1.0), this.gradient(this.permutations[point2Index1 + 1], currentX - 1.0, currentY, currentZ - 1.0));
						xLerp4 = this.lerp(berpX, this.gradient(this.permutations[point1Index2 + 1], currentX, currentY - 1.0, currentZ - 1.0), this.gradient(this.permutations[point2Index2 + 1], currentX - 1.0, currentY - 1.0, currentZ - 1.0));
					}
					
					double yLerp1 = this.lerp(berpY, xLerp1, xLerp2);
					double yLerp2 = this.lerp(berpY, xLerp3, xLerp4);
					double value = this.lerp(berpZ, yLerp1, yLerp2);
					
					int index = counter++;
					values[index] += value * multiplier;
				}
			}
		}
	}
	
	static {
		VEC3_X = new double[] { 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0 };
		VEC3_Y = new double[] { 1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0 };
		VEC3_Z = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0 };
	}
}
