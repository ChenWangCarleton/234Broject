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
public class IndependentForJson{
	File target;
	String blank="     ";
//	Document document;
	TreeSet<String> firstLevel=new TreeSet<>();
	TreeSet<String> secondLevel=new TreeSet<>();
	TreeSet<String> thirdLevel=new TreeSet<>();
	PrintWriter pw;
	String head="https://www.yourindependentgrocer.ca";
	public IndependentForJson(String fileName) throws IOException {
		target=new File(fileName);
		target.delete();
		pw=new PrintWriter(new FileWriter(target));
		String first="/Independent/Food";
		String source= "";
		FirstLevel(source,first);

	}
	public void FirstLevel(String source,String first) throws IOException {
		System.out.println("NEW VERSION::::::::::::");
		Document doc=Jsoup.connect(head+source).get();
		Elements e=doc.select("li[data-level=1]");
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(first)) {
				if(firstLevel.add(a.attr("href"))) {
					String fl=a.attr("href");
	//				System.out.println("FirstLevel:");
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

		Elements e=doc.select("li[data-level=2]");
		for(int x=0;x<e.size();x++) {
			Element a=e.get(x).select("a").first();
			if(a.attr("href").startsWith(second)&&!a.attr("href").contains(source)) {
				if(secondLevel.add(a.attr("href"))){
					String sl=a.attr("href");
		//			System.out.println(blank+"SecondLevel:");
					System.out.println(blank+a.text()+"  --->  "+head+sl);
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
					productLevel(tl);
				}
			}
			
		}
		if(hasPro) {
	
			productLevel(source);
		}
	}
	public void productLevel(String source) throws IOException {
		String lh="?filters=&sort=price-asc";
		String hl="?filters=&sort=price-desc";
		Document doc=Jsoup.connect(head+source+lh).get();
		HashMap<String,String> products=new HashMap<>();
		Elements e=doc.select("div.product-info");
		ArrayList<String> salads=new ArrayList<>();
		for(int x=0;x<e.size();x++) {
			Element p=e.get(x);
		//	Element name=p.select
			String brand=p.select("span.js-product-entry-brand").text();
			brand=brand.replaceAll("\u00a0"," ");
			String name=p.select("span.js-product-entry-name").text();
			String price=p.select("span.reg-price-text").text();
			String quan=p.select("span.js-product-entry-size-detail").text();
			if(doc.title().contains("Salads")) {
				salads.add(name);
			}
			products.put(name+"@b@"+brand+"@q@"+quan,price);
		}
		

		System.out.println(blank+blank+blank+doc.title()+"     product Level:");

		for(int x=0;x<products.size();x++) {
			pw.println(products.keySet().toArray()[x]+"@p@"+products.get(products.keySet().toArray()[x]));
			pw.flush();
		}

	}
	public static void main(String args[]) throws IOException{
		String fn="D:\\IndependentForJson.txt";
		IndependentForJson l=new IndependentForJson(fn);
	}
}