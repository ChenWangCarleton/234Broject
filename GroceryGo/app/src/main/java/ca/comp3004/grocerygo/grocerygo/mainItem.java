package ca.comp3004.grocerygo.grocerygo;

/**
 * Created by Abdullrhman Aljasser on 2017-10-31.
 */

public class mainItem {

    public String category="";
    public String[] price=new String[3];
    public String description="";
    public String name="";
    public String[] stores=new String[3];
    public int productID;
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
            if(match(name, i.name)) {
                if(match(description,i.description)) {
                    return true;
                }
            }
        }
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
    public String toString() {
        return "Product ID:"+productID+"   category: "+category+"  name: "+name+"  description: "+description+printStorePrice();
        //return "   category: "+category+"  brand: "+brand+"  name: "+name+"  description: "+description+"    price: "+price.toString()+"  stores:"+stores.toString();
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
}