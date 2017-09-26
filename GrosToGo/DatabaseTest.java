

public class DatabaseTest{
  public static void main(String[] args){
    ItemList list = new ItemList();
    list.addItem("Apple", Item.Category.FRUIT);
    //System.out.println(list.searchItem("Apple").getName());
    //System.out.println(list.searchItem("Apple").getCategory());
    //System.out.println(list.getSize());
    list.addItem("Pork", Item.Category.MEAT);
    //System.out.println(list.getSize());
    //list.removeItem("Apple");
    //System.out.println(list.getSize());
    //System.out.println(list.searchItem("Pork").getName());
    System.out.println(list.searchItem("Apple").getId());
    System.out.println(list.searchItem("Pork").getId());
    list.addItem("Banana", Item.Category.MEAT);
    System.out.println(list.searchItem("Banana").getId());
    list.addItem("Peach", Item.Category.FRUIT);
    list.addItem("Orange", Item.Category.FRUIT);
    list.save();
  }
}