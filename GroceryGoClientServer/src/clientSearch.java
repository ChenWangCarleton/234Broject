
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

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
		
		String all="D:\\GeneralSearch.json";//test for read getall string
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
	 	}
	//	System.out.println(text);
		ArrayList<itemForGetAll> result=cs.readGetAll(text);
		for(int x=0;x<result.size();x++) {
			System.out.println(result.get(x).toString());
		}
	}
}