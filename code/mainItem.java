import java.util.ArrayList;

public class mainItem{

	public String category="";
	public String brand="";
	public String[] price=new String[2];
	public String quantifier="";
	public String name="";
	public String[] stores=new String[2];
	public int productID;
	public mainItem(Items i,int id) {
		int temp;
		if(i.store.equals("Loblaws"))temp=0;
		else temp=1;
		category=i.category;
		brand=i.brand;
		quantifier=i.quantifier;
		name=i.name;
		productID=id;
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
	public boolean similiarTo(Items i) {
		if(this.category.equals(i.category)) {
			if(this.brand==null&&i.brand==null) {
				if(this.quantifier==null&&i.quantifier==null) {		
					String[] thisI=this.name.split(" ");
					String[] iI=i.name.split(" ");
					int count=0;
					int half=0;
					if(thisI.length<iI.length) {
						half=(int) Math.floor(thisI.length/1.0);
						for(int x=0;x<thisI.length;x++) {
							if(i.name.contains(thisI[x]))count++;
						}
					}
					else {
						half=(int) Math.floor(iI.length/1.0);
						for(int x=0;x<iI.length;x++) {
							if(this.name.contains(iI[x]))count++;
						}
					}
					if(count>=half)return true;
				}
				else if(this.quantifier!=null&&i.quantifier!=null) {
					if(this.quantifier.equals(i.quantifier)) {
						String[] thisI=this.name.split(" ");
						String[] iI=i.name.split(" ");
						int count=0;
						int half=0;
						if(thisI.length<iI.length) {
							half=(int) Math.floor(thisI.length/1.0);
							for(int x=0;x<thisI.length;x++) {
								if(i.name.contains(thisI[x]))count++;			
								}
						}
						else {
							half=(int) Math.floor(iI.length/1.0);
							for(int x=0;x<iI.length;x++) {
								if(this.name.contains(iI[x]))count++;
							}
						}
						if(count>=half)return true;
						}
					}
				}
				else if(this.brand!=null&&i.brand!=null){					
					if(this.brand.equals(i.brand)) {
						if(this.quantifier==null&&i.quantifier==null) {			
							String[] thisI=this.name.split(" ");
							String[] iI=i.name.split(" ");
							int count=0;
							int half=0;
							if(thisI.length<iI.length) {
								half=(int) Math.floor(thisI.length/1.0);
								for(int x=0;x<thisI.length;x++) {
									if(i.name.contains(thisI[x]))count++;
								}
							}
							else {
								half=(int) Math.floor(iI.length/1.0);
								for(int x=0;x<iI.length;x++) {
									if(this.name.contains(iI[x]))count++;
								}
							}
							if(count>=half)return true;
						}
						else if(this.quantifier!=null&&i.quantifier!=null) {
							if(this.quantifier.equals(i.quantifier)) {
								String[] thisI=this.name.split(" ");
								String[] iI=i.name.split(" ");
								int count=0;
								int half=0;
								if(thisI.length<iI.length) {
									half=(int) Math.floor(thisI.length/1.0);
									for(int x=0;x<thisI.length;x++) {
										if(i.name.contains(thisI[x]))count++;
									}
								}
								else {
									half=(int) Math.floor(iI.length/1.0);
									for(int x=0;x<iI.length;x++) {
										if(this.name.contains(iI[x]))count++;
									}
								}
								if(count>=half)return true;
							}
						}
					}
				}
			}
		return false;
	}
	public String printStorePrice() {
		String str="";
		for(int x=0;x<2;x++) {
			if(stores[x]!=null) {
				str=str+"   store:"+stores[x]+"    price: "+price[x];
			}
		}
		return str;
	}
	public String toString() {
		return "Product ID:"+productID+"   category: "+category+"  brand: "+brand+"  name: "+name+"  quantifier: "+quantifier+printStorePrice();
		//return "   category: "+category+"  brand: "+brand+"  name: "+name+"  quantifier: "+quantifier+"    price: "+price.toString()+"  stores:"+stores.toString();
	}
}