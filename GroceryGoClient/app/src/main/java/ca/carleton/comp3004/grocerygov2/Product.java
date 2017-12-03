package ca.carleton.comp3004.grocerygov2;

/**
 * Created by Abdullrhman Aljasser on 2017-11-09.
 */

public class Product{
    public int id;
    public String na;
    public String ca;

    public String category="";
    public String[] price=new String[3];
    public String description="";
    public String name="";
    public String[] stores=new String[3];
    public int productID;
    public String brand;
    public String image;

    public Product(int i, String name, String cat) {
        this.id = i;
        this.ca = cat;
        this.na = name;
    }
    public Product(){
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

