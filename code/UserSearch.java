import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	public static void main(String[] args) {

		ObjectMapper mapper = new ObjectMapper();

		File jsonInput=new File("D:\\LoblawsJson.json");
		InputStream is;
		ArrayList<Items>  Itemss=new ArrayList<>();
		try {
			 is=new FileInputStream(jsonInput);
			 JsonReader reader=Json.createReader(is);
			 JsonObject itObj=reader.readObject();
			 reader.close();
			 JsonArray itsObj=itObj.getJsonArray("Deli & Ready Meals");
			 for(JsonValue value: itsObj) {
				 Itemss.add(mapper.readValue(value.toString(), Items.class));
			 }
			
			 String inp="Spreadable Cheddar";
			 Integer id=907;
			ArrayList<Items> answer=new ArrayList<>();
			for(Items i: Itemss) {
			//	System.out.println(i.toString());
				if(i.name.equals(inp)) {
					answer.add(i);
				}
				if(id.equals(i.productID))answer.add(i);
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