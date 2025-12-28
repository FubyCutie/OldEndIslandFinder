package uk.fuby;

import java.util.Random;

public class TheEndChunkGenerator {
	
	private final PerlinNoiseGenerator minLimitPerlinNoise;
	private final PerlinNoiseGenerator maxLimitPerlinNoise;
	private final PerlinNoiseGenerator perlinNoise1;
	public PerlinNoiseGenerator floatingIslandScale;
	public PerlinNoiseGenerator floatingIslandNoise;
	
	double[] perlinNoiseBuffer;
	double[] minLimitPerlinNoiseBuffer;
	double[] maxLimitPerlinNoiseBuffer;
	
	
	public TheEndChunkGenerator(long seed) {
		Random random = new Random(seed);
		this.minLimitPerlinNoise = new PerlinNoiseGenerator(random, 16);
		this.maxLimitPerlinNoise = new PerlinNoiseGenerator(random, 16);
		this.perlinNoise1 = new PerlinNoiseGenerator(random, 8);
		this.floatingIslandScale = new PerlinNoiseGenerator(random, 10);
		this.floatingIslandNoise = new PerlinNoiseGenerator(random, 16);
	}
	
	public boolean doThing(int x, int y, int z) {
		int sizeX = 3; // idk but these are constants
		//int sizeY = 33; // this got inlined
		int sizeZ = 3;
		
		double scaleX = 684.412 * 2.0;
		double scaleY = 684.412;
		
		this.minLimitPerlinNoiseBuffer = this.minLimitPerlinNoise.getRegion(this.minLimitPerlinNoiseBuffer, x, y, z, sizeX, 33, sizeZ, scaleX, scaleY, scaleX);
		this.maxLimitPerlinNoiseBuffer = this.maxLimitPerlinNoise.getRegion(this.maxLimitPerlinNoiseBuffer, x, y, z, sizeX, 33, sizeZ, scaleX, scaleY, scaleX);
		
		double max = Double.NEGATIVE_INFINITY; // I think this is worth it, but it's hard to say
		for (double d : minLimitPerlinNoiseBuffer) {
			if (d > max) max = d;
		}
		for (double d : maxLimitPerlinNoiseBuffer) {
			if (d > max) max = d;
		}
		if (max < 55296) return false; // cannot possibly generate an island without at least one of these
		
		
		this.perlinNoiseBuffer = this.perlinNoise1.getRegion(this.perlinNoiseBuffer, x, y, z, sizeX, 33, sizeZ, scaleX / 80.0, scaleY / 160.0, scaleX / 80.0);
		
		int i = 0;
		for (int p = 0; p < sizeX; ++p) {
			for (int q = 0; q < sizeZ; ++q) {
				for (int ySample = 0; ySample < 33; ++ySample) {
					
					double density;
					double minSample = this.minLimitPerlinNoiseBuffer[i] / 512.0;
					double maxSample = this.maxLimitPerlinNoiseBuffer[i] / 512.0;
					
					if (minSample < 108 && maxSample < 108) {
						++i; // we lerp between these and subtract 108, and need >0, this is an easy early fail
						continue;
					}
					
					double lerpNoise = (this.perlinNoiseBuffer[i] / 10.0 + 1.0) / 2.0;
					
					if (lerpNoise < 0.0) {
						density = minSample;
					}
					else if (lerpNoise > 1.0) {
						density = maxSample;
					}
					else {
						density = minSample + (maxSample - minSample) * lerpNoise;
					}
					
					if (density > 108) { // above threshold
						if (ySample >= 8 && ySample <= 14) return true; // won't be modified further
					} else { // might be modified, but modification will only decrease it
						++i;
						continue;
					}
					
					density -= 108.0;
					
					double clampedSample;
					
					if (ySample > 14) { // not quite sure what this is doing
						clampedSample = (ySample - 14.0d) / 64.0f;
						clampedSample = MathHelper.clamp(clampedSample, 0.0, 1.0);
						density = density * (1.0 - clampedSample) + -3000.0 * clampedSample; // scales down and subtracts large number
					}
					
					if (ySample < 8) { // not quite sure what this is doing
						clampedSample = (8 - ySample) / 7.0f;
						density = density * (1.0 - clampedSample) + -30.0 * clampedSample; // scales down and subtracts large number
					}
					
					if (density > 0) return true;
					
					++i;
				}
			}
		}
		return false;
	}
	
	
}
