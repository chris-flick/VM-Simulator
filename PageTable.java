import java.lang.Math;

public class PageTable{
	
	private int [] pageTable;
	
	public PageTable(){
		pageTable = new int[(int) Math.pow(2, 20)];
		for (int i = 0; i < pageTable.length; i++){
			pageTable[i] = -1;
		}
	}
	
	public int [] getPageTable(){
		return pageTable;
	}
	
	public boolean checkPageTable(int pageNum){
		if (pageTable[pageNum] != -1)
			return true;
		else
			return false;
	}
	
	public void setFrameNum(int pageNum, int frameIndex){
		pageTable[pageNum] = frameIndex;
	}
	
	public int getFrameIndex(int pageNum){
		return pageTable[pageNum];
	}
	
	public int calculatePage(String addr){
		long decimal = Long.parseLong(addr, 16);
		int pageNum = (int) (decimal/(4096));
		
		return pageNum;
	}
}