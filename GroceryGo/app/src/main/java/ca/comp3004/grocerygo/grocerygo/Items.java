package ca.comp3004.grocerygo.grocerygo;

/**
 * Created by Abdullrhman Aljasser on 2017-10-31.
 */

public class Items{

    public String category="";
    public String price="";
    public String description="";
    public String name="";
    public String store="";
//	public int productID;

    public String toString() {
        //return "Product ID:"+productID+"   category: "+category+"  brand: "+brand+"  name: "+name+"  description: "+description+"    price: "+price;
        return "   category: "+category+"  name: "+name+"  description: "+description+"    price: "+price+"   store:"+store;
    }
}