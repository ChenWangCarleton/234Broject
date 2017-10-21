import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
public class UserSearch {
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String source="D:\\MainJson.json";
	public void generalSearch(String target) throws IOException {
		File f=new File(target);
		PrintWriter fw=new PrintWriter(new FileWriter(f));
		ObjectMapper mapper = new ObjectMapper();
		File jsonInput=new File(source);
		InputStream is;
		ArrayList<mainItem>  Itemss=new ArrayList<>();
		is=new FileInputStream(jsonInput);
		JsonReader reader=Json.createReader(is);
		JsonObject itObj=reader.readObject();
		reader.close();		
		for(int x=0;x<categories.length;x++) {
			 JsonArray itsObj=itObj.getJsonArray(categories[x]);
			 for(JsonValue value: itsObj) {
				 Itemss.add(mapper.readValue(value.toString(), mainItem.class));
			 }
		}
	}
	public ArrayList<mainItem> searchByName(String name){
		ArrayList<mainItem> result=new ArrayList<>();
		return result;
	}
	public mainItem searchByID(int id) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File jsonInput=new File(source);
		InputStream is;
		is=new FileInputStream(jsonInput);
		JsonReader reader=Json.createReader(is);
		JsonObject itObj=reader.readObject();
		reader.close();		
		for(int x=0;x<categories.length;x++) {
			ArrayList<mainItem>  Itemss=new ArrayList<>();

			 JsonArray itsObj=itObj.getJsonArray(categories[x]);
			 for(JsonValue value: itsObj) {
				 Itemss.add(mapper.readValue(value.toString(), mainItem.class));
			 }
			 for(int o=0;o<Itemss.size();o++) {
				 if(id==Itemss.get(o).productID) {
					 return Itemss.get(o);
				 }
			 }
		}
		return null;
	}
	public static void main(String[] args) {
		UserSearch us=new UserSearch();
		try {
			us.generalSearch( "D:\\GeneralSearch.json");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			System.out.println(us.searchByID(3242).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}