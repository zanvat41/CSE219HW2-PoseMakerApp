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
import pm.controller.PageEditController;
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
	JsonObject jso = Json.createObjectBuilder()
                .add("Background Color", background)
		.build();
	return jso;
    }  
    
        // HELPER METHOD FOR SAVING DATA TO A JSON FORMAT
        private void addChildrenToTagTreeJsonObject(Pane canvas, JsonArrayBuilder arrayBuilder) {
	ObservableList<Node> children = canvas.getChildren();
	for (Node n : children) {
            if(n.getClass().toString().equals(ELLIPSE)) {
                Ellipse e = (Ellipse) n;
                JsonObject jso = Json.createObjectBuilder()
                    .add("Shape", "Ellipse")
                    .add("CenterX", ((Double) e.getCenterX()).toString())
                    .add("CenterY", ((Double) e.getCenterY()).toString())
                    .add("RadiusX", ((Double) e.getRadiusX()).toString())    
                    .add("RadiusY", ((Double) e.getRadiusY()).toString())
                    .add("TranslateX", ((Double) e.getTranslateX()).toString())
                    .add("TranslateY", ((Double) e.getTranslateY()).toString())
                    .add("Fill", e.getFill().toString())
                    .add("Stroke", e.getStroke().toString())
                    .add("StrokeWidth", ((Double) e.getStrokeWidth()).toString())    
                    .build();
                arrayBuilder.add(jso);
                shapeCounter ++;
                
            } else if (n.getClass().toString().equals(RECT)){
                Rectangle r = (Rectangle) n;
                JsonObject jso = Json.createObjectBuilder()
                    .add("Shape", "Rect")
                    .add("X", ((Double) r.getX()).toString())
                    .add("Y", ((Double) r.getY()).toString())
                    .add("Width", ((Double) r.getWidth()).toString())    
                    .add("Height", ((Double) r.getHeight()).toString())
                    .add("TranslateX", ((Double) r.getTranslateX()).toString())
                    .add("TranslateY", ((Double) r.getTranslateY()).toString())
                    .add("Fill", r.getFill().toString())
                    .add("Stroke", r.getStroke().toString())
                    .add("StrokeWidth", ((Double) r.getStrokeWidth()).toString())    
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
        PoseMaker app = (PoseMaker) dataManager.getApp();
        app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
        BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
        BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
        BorderPane left = (BorderPane) pmWorkspace1.getLeft();
        BorderPane toolBar = (BorderPane) left.getTop();
        BorderPane mid = (BorderPane) toolBar.getCenter();
        VBox backgroundColor = (VBox) mid.getTop();
        ColorPicker cp = (ColorPicker) backgroundColor.getChildren().get(1);
        Pane canvas = (Pane) pmWorkspace1.getCenter();
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        PageEditController ctrl = workspace.getController();
        
	// FIRST UPDATE THE ROOT
	JsonObject rootJso = jsonTagsArray.getJsonObject(0);
        cp.setValue(Color.valueOf(rootJso.getString("Background Color")));
        canvas.setStyle("-fx-background-color: #" +  rootJso.getString("Background Color").substring(2) +";");
        
        
        
        // AND NOW JUST GO THROUGH THE REST OF THE ARRAY
        ctrl.clearArray();
	for (int i = 1; i < jsonTagsArray.size(); i++) {
	    JsonObject nodeJso = jsonTagsArray.getJsonObject(i);
            if (nodeJso.getString("Shape").equals("Ellipse")) {
                double centerX, centerY, radiusX, radiusY, translateX, translateY, sW;
                String fill, stroke;
                centerX = Double.parseDouble(nodeJso.getString("CenterX"));
                centerY = Double.parseDouble(nodeJso.getString("CenterY"));
                radiusX = Double.parseDouble(nodeJso.getString("RadiusX"));
                radiusY = Double.parseDouble(nodeJso.getString("RadiusY"));
                translateX = Double.parseDouble(nodeJso.getString("TranslateX"));
                translateY = Double.parseDouble(nodeJso.getString("TranslateY"));
                sW = Double.parseDouble(nodeJso.getString("StrokeWidth"));
                fill = nodeJso.getString("Fill");
                stroke = nodeJso.getString("Stroke");
                Ellipse e = new Ellipse((double) centerX, (double) centerY, (double) radiusX, (double) radiusY);
                e.setTranslateX(translateX);
                e.setTranslateY(translateY);
                e.setStroke(Color.valueOf(stroke));
                e.setStrokeWidth(sW);
                e.setFill(Color.valueOf(fill));
                canvas.getChildren().add(e);
                ctrl.addToArray(e);
            } else if(nodeJso.getString("Shape").equals("Rect")) {
                double X, Y, width, height, translateX, translateY, sW;
                String fill, stroke;
                X = Double.parseDouble(nodeJso.getString("X"));
                Y = Double.parseDouble(nodeJso.getString("Y"));
                width = Double.parseDouble(nodeJso.getString("Width"));
                height = Double.parseDouble(nodeJso.getString("Height"));
                translateX = Double.parseDouble(nodeJso.getString("TranslateX"));
                translateY = Double.parseDouble(nodeJso.getString("TranslateY"));
                sW = Double.parseDouble(nodeJso.getString("StrokeWidth"));
                fill = nodeJso.getString("Fill");
                stroke = nodeJso.getString("Stroke");
                Rectangle r = new Rectangle((double) X, (double) Y, (double) width, (double) height);
                r.setTranslateX(translateX);
                r.setTranslateY(translateY);
                r.setStroke(Color.valueOf(stroke));
                r.setStrokeWidth(sW);
                r.setFill(Color.valueOf(fill));
                canvas.getChildren().add(r);
                ctrl.addToArray(r);
            
            }
            
	}
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
