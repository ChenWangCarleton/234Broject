import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;



public class Independent{
	File target;
	PrintWriter fw;
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String url="https://www.yourindependentgrocer.ca";
	String noImg="https://assets.shop.loblaws.ca/products/NoImage/b1/en/front/NoImage_front_a06.png";
	static boolean  status=false;
	ArrayList<String> failUrl=new ArrayList<>();
	WebDriver web;
	WebDriver getFirst;
	WebDriver forP;
	public Independent() {
		status=false;
	}
	
	//HashSet<String> data1=new HashSet<>();
	String blank="     ";
	TreeSet<String> firstLevel=new TreeSet<>();
	TreeSet<String> secondLevel=new TreeSet<>();
	TreeSet<String> thirdLevel=new TreeSet<>();
	public void execute(String targetFile) throws Exception {
		status=false;
		target=new File(targetFile);
		fw=new PrintWriter(new FileWriter(target));
		String first="/Independent/Food";
		String source= "";

		DesiredCapabilities caps=new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		getFirst=new PhantomJSDriver(caps);
		getFirst.get(url);
		checkPageIsReady(getFirst);
		System.out.println(getFirst.getTitle());

		WebElement f=getFirst.findElement(By.cssSelector("li[data-auid='food']"));
		List<WebElement> ff=f.findElements(By.cssSelector("li[data-level='1']"));
		for(int x=0;x<ff.size();x++) {
			if(!ff.get(x).getAttribute("class").equals("last see-all")) {
				List<WebElement> fs=ff.get(x).findElements(By.cssSelector("li[data-level='2']"));
				for(int y=0;y<fs.size();y++) {
					if(!fs.get(y).getAttribute("class").equals("last see-all")) {
						List<WebElement> fl=fs.get(y).findElements(By.cssSelector("li[data-level='3']"));
						for(int z=0;z<fl.size();z++) {
							if(!fl.get(z).getAttribute("class").equals("last see-all"))
							firstLevel.add(fl.get(z).findElement(By.tagName("a")).getAttribute("href"));
						}
					}
				}
			}
		}

		System.out.println(firstLevel.size());
		String lastCate="";
		ArrayList<ArrayList<String>> fir=new ArrayList<>();
		int current=-1;
		ArrayList<String> cat=new ArrayList<>();
		for(int x=0;x<firstLevel.size();x++) {
			String t=firstLevel.pollFirst();
			String thisCate=getCate(t.substring(t.indexOf("/Food/")+6,t.length()));
			if(!thisCate.equals(lastCate)) {
				current++;
				fir.add(new ArrayList<String>());
				cat.add(thisCate);
			}
			fir.get(current).add(t);
			lastCate=thisCate;
		}
		for(int x=0;x<fir.size();x++) {
			if(x==0) {
				fw.print("{");
			}
			fw.println("\""+cat.get(x)+"\":[");
			fw.flush();
			for(int y=0;y<fir.get(x).size();y++) {
				System.out.println(fir.get(x).get(y));//for Independenting
				if(y==fir.get(x).size()-1) {
				//	productLevel(fir.get(x).get(y),true,cat.get(x));
				}
				else {
				//	productLevel(fir.get(x).get(y),false,cat.get(x));
				}
			}
			if(x==fir.size()-1)fw.println("]}");
			else fw.println("],");
			fw.flush();
		}
		getFirst.quit();
		status=true;
	}
	//	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};

	public String getCate(String ca) {
		if(ca.startsWith("Fruits"))return categories[0];
		else if(ca.startsWith("Deli"))return categories[1];
		else if(ca.startsWith("Bakery"))return categories[2];
		else if(ca.startsWith("Meat"))return categories[3];
		else if(ca.startsWith("Dairy"))return categories[4];
		else if(ca.startsWith("Drinks"))return categories[5];
		else if(ca.startsWith("Frozen"))return categories[6];
		else if(ca.startsWith("Pantry"))return categories[7];
		
		
		return "null";
		
		
	}
	public void printProduct(String source,boolean isLast,String cate) {
		DesiredCapabilities cap=new DesiredCapabilities();
		cap.setJavascriptEnabled(true);
		cap.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");					
		forP=new PhantomJSDriver(cap);
		forP.get(source);
		checkPageIsReady(forP);
		System.out.println(source);//for Independenting
		int count=0;

		boolean present=false;
		do {
			present=isElementPresent(By.className("module-product-info"),forP);
			count++;
			System.out.println(count);
			if(count==50) {
				failUrl.add(source);
				return;
			}
		}while(!present);
		WebElement pp=forP.findElement(By.className("module-product-info"));
		String n="";
		do {
			n=pp.findElement(By.className("product-name")).getText();
			count++;
			System.out.println(count);
			if(count==100) {
				failUrl.add(source);
				return;
			}
		}while(n.equals(""));
		n=n.replaceAll("\"","'");
		n=replaceUmlaute(n);
		n=n.replaceAll("\r", "").replace("\n", "aaaaaaaaaa").replaceAll("\n", "");
		String b="";
		String d="";
		String[] temp=n.split("aaaaaaaaaa");
		if(temp.length==1) {
			if(n.charAt(n.length()-1)==')') {
				while(n.charAt(n.length()-1)!='('){
					d=n.charAt(n.length()-1)+d;
					n=n.substring(0, n.length()-1);
				}
				d=n.charAt(n.length()-1)+d;
				n=n.substring(0, n.length()-1);
				while(n.charAt(n.length()-1)==' ')n=n.substring(0,n.length()-1);
			}
		}
		else {
			b=temp[0];
			b=b.substring(0, b.length()-1);
			n=temp[1];
			if(n.charAt(n.length()-1)==')') {
				while(n.charAt(n.length()-1)!='('){
					d=n.charAt(n.length()-1)+d;
					n=n.substring(0, n.length()-1);
				}
				d=n.charAt(n.length()-1)+d;
				n=n.substring(0, n.length()-1);
				while(n.charAt(n.length()-1)==' ')n=n.substring(0,n.length()-1);
				while(n.charAt(0)==' ')n=n.substring(1);
			}	
		}
		if(b.equals(""))b="null";
		else b="\""+b+"\"";
		if(d.equals(""))d="null";
		else d="\""+d.replace("(", "").replace(")","")+"\"";
		n="\""+n+"\"";
		WebElement pr=pp.findElement(By.className("pricing-module"));
		String p="";
		do{
			p=pr.findElement(By.className("reg-price-text")).getText();
			if(p==null||p.length()==0)p=pr.findElement(By.className("sale-price-text")).getText();
			count++;
			System.out.println(count);
			if(count==150) {
				failUrl.add(source);
				return;
			}
		}while(p.equals(""));
		p="\""+p+"\"";
	//	prices.add(pr);
		String i="";
		i=forP.findElement(By.className("module-product-viewer")).findElement(By.className("module-product-thumbnail")).findElement(By.tagName("picture")).findElement(By.tagName("img")).getAttribute("srcset");
		if(i==null||i.equals("")||i.length()<5)i=noImg;
		i="\""+i+"\"";
		System.out.println(b+"     "+n+"    "+p+"     "+d);
	//	names.add(n);
		if(isLast) {
			fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"brand\":"+b+",\"image\":"+i+",\"store\":\"Independent\"}");
		}
		else {
			fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"brand\":"+b+",\"image\":"+i+",\"store\":\"Independent\"},");
		}
		fw.flush();
		forP.quit();
	}
	public void productLevel(String source,boolean isLast,String cate) throws Exception {


		DesiredCapabilities caps=new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		web=new PhantomJSDriver(caps);
		//checkPageIsReady(web);
		web.get(source);
		checkPageIsReady(web);
		List<WebElement> e=web.findElements(By.className("product-info"));
		System.out.println(e.size());
		ArrayList<String> names=new ArrayList<>();
		ArrayList<String> descriptions=new ArrayList<>();
		ArrayList<String> prices=new ArrayList<>();
		ArrayList<String> brands=new ArrayList<>();
		if(e.size()==60) {
			if(isElementPresent(By.className("show-more-text"),web)) {
				WebElement temp=web.findElement(By.className("show-more-text"));
				WebElement but=temp.findElement(By.tagName("button"));
				String total=but.getAttribute("data-total-items");
				int tot=Integer.parseInt(total);
				int totalClicks=(tot-1)/e.size();
				System.out.println(tot+"    "+totalClicks);
				for(int z=0;z<totalClicks;z++) {
					WebDriverWait wait = new WebDriverWait(web, 10);
					WebElement more=wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[class='btn-inline-link btn-show-more']")));
					more.click();
					System.out.println("clicked");
					checkPageIsReady(web);
				}
				trackTime t=new trackTime();
				t.start();
				while(e.size()!=tot) {
					e=web.findElements(By.className("product-info"));
					t.stop();
					if(t.elapsedSeconds()>360) {
						failUrl.add(url+source);
						break;//track time, break after 11mins
					}
				}
				for(int x=0;x<e.size();x++) {//printProduct
					WebElement a=e.get(x).findElement(By.tagName("a"));
					String url=a.getAttribute("href");
					if(isLast&&x==e.size()-1)
						printProduct(url,true,cate);
					else
						printProduct(url,false,cate);
				}
			}
			else {
				//System.out.println("button not found");
				for(int x=0;x<e.size();x++) {	
					WebElement a=e.get(x).findElement(By.tagName("a"));
					String url=a.getAttribute("href");
					if(isLast&&x==e.size()-1)
						printProduct(url,true,cate);
					else
						printProduct(url,false,cate);
				}
			}
		}
		else {
			for(int x=0;x<e.size();x++) {
				WebElement a=e.get(x).findElement(By.tagName("a"));
				String url=a.getAttribute("href");
				if(isLast&&x==e.size()-1)
					printProduct(url,true,cate);
				else
					printProduct(url,false,cate);
			}
		}
/*		for(int x=0;x<e.size();x++) {
			fw.println(names.get(x)+"   :  "+prices.get(x));
			fw.flush();
		}*/
		
		/*for(int x=0;x<e.size();x++) {
			String n=names.get(x);
			n="\""+n.replaceAll("\"", "'")+"\"";
			String d=descriptions.get(x);
			if(d==null||d.equals("")||d.length()==0) {
				d="null";
			}
			else {
				d="\""+d.replaceAll("\"", "'")+"\"";
			}
			String b=brands.get(x);
			b=b.replaceAll("\"", "'");
			if(b==null||b.equals("")||b.length()==0) {
				b="null";
			}
			else {
				b="\""+b+"\"";
			}			
			String p=prices.get(x);
			if(p==null||p.equals("")||p.length()==0) {
				p="null";
			}
			else {
				p="\""+p+"\"";
			}
			if(isLast&&x==e.size()-1) {
				fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"brand\":"+b+",\"store\":\"Independent\"}");
			}
			else {
				fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"brand\":"+b+",\"store\":\"Independent\"},");
			}
			fw.flush();
			System.out.println(n+"   "+d+"    "+p);
		}*/
		if(e.size()==0) {
			failUrl.add(url+source);
		}
		web.quit();
	}
	public boolean hasBrand(WebElement pro ) {
		try {
			pro.findElement(By.className("js-product-entry-brand"));
			return true;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
	public boolean hasDescription(WebElement pro ) {
		try {
			pro.findElement(By.className("js-product-entry-size-detail"));
			return true;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
	public boolean isElementPresent(By by, WebDriver driver){
        try{
            driver.findElement(by);
            return true;
        }
        catch(NoSuchElementException e){
            return false;
        }
    }
	private static String[][] UMLAUT_REPLACEMENTS = { { new String("Ä"), "A" }, { new String("Ü"), "U" }, { new String("Ö"), "O" }, { new String("ä"), "a" }, { new String("ü"), "u" }, { new String("ö"), "o" }, { new String("ß"), "ss" },{new String("é"),"e"} ,{new String("É"),"E"}};
	public static String replaceUmlaute(String orig) {
	    String result = orig;

	    for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
	        result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
	    }

	    return result;
	}
	ArrayList<String> brands=new ArrayList<>();//////

	public void addToBrands(String brand) {
		if(brand.length()==0||brand.equals("")||brand==null)return;
		//HashSet<String> notBrand=new HashSet<>();
	//	notBrand.add("Coca-Cola");
	//	notBrand.add("Pepsi");
	//	notBrand.add("Dr. Pepper");
	//	notBrand.add("Sprite");
		brand=replaceUmlaute(brand);
		if(brand.length()>0) {
			if(brand.charAt(brand.length()-1)==' ')brand=brand.substring(0, brand.length()-1);
		}
	//	if(brand.equals("p"))brand="Dare";
	//	if(notBrand.contains(brand))
		//	return;
		for(int x=0;x<brands.size();x++) {
			if(brands.get(x).equals(brand))
				return;
		}
		brands.add(brand);
	}
	public void checkPageIsReady(WebDriver driver) {
		  
		  JavascriptExecutor js = (JavascriptExecutor)driver;
		  
		  
		  //Initially bellow given if condition will check ready state of page.
		  if (js.executeScript("return document.readyState").toString().equals("complete")){ 
		   System.out.println("Page Is loaded.");
		   return; 
		  } 
		  
		  //This loop will rotate for 40 times to check If page Is ready after every 1 second.
		  for (int i=0; i<40; i++){ 
		   try {
		    Thread.sleep(1000);
		    }catch (InterruptedException e) {} 
		   //To check page ready state.
		   if (js.executeScript("return document.readyState").toString().equals("complete")){ 
		    break; 
		   }   
		  }
	 }	
	
	public static void main(String[] args) throws Exception {
		int count=0;
		Independent s = null;
	//	do {
		try {
			s=new Independent();
			s.execute("D:\\Independent.json");
		}catch(Exception e) {
			if(s.forP!=null)s.forP.quit();
			if(s.getFirst!=null)s.getFirst.quit();
			if(s.web!=null)s.web.quit();
			System.out.println("check the file path or internet connection and try again later");
		//	System.out.println();
			e.printStackTrace();
		}
		finally {
		//	System.out.println(count+"                 count             "+count);
			for(int x=0;x<s.brands.size();x++) {
				System.out.println(s.brands.get(x));
			}
		//	TimeUnit.SECONDS.sleep(10);

		}
	//	}while(!Independent.status);
		for(int x=0;x<s.failUrl.size();x++) {
			System.out.println(s.failUrl.get(x));
	}
	}
}