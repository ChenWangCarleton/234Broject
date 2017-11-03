
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
	String[] storesOrder= {"Loblaws","Independent","Walmart"};
	public static int id=0;
	public mergeToOne(ArrayList<String> source,String target) throws IOException {
		ArrayList<File> sources=new ArrayList<>();
		ArrayList<InputStream> is=new ArrayList<>();
		ArrayList<JsonReader> reader=new ArrayList<>();
		ArrayList<JsonObject> itObj=new ArrayList<>();
		ArrayList<JsonArray> itsObj=new ArrayList<>();
		ArrayList<mainItem> items=new ArrayList<>();
		File tar=new File(target);
		PrintWriter fw=new PrintWriter(new FileWriter(tar));
		//ArrayList<mainItem> itemsToPrint=new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for(int x=0;x<source.size();x++) {
			sources.add(new File(source.get(x)));
			is.add(new FileInputStream(sources.get(x)));
			reader.add(Json.createReader(is.get(x)));
			try {
			itObj.add(reader.get(x).readObject());
			}catch(Exception e) {
				System.out.println(x+"    "+source.get(x));
			}
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
							int storeId=0;
							for(int l=0;l<storesOrder.length;l++) {
								if(temp.store.equals(storesOrder[l])) {
									storeId=l;
								}
							}
							if(!items.get(z).toString().contains(temp.store)) {
								items.get(z).price[storeId]=temp.price;
								items.get(z).stores[storeId]=temp.store;
							}
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
				for(int i=0;i<storesOrder.length;i++) {
					if(items.get(x).stores[i]!=null) {
						if(i!=storesOrder.length-1) {
							if(items.get(x).price[i]!=null) {
								price=price+"\""+items.get(x).price[i]+"\",";
							}
							else {
								price=price+"null,";
							}
							store=store+"\""+items.get(x).stores[i]+"\",";
						}
						else {
							if(items.get(x).price[i]!=null) {
								price=price+"\""+items.get(x).price[i]+"\"";
							}
							else {
								price=price+"null";
							}
							store=store+"\""+items.get(x).stores[i]+"\"";							
						}
					}
					else {
						if(i!=storesOrder.length-1) {
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
				//String brand=items.get(x).brand==null?"null":"\""+items.get(x).brand+"\"";
				String description=items.get(x).description==null?"null":"\""+items.get(x).description+"\"";
            	toPrint="{\"productID\":"+items.get(x).productID+",\"category\":\""+items.get(x).category+"\",\"name\":\""+items.get(x).name+"\",\"stores\":"+store+",\"price\":"+price+",\"description\":"+description+"}";
				if(x!=items.size()-1) {
					fw.println(toPrint+",");
				}
				else {
					fw.println(toPrint);
				}
				fw.flush();
				
			//	if(items.get(x).stores[0]!=null&&items.get(x).stores[1]!=null)
				System.out.println(items.get(x).toString());
			}
			itsObj=new ArrayList<>();
			items=new ArrayList<>();
			if(y!=7)
				fw.println("],");
			else fw.println("]}");
			fw.flush();
		}
		
		fw.close();
	}
	
	public static void main(String args[]) throws IOException {
		ArrayList<String> source = new ArrayList<>();
		source.add("D:\\LoblawsJson.json");
		source.add("D:\\IndependentJson.json");
		source.add("D:\\WalmartJson.json");
		String target="D:\\MainJson.json";
		mergeToOne m=new mergeToOne(source,target);
		
	}
}