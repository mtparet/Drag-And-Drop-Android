package exp.mtparet.dragdrop.data;

public class OneItem {
	
	private int id;
	private String name;
	
	public OneItem(OneItem op){
		this.id = op.id;
		this.name = op.name;
	}
	
	public OneItem(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	

}
