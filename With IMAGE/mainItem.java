import java.util.ArrayList;

public class mainItem{

	public String category="";
	public String[] price=new String[3];
	public String description="";
	public String name="";
	public String[] stores=new String[3];
	public int productID;
	public String brand="";
	public String image="";
	public mainItem() {
		
	}
	public mainItem(Items i,int id) {
		String[] storesOrder= {"Loblaws","Independent","Walmart"};
		int temp;
		for(temp=0;temp<storesOrder.length;temp++) {
			if(i.store.equals(storesOrder[temp])) {
				break;
			}
		}
		if(temp<0||temp>=storesOrder.length) {
			System.out.println("An ERROR occured in mainItem");
			temp=2;
		}
		category=i.category;
		description=i.description;
		name=i.name;
		brand=i.brand;
		productID=id;
		image=i.image;
		for(int x=0;x<stores.length;x++) {
			if(x==temp) {
				price[x]=i.price;
				stores[x]=i.store;
			}
			else {
				price[x]=null;
				stores[x]=null;
			}
		}
	}
	public mainItem(int id) {
		productID=id;
	}
	public boolean match(String one, String two) {
		if(one==null&&two==null)return true;
		if((one==null&&two!=null)||(one!=null&&two==null))return false;
		if(one.contains(two)||two.contains(one)) {
			return true;
		}
		String[] oneA=one.split(" ");
		String[] twoA=two.split(" ");
		if(oneA.length<twoA.length) {
			int count=0;
			int portion=(int)Math.floor(oneA.length/1.0);//can be modified to fulfill the need of loosely exact or strictly exact
			for(int x=0;x<oneA.length;x++) {
				if(two.contains(oneA[x])) {
					count++;
				}
			}
			if(count>=portion)return true;
		}
		else {
			int count=0;
			int portion=(int)Math.floor(twoA.length/1.0);
			for(int x=0;x<twoA.length;x++) {
				if(one.contains(twoA[x])) {
					count++;
				}
			}
			if(count>=portion)return true;			
		}
		return false;
	}
	public boolean similiarTo(Items i) {
		
		if(category.equals(i.category)) {
			
			
			//if(match(name, i.name)) {
				if(brandMatch(i)) {//before  19689   after 21936   i&w 375
					if(nameMatch(i)) {//after 21864    i&w 387
					//	if(match(description,i.description)) {
					//	if(descriptionMatch(i))	{//after 21849   i&w  393     //if ignore descriptionMatch  18705   658
						if(!sameStore(i))//after sameStore   24326    i&w  1340
							return true;
					//	}
						//}
					}
				}
		//	}
		}
		return false;
	}
	public boolean sameStore(Items i) {
		int ind=-1;
		if(i.store.equals("Loblaws"))ind=0;
		if(i.store.equals("Independent"))ind=1;
		if(i.store.equals("Walmart"))ind=2;
		if(ind>=0)
			if(stores[ind]==null) {
				return false;
			}
		return true;
	}
	public boolean descriptionMatch(Items i) {
		if(description==null&&i.description==null)return true;
		if((description==null&&i.description!=null)||(description!=null&&i.description==null))return false;
		if(description.contains(i.description)||i.description.contains(description)||description.contains(i.description.toLowerCase())||i.description.contains(description.toLowerCase())||description.contains(i.description.toUpperCase())||i.description.contains(description.toUpperCase())) {
			return true;
		}
		String[] oneA=description.split(" ");
		String[] twoA=i.description.split(" ");
		if(oneA.length<twoA.length) {
			int count=0;
			int portion=(int)Math.floor(oneA.length/1.0);//can be modified to fulfill the need of loosely exact or strictly exact
			for(int x=0;x<oneA.length;x++) {
				if(i.description.contains(oneA[x])||i.description.contains(oneA[x].toLowerCase())||i.description.contains(oneA[x].toUpperCase())) {
					count++;
				}
			}
			if(count>=portion)return true;
		}
		else {
			int count=0;
			int portion=(int)Math.floor(twoA.length/1.0);
			for(int x=0;x<twoA.length;x++) {
				if(description.contains(twoA[x])||description.contains(twoA[x].toLowerCase())||description.contains(twoA[x].toUpperCase())) {
					count++;
				}
			}
			if(count>=portion)return true;			
		}
		return false;
	}
	public boolean nameMatch(Items i) {
		if(name==null&&i.name==null)return true;
		if((name==null&&i.name!=null)||(name!=null&&i.name==null))return false;
		if(name.contains(i.name)||i.name.contains(name)||name.contains(i.name.toLowerCase())||i.name.contains(name.toLowerCase())||name.contains(i.name.toUpperCase())||i.name.contains(name.toUpperCase())) {
			return true;
		}
		String[] oneA=name.split(" ");
		String[] twoA=i.name.split(" ");
		if(oneA.length<twoA.length) {
			int count=0;
			int portion=(int)Math.floor(oneA.length/1.0);//can be modified to fulfill the need of loosely exact or strictly exact
			for(int x=0;x<oneA.length;x++) {
				if(i.name.contains(oneA[x])||i.name.contains(oneA[x].toLowerCase())||i.name.contains(oneA[x].toUpperCase())) {
					count++;
				}
			}
			if(count>=portion)return true;
		}
		else {
			int count=0;
			int portion=(int)Math.floor(twoA.length/1.0);
			for(int x=0;x<twoA.length;x++) {
				if(name.contains(twoA[x])||name.contains(twoA[x].toLowerCase())||name.contains(twoA[x].toUpperCase())) {
					count++;
				}
			}
			if(count>=portion)return true;			
		}
		return false;
	}
	public boolean brandMatch(Items i) {
		if(brand==null||brand.length()==0||brand.equals("null")) {
			if(i.brand==null||i.brand.length()==0||i.brand.equals("null"))
				return true;
			else {
				return false;
			}
		}
		if(i.brand==null||i.brand.length()==0||i.brand.equals("null"))
			return false;
		
		if(brand.equalsIgnoreCase(i.brand))
			return true;
		
		return false;
	}
	public String printStorePrice() {
		String str="";
		for(int x=0;x<3;x++) {
			if(stores[x]!=null) {
				str=str+"   store:"+stores[x]+"    price: "+price[x];
			}
		}
		return str;
	}
	public double[] getPrices(){
		double[] prices=new double[price.length];
		for(int x=0;x<price.length;x++){
			if(price[x]==null){
				prices[x]=-1;
			}
			else{
				prices[x]=Double.parseDouble(price[x].substring(1,price[x].length()));
			}
		}
		return prices;
		
	}
	public String toString() {
		return "Product ID:"+productID+"   category: "+category+"  name: "+name+"  description: "+description+"		brand:"+brand+printStorePrice();
		//return "   category: "+category+"  brand: "+brand+"  name: "+name+"  description: "+description+"    price: "+price.toString()+"  stores:"+stores.toString();
	}
}