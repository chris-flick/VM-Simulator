import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class NRU{
	
	private Page [] ft;

	public NRU(String filename, int numframes, int refresh){
		
		// counters to keep track 
		int memoryaccess = 0;
		int faults = 0;
		int diskwrites = 0;
		int time = 0;
		int prevtime = 0;
		
		if (refresh == 0){
			refresh = 55;
		}
		
		
		//initialize PageTable object
		PageTable pt = new PageTable();
		//Create frame table to store Page objects
		ft = new Page[numframes];
		
		
		int numItems = 0;	//keep track of whether frame table is full
		String line = null;
		
		
		try{
			//use BufferedReader to read in files
			FileReader fr = new FileReader(filename);
			
			BufferedReader br = new BufferedReader(fr);
			
			PrintWriter writer = new PrintWriter("nru_results_" + filename + ".txt", "UTF-8");
			
			while((line = br.readLine()) != null){
				//Splits line into address and R/W operation
				String str[] = line.split(" ", 2);
				String addr = str[0];
				String op = str[1];
				
				//increment memory accessed counter for each line read in
				memoryaccess++;
				
				//increment time counter after every loop
				time++;
				
				if ((time - prevtime) >= refresh){
					resetReferenced(numItems);
					prevtime = time;
				}
				
				//calls method to calculate the page number of current address read in
				int pageNum = pt.calculatePage(addr);
				
				//checks to see if the current page is already loaded into RAM
				if (pt.checkPageTable(pageNum)){
					//if already loaded, grab the index that it's in the frame table in order to update
					int frameIndex = pt.getFrameIndex(pageNum);
					
					//if R operation, set referenced bit to true and update timestamp
					//if W operation, set referenced bit to true, set dirty bit to true, and update timestamp
					if (op.equals("R")){
						ft[frameIndex].setReferenced(true);
					}
					else{
						ft[frameIndex].setReferenced(true);
						ft[frameIndex].setDirty();
					}
					
					writer.println("hit");
				}
				else{
					//else statement means that page was not found in RAM
					
					//creates page object based on whether it's a read or write operation
					Page p = null;
					
					if (op.equals("R")){
						p = new Page(pageNum, false);
					}
					else{
						p = new Page(pageNum, true);
					}
					
					if (numItems != numframes){
						ft[numItems] = p;
						pt.setFrameNum(pageNum, numItems);	//set value in pagetable to equal its position in the queue
						numItems++;
						faults++;
						writer.println("page fault -- no eviction");
					}
					else{
						boolean evicted = false;
						ArrayList<Integer> al = new ArrayList<Integer>(numframes);
						faults++;
							
						//checks for a page that has referenced = 0 and dirty = 0
						for (int i = 0; i < numframes; i++){
							if (!ft[i].isReferenced() && !ft[i].isDirty()){
								Integer in = new Integer(i);
								al.add(in);
								evicted = true;
								
							}
						}
						
						//if evicted, replace page in frame table
						if (evicted){
							Random rand = new Random();
							Integer in = al.get(rand.nextInt(al.size()));
							int i = in.intValue();
						
							pt.setFrameNum(ft[i].getPageNum(), -1);
							ft[i] = p;
							pt.setFrameNum(pageNum, i);
							writer.println("page fault -- evict clean");
						}
						
						
							
						//checks for page that has referenced = 0 and dirty = 1
						if (!evicted){
							for (int i = 0; i < numframes; i++){
								if (!ft[i].isReferenced() && ft[i].isDirty()){						
									Integer in = new Integer(i);
									al.add(in);
									evicted = true;
									diskwrites++;
									break;
								}
							}
							
							//if evicted, replace page in frame table
							if (evicted){
								Random rand = new Random();
								Integer in = (al.get(rand.nextInt(al.size())));
								int i = in.intValue();
							
								pt.setFrameNum(ft[i].getPageNum(), -1);
								ft[i] = p;
								pt.setFrameNum(pageNum, i);
								writer.println("page fault -- evict dirty");
							}
						}
						
						//checks for page that has referenced = 1 and dirty = 0	
						if (!evicted){
							for (int i = 0; i < numframes; i++){
								if (ft[i].isReferenced() && !ft[i].isDirty()){
			
									Integer in = new Integer(i);
									al.add(in);
									evicted = true;
									break;
								}
							}
							
							//if evicted, replace page in frame table
							if (evicted){
								Random rand = new Random();
								Integer in = (al.get(rand.nextInt(al.size())));
								int i = in.intValue();
							
								pt.setFrameNum(ft[i].getPageNum(), -1);
								ft[i] = p;
								pt.setFrameNum(pageNum, i);
								writer.println("page fault -- evict clean");
							}
						}
							
						//checks for page that has referenced = 1 and dirty = 1
						if (!evicted){
							for (int i = 0; i < numframes; i++){
								if (ft[i].isReferenced() && ft[i].isDirty()){	
									Integer in = new Integer(i);
									al.add(in);
									evicted = true;		
									diskwrites++;
									break;
								}
							}
							
							//if evicted, replace page in frame table
							if (evicted){
								Random rand = new Random();
								Integer in = (al.get(rand.nextInt(al.size())));
								int i = in.intValue();
							
								pt.setFrameNum(ft[i].getPageNum(), -1);
								ft[i] = p;
								pt.setFrameNum(pageNum, i);
								writer.println("page fault -- evict dirty");
							}
						}
					}
					
				}
				
				
			}
			
			

			System.out.println("NRU");
			System.out.println("Number of frames: " + numframes);
			System.out.println("Total memory accesses: " + memoryaccess);
			System.out.println("Total page faults: " + faults);
			System.out.println("Total writes to disk: " + diskwrites);
			
			writer.println();
			writer.println(filename);
			writer.println("NRU");
			writer.println("Number of frames: " + numframes);
			writer.println("Total memory accesses: " + memoryaccess);
			writer.println("Total page faults: " + faults);
			writer.println("Total writes to disk: " + diskwrites);
			
			writer.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file '" + filename + "'");
		}
		catch(IOException ex){
			System.out.println("Error reading file");
		}
	}
	
	public void resetReferenced(int numItems){
		for (int i = 0; i < numItems; i++){
			ft[i].setReferenced(false);
		}
	}
}