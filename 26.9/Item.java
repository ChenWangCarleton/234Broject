public class Item{
	public Item(float p,int s,String n,int i,int c){
		price=p;
		store=s;
		name=n;
		idServer=i;
		category=c;
		idApp=counter++;
	}
	public Item() {
		idApp=counter++;
	}
	private float price;
	private int store;
	private String name;
	private int idApp;
	private static int counter=0;
	private int idServer;
	private int category;
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getStore() {
		return store;
	}
	public void setStore(int store) {
		this.store = store;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIdApp() {
		return idApp;
	}
	public void setIdApp(int idApp) {
		this.idApp = idApp;
	}
	public int getIdServer() {
		return idServer;
	}
	public void setIdServer(int idServer) {
		this.idServer = idServer;
	}
	public String toString() {
		return "Name: "+name+",From: "+store+", idServer: "+idServer+" Category:"+category+" Price: "+price;
	}
	
	
}