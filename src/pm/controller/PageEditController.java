package pm.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.web.WebEngine;
import javax.imageio.ImageIO;
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

    private double bX, bY; // Starting point for drawing
    private double eX, eY; // Ending point for drawing
    private double bX1, bY1; // Starting point for selecting
    private double bX2, bY2; // Starting point for selecting and dragging
    private double eX1, eY1; // Ending point for selecting and dragging
    private double scX, scY;
    
    private Shape selectedItem = null;
    private Shape lastItem = null;
    private Color lastColor;
    private double lastWidth;
    private ArrayList<Shape> shapes = new ArrayList();
    private Rectangle rect;
    private Ellipse ellipse;
    
    private boolean selected = false;
    
    private String bgColor = "ffef84";
    private String fColor = "Color.WHITE";
    private String otColor = "Color.WHITE";
    
    private Color fill = Color.WHITE;
    private Color outline = Color.WHITE;
    private double thickness = 0;
    
    private boolean enabled;
    
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
        if(enabled) {                
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            // FIRST, CHANGE THE SIZE OF THE CURSOR
            app.getGUI().getAppPane().setCursor(Cursor.CROSSHAIR);
        
            // THEN DRAW
            canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    bX = mouseEvent.getX();
                    bY = mouseEvent.getY();
                    eX = bX;
                    eY = bY;
                }
            }); 
        
            canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    drawRect(canvas);
                    deleteRect(canvas);
                }
            }); 
        
            canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    eX = mouseEvent.getX();
                    eY = mouseEvent.getY();
                    deleteRect(canvas);
                    drawRect(canvas);
                }
            });
        }
    }

    private void drawRect(Pane canvas) {
        // MARK THE FILE AS EDITED
        AppFileController afc = new AppFileController(app);
        afc.markAsEdited(app.getGUI());
        rect = new Rectangle(bX, bY, eX - bX, eY - bY);
        rect.setFill(fill);
        rect.setStroke(outline);
        rect.setStrokeWidth(thickness);
        canvas.getChildren().add(rect);
        shapes.add(rect);
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.refreshButtons(selected);
    }
    
    private void deleteRect(Pane canvas) {
        canvas.getChildren().remove(rect);
        shapes.remove(rect);
    }
    
    
    public void addEllipse() {
        if(enabled) {
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            // FIRST, CHANGE THE SIZE OF THE CURSOR
            app.getGUI().getAppPane().setCursor(Cursor.CROSSHAIR);
        
            // THEN DRAW
            canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    bX = mouseEvent.getX();
                    bY = mouseEvent.getY();
                    eX = bX;
                    eY = bY;
                }
            }); 
        
            canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    drawEllipse(canvas);
                    deleteEllipse(canvas);
                }
            }); 
        
            canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    eX = mouseEvent.getX();
                    eY = mouseEvent.getY();
                    deleteEllipse(canvas);
                    drawEllipse(canvas);
                }
            }); 
        }
    }

    private void drawEllipse(Pane canvas) {
        // MARK THE FILE AS EDITED
        AppFileController afc = new AppFileController(app);
        afc.markAsEdited(app.getGUI());
        ellipse = new Ellipse((eX + bX) / 2, (eY + bY) / 2, (eX - bX) / 2, (eY - bY) / 2);
        ellipse.setFill(fill);
        ellipse.setStroke(outline);
        ellipse.setStrokeWidth(thickness);
        canvas.getChildren().add(ellipse);
        shapes.add(ellipse);
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.refreshButtons(selected);
    }
    
    private void deleteEllipse(Pane canvas) {
        canvas.getChildren().remove(ellipse);
        shapes.remove(ellipse);
    }
    
    public void selectShape() {
        if(enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            // FIRST, CHANGE THE SIZE OF THE CURSOR
            app.getGUI().getAppPane().setCursor(Cursor.DEFAULT);
        
            // THEN SELECT THE SHAPE
            canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                    public void handle(MouseEvent mouseEvent) {
                    scX = mouseEvent.getSceneX();
                    scY = mouseEvent.getSceneY();
                    bX1 = mouseEvent.getX();
                    bY1 = mouseEvent.getY();
                    select();
                    if(selected) {
                        bX2 = selectedItem.getTranslateX();
                        bY2 = selectedItem.getTranslateY();
                    }
                }
            });
        
            // DRAG AND DROP TO RELOCATE THE SHAPE
            canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (selected) {
                        // Mark as edited
                        afc.markAsEdited(app.getGUI());
                        double offsetX = mouseEvent.getSceneX() - scX;
                        double offsetY = mouseEvent.getSceneY() - scY;
                        double newTranslateX = bX2 + offsetX;
                        double newTranslateY = bY2 + offsetY;
                        if(selectedItem.isPressed()) {
                            selectedItem.setTranslateX(newTranslateX);
                            selectedItem.setTranslateY(newTranslateY);
                        }
                    }
                }
            });
        }
    }
    
    private void select() {
        boolean contains = false;
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
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
            fill = (Color) selectedItem.getFill();
            outline = (Color) selectedItem.getStroke();
            thickness = selectedItem.getStrokeWidth();
            workspace.updateTools((Color) selectedItem.getFill(), (Color) selectedItem.getStroke(), selectedItem.getStrokeWidth());
            lastColor = (Color) selectedItem.getStroke();
            lastWidth = selectedItem.getStrokeWidth();
            selectedItem.setStroke(Color.YELLOW); 
            if(selectedItem.getStrokeWidth() >= 2){
                selectedItem.setStrokeWidth(selectedItem.getStrokeWidth());
            } else {
                selectedItem.setStrokeWidth(2);
            }
            lastItem = selectedItem;
            selected = true;
        } else {
            if(selected) {
                lastItem.setStroke(lastColor);
                lastItem.setStrokeWidth(lastWidth);
                selectedItem = null;
                lastItem = null;
                selected = false;
            }
        }
        workspace.refreshButtons(selected);
    }
    
    public void removeShape() {
        if(enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
                
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            canvas.getChildren().remove(selectedItem);
            shapes.remove(selectedItem);
            selected =  false;
            Workspace workspace = (Workspace) app.getWorkspaceComponent();
            workspace.refreshButtons(selected);
        }
    }
    
    
    public void moveToFront() {
        if(enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
                
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            if(selected) {
                int index = canvas.getChildren().indexOf(selectedItem);
                if (index + 1 < canvas.getChildren().size())
                    swap(index, index + 1);
            }
        }
    }
    
    public void moveToBack() {
        if(enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
            
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            if(selected){
                int index = canvas.getChildren().indexOf(selectedItem);
                if (index >= 1)
                    swap(index, index - 1);
            }
        }
    }
    
    private void swap(int x, int y) {
        BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
        BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
        Pane canvas = (Pane) pmWorkspace1.getCenter();
        Shape shapeToSwap = (Shape) canvas.getChildren().get(y);
        Circle temp = new Circle();
        canvas.getChildren().set(x, temp);
        canvas.getChildren().set(y, selectedItem);
        canvas.getChildren().set(x, shapeToSwap);
    }

    public void changeBackgroundColor(ColorPicker color) {
        if(enabled) {
            // MARK THE FILE AS EDITED
            AppFileController afc = new AppFileController(app);
            afc.markAsEdited(app.getGUI());
            
            bgColor = color.getValue().toString();
            bgColor = bgColor.substring(2);
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            pmWorkspace1.getCenter().setStyle("-fx-background-color: #" +  bgColor +";");
        }
    }

    public void changFillColor(ColorPicker color) {
        if(enabled) {
        fColor = color.getValue().toString();
        fColor = fColor.substring(2);
        fill = Color.valueOf(fColor);  
        if(selected) {    
            updateSelectedItem();
        }
        }
    }

    public void changeOutlineColor(ColorPicker color) {
        if(enabled) {
        otColor = color.getValue().toString();
        otColor = otColor.substring(2);
        outline = Color.valueOf(otColor);
        if(selected) {    
            updateSelectedItem();
        }
        }
    }
    
    public void changeOutlineThickness(Slider s) {
        if(enabled) {
            thickness = s.getValue();
            if(selected) {    
                updateSelectedItem();
            }
        }
    }
    
    private void updateSelectedItem() {
        // MARK THE FILE AS EDITED
        AppFileController afc = new AppFileController(app);
        afc.markAsEdited(app.getGUI());
        
        selectedItem.setFill(fill);
        lastColor = outline;
        lastWidth = thickness;
        if(thickness >= 2){
            selectedItem.setStrokeWidth(thickness);
        } else {
            selectedItem.setStrokeWidth(2);
        }
    }
    
    public void enable(boolean isAble) {
        enabled = isAble;
    }

    public void snapShot() {
        try {
            BorderPane pmWorkspace = (BorderPane) app.getGUI().getAppPane().getCenter();
            BorderPane pmWorkspace1 = (BorderPane) pmWorkspace.getCenter();
            Pane canvas = (Pane) pmWorkspace1.getCenter();
            SnapshotParameters parameters = new SnapshotParameters();
            WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), wi);

            File output = new File("Pose.png");
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", output);
            } catch (IOException ex) {
                // do sth
            }
        
    }

    public void clearArray() {
        shapes.clear();
    }

    public void addToArray(Shape s) {
        shapes.add(s);
    }

}
