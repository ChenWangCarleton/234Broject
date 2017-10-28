import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.codehaus.jackson.map.ObjectMapper;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class GeneralSearch{
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String[] storesOrder= {"Loblaws","Independent"};
	public static int id=0;
	public GeneralSearch(String source,String target) throws IOException {
		File f=new File(target);
		PrintWriter fw=new PrintWriter(new FileWriter(f));
		ObjectMapper mapper = new ObjectMapper();
		File jsonInput=new File(source);
		InputStream is;
		is=new FileInputStream(jsonInput);
		JsonReader reader=Json.createReader(is);
		JsonObject itObj=reader.readObject();
		reader.close();		
		for(int x=0;x<categories.length;x++) {
			ArrayList<mainItem>  items=new ArrayList<>();
			 JsonArray itsObj=itObj.getJsonArray(categories[x]);
			 for(JsonValue value: itsObj) {
				 items.add(mapper.readValue(value.toString(), mainItem.class));
			 }
			 if(x==0) {
					fw.println("{\""+categories[x]+"\":[");
			 }
			 else {
					fw.println("\""+categories[x]+"\":[");
			 }
			 fw.flush();
			 for(int y=0;y<items.size();y++) {
				 String toPrint="";
				// String brand=items.get(y).brand==null?"null":"\""+items.get(y).brand+"\"";
				 String description=items.get(y).description==null?"null":"\""+items.get(y).description+"\"";
	            toPrint="{\"productID\":"+items.get(y).productID+",\"name\":\""+items.get(y).name+"\",\"description\":"+description+"}";
				if(y!=items.size()-1) {
					fw.println(toPrint+",");
				}
				else {
					fw.println(toPrint);
				}
				fw.flush();
			 }
			 if(x!=7) {
				 fw.println("],");
			 }
			 else {
				 fw.println("]}");
			 }
			 fw.flush();
		}
	}
	
	public static void main(String args[]) throws IOException {
		GeneralSearch m=new GeneralSearch("D:\\MainJson.json","D:\\GeneralSearch.json");
		
	}
}