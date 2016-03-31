import java.io.*;
import java.lang.Math;
import java.util.ArrayList;

public class Opt{

	public Opt(String filename, int numframes){
		
		// counters to keep track 
		int memoryaccess = 0;
		int faults = 0;
		int diskwrites = 0;
		
		ArrayList<ArrayList<Integer>> positionArray = new ArrayList<ArrayList<Integer>>();
		
		for (int i = 0; i < 1048576; i++){
			positionArray.add(new ArrayList<Integer>());
		}
		
		//initialize PageTable object
		PageTable pt = new PageTable();
		//Create frame table to store Page objects
		Page [] ft = new Page[numframes];
		
		int numItems = 0;	//keep track of whether frame table is full
		String line = null;
		
		try{
			//use BufferedReader to read in files
			FileReader fr = new FileReader(filename);
			
			BufferedReader br = new BufferedReader(fr);
			
			while((line = br.readLine()) != null){
				//Splits line into address and R/W operation
				String str[] = line.split(" ", 2);
				String addr = str[0];
				String op = str[1];
				
				//calls method to calculate the page number of current address read in
				int pageNum = pt.calculatePage(addr);
				
				positionArray.get(pageNum).add(new Integer(memoryaccess));
				
				memoryaccess++;
			}
			
			fr = new FileReader(filename);
			
			br = new BufferedReader(fr);
			
			PrintWriter writer = new PrintWriter("opt_results_" + filename + ".txt", "UTF-8");
			
			memoryaccess = 0;
			
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
						int maxPos = 0;
						int evictPage = 0;
						boolean evicted = false;
						faults++;
						
						//checks to see if there's a page with no lines left in arraylist
						//if so, evict that page because it won't be needed again
						for (int i = 0; i < ft.length; i++){
							if (positionArray.get(ft[i].getPageNum()).size() == 0){
								evictPage = i;
								evicted = true;
							}
						}
						
						if (!evicted){
							//goes through all pages and removes line references that are less than current line in file
							for (int i = 0; i < ft.length; i++){
								
								ArrayList<Integer> temp = positionArray.get(ft[i].getPageNum());
								Integer in = temp.get(0);
								int j = in.intValue();
								
								//removes line references until current reference is greater than memory access
								while (j < memoryaccess && !evicted){
									
									positionArray.get(ft[i].getPageNum()).remove(0);
									
									//grabs next line reference from arrayList
									//if size is 0, then evict that page
									if (positionArray.get(ft[i].getPageNum()).size() != 0){	
										temp = positionArray.get(ft[i].getPageNum());
										in = temp.get(0);
										j = in.intValue();
									}
									else{
										evicted = true;
										evictPage = i;
									}
								}
							}
						}
						
						//if a page hasnt been evicted yet, find which one is furthest away in file
						if (!evicted){
							for (int i = 0; i < ft.length; i++){
									
								ArrayList<Integer> temp = positionArray.get(ft[i].getPageNum());
								Integer in = temp.get(0);
								int j = in.intValue();
						
								if (j > maxPos){
									maxPos = j;
									evictPage = i;
								}	
							}
						}
						
						//if dirty, have to write to disk. Increments diskwrites counter
						if (ft[evictPage].isDirty()){
							diskwrites++;
							writer.println("page fault -- evict dirty");
						}
						else{
							writer.println("page fault -- evict clean");
						}
						
						pt.setFrameNum(ft[evictPage].getPageNum(), -1);	//set page index in pagetable to -1 in order to show that page is currently not in RAM
						ft[evictPage] = p;								//assign current position in queue to new page that is entering RAM
						pt.setFrameNum(pageNum, evictPage);					//update page index in pagetable for new page in order to tell which index in queue it can be found
						
						
					}
				}
				
			}
			
			System.out.println("Opt");
			System.out.println("Number of frames: " + numframes);
			System.out.println("Total memory accesses: " + memoryaccess);
			System.out.println("Total page faults: " + faults);
			System.out.println("Total writes to disk: " + diskwrites);
			
			writer.println();
			writer.println(filename);
			writer.println("Opt");
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