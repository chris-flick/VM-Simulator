import java.io.*;

public class Aging{
	
	private Page [] ft;
	private int numItems = 0;	//keep track of whether frame table is full

	public Aging(String filename, int numframes, int refresh){
		
		// counters to keep track 
		int memoryaccess = 0;
		int faults = 0;
		int diskwrites = 0;
		int time = 0;
		
		if (refresh == 0){
			refresh = 55;
		}
		
		//initialize PageTable object
		PageTable pt = new PageTable();
		//Create frame table to store Page objects
		ft = new Page[numframes];
		
		String line = null;
		
		try{
			//use BufferedReader to read in files
			FileReader fr = new FileReader(filename);
			
			BufferedReader br = new BufferedReader(fr);
			
			PrintWriter writer = new PrintWriter("aging_results_" + filename + ".txt", "UTF-8");
			
			while((line = br.readLine()) != null){
				//Splits line into address and R/W operation
				String str[] = line.split(" ", 2);
				String addr = str[0];
				String op = str[1];
				
				//increment memory accessed counter for each line read in
				memoryaccess++;
				
				//increment time counter after every loop
				time++;
				
				if (time == refresh){
					shiftBits();
					time = 0;
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
						faults++;
						int evictPage = calculateValue();
						
						if (ft[evictPage].isDirty()){
							diskwrites++;
							writer.println("page fault -- evict dirty");
						}
						else{
							writer.println("page fault -- evict clean");
						}
						
						pt.setFrameNum(ft[evictPage].getPageNum(), -1);
						ft[evictPage] = p;
						pt.setFrameNum(pageNum, evictPage);
					}
				}
				
			}
			
			System.out.println("Aging");
			System.out.println("Number of frames: " + numframes);
			System.out.println("Total memory accesses: " + memoryaccess);
			System.out.println("Total page faults: " + faults);
			System.out.println("Total writes to disk: " + diskwrites);
			
			writer.println();
			writer.println(filename);
			writer.println("Aging");
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
	
	public void shiftBits(){
		for (int i = 0; i < numItems; i++){
			ft[i].shiftTime();
		}
	}
	
	public int calculateValue(){
		boolean [] temp;
		int minValue = 999999999;
		int currentValue = 0;
		int evictPage = 0;
		
		int exponent = 7;
		
		for (int i = 0; i < numItems; i++){
			currentValue = 0;
			temp = ft[i].getTime();
			exponent = 7;
			for (int j = 0; j < temp.length; j++){				
				if (temp[j]){
					currentValue += Math.pow(2, exponent);
				}
				exponent--;
			}
			
			if (ft[i].isReferenced()){
				currentValue += Math.pow(2, 8);
			}
			
			if (currentValue < minValue){
				minValue = currentValue;
				evictPage = i;
			}
		}
		
		return evictPage;
	}
}