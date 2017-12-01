import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
//this class contains three functions used in android client for converting json string from databse to objects. each function is placed in proper place. and they should all use a class called product, that contains all the attrs from itemForGetAll.java and mainItem.java
public class clientSearch{
	public ArrayList<itemForGetAll> readGetAll(String jsonStr) throws  Exception{
	//	ArrayList<itemForGetAll> result=new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<itemForGetAll> result = mapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, itemForGetAll.class));
		return result;
	}
	public mainItem readSearchID(String jsonStr) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		mainItem result=mapper.readValue(jsonStr, mainItem.class);
		return result;
	}
	public ArrayList<mainItem> readProcess(String jsonStr) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<mainItem> result = mapper.readValue(jsonStr, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, mainItem.class));
		return result;
	}
	public static void main(String[] args) throws Exception {
		clientSearch cs=new clientSearch();
		/*String id="D:\\ByID.json";//test for read searchbyid string
		File i=new File(id);
 		String text  = "";
		try{
	 		Scanner fileScanner = new Scanner(i);
	 		while (fileScanner.hasNextLine()) {
	 		    // get the token *once* and assign it to a local variable
	 		    text += fileScanner.nextLine();
	 		}
	 			fileScanner.close();
	 		//	System.out.println(text);
	 	}
		catch(IOException e){
	 			System.out.println("scanner could not open");
	 	}
		mainItem s=cs.readSearchID(text);
		System.out.println(s.toString());*/
		
/*		String all="D:\\GeneralSearch.json";//test for read getall string
		File i=new File(all);
		String text="";
		try{
	 		Scanner fileScanner = new Scanner(i);
	 		while (fileScanner.hasNextLine()) {
	 		    // get the token *once* and assign it to a local variable
	 		    text += fileScanner.nextLine();
	 		}
	 			fileScanner.close();
	 		//	System.out.println(text);
	 	}
		catch(IOException e){
	 			System.out.println("scanner could not open");
	 	}*/
	//	System.out.println(text);
		//String text="{\"productID\":2,\"category\":\"Fruits & Vegetables\",\"name\":\"Red Delicious Apples\",\"brand\":\"President's Choice\",\"stores\":[\"Loblaws\",\"Independent\",null],\"price\":[\"$7.99\",\"$7.99\",null],\"description\":\"3 lb bag\"}";
		//String text="[{\"productID\":5,\"category\":\"Fruits & Vegetables\",\"name\":\"Anjou Pears\",\"brand\":null,\"stores\":[\"Loblaws\",\"Independent\",null],\"price\":[\"$1.32\",\"$4.99\",null],\"description\":null},{\"productID\":100,\"category\":\"Fruits & Vegetables\",\"name\":\"Red Peppers\",\"brand\":null,\"stores\":[\"Loblaws\",\"Independent\",null],\"price\":[\"$2.11\",\"$4.99\",null],\"description\":null},{\"productID\":1000,\"category\":\"Fruits & Vegetables\",\"name\":\"Baby Eggplants (1pack)\",\"brand\":null,\"stores\":[null,\"Independent\",null],\"price\":[null,\"$3.04\",null],\"description\":null},{\"productID\":2000,\"category\":\"Deli & Ready Meals\",\"name\":\"Tomato Olive Croissants\",\"brand\":null,\"stores\":[\"Loblaws\",\"Independent\",null],\"price\":[\"$3.49\",\"$1.99\",null],\"description\":\"220 g\"},{\"productID\":10000,\"category\":\"Drinks\",\"name\":\"Diet Cranberry Low Calorie Beverage\",\"brand\":\"Ocean Spray\",\"stores\":[null,null,\"Walmart\"],\"price\":[null,null,\"$3.33\"],\"description\":\"1.89 L\"}]";
	//	String text="[{\"productID\":100,\"category\":\"Fruits & Vegetables\",\"name\":\"Red Peppers\",\"brand\":null,\"stores\":[\"Loblaws\",\"Independent\",null],\"price\":[\"$2.11\",\"$4.99\",null],\"description\":null}]";
		/*	ArrayList<itemForGetAll> result=cs.readGetAll(text);
		for(int x=0;x<result.size();x++) {
			System.out.println(result.get(x).toString());
		}*/
		ArrayList<itemForGetAll> m=cs.readGetAll(text);
		for(itemForGetAll s:m)System.out.println(s.toString());
	/*	mainItem m=cs.readSearchID(text);
		System.out.println(m.toString());*/
		/*ArrayList<mainItem> m=cs.readProcess(text);
		for(mainItem i:m)
		System.out.println(i.toString());*/

	}
}