package pm.controller;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.web.WebEngine;
import properties_manager.PropertiesManager;
import saf.controller.AppFileController;
import saf.ui.AppMessageDialogSingleton;
import saf.ui.AppYesNoCancelDialogSingleton;
import pm.PoseMaker;
import pm.data.DataManager;
import pm.file.FileManager;
import pm.gui.Workspace;

/**
 * This class provides event programmed responses to workspace interactions for
 * this application for things like adding elements, removing elements, and
 * editing them.
 *
 * @author Zhe Lin
 * @version 1.0
 */
public class PageEditController {

    // HERE'S THE FULL APP, WHICH GIVES US ACCESS TO OTHER STUFF
    PoseMaker app;

    double bX, bY; // Starting point for drawing
    double eX, eY; // Ending point for drawing
    double bX1, bY1; // Starting point for selecting
    double bX2, bY2; // Starting point for selecting and dragging
    double eX1, eY1; // Ending point for selecting and dragging
    double scX, scY;
    
    Shape selectedItem = null;
    Shape lastItem = null;
    Color lastColor;
    double lastWidth;
    ArrayList<Shape> shapes = new ArrayList();
    Rectangle rect;
    Ellipse ellipse;
    
    boolean selected = false;
    
    String bgColor = "ffef84";
    String fColor = "Color.WHITE";
    String otColor = "Color.WHITE";
    
    Color fill = Color.WHITE;
    Color outline = Color.WHITE;
    double thickness = 0;
    
    /**
     * Constructor for initializing this object, it will keep the app for later.
     *
     * @param initApp The JavaFX application this controller is associated with.
     */
    public PageEditController(PoseMaker initApp) {
	// KEEP IT FOR LATER
	app = initApp;
    }

    /**
     * This mutator method lets us enable or disable this controller.
     *
     * @param enableSetting If false, this controller will not respond to
     * workspace editing. If true, it will.
     */
    /*public void enable(boolean enableSetting) {
	enabled = enableSetting;
    }*/
    
    public void addRect() {
        // FIRST, CHANGE THE SIZE OF THE CURSOR
        app.getGUI().getAppPane().setCursor(Cursor.CROSSHAIR);
        
        // THEN DRAW
        app.getGUI().getAppPane().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                bX = mouseEvent.getX();
                bY = mouseEvent.getY();
                eX = bX;
                eY = bY;
            }
        }); 
        
        app.getGUI().getAppPane().setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                drawRect();
                deleteRect();
            }
        }); 
        
        app.getGUI().getAppPane().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                eX = mouseEvent.getX();
                eY = mouseEvent.getY();
                deleteRect();
                drawRect();
            }
        }); 
    }

    private void drawRect() {
        rect = new Rectangle(bX, bY, eX - bX, eY - bY);
        rect.setFill(fill);
        rect.setStroke(outline);
        rect.setStrokeWidth(thickness);
        app.getGUI().getAppPane().getChildren().add(rect);
        shapes.add(rect);
    }
    
    private void deleteRect() {
        app.getGUI().getAppPane().getChildren().remove(rect);
        shapes.remove(rect);
    }
    
    
    public void addEllipse() {
        // FIRST, CHANGE THE SIZE OF THE CURSOR
        app.getGUI().getAppPane().setCursor(Cursor.CROSSHAIR);
        
        // THEN DRAW
        app.getGUI().getAppPane().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                bX = mouseEvent.getX();
                bY = mouseEvent.getY();
                eX = bX;
                eY = bY;
            }
        }); 
        
        app.getGUI().getAppPane().setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                drawEllipse();
                deleteEllipse();
            }
        }); 
        
        app.getGUI().getAppPane().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                eX = mouseEvent.getX();
                eY = mouseEvent.getY();
                deleteEllipse();
                drawEllipse();
            }
        }); 
    }

    private void drawEllipse() {
        ellipse = new Ellipse((eX + bX) / 2, (eY + bY) / 2, (eX - bX) / 2, (eY - bY) / 2);
        ellipse.setFill(fill);
        ellipse.setStroke(outline);
        ellipse.setStrokeWidth(thickness);
        app.getGUI().getAppPane().getChildren().add(ellipse);
        shapes.add(ellipse);
    }
    
    private void deleteEllipse() {
        app.getGUI().getAppPane().getChildren().remove(ellipse);
        shapes.remove(ellipse);
    }
    
    public void selectShape() {
        // FIRST, CHANGE THE SIZE OF THE CURSOR
        app.getGUI().getAppPane().setCursor(Cursor.DEFAULT);
        
        // THEN SELECT THE SHAPE
        app.getGUI().getAppPane().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                scX = mouseEvent.getSceneX();
                scY = mouseEvent.getSceneY();
                bX1 = mouseEvent.getX();
                bY1 = mouseEvent.getY();
                select();
                bX2 = selectedItem.getTranslateX();
                bY2 = selectedItem.getTranslateY();
            }
        });
        
        // DRAG AND DROP TO RELOCATE THE SHAPE
        app.getGUI().getAppPane().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double offsetX = mouseEvent.getSceneX() - scX;
                double offsetY = mouseEvent.getSceneY() - scY;
                double newTranslateX = bX2 + offsetX;
                double newTranslateY = bY2 + offsetY;
                if(selectedItem.isPressed()) {
                    selectedItem.setTranslateX(newTranslateX);
                    selectedItem.setTranslateY(newTranslateY);
                }
            }
        });
    }
    
    private void select() {
        Point2D p = new Point2D(bX1, bY1);
        boolean contains = false;
        for (Shape s : shapes) {
            if(s.isPressed()) {
                selectedItem = s;
                contains = true;
            }
        }
        if (contains) {
            if(selected){
                // Disselect the previous selected item
                lastItem.setStroke(lastColor);
                lastItem.setStrokeWidth(lastWidth);
            }
            
            lastColor = (Color) selectedItem.getStroke();
            lastWidth = selectedItem.getStrokeWidth();
            selectedItem.setStroke(Color.YELLOW); 
            selectedItem.setStrokeWidth(5);
            lastItem = selectedItem;
            selected = true;
        }
    }
    
    public void removeShape() {
        app.getGUI().getAppPane().getChildren().remove(selectedItem);
        shapes.remove(selectedItem);
        selected =  false;
    }
    
    
    public void moveToFront() {
        int index = app.getGUI().getAppPane().getChildren().indexOf(selectedItem);
        if (index + 1 < app.getGUI().getAppPane().getChildren().size())
            swap(index, index + 1);
    }
    
    public void moveToBack() {
        int index = app.getGUI().getAppPane().getChildren().indexOf(selectedItem);
        if (index >= shapes.size())
            swap(index, index - 1);
    }
    
    private void swap(int x, int y) {
        Shape shapeToSwap = (Shape) app.getGUI().getAppPane().getChildren().get(y);
        Circle temp = new Circle();
        app.getGUI().getAppPane().getChildren().set(x, temp);
        app.getGUI().getAppPane().getChildren().set(y, selectedItem);
        app.getGUI().getAppPane().getChildren().set(x, shapeToSwap);
    }

    public void changeBackgroundColor(ColorPicker color) {
        bgColor = color.getValue().toString();
        bgColor = bgColor.substring(2);
        BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
        pmWorkspace.getCenter().setStyle("-fx-background-color: #" +  bgColor +";");
    }

    public void changFillColor(ColorPicker color) {
        fColor = color.getValue().toString();
        fColor = fColor.substring(2);
        fill = Color.valueOf(fColor);  
        if(selected) {    
            updateSelectedItem();
        }
    }

    public void changeOutlineColor(ColorPicker color) {
        otColor = color.getValue().toString();
        otColor = otColor.substring(2);
        outline = Color.valueOf(otColor);
        if(selected) {    
            updateSelectedItem();
        }
        
    }
    
    public void changeOutlineThickness(Slider s) {
        thickness = s.getValue();
        if(selected) {    
            updateSelectedItem();
        }
    }
    
    private void updateSelectedItem() {
        selectedItem.setFill(fill);
        lastColor = outline;
        lastWidth = thickness;
    }
    
    /**
     * This function responds live to the user typing changes into a text field
     * for updating element attributes. It will respond by updating the
     * appropriate data and then forcing an update of the temp site and its
     * display.
     *
     * @param selectedTag The element in the DOM (our tree) that's currently
     * selected and therefore is currently having its attribute updated.
     *
     * @param attributeName The name of the attribute for the element that is
     * currently being updated.
     *
     * @param attributeValue The new value for the attribute that is being
     * updated.
     */
    /*public void handleAttributeUpdate(HTMLTagPrototype selectedTag, String attributeName, String attributeValue) {
	if (enabled) {
	    try {
                // MARK THE FILE AS EDITED
                AppFileController afc = new AppFileController(app);
                afc.markAsEdited(app.getGUI());
                
		// FIRST UPDATE THE ELEMENT'S DATA
		selectedTag.addAttribute(attributeName, attributeValue);

		// THEN FORCE THE CHANGES TO THE TEMP HTML PAGE
		FileManager fileManager = (FileManager) app.getFileComponent();
		fileManager.exportData(app.getDataComponent(), TEMP_PAGE);

		// AND FINALLY UPDATE THE WEB PAGE DISPLAY USING THE NEW VALUES
		Workspace workspace = (Workspace) app.getWorkspaceComponent();
		workspace.getHTMLEngine().reload();
	    } catch (IOException ioe) {
		// AN ERROR HAPPENED WRITING TO THE TEMP FILE, NOTIFY THE USER
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
		dialog.show(props.getProperty(ATTRIBUTE_UPDATE_ERROR_TITLE), props.getProperty(ATTRIBUTE_UPDATE_ERROR_MESSAGE));
	    }
	}
    } */

    /**
     * This function responds to when the user tries to add an element to the
     * tree being edited.
     *
     * @param element The element to add to the tree.
     */
    /*public void handleAddElementRequest(HTMLTagPrototype element) {
	if (enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
            
	    Workspace workspace = (Workspace) app.getWorkspaceComponent();

	    // GET THE TREE TO SEE WHICH NODE IS CURRENTLY SELECTED
	    TreeView tree = workspace.getHTMLTree();
	    TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
	    HTMLTagPrototype selectedTag = (HTMLTagPrototype) selectedItem.getValue();

	    // MAKE A NEW HTMLTagPrototype AND PUT IT IN A NODE
	    HTMLTagPrototype newTag = element.clone();
	    TreeItem newNode = new TreeItem(newTag);

	    // CHECK IF THE NEW TAG IS BEING ADDED TO A LEGAL PARENT 
            ArrayList<String> legals = newTag.getLegalParents();
            boolean isLegal = false;
            for(int i = 0; i < legals.size(); i++) {
                if(legals.get(i).equals(selectedTag.getTagName())) {
                     isLegal = true;
                }
            }
            // IF THE SELECTED NODE IS ITS LEGAL PARENT
            if(isLegal){
                // ADD THE NEW NODE
	        selectedItem.getChildren().add(newNode);
            
                // SELECT THE NEW NODE
                tree.getSelectionModel().select(newNode);
                selectedItem.setExpanded(true);
            } else {
                PropertiesManager props = PropertiesManager.getPropertiesManager();
		AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
		dialog.show(props.getProperty(ADD_ELEMENT_ILLEGAL_TITLE), props.getProperty(ADD_ELEMENT_ILLEGAL_MESSAGE));      
            }

	    // FORCE A RELOAD OF TAG EDITOR
	    workspace.reloadWorkspace();

	    try {
		FileManager fileManager = (FileManager) app.getFileComponent();
		fileManager.exportData(app.getDataComponent(), TEMP_PAGE);
	    } catch (IOException ioe) {
		// AN ERROR HAPPENED WRITING TO THE TEMP FILE, NOTIFY THE USER
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
		dialog.show(props.getProperty(ADD_ELEMENT_ERROR_TITLE), props.getProperty(ADD_ELEMENT_ERROR_MESSAGE));
	    }
	}
    }*/

     /**
     * This function responds to when the user tries to remove an element to the
     * tree being edited.
     */
    /*public void handleRemoveElementRequest() {
	if (enabled) {     
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
            
	    Workspace workspace = (Workspace) app.getWorkspaceComponent();
            PropertiesManager props = PropertiesManager.getPropertiesManager();

	    // GET THE TREE TO SEE WHICH NODE IS CURRENTLY SELECTED
	    TreeView tree = workspace.getHTMLTree();
	    TreeItem selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
	    HTMLTagPrototype selectedTag = (HTMLTagPrototype) selectedItem.getValue();
            
            // CHECK IF THE USER VERIFY THE EDIT
            // CHECK IF THE SELECTED ITEM IS LEGAL TO BE DELETED
            String name = selectedTag.getTagName();
            boolean isLegal = true;
            if(name.equals(TAG_HTML)) {
                isLegal = false;
            } else if(name.equals(TAG_HEAD)) {
                isLegal = false;
            } else if(name.equals(TAG_TITLE)) {
                isLegal = false;
            } else if(name.equals(TAG_LINK)) {
                isLegal = false;
            } else if(name.equals(TAG_BODY)) {
                isLegal = false;
            }
            
            // DELETE THAT NODE IF IT IS LEGAL TO
            if(isLegal) {
                // PROMPT THE USER TO VERIFY THE REMOVAL EDIT
                AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
                yesNoDialog.show(props.getProperty(REMOVAL_VERIFICATION_TITLE), props.getProperty(REMOVAL_VERIFICATION_MESSAGE));
                // AND NOW GET THE USER'S SELECTION
                String selection = yesNoDialog.getSelection();
                if (selection.equals(AppYesNoCancelDialogSingleton.YES)){
                    selectedItem.getParent().getChildren().remove(selectedItem);
                }
            } else {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(ILLEGAL_NODE_REMOVAL_ERROR_TITLE), props.getProperty(ILLEGAL_NODE_REMOVAL_ERROR_MESSAGE));
            }
            
	    // FORCE A RELOAD OF TAG EDITOR
	    workspace.reloadWorkspace();

	    try {
		FileManager fileManager = (FileManager) app.getFileComponent();
		fileManager.exportData(app.getDataComponent(), TEMP_PAGE);
	    } catch (IOException ioe) {
		// AN ERROR HAPPENED WRITING TO THE TEMP FILE, NOTIFY THE USER
		AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
		dialog.show(props.getProperty(REMOVE_ELEMENT_ERROR_TITLE), props.getProperty(REMOVE_ELEMENT_ERROR_MESSAGE));
	    }
	}
    }*/
    
    /**
     * This function provides a response to when the user changes the CSS
     * content. It responds but updating the data manager with the new CSS text,
     * and by exporting the CSS to the temp css file.
     *
     * @param cssContent The css content.
     *
     */
    /*public void handleCSSEditing(String cssContent) {
	if (enabled) {
	    try {
                // MARK THE FILE AS EDITED
                AppFileController afc = new AppFileController(app);
                afc.markAsEdited(app.getGUI());
                
		// MAKE SURE THE DATA MANAGER GETS THE CSS TEXT
		DataManager dataManager = (DataManager) app.getDataComponent();
		dataManager.setCSSText(cssContent);

		// WRITE OUT THE TEXT TO THE CSS FILE
		FileManager fileManager = (FileManager) app.getFileComponent();
		fileManager.exportCSS(cssContent, TEMP_CSS_PATH);

		// REFRESH THE HTML VIEW VIA THE ENGINE
		Workspace workspace = (Workspace) app.getWorkspaceComponent();
		WebEngine htmlEngine = workspace.getHTMLEngine();
		htmlEngine.reload();
	    } catch (IOException ioe) {
		AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
		PropertiesManager props = PropertiesManager.getPropertiesManager();
		dialog.show(props.getProperty(CSS_EXPORT_ERROR_TITLE), props.getProperty(CSS_EXPORT_ERROR_MESSAGE));
	    }
	}
    }*/
}
