public class Items{
//this is an entity class needed for one of the steps in merging. when the json file created by each webscraper is read using jackson, this class is used to store the data temporarily.
	public String category="";
	public String price="";
	public String description="";
	public String name="";
	public String store="";
//	public int productID;
	public String brand="";
	public String image="";
	public String toString() {
		//return "Product ID:"+productID+"   category: "+category+"  brand: "+brand+"  name: "+name+"  description: "+description+"    price: "+price;
		return "   category: "+category+"  name: "+name+"  description: "+description+"    price: "+price+"     brand:"+brand+"   store:"+store;
	}
}