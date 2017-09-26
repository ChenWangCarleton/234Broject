import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sun.misc.Launcher;

public class GUI extends Application {
  

 @Override
 public void start(Stage primaryStage) throws Exception {
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
    
  final ObservableList<Item> itemData = FXCollections.observableArrayList(list.getList());
  
  //setup stage
  BorderPane rootStagePane = new BorderPane();
  rootStagePane.setMinHeight(50);
  Scene scRoot = new Scene(rootStagePane);
  primaryStage.setScene(scRoot);
  
  //init table component
  TableView<Item> itemTable = new TableView<>();
  
  TableColumn<Item, Integer> nameCol = new TableColumn<>("Name");
  TableColumn<Item, Integer> categoryCol = new TableColumn<>("Category");
  
  itemTable.getColumns().addAll(nameCol,categoryCol);
  
  nameCol.setCellValueFactory(new PropertyValueFactory<Item,Integer>("Name"));
  categoryCol.setCellValueFactory(new PropertyValueFactory<Item,Integer>("Category"));
  
  itemTable.setItems(itemData);
  
  //init other components
  Label idLabel = new Label("ID");
  Label nameLabel = new Label("Name");
  Label categoryLabel = new Label("Category");
  
  TextField nameTextField = new TextField("");
  
  ObservableList<String> options = FXCollections.observableArrayList(
          "FRUIT",
          "MEAT"
      );
  ComboBox categoryOpts = new ComboBox(options);
  
  Button addBtn = new Button("Add Item");
  Button removeBtn = new Button("Remove Item");
  //Button showBtn = new Button("Show Item List");
    
  HBox nameHBox = new HBox();
  HBox categoryHBox = new HBox();
  
  HBox buttonsHBox = new HBox();
  nameHBox.getChildren().addAll(nameLabel,nameTextField);
  categoryHBox.getChildren().addAll(categoryLabel,categoryOpts);
  buttonsHBox.getChildren().addAll(addBtn,removeBtn); 
  
  //Button functions
  //add button
  addBtn.setOnMouseClicked((evt)->{
   list.addItem(nameTextField.getText(),Item.Category.values()[categoryOpts.getSelectionModel().getSelectedIndex()]);
   
   final ObservableList<Item> newData = FXCollections.observableArrayList(list.getList());
   //renew table
   itemTable.setItems(newData);
   list.save();
  });
  //remove button
  removeBtn.setOnMouseClicked((evt)->{
   
   //renew table
   itemTable.setItems(itemData);
  });
  
  //show stage
  VBox vBox = new VBox();
  vBox.getChildren().addAll(itemTable,nameHBox,categoryHBox,buttonsHBox);
  rootStagePane.setCenter(vBox);
  primaryStage.show();
 }

 public static void main(String[] args) {
  // TODO Auto-generated method stub
  
  launch(args);
 }

}
