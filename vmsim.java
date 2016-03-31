import java.io.*;

public class vmsim{
	public static void main(String [] args){
	
		int refresh = 0;
		String filename = null;
		
		if (args.length != 5 && args.length != 7){
			System.out.println("Incorrect Arguments. Arguments should be "
			+ "\"java vmsim -n <numframes> -a <algorithm> [-r <refresh rate>] <tracefile>\"");
			
			return;
		}
	
		int numframes = Integer.parseInt(args[1]);
		String alg = args[3];
		
		if (args.length == 5){
			filename = args[4];
		}
		else if (args.length == 7){
			refresh = Integer.parseInt(args[5]);
			filename = args[6];
		}
		
		if (alg.toLowerCase().equals("opt")){
			Opt o = new Opt(filename, numframes);
		}
		else if (alg.toLowerCase().equals("clock")){
			Clock c = new Clock(filename, numframes);
		}
		else if (alg.toLowerCase().equals("nru")){
			NRU n = new NRU(filename, numframes, refresh);
		}
		else if (alg.toLowerCase().equals("aging")){
			Aging a = new Aging(filename, numframes, refresh);
		}
	
	}
}