package pm.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.UIManager;
import pm.PoseMaker;
import pm.data.DataManager;
import pm.gui.Workspace;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author Zhe Lin
 * @version 1.0
 */
public class FileManager implements AppFileComponent {
    
    private final String ELLIPSE = "class javafx.scene.shape.Ellipse";
    private final String RECT = "class javafx.scene.shape.Rectangle";
    

    /**
     * This method is for saving user work, which in the case of this
     * application means the data that constitutes the page DOM.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        StringWriter sw = new StringWriter();

	// LOAD IN WORKSPACE
	DataManager dataManager = (DataManager)data;
        PoseMaker app = (PoseMaker) dataManager.getApp();

	// THEN THE TREE
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
        BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
        BorderPane left = (BorderPane) pmWorkspace1.getLeft();
        BorderPane toolBar = (BorderPane) left.getTop();
        BorderPane mid = (BorderPane) toolBar.getCenter();
        VBox backgroundColor = (VBox) mid.getTop();
        ColorPicker cp = (ColorPicker) backgroundColor.getChildren().get(1);
        Pane root = (Pane) pmWorkspace1.getCenter();
	fillArrayWithShapes(root, arrayBuilder, cp);
	JsonArray nodesArray = arrayBuilder.build();
	
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add("SHAPE_TREE", nodesArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    int shapeCounter;
    
    private void fillArrayWithShapes(Pane root, JsonArrayBuilder arrayBuilder, ColorPicker cp) {
	shapeCounter = 0;
	
	JsonObject tagObject = makeTagJsonObject(cp);
	arrayBuilder.add(tagObject);
	
	addChildrenToTagTreeJsonObject(root, arrayBuilder);
    }
    
    // HELPER METHOD FOR SAVING DATA TO A JSON FORMAT
    private JsonObject makeTagJsonObject(ColorPicker cp) {
	String background = 
        cp.getValue().toString();
        //System.out.println(cp.getValue().toString());
        //System.out.println(UIManager.getColor(canvas.getStyle()));
	//HashMap<String, String> attributes = tag.getAttributes();
	//ArrayList<String> legalParents = tag.getLegalParents();
	JsonObject jso = Json.createObjectBuilder()
                .add("Background Color", background)
		.build();
	return jso;
    }  
    
        // HELPER METHOD FOR SAVING DATA TO A JSON FORMAT
        private void addChildrenToTagTreeJsonObject(Pane canvas, JsonArrayBuilder arrayBuilder) {
	ObservableList<Node> children = canvas.getChildren();
	for (Node n : children) {
            System.out.println(n.getClass().toString());
            if(n.getClass().toString().equals(ELLIPSE)) {
                Ellipse e = (Ellipse) n;
                JsonObject jso = Json.createObjectBuilder()
                    .add("Ellipse", shapeCounter)
                    .add("CenterX", e.getCenterX())
                    .add("CenterY", e.getCenterY())
                    .add("RadiusX", e.getRadiusX())    
                    .add("RadiusY", e.getRadiusY())
                    .add("Translate X", e.getTranslateX())
                    .add("Translate Y", e.getTranslateY())
                    .add("Fill", e.getFill().toString())
                    .add("Stroke", e.getStroke().toString())
                    .add("StrokeWidth", e.getStrokeWidth())    
                    .build();
                arrayBuilder.add(jso);
                shapeCounter ++;
                
            } else if (n.getClass().toString().equals(RECT)){
                Rectangle r = (Rectangle) n;
                JsonObject jso = Json.createObjectBuilder()
                    .add("Rectangle", shapeCounter)
                    .add("X", r.getX())
                    .add("Y", r.getY())
                    .add("Width", r.getWidth())    
                    .add("Height", r.getHeight())
                    .add("Translate X", r.getTranslateX())
                    .add("Translate Y", r.getTranslateY())
                    .add("Fill", r.getFill().toString())
                    .add("Stroke", r.getStroke().toString())
                    .add("StrokeWidth", r.getStrokeWidth())    
                    .build();
                arrayBuilder.add(jso);
                shapeCounter ++;
            
            }
	}
    }
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        // CLEAR THE OLD DATA OUT
	DataManager dataManager = (DataManager)data;
	dataManager.reset();
        
        // MARK IT AS LOAD FILE
        PoseMaker app = (PoseMaker) dataManager.getApp();
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.setLoaded(true);
	
	// LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);
        //System.out.println(filePath);
	
	// LOAD THE TAG TREE
	JsonArray jsonTagTreeArray = json.getJsonArray("SHAPE_TREE");
	loadTreeTags(jsonTagTreeArray, dataManager);
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    private void loadTreeTags(JsonArray jsonTagsArray, DataManager dataManager) {
        ArrayList<Node> nodes = new ArrayList();
        PoseMaker app = (PoseMaker) dataManager.getApp();
        BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
        BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
        BorderPane left = (BorderPane) pmWorkspace1.getLeft();
        BorderPane toolBar = (BorderPane) left.getTop();
        BorderPane mid = (BorderPane) toolBar.getCenter();
        VBox backgroundColor = (VBox) mid.getTop();
        ColorPicker cp = (ColorPicker) backgroundColor.getChildren().get(1);
        Pane canvas = (Pane) pmWorkspace1.getCenter();
	
        //System.out.println(jsonTagsArray);
        
	// FIRST UPDATE THE ROOT
	JsonObject rootJso = jsonTagsArray.getJsonObject(0);
        cp.setValue(Color.valueOf(rootJso.getString("Background Color")));
        canvas.setStyle("-fx-background-color: #" +  rootJso.getString("Background Color").substring(2) +";");
        
        //System.out.println(rootJso);
	// HTMLTagPrototype rootData = loadTag(rootJso);
	// TreeItem root = dataManager.getHTMLRoot();
	// nodes.add(root);
	// root.getChildren().clear();
	// root.setValue(rootData);
    }    
    
    /**
     * This method exports the contents of the data manager to a 
     * Web page including the html page, needed directories, and
     * the CSS file.
     * 
     * @param data The data management component.
     * 
     * @param filePath Path (including file name/extension) to where
     * to export the page to.
     * 
     * @throws IOException Thrown should there be an error writing
     * out data to the file.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {

    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// NOTE THAT THE Web Page Maker APPLICATION MAKES
	// NO USE OF THIS METHOD SINCE IT NEVER IMPORTS
	// EXPORTED WEB PAGES
    }
}
