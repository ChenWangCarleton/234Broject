import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
	public static void main(String[] args) {

		ObjectMapper mapper = new ObjectMapper();

		File jsonInput=new File("D:\\LoblawsJson.json");
		InputStream is;
		Set<Items>  Itemss=new HashSet<>();
		try {
			 is=new FileInputStream(jsonInput);
			 JsonReader reader=Json.createReader(is);
			 JsonObject itObj=reader.readObject();
			 reader.close();
			 JsonArray itsObj=itObj.getJsonArray("Bakery");
			 for(JsonValue value: itsObj) {
				 Itemss.add(mapper.readValue(value.toString(), Items.class));
			 }
			
			 String inp="12 Grain Bread, Sliced";
			Set<Items> answer=new HashSet<>();
			for(Items i: Itemss) {
				if(i.name.contains(inp)) {
					answer.add(i);
				}
			}
			for(Items i:answer) {
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