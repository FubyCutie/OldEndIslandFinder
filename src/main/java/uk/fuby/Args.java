package uk.fuby;

public class Args {
	public int searchDistance;
	public long seed;
	public boolean extraOutput;
	public int startX;
	public int startZ;
	public int startDistance;
	
	public Args(
			int searchDistance,
			long seed,
			boolean extraOutput,
			int startX,
			int startZ,
			int startDistance
	) {
		this.searchDistance = searchDistance;
		this.seed = seed;
		this.extraOutput = extraOutput;
		this.startX = startX;
		this.startZ = startZ;
		this.startDistance = startDistance;
	}
	
	
	@Override
	public String toString() {
		return  "startDistance: " + this.startDistance + "\n" +
				"searchDistance: " + this.searchDistance + "\n" +
				"startX: " + this.startX + "\n" +
				"startZ: " + this.startZ + "\n" +
				"seed: " + this.seed + "\n" +
				"extraOutput: " + this.extraOutput;
	}
	
	public static void showUsage() {
		String usage = "" +
				"Usage: OldEndIslandFinder -s [seed] [-d distance] [-q]\n" +
				"\n" +
				"Help:\n" +
				"  -h, --help      displays this text\n" +
				"\n" +
				"Settings:\n" +
				"  -s, --seed      sets the seed to search on\n" +
				"  -d, --distance  sets the search distance in chunks, diameter\n" +
				"  -r, --radius    similarly sets distance but measured in radius\n" +
				"  -x              chooses the chunk x coordinate to center the search on\n" +
				"  -z              chooses the chunk z coordinate to center the search on\n" +
				"      --start     set the starting distance in chunks, diameter\n" +
				"\n" +
				"Flags:\n" +
				"  -q, --quiet     do not output on each island, only at the end\n" +
				"\n" +
				"Default Values:\n" +
				"  distance        3064\n" +
				"  x               0\n" +
				"  z               0\n" +                                //      don't make lines
				"  start           26\n" +                               //      longer than this
				"  quiet           off\n" +                              //                |
				"\n" +                                                   //                v
				"Note: the noise map nearly repeats after 3064 chunks, this means you can\n" +
				"think of the any given 3064x3064 region as being representative of the\n" +
				"entire world. This isn't precisely true, so a larger search distance\n" +
				"does help find more islands, but it's very unlikely to find any islands\n" +
				"at all if the first 3064 chunks have none.\n" +
				"\n" +
				"Additionally, the noise subtraction hits its minimum of -200 at 200 blocks\n" +
				"away, or about 13 chunks. This means any islands found closer are less\n" +
				"likely to be part of the repeating pattern and in fact can be thought of\n" +
				"as part of the main island.\n" +
				"\n" +
				"A good seed for testing is -1420259651 as it contains many islands\n" +
				"\n" +
				"java -jar OldEndIslandFinder.jar -s -1420259651\n";
		System.out.println(usage);
		System.exit(0);
	}
	
	public static Args parseArgs(String[] rawArgs) {
		int length = rawArgs.length;
		int i = 0; // arg index
		
		// defaults
		Long seed = null;
		int searchDistance = 3064;
		boolean extraOutput = true;
		int x = 0;
		int z = 0;
		int startDistance = 26;
		
		while (i < length) {
			String arg = rawArgs[i];
			if (arg.equals("-h") || arg.equals("--help")) {
				showUsage();
			}
			
			if (arg.equals("-s") || arg.equals("--seed")) {
				if (i++ < length) {
					String rawSeed = rawArgs[i];
					try {
						seed = Long.parseLong(rawSeed);
					} catch (Exception e) {
						throw new RuntimeException("Invalid seed provided.");
					}
				}
			}
			
			if (arg.equals("-d") || arg.equals("--distance")) {
				if (i++ < length) {
					String rawDistance = rawArgs[i];
					try {
						searchDistance = Integer.parseInt(rawDistance);
					} catch (Exception e) {
						throw new RuntimeException("Invalid distance provided.");
					}
				}
			}
			
			if (arg.equals("-r") || arg.equals("--radius")) {
				if (i++ < length) {
					String rawDistance = rawArgs[i];
					try {
						searchDistance = Integer.parseInt(rawDistance) * 2;
					} catch (Exception e) {
						throw new RuntimeException("Invalid distance provided.");
					}
				}
			}
			
			if (arg.equals("--start")) {
				if (i++ < length) {
					String rawStart = rawArgs[i];
					try {
						startDistance = Integer.parseInt(rawStart);
					} catch (Exception e) {
						throw new RuntimeException("Invalid z provided.");
					}
				}
			}
			
			if (arg.equals("-x")) {
				if (i++ < length) {
					String rawX = rawArgs[i];
					try {
						x = Integer.parseInt(rawX);
					} catch (Exception e) {
						throw new RuntimeException("Invalid x provided.");
					}
				}
			}
			
			if (arg.equals("-z")) {
				if (i++ < length) {
					String rawZ = rawArgs[i];
					try {
						z = Integer.parseInt(rawZ);
					} catch (Exception e) {
						throw new RuntimeException("Invalid z provided.");
					}
				}
			}
			
			if (arg.equals("-q") || arg.equals("--quiet")) {
				extraOutput = false;
			}
			
			i++;
		}
		
		if (seed == null) {
			System.out.println("A seed must be provided.");
			showUsage();
		}
		
		return new Args(searchDistance, seed, extraOutput, x, z, startDistance);
	}
	
}
