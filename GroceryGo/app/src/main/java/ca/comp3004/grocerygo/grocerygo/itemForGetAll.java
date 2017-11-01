package ca.comp3004.grocerygo.grocerygo;

/**
 * Created by Abdullrhman Aljasser on 2017-10-31.
 */

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
