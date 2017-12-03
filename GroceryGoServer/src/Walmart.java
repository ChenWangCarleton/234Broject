import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
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
	String[] linksBrand= {"/bakery/bagels-buns-rolls/N-3954","/bakery/desserts/N-3846","/bakery/sliced-bread/N-3845","/bakery/wraps-pita-flatbread/N-3955","/dairy-eggs/eggs/N-3850","/dairy-eggs/milk-cream/N-3851","/dairy-eggs/cheese/N-3849","/dairy-eggs/yogurt/N-3853","/deli/hummus-spreads/N-3953","/deli/deli-cheese/N-3817","/deli/deli-meat/N-3818","/grocery/produce/tofu-soy-products/N-4523","/drinks/coffee/N-3815","/drinks/tea-hot-drinks/N-3812","/drinks/water/N-3813","/drinks/juice/N-3810","/frozen-food/ice-cream-treats/N-3828","/frozen-food/meals-sides/N-3826","/frozen-food/pizza/N-3832","/frozen-food/vegetables/N-3825","/fruits/N-3852","/vegetables/N-3854","/salad-greens-herbs/N-4524","/meat-seafood/seafood/N-3824","/meat-seafood/beef/N-3820","/meat-seafood/pork/N-3822","/meat-seafood/poultry/N-3821","/pantry-food/pasta-rice-beans/N-3835","/pantry-food/baking-needs/N-3833","/pantry-food/canned-food/N-3839","/pantry-food/chips-snacks/N-3842","/natural-organic-food/N-3992"};
	String noImg="https://assets.shop.loblaws.ca/products/NoImage/b1/en/front/NoImage_front_a06.png";

	String url="https://www.walmart.ca/en/grocery";
	WebDriver ABdriver;
	WebDriver tempDri;
	WebDriver dri;
	WebDriver driver;
	
	static boolean  status=false;
	ArrayList<String> brands=new ArrayList<>();//////
	ArrayList<String> failUrl=new ArrayList<>();
	
	//method used to convert Germany characters to English.
	private static String[][] UMLAUT_REPLACEMENTS = { { new String("Ä"), "A" }, { new String("Ü"), "U" }, { new String("Ö"), "O" }, { new String("ä"), "a" }, { new String("ü"), "u" }, { new String("ö"), "o" }, { new String("ß"), "ss" },{new String("é"),"e"} ,{new String("É"),"E"}};
	public static String replaceUmlaute(String orig) {
	    String result = orig;

	    for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
	        result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
	    }

	    return result;
	}
	
	//in walmart to avoid go to each item's web page to collect data, we first get the all the brands and then check if an item belongs to one brand. 
	// this func is used to check if there are hiding brands in each web page
	public boolean hasMoreBrand(WebElement w) {
		try {
			WebElement m=w.findElement(By.className("moreLess"));
	//		System.out.println("text:    "+m.getText()+"       link:"+m.getAttribute("link"));
			if(m.getAttribute("link").contains("showMoreIds")) {
			//	m.click();
				return true;
			}
			else return false;
		}catch(Exception e) {
				e.printStackTrace();		
				return false;
		}
	}
	
	//this func is used to collect all the brands in one web page
	public String getBrand(ArrayList<String> brands,String name) {// set brand as an attribute.  read and store all the brand first.!!!
		for(int x=0;x<brands.size();x++) {
			if(name.contains(brands.get(x))||name.contains(brands.get(x).toLowerCase())||name.contains(brands.get(x).toUpperCase())) {//startsWith or Contains
				return brands.get(x);
			}
		}
		
		return null;
	}
	
	//this func is used to separate brand from item's displaying name. (just to check if the name contains brand from the brands that are already collected
	public String removeBrand(String name, String brand) {
		if(name.contains(brand)) {
			name=name.replace(brand, "");
		}
		else if(name.contains(brand.toLowerCase())){
			name=name.replace(brand.toLowerCase(), "");

		}
		else if(name.contains(brand.toUpperCase())) {
			name=name.replace(brand.toUpperCase(), "");
		}
		if(name.startsWith(" ")) {
			name=name.substring(1);
		}
		while(name.charAt(name.length()-1)==' '||name.charAt(name.length()-1)==','||name.charAt(name.length()-1)=='.') {
			name=name.substring(0, name.length()-1);
		}
		return name;
	}
	
	//this func is used to add brands to brand list. need to filter the special cases
	public void addToBrands(String brand) {
		HashSet<String> notBrand=new HashSet<>();//considered brand in walmart but not in loblaws and independent
		notBrand.add("Coca-Cola");
		notBrand.add("Pepsi");
		notBrand.add("Dr. Pepper");
		notBrand.add("Sprite");
		brand=replaceUmlaute(brand);
		if(brand.equals("p"))brand="Dare";
		if(notBrand.contains(brand))
			return;
		for(int x=0;x<brands.size();x++) {
			if(brands.get(x).equals(brand))
				return;
		}
		brands.add(brand);
	}
	 
	 //func used to run before actually collecting items' data. goes through each major subcategories and collects all the brand
	 public void addBrand() {
			DesiredCapabilities caps=new DesiredCapabilities();
			caps.setJavascriptEnabled(true);
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		for(int ca=0;ca<linksBrand.length;ca++){
			ABdriver=new PhantomJSDriver(caps);
			String parentUrl=url+linksBrand[ca];
			ABdriver.get(parentUrl);
			checkPageIsReady(ABdriver);
			System.out.println(parentUrl);
			WebElement rmBrand=ABdriver.findElement(By.id("rm-Brand"));
			if(hasMoreBrand(rmBrand)) {
				WebElement a=rmBrand.findElement(By.className("moreLess"));
				tempDri=new PhantomJSDriver(caps);
				tempDri.get(url+a.getAttribute("link"));
				a=tempDri.findElement(By.id("rm-Brand"));
				List<WebElement> lis=a.findElements(By.tagName("li"));
				for(int x=0;x<lis.size()-1;x++) {
					String brand="";
					brand=lis.get(x).findElement(By.tagName("input")).getAttribute("name");
					if(brand.contains("n/a")||brand.contains("N/A")) {
						
					}
					else {
						brand=removeMarkSymbol(brand);
						addToBrands(brand);
					}
				}
				tempDri.quit();
			}
			else {
				WebElement a=ABdriver.findElement(By.id("rm-Brand"));
				List<WebElement> lis=a.findElements(By.tagName("li"));
				for(int x=0;x<lis.size()-1;x++) {
					String brand="";
					brand=lis.get(x).findElement(By.tagName("input")).getAttribute("name");
					if(brand.contains("n/a")||brand.contains("N/A")) {
						
					}
					else {
						brand=removeMarkSymbol(brand);
						addToBrands(brand);
					}
				}
			}
			ABdriver.quit();
		}
	 }
	
	
	//function used to start webscraping
	public void execute(String targetFile)  throws Exception{
		status=false;
		addBrand();
		target=new File(targetFile);
		fw=new PrintWriter(new FileWriter(target));
		DesiredCapabilities caps=new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs-2.1.1-windows/bin/phantomjs.exe");
	for(int ca=0;ca<categories.length;ca++){
		driver=new PhantomJSDriver(caps);
		String parentUrl=url+links[ca];
		driver.get(parentUrl);
		checkPageIsReady(driver);
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


				dri=new PhantomJSDriver(caps);

				dri.get(parentUrl+pre+(x+1));
				checkPageIsReady(dri);
				List<WebElement> products=dri.findElements(By.className("thumb-inner-wrap"));
				System.out.println("Size of"+dri.getTitle()+" is: "+products.size());
				int nameNotNullCount=0;
				int priceNotNullCount=0;
				ArrayList<String> names=new ArrayList<>();
				ArrayList<String> prices=new ArrayList<>();
				ArrayList<String> descriptions=new ArrayList<>();
				ArrayList<String> imgs=new ArrayList<>();
				int combo=0;
				int loopCount=0;
			do {	
				combo=0;
				names=new ArrayList<>();
				prices=new ArrayList<>();
				descriptions=new ArrayList<>();
				imgs=new ArrayList<>();
				int tempCombo=0;
				boolean lastIsNull=false;
				for(int y=0;y<products.size();y++) {
					String name="";
					String description="";
					String price="";
					String img="";
			//		int count=0;
			//		do {
					if(products.get(y).findElement(By.className("centered-img-wrap")).findElement(By.tagName("img")).getAttribute("class").equals("image"))
						img=products.get(y).findElement(By.className("centered-img-wrap")).findElement(By.tagName("img")).getAttribute("src");
					else img="https:"+products.get(y).findElement(By.className("centered-img-wrap")).findElement(By.tagName("img")).getAttribute("data-original");
					//			count++;
				//		if(count==51) {
				//			img=noImg;
				//			break;
				//		}
				//	}while(!img.startsWith("http"));
					if(img==null||img.equals("")||img.length()<5)img=noImg;
					img="\""+img+"\"";
					name=products.get(y).findElement(By.className("details")).findElement(By.className("thumb-header")).getText();
					description=products.get(y).findElement(By.className("details")).findElement(By.className("description")).getText();
					name=name.replaceAll("\"", "'");					
					name=name.replaceAll("\r\n", " ");				
					name=name.replaceAll("\n", " ");
					name=name.replaceAll("\r", " ");
					name=name.replaceAll("Mr .", "Mr.").replaceAll("Mr. ", "Mr.");
					name=removeMarkSymbol(name);
					name=replaceUmlaute(name);
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
					imgs.add(img);
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
					String brand="";
					brand=getBrand(brands,names.get(y));
					if(brand==null) {
						brand="null";
					}
					else if(brand.equals("Oreo")) {//special case for  Oreo( it seems should be  Christie) 
						brand="Christie";
						if(names.get(y).contains(brand)) {
							names.set(y, names.get(y).replace(brand+" ", ""));
						}
						if(names.get(y).contains("NESTLÉ")) {
							names.set(y,names.get(y).replace("NESTLÉ", ""));
						}
						brand="\""+brand+"\"";
					}
					else {
						//special case for Aquafina 1L  & Evian  & Dasani  &  7UP  & Monster Energy  
						if(names.get(y).equals("Aquafina")||names.get(y).equals("Evian")||names.get(y).equals("Dasani")||names.get(y).equals("7UP")||names.get(y).equals("Monster Energy")) {
							if(names.get(y).equals("7UP")) {
								names.set(y, "7 UP");
							}
							else if(names.get(y).equals("Monster Energy")) {
								names.set(y, "drink");
							}
							else {
								names.set(y,"water");
							}
						}
						names.set(y, removeBrand(names.get(y),brand));
						brand="\""+brand+"\"";
					}
					if(x==c.size()-1&&y==products.size()-1) {
						fw.println("{\"category\":\""+categories[ca]+"\",\"name\":\""+names.get(y)+"\",\"price\":"+prices.get(y)+",\"description\":"+descriptions.get(y)+",\"brand\":"+brand+",\"image\":"+imgs.get(y)+",\"store\":\"Walmart\"}");
					}
					else {
						fw.println("{\"category\":\""+categories[ca]+"\",\"name\":\""+names.get(y)+"\",\"price\":"+prices.get(y)+",\"description\":"+descriptions.get(y)+",\"brand\":"+brand+",\"image\":"+imgs.get(y)+",\"store\":\"Walmart\"},");
					}
					fw.flush();					
				}
				if(nameNotNullCount==0||priceNotNullCount==0||products.size()==0) {
					failUrl.add(parentUrl+pre+(x+1));
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
	
	//check if the page is loaded successfully
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
	
	//remove mark symbol 
	public String removeMarkSymbol(String r) {
        r = r.replaceAll(  "\u00B0" ,"").replaceAll("\u00a9","").replaceAll(  "\u00AE" ,"").replaceAll( "\u2122" ,"");
		return r;
	}
	public Walmart() {
		status=false;
	}
	public static void main(String[] args) throws Exception{
		int count=0;
		Walmart s = null;
	//	do {
		try {
			s=new Walmart();
			s.execute("D:\\Walmart.json");
		}catch(Exception e) {
			s.ABdriver.quit();
			s.tempDri.quit();
			s.dri.quit();
			s.driver.quit();
			System.out.println("check the file path or internet connection and try again later");
			e.printStackTrace();
		}
		finally {
			System.out.println(count+"                 count             "+count);
		//	TimeUnit.SECONDS.sleep(40);
			
		}
	//	}while(!Walmart.status);
		for(int x=0;x<s.failUrl.size();x++) {
			System.out.println(s.failUrl.get(x));
		}
	
	}
}