public class FrameTable{
	
	private PageTable [] frameTable;
	
	public FrameTable(int numframes){
		frameTable = new PageTable[numframes];
	}
	
	public PageTable [] getFrameTable(){
		return frameTable;
	}
	
	public int calculatePage(String addr){
		long decimal = Long.parseLong(addr, 16);
		int pageNum = (int) (decimal/(4096));
		
		return pageNum;
	}

}