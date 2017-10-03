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
import java.nio.file.Files;
import java.nio.file.Paths;


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
    save();
  }
  
  public void removeItem(String n){
    list.remove(searchItem(n));
  }
  
  
  //save
  public void save(){
    JSONArray arr = new JSONArray();
    for(int i = 0; i < list.size(); i++){
      JSONObject obj = new JSONObject();
      list.get(i).write(obj);
      arr.add(obj);
    }
    //save to file
    String s = arr.toString();
    String filePath = "ItemList.json";
    CharBuffer cbuf = null;
    File file = new File(filePath);
    try
    {
      byte [] buff=new byte[]{};  
      FileOutputStream output=new FileOutputStream(filePath);  
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
  
  //load
  public void load(){
    //read from file
    
    String s = null;
    String filePath = "ItemList.json";
    File file = new File(filePath);
    if (!file.exists()) return;
    try
    {
      s = new String(Files.readAllBytes(Paths.get(filePath)));
      
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    JSONParser parser = new JSONParser();
    try
    {
      Object obj = parser.parse(s);
      JSONArray arr = (JSONArray)obj;
      
      for (int i = 0; i < arr.size(); i++){

        Item tempItem = new Item();
        
        tempItem.read((JSONObject)arr.get(i));
        
        list.add(tempItem);
        
      }
    }
    catch (ParseException pe)
    {
      System.out.println("position: " + pe.getPosition());
      System.out.println(pe);
    }
  }
  
  
}