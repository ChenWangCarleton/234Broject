import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.LinkedList;

import java.nio.CharBuffer;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.FileReader;  
import java.io.FileNotFoundException;  
import java.io.IOException;


public class ItemList {
  public LinkedList<Item> list = new LinkedList<Item>();
  JSONObject jobj = new JSONObject();
  
  //getters
  public int getSize(){return list.size();}
  public LinkedList<Item> getList(){return list;}
  //member functions
  public Item searchItem(String n)
  {;
    
    for (int index = 0;index < list.size();index++){
      if(list.get(index).getName().equals(n)){
       return list.get(index); 
      }
    }
    return null;
  }
  
  public void addItem(String n, Item.Category c){
    list.add(new Item(n,c));
  }
  
  public void removeItem(String n){
    list.remove(searchItem(n));
  }
  
  
  //save & load
  public void save(){
    JSONArray arr = new JSONArray();
    for(int i = 0; i < list.size(); i++){
      JSONObject obj = new JSONObject();
      list.get(i).write(obj);
      arr.add(obj);
    }
    //save to file
    String s = arr.toString();
    String filePath = "/Users/YaminoCastle/Desktop/3004/test code/ItemList.json";
    CharBuffer cbuf = null;
    File file = new File(filePath);
    try
    {
      byte [] buff=new byte[]{};  
      FileOutputStream output=new FileOutputStream("/Users/YaminoCastle/Desktop/3004/test code/ItemList.json");  
      buff=s.getBytes();  
      output.write(buff, 0, buff.length);  
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  
}