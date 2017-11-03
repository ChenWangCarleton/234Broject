
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class LoblawsHTML{
	File target;
	String blank="     ";
//	Document document;
	TreeSet<String> firstLevel=new TreeSet<>();
	TreeSet<String> secondLevel=new TreeSet<>();
	TreeSet<String> thirdLevel=new TreeSet<>();
	PrintWriter pw;
	String head="https://www.loblaws.ca";
	static boolean status=false;
	public LoblawsHTML() {
		status=false;
	}
	public void execute(String tar) throws Exception {
		target=new File(tar);
		pw=new PrintWriter(new FileWriter(target));
		String first="/Food/";
		String source= "";
		FirstLevel(source,first);
		status=true;
	}
	public void FirstLevel(String source,String first) throws Exception {
		Document doc=Jsoup.connect(head+source).get();
		Elements e=doc.select("li[data-level=1]");
		ArrayList<String> firstl=new ArrayList<>();;
		ArrayList<String> text=new ArrayList<>();
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(first)) {
				if(firstLevel.add(a.attr("href"))) {
					firstl.add(a.attr("href"));
					text.add(a.text());
				}
			}
		}
		int fru=0;
		int org=8;
		boolean firstS=true;
		for(int x=0;x<firstl.size();x++) {
			String fl=firstl.get(x);
			if(fl.contains("Fruits-%26-Vegetables")) {
				fru=x;
				continue;
			}
			if(fl.contains("Natural-%26-Organic")) {
				org=x;
				continue;
			}
			if(firstS) {
				pw.println("{\""+text.get(x)+"\":[");
			}
			else {
				pw.println("],");
				pw.println("\""+text.get(x)+"\":[");
			}
			pw.flush();
			System.out.println(firstl.get(x)+"  --->  "+head+fl);
			String second=fl.substring(0,fl.indexOf("/c/"));
			SecondLevel(firstl.get(x),second,text.get(x));
			firstS=false;
		}
		String fl=firstl.get(fru);
		System.out.println(firstl.get(fru)+"  --->  "+head+fl);
		String second=fl.substring(0,fl.indexOf("/c/"));
		pw.println("],");
		pw.println("\""+text.get(fru)+"\":[");
		pw.flush();
		SecondLevel(firstl.get(fru),second,text.get(fru));
		
//		fl=firstl.get(org);
//		second=fl.substring(0,fl.indexOf("/c/"));
//		SecondLevel(firstl.get(org),second,text.get(fru));
		pw.println("]}");
		pw.flush();

		
	}
	public void SecondLevel(String source, String second,String cate) throws Exception {
		Document doc=Jsoup.connect(head+source).get();
		ArrayList<String> sle=new ArrayList<>();
		ArrayList<String> text=new ArrayList<>();
		Elements e=doc.select("li[data-level=2]");
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(second)&&!a.attr("href").contains(source)) {
				if(secondLevel.add(a.attr("href"))){
					sle.add(a.attr("href"));
					text.add(a.text());
				}
			}
		}
		for(int x=0;x<sle.size();x++) {
			String sl=sle.get(x);
			System.out.println(blank+sle.get(x)+"  --->  "+head+sl);
			String third=sl.substring(0,sl.indexOf("/c/"));
			if(x==sle.size()-1) {
				ThirdLevel(sle.get(x),third,true,cate);
			}
			else {
				ThirdLevel(sle.get(x),third,false,cate);
			}
		}
	}
	public void ThirdLevel(String source, String third, boolean isLast,String cate) throws Exception {
		Document doc=Jsoup.connect(head+source).get();
		boolean hasPro=true;
		Elements e=doc.select("li[data-level=3]");
		ArrayList<String> tle=new ArrayList<>();
		ArrayList<String> text=new ArrayList<>();
		for(int x=0;x<e.size();x++) {			
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(third)&&!a.attr("href").contains(source)) {
				if(thirdLevel.add(a.attr("href"))){
					hasPro=false;
					tle.add(a.attr("href"));
					text.add(a.text());
				}
			}
			
		}
		if(hasPro) {
			if(isLast) {
				productLevel(source,true,cate);
			}
			else {
				productLevel(source,false,cate);
			}
		}
		else {
			for(int x=0;x<tle.size();x++) {
				String tl=tle.get(x);
				System.out.println(blank+blank+text.get(x)+"  --->  "+head+tl);
				if(isLast&&x==tle.size()-1) {
					productLevel(tl,true,cate);
				}
				else {
					productLevel(tl,false,cate);
				}
			}
		}
	}
	public void productLevel(String source,boolean isLast,String cate) throws Exception {
		Document doc=Jsoup.connect(head+source).get();
		ArrayList<String> n=new ArrayList<>();
		ArrayList<String> d=new ArrayList<>();
		ArrayList<String> pr=new ArrayList<>();

		Elements e=doc.select("div.product-info");
		System.out.println("raw product size :       "+e.size());
		for(int x=0;x<e.size();x++) {
			Element p=e.get(x);
		//	Element name=p.select
			String brand=p.select("span.js-product-entry-brand").text();
			brand=brand.replaceAll("\u00a0"," ");
			String name=p.select("span.js-product-entry-name").text();
			String price=p.select("span.reg-price-text").text();
			String description=p.select("span.js-product-entry-size-detail").text();
			if(brand==null||brand.equals("")||brand.length()==0) {
				name=name;
			}
			else {
				name=brand+" "+name;
			}
			name="\""+name.replaceAll("\"", "'")+"\"";
			if(price==null||price.equals("")||price.length()==0) {
				price="null";
			}
			else {
				price="\""+price+"\"";
			}
			if(description==null||description.equals("")||description.length()==0) {
				description="null";
			}
			else {
				description="\""+description.replaceAll("\"", "'")+"\"";
			}
			description=description.replaceAll("\\(", "");
			description=description.replaceAll("\\)", "");
			n.add(name);
			pr.add(price);
			d.add(description);
		}
		

		//System.out.println("product sizee: "+products.size()+blank+blank+doc.title()+"     product Level:");

		for(int x=0;x<n.size();x++) {
			if(isLast&&x==n.size()-1) {
				pw.println("{\"category\":\""+cate+"\",\"name\":"+n.get(x)+",\"description\":"+d.get(x)+",\"price\":"+pr.get(x)+",\"store\":\"LoblawsHTML\"}");
			}
			else {
				pw.println("{\"category\":\""+cate+"\",\"name\":"+n.get(x)+",\"description\":"+d.get(x)+",\"price\":"+pr.get(x)+",\"store\":\"LoblawsHTML\"},");
			}
			pw.flush();
			System.out.println(n.get(x)+"   "+d.get(x)+"    "+pr.get(x));	
		//	System.out.println(blank+blank+blank+products.keySet().toArray()[x]+"    price:  "+products.get(products.keySet().toArray()[x]));
		}
	}
	public static void main(String args[]) throws IOException{
		String fn="D:\\LoblawsHTMLJson.json";
		LoblawsHTML l=new LoblawsHTML();
		try {
			l.execute(fn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			System.out.println(l.status);
		}
	}
}