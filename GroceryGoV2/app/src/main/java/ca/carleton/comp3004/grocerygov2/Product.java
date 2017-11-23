package ca.carleton.comp3004.grocerygov2;

/**
 * Created by Abdullrhman Aljasser on 2017-11-09.
 */

public class Product{
    public int id;
    public String na;
    public String ca;

    public Product(int i, String name, String cat) {
        this.id = i;
        this.ca = cat;
        this.na = name;
    }
    public Product(){
    }
}

