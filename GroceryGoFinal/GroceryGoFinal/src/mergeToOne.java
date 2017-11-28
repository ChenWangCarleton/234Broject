import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class mergeToOne{
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String[] storesOrder= {"Loblaws","Independent","Walmart"};
	String target;
	ArrayList<String> source;
	boolean dataCollected=false;
	public static int id=0;
	public mergeToOne(ArrayList<String> sou,String tar) {
		this.target=tar;
		this.source=sou;
	}
	public void collectData() throws Exception {
		dataCollected=false;
		Loblaws lob=new Loblaws();
		Independent ind=new Independent();
		Walmart wal=new Walmart();
		Thread thread = new Thread(new Runnable() {

		    @Override
		    public void run() {
		    	do {
					try {
						ind.execute(source.get(1));
					}catch(Exception e) {
					//	if(ind.forP!=null)ind.forP.quit();
					//	if(ind.web!=null)ind.web.quit();
					//	if(ind.getFirst!=null)ind.getFirst.quit();
						e.printStackTrace();
					}
				}while(!Independent.status); 
		    }
		            
		});
		        
		thread.start();
		Thread thread1 = new Thread(new Runnable() {

		    @Override
		    public void run() {
				do {
					try {
						wal.execute(source.get(2));
					}catch(Exception e) {
						if(wal.ABdriver!=null)wal.ABdriver.quit();
						if(wal.tempDri!=null)wal.tempDri.quit();
						if(wal.dri!=null)wal.dri.quit();
						if(wal.driver!=null)wal.driver.quit();
						e.printStackTrace();
					}
				}while(!Walmart.status);      
		    }
		            
		});
		        
		thread1.start();
		Thread thread2 = new Thread(new Runnable() {

		    @Override
		    public void run() {
				do {
					try {
						lob.execute(source.get(0));
					}catch(Exception e) {
						if(lob.web!=null)lob.web.close();
						e.printStackTrace();
					}
				}while(!Loblaws.status);  
		    }
		            
		});
		        
		thread2.start();

		while(!Walmart.status||!Independent.status||!Loblaws.status) {
			TimeUnit.MINUTES.sleep(5);
		}
		dataCollected=true;
	}
	public void merge() throws IOException {
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
					if(temp.price!=null&&temp.price.startsWith("$999"))
						break;
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
				String brand=items.get(x).brand==null?"null":"\""+items.get(x).brand+"\"";
            	toPrint="{\"productID\":"+items.get(x).productID+",\"category\":\""+items.get(x).category+"\",\"name\":\""+items.get(x).name+"\",\"brand\":"+brand+",\"stores\":"+store+",\"price\":"+price+",\"description\":"+description+",\"image\":\""+items.get(x).image+"\"}";
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
		source.add("D:\\Walmart.json");
		source.add("D:\\Loblaws.json");
		source.add("D:\\Independent.json");
		String target="D:\\Main.json";
		mergeToOne m=new mergeToOne(source,target);
		try {
			//m.collectData();
			m.merge();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//m.
		
	}
}