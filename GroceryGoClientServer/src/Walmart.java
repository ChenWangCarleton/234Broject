
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
public class Walmart{
	File target;
	PrintWriter fw;
	String[] categories= {"Fruits & Vegetables","Deli & Ready Meals","Bakery","Meat & Seafood","Dairy and Eggs","Drinks","Frozen","Pantry"};
	String[] links= {"/fruits-and-vegetables/N-3799","/deli/N-3792","/bakery/N-3796","/meat-seafood/N-3793","/dairy-eggs/N-3798","/drinks/N-3791","/frozen-food/N-3795","/pantry-food/N-3794"};
	String basic="https://www.walmart.ca/en/grocery";
	static boolean  status=false;
	ArrayList<String> fail=new ArrayList<>();
	public void execute(String tar)  throws Exception{
		target=new File(tar);
		fw=new PrintWriter(new FileWriter(target));
		DesiredCapabilities caps=new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
	for(int ca=0;ca<categories.length;ca++){
		WebDriver driver=new PhantomJSDriver(caps);
		String parentUrl=basic+links[ca];
		driver.get(parentUrl);
		System.out.println(parentUrl);

		if(ca==0) {
			fw.println("{\""+categories[ca]+"\":[");
		}
		else {
			fw.println("\""+categories[ca]+"\":[");
		}
		fw.flush();
			WebElement e=driver.findElement(By.className("page-select"));
			List<WebElement> c=e.findElements(By.tagName("option"));
			String pre="/page-";
			for(int x=0;x<c.size();x++) {


				WebDriver dri=new PhantomJSDriver(caps);

				dri.get(parentUrl+pre+(x+1));
				checkPageIsReady(dri);
				List<WebElement> products=dri.findElements(By.className("thumb-inner-wrap"));
				System.out.println("Size of"+dri.getTitle()+" is: "+products.size());
				int nameNotNullCount=0;
				int priceNotNullCount=0;
				ArrayList<String> names=new ArrayList<>();
				ArrayList<String> prices=new ArrayList<>();
				ArrayList<String> descriptions=new ArrayList<>();
				int combo=0;
				int loopCount=0;
			do {	
				combo=0;
				names=new ArrayList<>();
				prices=new ArrayList<>();
				descriptions=new ArrayList<>();
				int tempCombo=0;
				boolean lastIsNull=false;
				for(int y=0;y<products.size();y++) {
					String name="";
					String description="";
					String price="";
					name=products.get(y).findElement(By.className("details")).findElement(By.className("thumb-header")).getText();
					description=products.get(y).findElement(By.className("details")).findElement(By.className("description")).getText();
					name=name.replaceAll("\"", "'");					
					name=name.replaceAll("\r\n", " ");				
					name=name.replaceAll("\n", " ");
					name=name.replaceAll("\r", " ");
					if(description.length()>40)description="null";//omit useless long text description
					else {
						description=description.replaceAll("\"", "'");
						description="\""+description+"\"";
					}
					description=description.replaceAll("\r\n", " ");
					description=description.replaceAll("\n", " ");
					description=description.replaceAll("\r", " ");
					if(ca!=0&&ca!=3) {
						price=products.get(y).findElement(By.cssSelector("div[class='price-current']")).getText();
					}
					else {
						price=products.get(y).findElement(By.cssSelector("div[class='price-current width-adjusted']")).getText();						
					}
					if(price.length()>1) {
						if(!price.contains("$")) {
							price=price.substring(0,price.length()-1);
							Double temp=Double.parseDouble(price);
							temp=temp/100;
							price="$"+temp.toString();
						}
						price="\""+price+"\"";
					}
					else {
						price="null";
					}
					names.add(name);
					prices.add(price);
					descriptions.add(description);
					if(!name.equals("")) {
						nameNotNullCount++;
					}
					if(!price.equals("null")) {
						priceNotNullCount++;
						if(tempCombo>0) {
							if(tempCombo>combo)combo=tempCombo;
							tempCombo=0;
						}
						lastIsNull=false;
					}
					else {
						if(lastIsNull) {
							tempCombo++;
							if(tempCombo>combo)combo=tempCombo;
						}
						lastIsNull=true;
					}
					System.out.println(name+" "+description+" "+price);
				}
				loopCount++;
				if(loopCount>100) {
					break;
				}
			}while((products.size()>0&&(nameNotNullCount==0||priceNotNullCount==0))||combo>3);
				dri.quit();
				for(int y=0;y<products.size();y++) {
					if(x==c.size()-1&&y==products.size()-1) {
						fw.println("{\"category\":\""+categories[ca]+"\",\"name\":\""+names.get(y)+"\",\"price\":"+prices.get(y)+",\"description\":"+descriptions.get(y)+",\"store\":\"Walmart\"}");
					}
					else {
						fw.println("{\"category\":\""+categories[ca]+"\",\"name\":\""+names.get(y)+"\",\"price\":"+prices.get(y)+",\"description\":"+descriptions.get(y)+",\"store\":\"Walmart\"},");
					}
					fw.flush();					
				}
				if(nameNotNullCount==0||priceNotNullCount==0||products.size()==0) {
					fail.add(parentUrl+pre+(x+1));
				}
				}
			//}
			if(ca==categories.length-1) {
				fw.println("]}");
			}
			else {
				fw.println("],");
			}
			fw.flush();
			driver.quit();
			status=true;
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
	
	
	public Walmart() {
		status=false;
	}
	public static void main(String[] args) throws Exception{
		int count=0;
		Walmart s = null;
		do {
		try {
			s=new Walmart();
			s.execute("D:\\WalmartJson.json");
		}catch(Exception e) {
			System.out.println("check the file path or internet connection and try again later");
		}
		finally {
			System.out.println(count+"                 count             "+count);
			TimeUnit.SECONDS.sleep(40);

		}
		}while(!Walmart.status);
		for(int x=0;x<s.fail.size();x++) {
			System.out.println(s.fail.get(x));
		}
	
	}
}