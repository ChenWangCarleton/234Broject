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

public class mergeToOne{
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String[] storesOrder= {"Loblaws","Independent"};
	public static int id=0;
	public mergeToOne(ArrayList<String> source,String target) throws IOException {
		ArrayList<File> sources=new ArrayList<>();
		ArrayList<InputStream> is=new ArrayList<>();
		ArrayList<JsonReader> reader=new ArrayList<>();
		ArrayList<JsonObject> itObj=new ArrayList<>();
		ArrayList<JsonArray> itsObj=new ArrayList<>();
		ArrayList<mainItem> items=new ArrayList<>();
		File tar=new File(target);
		PrintWriter fw=new PrintWriter(new FileWriter(target));
		//ArrayList<mainItem> itemsToPrint=new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for(int x=0;x<source.size();x++) {
			sources.add(new File(source.get(x)));
			is.add(new FileInputStream(sources.get(x)));
			reader.add(Json.createReader(is.get(x)));
			itObj.add(reader.get(x).readObject());
		}
		for(int y=0;y<categories.length;y++) {
			if(y==0) {
				fw.println("{\""+categories[y]+"\":[");
			}
			else {
				fw.println("\""+categories[y]+"\":[");
			}
			fw.flush();
			for(int x=0;x<itObj.size();x++) {
				itsObj.add(itObj.get(x).getJsonArray(categories[y]));
				for(JsonValue value: itsObj.get(x)) {
					Items temp=mapper.readValue(value.toString(), Items.class);
					//write a compare function
					boolean has=false;
					for(int z=0;z<items.size();z++) {
						if(items.get(z).similiarTo(temp)) {
							has=true;
							int storeId;
							if(temp.store=="Loblaws")storeId=0;
							else storeId=1;
							items.get(z).price[storeId]=temp.price;
							items.get(z).stores[storeId]=temp.store;
						}
					}
					if(!has) {
						items.add(new mainItem(temp,id));
						id++;
					//	itemsToPrint.add(new mainItem(temp,id));
					//	id++;
					}
				}
			}
			for(int x=0;x<items.size();x++) {
				String toPrint="";
				String price="[";
				String store="[";
				for(int i=0;i<2;i++) {
					if(items.get(x).stores[i]!=null) {
						if(i!=1) {
							price=price+"\""+items.get(x).price[i]+"\",";
							store=store+"\""+items.get(x).stores[i]+"\",";
						}
						else {
							price=price+"\""+items.get(x).price[i]+"\"";
							store=store+"\""+items.get(x).stores[i]+"\"";							
						}
					}
					else {
						if(i!=1) {
							price=price+"null,";
							store=store+"null,";
						}
						else {
							price=price+"null";
							store=store+"null";							
						}						
					}
				}
				price+="]";
				store+="]";
				String brand=items.get(x).brand==null?"null":"\""+items.get(x).brand+"\"";
				String quantifier=items.get(x).quantifier==null?"null":"\""+items.get(x).quantifier+"\"";
            	toPrint="{\"productID\":"+items.get(x).productID+",\"category\":\""+items.get(x).category+"\",\"name\":\""+items.get(x).name+"\",\"stores\":"+store+",\"price\":"+price+",\"brand\":"+brand+",\"quantifier\":"+quantifier+"}";
				if(x!=items.size()-1) {
					fw.println(toPrint+",");
				}
				else {
					fw.println(toPrint);
				}
				fw.flush();
				
				if(items.get(x).stores[0]!=null&&items.get(x).stores[1]!=null)
				System.out.println(items.get(x).toString());
			}
			itsObj=new ArrayList<>();
			items=new ArrayList<>();
			if(y!=7)
				fw.println("],");
			else fw.println("]}");
			fw.flush();
		}
		
		
	}
	
	public static void main(String args[]) throws IOException {
		ArrayList<String> source = new ArrayList<>();
		source.add("D:\\LoblawsJson.json");
		source.add("D:\\IndependentJson.json");
		String target="D:\\MainJson.json";
		mergeToOne m=new mergeToOne(source,target);
		
	}
}