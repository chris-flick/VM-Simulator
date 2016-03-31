import java.io.*;

public class Clock{
	
	public Clock(String filename, int numframes){
		
		// counters to keep track 
		int memoryaccess = 0;
		int faults = 0;
		int diskwrites = 0;
		
		//initialize PageTable object
		PageTable pt = new PageTable();
		//Create queue of Page objects
		Page [] queue = new Page[numframes];
		
		//hand to keep track of current item in circular queue
		int hand = 0;
		
		String line = null;
		
		try{
			//use BufferedReader to read in files
			FileReader fr = new FileReader(filename);
			
			BufferedReader br = new BufferedReader(fr);
			
			PrintWriter writer = new PrintWriter("clock_results_" + filename + ".txt", "UTF-8");
			
			while((line = br.readLine()) != null){
				//Splits line into address and R/W operation
				String str[] = line.split(" ", 2);
				String addr = str[0];
				String op = str[1];
				
				//increment memory accessed counter for each line read in
				memoryaccess++;
				
				//calls method to calculate the page number of current address read in
				int pageNum = pt.calculatePage(addr);
				
				//checks to see if the current page is already loaded into RAM
				if (pt.checkPageTable(pageNum)){
					//if already loaded, grab the index that it's in the queue in order to update
					int frameIndex = pt.getFrameIndex(pageNum);
					
					//if R operation, set referenced bit to true and update time
					//if W operation, set referenced bit to true, set dirty bit to true, and update timestamp
					if (op.equals("R")){
						queue[frameIndex].setReferenced(true);
					}
					else{
						queue[frameIndex].setReferenced(true);
						queue[frameIndex].setDirty();
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
					
					//if current position in queue is null, that means there is extra space in queue
					if (queue[hand] == null){
				
						queue[hand] = p;				//place page in queue
						pt.setFrameNum(pageNum, hand);	//set value in pagetable to equal its position in the queue
						hand++;							//increment the hand pointer
						faults++;						//increment the faults counter
						
						//if hand == numframes, reset to beginning of queue, thus creating a circular queue
						if (hand == numframes){
							hand = 0;
						}
						
						writer.println("page fault -- no eviction");
					}
					else{
						//this else statement means queue is full, so a page needs to be evicted
						boolean evicted = false;		//created a boolean to declare whether a page has been evicted
						faults++;						//increment faults counter
						
						//will loop until page is evicted
						while (!evicted){
							//checks if current page is referenced
							if (queue[hand].isReferenced()){
								queue[hand].setReferenced(false);		//set referenced to false for page
								hand++;									//increments to next position in queue
								
								//if hand == numframes, reset to beginning of queue, thus creating a circular queue
								if (hand == numframes){
									hand = 0;
								}
							}
							else if(!queue[hand].isReferenced()){
								//entering this else if means an unreferenced page was found
					
								evicted = true;						//set evicted to true to exit loop
								
								//if dirty, have to write to disk. Increments diskwrites counter
								if (queue[hand].isDirty()){
									diskwrites++;
									writer.println("page fault -- evict dirty");
								}
								else{
									writer.println("page fault -- evict clean");
								}
								
								pt.setFrameNum(queue[hand].getPageNum(), -1);	//set page index in pagetable to -1 in order to show that page is currently not in RAM
								queue[hand] = p;								//assign current position in queue to new page that is entering RAM
								pt.setFrameNum(pageNum, hand);					//update page index in pagetable for new page in order to tell which index in queue it can be found
								hand++;											//increment hand to next position in queue
								
								//if hand == numframes, reset to beginning of queue, thus creating a circular queue
								if (hand == numframes){
									hand = 0;
								}
								
							}
						}
					}
				}
			}
			
			System.out.println("\nClock");
			System.out.println("Number of frames: " + numframes);
			System.out.println("Total memory accesses: " + memoryaccess);
			System.out.println("Total page faults: " + faults);
			System.out.println("Total writes to disk: " + diskwrites);
			
			writer.println();
			writer.println(filename);
			writer.println("Clock");
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
}