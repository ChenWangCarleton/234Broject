import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class UserSearch {
	public static void main(String[] args) {

		ObjectMapper mapper = new ObjectMapper();

		File jsonInput=new File("D:\\items.json");
		InputStream is;
		Set<Item>  items=new HashSet<>();
		try {
			 is=new FileInputStream(jsonInput);
			 JsonReader reader=Json.createReader(is);
			 JsonObject itObj=reader.readObject();
			 reader.close();
			 JsonArray itsObj=itObj.getJsonArray("items");
			 for(JsonValue value: itsObj) {
				 items.add(mapper.readValue(value.toString(), Item.class));
			 }
			
			 String inp="milk";
			Set<Item> answer=new HashSet<>();
			for(Item i: items) {
				if(i.getName().contains(inp)) {
					answer.add(i);
				}
			}
			for(Item i:answer) {
				System.out.println(i);
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