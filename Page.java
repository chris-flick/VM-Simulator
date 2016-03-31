
public class Page{
	
	private int pageNum;
	private boolean referenced;
	private boolean dirty;
	private boolean [] time;
	
	public Page(int num, boolean dirt){
		pageNum = num;
		referenced = true;
		dirty = dirt;
		time = new boolean [8];
		
		for (int i = 0; i < time.length; i++){
			time[i] = false;
		}
	}
	
	public void setReferenced(boolean bool){
		referenced = bool;
	}
	
	public void setDirty(){
		dirty = true;	
	}
	
	public boolean isDirty(){
		return dirty;
	}
	
	public boolean isReferenced(){
		return referenced;
	}
	
	public int getPageNum(){
		return pageNum;
	}
	
	public boolean [] getTime(){
		return time;
	}
	
	public void shiftTime(){
		boolean [] temp = new boolean [8];
		
		for (int i = 0; i < time.length - 1; i++){
			temp[i + 1] = time[i];
		}
		
		if (referenced){
			temp[0] = true;
		}
		else{
			temp[0] = false;
		}
		
		referenced = false;
		
		time = temp;
	}
}