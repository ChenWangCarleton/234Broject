import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class txtToJson{
	public txtToJson(String fn,String tar) throws IOException {
		File source=new File(fn);
		File target=new File(tar);//new File(tar);
		BufferedReader br=new BufferedReader(new FileReader(source));
		PrintWriter fw=new PrintWriter(new FileWriter(target));
		int x=0;
		String cate="";
		String toPrint="";
		boolean isF=true;
		for (String line = br.readLine(); line != null; line = br.readLine(),x++) {
            if(line.startsWith("Category:")) {
            	if(cate=="") {
            		cate=line.substring(line.indexOf("Category:")+9,line.length());
            		fw.println("{\""+cate+"\":[");
            		fw.flush();
            		System.out.println(line.substring(line.indexOf("Category:")+9,line.length()));
            	}
            	else {
            		cate=line.substring(line.indexOf("Category:")+9,line.length());
            		fw.println();
            		fw.println("],");
            		fw.println("\""+cate+"\":[");
            		fw.flush();
            		isF=true;
            	}
            }
            else {
            	if(!isF) {
            		fw.println(",");
            		fw.flush();
          
            	}
            	String name=line.substring(0,line.indexOf("@b@"));
            	name="\""+name.replaceAll("\"", "(in)")+"\"";
            	String brand="null";
            	if(line.indexOf("@b@")+3==line.indexOf("@q@")) {
            		
            	}
            	else {
            	//	System.out.println("b:"+line.indexOf("@b@")+3+"   q:"+line.indexOf("@q@"));
            		brand="\""+line.substring(line.indexOf("@b@")+3,line.indexOf("@q@"))+"\"";
            	}
            	String quantifier="null";
            	if(line.indexOf("@q@")+3==line.indexOf("@p@")) {
            		
            	}
            	else {
            		quantifier="\""+line.substring(line.indexOf("@q@")+3,line.indexOf("@p@"))+"\"";
            	}
            	String price="\""+line.substring(line.indexOf("@p@")+3,line.length())+"\"";
            	toPrint="{\"category\":\""+cate+"\",\"name\":"+name+",\"price\":"+price+",\"brand\":"+brand+",\"quantifier\":"+quantifier+"}";
            	fw.print(toPrint);
            	isF=false;
            	//mapper.writeValue(target, obj);
            }
        }
		fw.println();
		fw.println("]}");
		fw.flush();
	/*	JSONArray ja=new JSONArray();
		for (String line = br.readLine(); line != null; line = br.readLine(),x++) {
            if(line.startsWith("Category:")) {
            	if(cate=="") {
            		cate=line.substring(line.indexOf("Category:")+9,line.length());
            		System.out.println(line.substring(line.indexOf("Category:")+9,line.length()));
            	}
            	else {
            		target=new File(tar+cate+".json");
            		cate=line.substring(line.indexOf("Category:")+9,line.length());
            		fw=new FileWriter(target);
            		JSONObject container=new JSONObject();
            		container.put("loblaws", ja);
            		fw.write(container.toString());
            		fw.toString();
            		ja=new JSONArray();
            	}
            }
            else {
            	JSONObject obj=new JSONObject();
            	String name=line.substring(0,line.indexOf("@b@"));
            	String brand="";
            	if(line.indexOf("@b@")+3==line.indexOf("@q@")) {
            		
            	}
            	else {
            	//	System.out.println("b:"+line.indexOf("@b@")+3+"   q:"+line.indexOf("@q@"));
            		brand=line.substring(line.indexOf("@b@")+3,line.indexOf("@q@"));
            	}
            	String quantifier="";
            	if(line.indexOf("@q@")+3==line.indexOf("@p@")) {
            		
            	}
            	else {
            		quantifier=line.substring(line.indexOf("@q@")+3,line.indexOf("@p@"));
            	}
            	String price=line.substring(line.indexOf("@p@")+3,line.length());
            	obj.put("category",cate);
            	obj.put("name",name);
            	obj.put("price",price);
            	obj.put("brand",brand);
            	obj.put("quantifier",quantifier);
            	//mapper.writeValue(target, obj);
            	ja.put(obj);
            }
        }*/
	}
	public static void main(String args[]) throws IOException {
		txtToJson t=new txtToJson("D:\\LoblawsForJson.txt","D:\\LoblawsJson.json");
	}
}