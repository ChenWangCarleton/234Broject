
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



public class LobLaws{
	File target;
	PrintWriter fw;
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String head="https://www.loblaws.ca";
	static boolean  status=false;
	ArrayList<String> fail=new ArrayList<>();
	
	public LobLaws() {
		status=false;
	}
	
	//HashSet<String> data1=new HashSet<>();
	String blank="     ";
	TreeSet<String> firstLevel=new TreeSet<>();
	TreeSet<String> secondLevel=new TreeSet<>();
	TreeSet<String> thirdLevel=new TreeSet<>();
	public void execute(String tar) throws Exception {
		target=new File(tar);
		fw=new PrintWriter(new FileWriter(target));
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
				fw.println("{\""+text.get(x)+"\":[");
			}
			else {
				fw.println("],");
				fw.println("\""+text.get(x)+"\":[");
			}
			fw.flush();
			System.out.println(firstl.get(x)+"  --->  "+head+fl);
			String second=fl.substring(0,fl.indexOf("/c/"));
			SecondLevel(firstl.get(x),second,text.get(x));
			firstS=false;
		}
		String fl=firstl.get(fru);
		System.out.println(firstl.get(fru)+"  --->  "+head+fl);
		String second=fl.substring(0,fl.indexOf("/c/"));
		fw.println("],");
		fw.println("\""+text.get(fru)+"\":[");
		fw.flush();
		SecondLevel(firstl.get(fru),second,text.get(fru));
		
//		fl=firstl.get(org);
//		second=fl.substring(0,fl.indexOf("/c/"));
//		SecondLevel(firstl.get(org),second,text.get(fru));
		fw.println("]}");
		fw.flush();

		
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

		DesiredCapabilities caps=new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		WebDriver web=new PhantomJSDriver(caps);
		//checkPageIsReady(web);
		web.get(head+source);
		checkPageIsReady(web);
		List<WebElement> e=web.findElements(By.className("product-info"));
		System.out.println(e.size());
		ArrayList<String> names=new ArrayList<>();
		ArrayList<String> descriptions=new ArrayList<>();
		ArrayList<String> prices=new ArrayList<>();
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
					if(t.elapsedSeconds()>660) {
						fail.add(head+source);
						break;//track time, break after 11mins
					}
				}
				for(int x=0;x<e.size();x++) {
					WebElement forName=e.get(x).findElement(By.className("product-name-wrapper"));
					String name="";
					if(hasBrand(forName)) {
						name=forName.findElement(By.className("js-product-entry-brand")).getText()+forName.findElement(By.className("js-product-entry-name")).getText();
					}
					else {
						name=forName.findElement(By.className("js-product-entry-name")).getText();
					}
					String price=e.get(x).findElement(By.className("reg-price-text")).getText();
					String description="";
					if(hasDescription(forName)) {
						description=forName.findElement(By.className("js-product-entry-size-detail")).getText();
					}
					name=name.replaceAll("&"+"nbsp;", " "); 
					name=name.replaceAll(String.valueOf((char) 160), " ");
					description=description.replaceAll("&"+"nbsp;", " ");
					description=description.replaceAll(String.valueOf((char) 160), " ");
					description=description.replaceAll("\\(","");
					description=description.replaceAll("\\)","");
					names.add(name);
					prices.add(price);
					descriptions.add(description);
				}
			}
			else {
				//System.out.println("button not found");
				for(int x=0;x<e.size();x++) {
					WebElement forName=e.get(x).findElement(By.className("product-name-wrapper"));
					String name="";
					if(hasBrand(forName)) {
						name=forName.findElement(By.className("js-product-entry-brand")).getText()+forName.findElement(By.className("js-product-entry-name")).getText();
					}
					else {
						name=forName.findElement(By.className("js-product-entry-name")).getText();
					}
					String price=e.get(x).findElement(By.className("reg-price-text")).getText();
					String description="";
					if(hasDescription(forName)) {
						description=forName.findElement(By.className("js-product-entry-size-detail")).getText();
					}
					name=name.replaceAll("&"+"nbsp;", " "); 
					name=name.replaceAll(String.valueOf((char) 160), " ");
					description=description.replaceAll("&"+"nbsp;", " ");
					description=description.replaceAll(String.valueOf((char) 160), " ");
					description=description.replaceAll("\\(","");
					description=description.replaceAll("\\)","");
					names.add(name);
					prices.add(price);
					descriptions.add(description);
				}
			}
		}
		else {
			for(int x=0;x<e.size();x++) {
				WebElement forName=e.get(x).findElement(By.className("product-name-wrapper"));
				String name="";
				if(hasBrand(forName)) {
					name=forName.findElement(By.className("js-product-entry-brand")).getText()+forName.findElement(By.className("js-product-entry-name")).getText();
				}
				else {
					name=forName.findElement(By.className("js-product-entry-name")).getText();
				}
				String price=e.get(x).findElement(By.className("reg-price-text")).getText();
				String description="";
				if(hasDescription(forName)) {
					description=forName.findElement(By.className("js-product-entry-size-detail")).getText();
				}
				name=name.replaceAll("&"+"nbsp;", " "); 
				name=name.replaceAll(String.valueOf((char) 160), " ");
				description=description.replaceAll("&"+"nbsp;", " ");
				description=description.replaceAll(String.valueOf((char) 160), " ");
				description=description.replaceAll("\\(","");
				description=description.replaceAll("\\)","");
				names.add(name);
				prices.add(price);
				descriptions.add(description);
			}
		}
		for(int x=0;x<e.size();x++) {
			String n=names.get(x);
			n="\""+n.replaceAll("\"", "'")+"\"";
			String d=descriptions.get(x);
			if(d==null||d.equals("")||d.length()==0) {
				d="null";
			}
			else {
				d="\""+d.replaceAll("\"", "'")+"\"";
			}
			String p=prices.get(x);
			if(p==null||p.equals("")||d.length()==0) {
				p="null";
			}
			else {
				p="\""+p+"\"";
			}
			if(isLast&&x==e.size()-1) {
				fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"store\":\"Loblaws\"}");
			}
			else {
				fw.println("{\"category\":\""+cate+"\",\"name\":"+n+",\"description\":"+d+",\"price\":"+p+",\"store\":\"Loblaws\"},");
			}
			fw.flush();
			System.out.println(n+"   "+d+"    "+p);
		}
		if(e.size()==0) {
			fail.add(head+source);
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
		LobLaws s = null;
	//	do {
		try {
			s=new LobLaws();
			s.execute("D:\\LoblawsJson.json");
		}catch(Exception e) {
			System.out.println("check the file path or internet connection and try again later");
		//	System.out.println();
			e.printStackTrace();
		}
		finally {
			System.out.println(count+"                 count             "+count);
			TimeUnit.SECONDS.sleep(10);

		}
	//	}while(!Loblaws.status);
		for(int x=0;x<s.fail.size();x++) {
			System.out.println(s.fail.get(x));
	}
	}
}