package ca.comp3004.grocerygo.grocerygo;

/**
 * Created by AyeJay on 10/8/2017.
 */

public class Product {
    private static int globalID;
    private int _id;
    private String _productName;
    private int _productID;

    //Empty Const
    public Product(){
        this._productName = null;
        this._productID = 0;
        this._id = -1; //ERROR
    }

    public Product(String productName) {
        this._id = globalID;
        this._productName = productName;
        this._productID = 0;
        globalID++;
    }
    public Product(String productName, int productID){
        this._id = globalID;
        this._productName = productName;
        this._productID = productID;
        globalID++;
    }

    public void set_productName(String _productName) {
        this._productName = _productName;
    }

    public int get_id() {
        return _id;
    }

    public String get_productName() {
        return _productName;
    }

    public int get_productID() {
        return _productID;
    }

    public void set_productID(int _productID) {
        this._productID = _productID;
    }

}
