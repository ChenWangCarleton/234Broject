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
public class LobLawsForJson{
	File target;
	String blank="     ";
	TreeSet<String> firstLevel=new TreeSet<>();
	TreeSet<String> secondLevel=new TreeSet<>();
	TreeSet<String> thirdLevel=new TreeSet<>();
	PrintWriter pw;
	String head="https://www.loblaws.ca";
	public LobLawsForJson(String fn) throws IOException {
		target=new File(fn);
		target.delete();
		pw=new PrintWriter(new FileWriter(target));
		String first="/Food/";
		String source= "";
		FirstLevel(source,first);

	}
	public void FirstLevel(String source,String first) throws IOException {
		Document doc=Jsoup.connect(head+source).get();
		Elements e=doc.select("li[data-level=1]");
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(first)) {
				if(firstLevel.add(a.attr("href"))) {
					String fl=a.attr("href");
					System.out.println(a.text()+"  --->  "+head+fl);
					pw.println("Category:"+a.text());
					pw.flush();
					String second=fl.substring(0,fl.indexOf("/c/"));
					SecondLevel(a.attr("href"),second);
				}	
			}
		}
	}
	public void SecondLevel(String source, String second) throws IOException {
		Document doc=Jsoup.connect(head+source).get();
		String title = doc.title(); //Get title
	//	System.out.println("Title: " + title); //Print title.
	//	Elements e=document.select("li[data-level=2]");
		Elements e=doc.select("li[data-level=2]");
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(second)&&!a.attr("href").contains(source)) {
				if(secondLevel.add(a.attr("href"))){
					String sl=a.attr("href");
		//			System.out.println(blank+"SecondLevel:");
					System.out.println(blank+a.text()+"  --->  "+head+sl);
					//pw.println(blank+head+sl);
					//pw.flush();
					String third=sl.substring(0,sl.indexOf("/c/"));
					ThirdLevel(a.attr("href"),third);
				}
			}
		}
	}
	public void ThirdLevel(String source, String third) throws IOException {
		Document doc=Jsoup.connect(head+source).get();
		boolean hasPro=true;
		String title = doc.title(); //Get title
	//	System.out.println(blank+blank+"Title: " + title); //Print title.
		Elements e=doc.select("li[data-level=3]");
		for(int x=0;x<e.size();x++) {			
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(third)&&!a.attr("href").contains(source)) {
				if(thirdLevel.add(a.attr("href"))){
					hasPro=false;
					String tl=a.attr("href");
			//		System.out.println(blank+blank+"ThirdLevel:                       ");
					System.out.println(blank+blank+a.text()+"  --->  "+head+tl);
				//	pw.println(blank+blank+head+tl);
				//	pw.flush();
					productLevel(tl);
				}
			}
			
		}
		if(hasPro) {
		//	Elements options=doc.select("li.sort-item");
			
		/*	
		    String lhLink="";
		    String hlLink="";
		 	for(int y=0;y<options.size();y++) {
				if(options.get(y).select("a").text().contains("Low to High"))lhLink=options.get(y).select("a").attr("href");
				if(options.get(y).select("a").text().contains("High to Low"))hlLink=options.get(y).select("a").attr("href");
			}
			if(lhLink.length()>0&&hlLink.length()>0) {
			lhLink=lhLink.substring(lhLink.indexOf("?"),lhLink.length()-1);
			hlLink=hlLink.substring(hlLink.indexOf("?"),hlLink.length()-1);}*/
			productLevel(source);
		}
	}
	public void productLevel(String source) throws IOException {
		String lh="?filters=&sort=price-asc";
		String hl="?filters=&sort=price-desc";
		Document doc=Jsoup.connect(head+source+lh).get();
		HashMap<String,String> products=new HashMap<>();
		Elements e=doc.select("div.product-info");
	//	if(doc.title().contains("Salads"))System.out.println(e.size()+":  "+lh+"   "+hl+"     "+head+source);
		//ArrayList<String> salads=new ArrayList<>();
		for(int x=0;x<e.size();x++) {
			Element p=e.get(x);
		//	Element name=p.select
			String brand=p.select("span.js-product-entry-brand").text();
			brand=brand.replaceAll("\u00a0"," ");
			String name=p.select("span.js-product-entry-name").text();
			String price=p.select("span.reg-price-text").text();
			String quan=p.select("span.js-product-entry-size-detail").text();
		/*	if(doc.title().contains("Salads")) {
				salads.add(name);
			}*/
			products.put(name+"@b@"+brand+"@q@"+quan,price);
		}
		
/*		Document doc1=Jsoup.connect(head+source+hl).get();
		e=doc1.select("div.product-info");
		for(int x=0;x<e.size();x++) {
			Element p=e.get(x);
		//	Element name=p.select
			String brand=p.select("span.js-product-entry-brand").text();
			brand=brand.replaceAll("\u00a0"," ");
			String name=p.select("span.js-product-entry-name").first().text();
			String price=p.select("span.reg-price-text").first().text();
			String quan=p.select("span.js-product-entry-size-detail").text();
			
			products.put(brand+name+quan,price);
		}*/
		System.out.println(blank+blank+blank+doc.title()+"     product Level:");
	//	if(doc.title().contains("Salads"))System.out.println("arraylist size  :"+salads.size());
	//	System.out.println("SIZE:   "+products.size());
		for(int x=0;x<products.size();x++) {
			pw.println(products.keySet().toArray()[x]+"@p@"+products.get(products.keySet().toArray()[x]));
		//	String n=products.keySet().toArray()[x].toString();
		//	n=n.substring(0, n.indexOf("@b@"));
		//	pw.println(n+"@b@"+brand+"@p@");
			pw.flush();
		}
	//	System.out.println(products.toString());

	}
	public static void main(String args[]) throws IOException{
		LobLawsForJson l=new LobLawsForJson("D:\\LoblawsForJson.txt");
	}
}