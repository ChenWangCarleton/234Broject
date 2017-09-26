import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
public class GeneralSearch {
	public static void main(String[] args) {

		ObjectMapper mapper = new ObjectMapper();

		File jsonInput=new File("D:\\items.json");
		InputStream is;
		Item[] items=new Item[7];
		int x=0;
		try {
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			// Convert JSON string from file to Object
			 is=new FileInputStream(jsonInput);
			 JsonReader reader=Json.createReader(is);
			 JsonObject itObj=reader.readObject();
			 reader.close();
			 JsonArray itsObj=itObj.getJsonArray("items");
			 for(JsonValue value: itsObj) {
				 items[x]=mapper.readValue(value.toString(), Item.class);
				 System.out.println(items[x]);
				 x++;
				
				 
			 }
			 

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}