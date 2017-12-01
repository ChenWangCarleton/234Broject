//this is a test class for the getALl func. In the android client there is a product class that contains all attrs from this class and all the attrs from the mainItem. this is only a test class now.
public class itemForGetAll{
	public int id;
	public String na;
	public String ca;
	public itemForGetAll(int i,String name,String category) {
		id=i;
		na=name;
		ca=category;
	}
	public String toString() {
		return "productID:"+id+"  name:"+na+"   category:"+ca;
	}
	public itemForGetAll() {
		
	}
}