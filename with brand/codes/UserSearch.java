import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.codehaus.jackson.map.ObjectMapper;
public class UserSearch {
		String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String source="D:\\MainJsonWithBrand.json";
	String[] storesOrder= {"Loblaws","Independent","Walmart"};
	public String generalSearch() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File jsonInput=new File(source);
		InputStream is;
		ArrayList<itemForGetAll>  Items=new ArrayList<>();
		is=new FileInputStream(jsonInput);
		JsonReader reader=Json.createReader(is);
		JsonObject itObj=reader.readObject();
		reader.close();		
		for(int x=0;x<categories.length;x++) {
			 JsonArray itsObj=itObj.getJsonArray(categories[x]);
			 for(JsonValue value: itsObj) {
				 mainItem e=mapper.readValue(value.toString(), mainItem.class);
				 Items.add(new itemForGetAll(e.productID,e.name,e.category));
			 }

		}
		String result="[";
		 for(int y=0;y<Items.size();y++) {
			 if(y==Items.size()-1) {
				 result=result+"{\"id\":"+Items.get(y).id+",\"na\":\""+Items.get(y).na+"\",\"ca\":\""+Items.get(y).ca+"\"}";
			 }
			 else {
				 result=result+"{\"id\":"+Items.get(y).id+",\"na\":\""+Items.get(y).na+"\",\"ca\":\""+Items.get(y).ca+"\"},";
			 }
		 }
		 result=result+"]";
		 return result;//return the result string
	}

	public String searchByID(int id) throws IOException {
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
						String price="[";
						String store="[";
						for(int i=0;i<storesOrder.length;i++) {
							if(Itemss.get(o).stores[i]!=null) {
								if(i!=storesOrder.length-1) {
									if(Itemss.get(o).price[i]!=null) {
										price=price+"\""+Itemss.get(o).price[i]+"\",";
									}
									else {
										price=price+"null,";
									}
									store=store+"\""+Itemss.get(o).stores[i]+"\",";
								}
								else {
									if(Itemss.get(o).price[i]!=null) {
										price=price+"\""+Itemss.get(o).price[i]+"\"";
									}
									else {
										price=price+"null";
									}
									store=store+"\""+Itemss.get(o).stores[i]+"\"";							
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
						String description=Itemss.get(o).description==null?"null":"\""+Itemss.get(o).description+"\"";
						String brand=Itemss.get(o).brand==null?"null":"\""+Itemss.get(o).brand+"\"";
					 String result="{\"productID\":"+Itemss.get(o).productID+",\"category\":\""+Itemss.get(o).category+"\",\"name\":\""+Itemss.get(o).name+"\",\"brand\":"+brand+",\"stores\":"+store+",\"price\":"+price+",\"description\":"+description+"}";
					 return result;
				 }
			 }
		}
		return null;
	}
	 public static void main(String[] args){
		UserSearch us=new UserSearch();
	/*	try {//getall
			System.out.println(us.generalSearch());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		try {//searchbyid
			System.out.println(us.searchByID(2));
		}catch(Exception e) {
			e.printStackTrace();
		}


	}

}