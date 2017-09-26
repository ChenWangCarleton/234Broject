import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;


public class Item {
  public enum Category {FRUIT, MEAT}
  static int nextId = 3000;
  int id;
  Category cat;
  String name;
  ArrayList<Float> price;
  String imgName;
  
  /*public Item(String n, int c) {
    name = n;
    id = ++nextId;
    cat = Category.values()[c];  
    imgName = null;
  }*/
  
    public Item(String n, Category c) {
    name = n;
    id = ++nextId;
    cat = c;  
    imgName = null;
  }
  
//getter
  public int getId() {return id;}
  public String getName() {return name;}
  public Category getCategory() {return cat;}
  public float getPrice(int i) {return price.get(i);}
  public ArrayList getAllPrice() {return price;}
  public String getImgName() {return imgName;}
  
//setter  
  public void setName(String n) {name = n;}
  public void setCategory(Category c) {cat = c;}
  public void setPrice(int i, float p) {price.set(i,p);}
  public void setImg(String img) {imgName = img;}
  
//json savd & load
  //encoding:save
  public void write(JSONObject obj){
    obj.put("name", name);
    obj.put("itemID", id);
    obj.put("category", cat.ordinal());
    JSONArray priceArr = new JSONArray();
    /*for (float f : price){
      priceArr.add(f);
    }*/
    obj.put("price", priceArr);
    obj.put("img", imgName);
  }
  
  //decoding:load
  public void read(JSONObject obj){
    name = obj.get("name").toString();
    id = Integer.parseInt(obj.get("itemID").toString());
    cat = Category.values()[Integer.parseInt(obj.get("category").toString())];
    JSONArray priceArr = (JSONArray)obj.get("price");
    for (int i = 0; i < price.size(); i++){
      price.set(i, Float.parseFloat(priceArr.get(i).toString()));
    }
    imgName = obj.get("img").toString();
    
    //name = json["name"].toString();
  }
}