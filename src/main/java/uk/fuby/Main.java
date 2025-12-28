package uk.fuby;


import java.util.ArrayList;
import java.util.List;


public class Main {
	public static List<Pos> islands = new ArrayList<>();
	
	public static void main(String[] args) {
		Args arguments = Args.parseArgs(args);
		
		if (search(arguments)) {
			System.out.print("found " + islands.size() + " islands, at:");
			System.out.print(" [");
			islands.forEach(island -> {
				System.out.print("x: " + island.x + " z: " + island.z);
				if (islands.lastIndexOf(island) + 1 < islands.size()) {
					System.out.print(", ");
				}
			});
			System.out.print("]\n");
			System.exit(0);
		} else {
			System.exit(1);
		}
	}
	
	
	public static boolean search(Args args) {
		islands.clear();
		
		TheEndChunkGenerator gen = new TheEndChunkGenerator(args.seed);
		
		int searchDistance = args.searchDistance;
		int startDistance = args.startDistance / 2;
		int xOffset = args.startX;
		int zOffset = args.startZ;
		
		int x = 0, y = 0, z = Math.toIntExact(-startDistance);
		
		int dx = 1;
		int dz = 0;
		
		boolean negate = false;
		
		int currentDist = Math.toIntExact(startDistance + 1);
		
		
		for (long i = 0; i < (long) searchDistance * searchDistance; i++) {

			if (gen.doThing((x + xOffset) * 2, y, (z + zOffset) * 2)) {
				islands.add(new Pos((x + xOffset) * 16, (z + zOffset) * 16));
				if (args.extraOutput) {
					System.out.println("island found at x: " + (x + xOffset) * 16 + " z: " + (z + zOffset) * 16);
				}
			}

			x += dx;
			z += dz;

			if (x == currentDist && z == -currentDist) {
				currentDist++;
				//System.out.println("now checking chunks out to: " + currentDist); // unsure if I should bring this back
			}

			boolean xShouldTurn = Math.abs(x + dx) > currentDist;
			boolean zShouldTurn = Math.abs(z + dz) > currentDist;

			if (xShouldTurn || zShouldTurn) {
				if (negate) {
					dx = -dz;
					dz = 0;
				} else {
					dz = dx;
					dx = 0;
				}
				negate = !negate;
			}
		}
		return !islands.isEmpty();
	}
}