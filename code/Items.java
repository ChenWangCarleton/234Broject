public class Items{

	public String category="";
	public String brand="";
	public String price="";
	public String quantifier="";
	public String name="";
	public String store="";
//	public int productID;
	public boolean similiarTo(Items i) {
		if(this.category.equals(i.category)) {
			if(this.brand==null&&i.brand==null) {
				if(this.quantifier==null&&i.quantifier==null) {		
					String[] thisI=this.name.split(" ");
					String[] iI=i.name.split(" ");
					int count=0;
					int half=0;
					if(thisI.length<iI.length) {
						half=(int) Math.floor(thisI.length/2.0);
						for(int x=0;x<thisI.length;x++) {
							if(i.name.contains(thisI[x]))count++;
						}
					}
					else {
						half=(int) Math.floor(iI.length/2.0);
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
							half=(int) Math.floor(thisI.length/2.0);
							for(int x=0;x<thisI.length;x++) {
								if(i.name.contains(thisI[x]))count++;			
								}
						}
						else {
							half=(int) Math.floor(iI.length/2.0);
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
								half=(int) Math.floor(thisI.length/2.0);
								for(int x=0;x<thisI.length;x++) {
									if(i.name.contains(thisI[x]))count++;
								}
							}
							else {
								half=(int) Math.floor(iI.length/2.0);
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
									half=(int) Math.floor(thisI.length/2.0);
									for(int x=0;x<thisI.length;x++) {
										if(i.name.contains(thisI[x]))count++;
									}
								}
								else {
									half=(int) Math.floor(iI.length/2.0);
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
	public String toString() {
		//return "Product ID:"+productID+"   category: "+category+"  brand: "+brand+"  name: "+name+"  quantifier: "+quantifier+"    price: "+price;
		return "   category: "+category+"  brand: "+brand+"  name: "+name+"  quantifier: "+quantifier+"    price: "+price+"   store:"+store;
	}
}