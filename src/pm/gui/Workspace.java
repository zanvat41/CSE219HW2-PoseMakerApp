package pm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import pm.PoseMaker;
import pm.controller.PageEditController;
import properties_manager.PropertiesManager;
import saf.ui.AppGUI;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import saf.ui.AppMessageDialogSingleton;
import saf.ui.AppYesNoCancelDialogSingleton;
import pm.file.FileManager;
import pm.data.DataManager;
import pm.controller.PageEditController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author Zhe Lin
 * @version 1.0
 */
public class Workspace extends AppWorkspaceComponent {
    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS Workspace'S COMPONENTS TO A STYLE SHEET THAT IT USES
    static final String CLASS_MAX_PANE = "max_pane";
    static final String CLASS_RENDER_CANVAS = "render_canvas";
    static final String CLASS_BORDER_PANE = "bordered_pane";
    static final String CLASS_EDIT_TOOLBAR = "edit_toolbar";
    static final String CLASS_SUBHEADING_LABEL = "subheading_label";
    
    
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;
    
    // THIS HANDLES INTERACTIONS WITH PAGE EDITING CONTROLS
    PageEditController pageEditController;
    
    // WE'LL PUT THE WORKSPACE INSIDE A BORDER PANE
    BorderPane poseWorkSpace;

    // THESE ARE THE BUTTONS FOR ADDING AND REMOVING COMPONENTS
    BorderPane leftPane;
    BorderPane tagToolbar;
    BorderPane ButtonPane;
    BorderPane ColorPane;
    BorderPane BottomTool;
    FlowPane ShapeButtons;
    FlowPane FrontBack;
    VBox BackGroundColor;
    VBox FillColor;
    VBox OutlineColor;
    VBox Thickness;
    FlowPane Snapshot;
    
    // THE CANVAS
    Pane rightPane;
    
    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;

    // THE BUTTONS
    Button selectButton;
    Button removeButton;
    Button rectButton;
    Button ellipseButton;
    Button frontButton;
    Button backButton;
    Button bgcButton;
    Button fcButton;
    Button otcButton;
    Button SnapshotButton;
    
    // LABELS
    Label bgColor;
    Label fColor;
    Label otColor;
    Label otThick;
    
    // SLIDER
    Slider outlineSlider;
    
    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public Workspace(AppTemplate initApp) throws IOException {
	// KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();
        
        // THIS WILL PROVIDE US WITH OUR CUSTOM UI SETTINGS AND TEXT
	PropertiesManager propsSingleton = PropertiesManager.getPropertiesManager();

	// WE'LL ORGANIZE OUR WORKSPACE COMPONENTS USING A BORDER PANE
        BorderPane workspace = gui.getAppPane();
        
	poseWorkSpace = new BorderPane();

	// FIRST THE LEFT HALF OF THE SPLIT PANE
	leftPane = new BorderPane();

        // THEN THE RIGHT HALF OF THE SPLIT PANE
        rightPane = new Pane();
        
	// THIS WILL MANAGE ALL EDITING EVENTS
	pageEditController = new PageEditController((PoseMaker) app);

	// THIS IS THE TOP TOOLBAR
	tagToolbar = new BorderPane();
        ButtonPane = new BorderPane();
        ColorPane = new BorderPane();
        BottomTool = new BorderPane();
        ShapeButtons = new FlowPane();
        FrontBack = new FlowPane();
        BackGroundColor = new VBox();
        FillColor = new VBox();
        OutlineColor = new VBox();
        Thickness = new VBox();
        Snapshot = new FlowPane();


	// LOAD ALL SHAPES
	FileManager fileManager = (FileManager) app.getFileComponent();
	DataManager dataManager = (DataManager) app.getDataComponent();

        tagToolbar.setMaxWidth(275);
        tagToolbar.setMaxHeight(800);
        
        // INITIAL THE STYLE OF THE TOP TWO PANES
        ShapeButtons.setHgap(15);
        ShapeButtons.setPadding(new Insets(15));
        
        FrontBack.setHgap(15);
        FrontBack.setPadding(new Insets(15));
        
        // THE BUTTONS OF THE TOP TWO PANES
        selectButton = new Button();
        Image selectImage = new Image("file:./images/SelectionTool.png");
        selectButton.setGraphic(new ImageView(selectImage));
	ShapeButtons.getChildren().add(selectButton);
   
        removeButton = new Button();
        Image removeImage = new Image("file:./images/Remove.png");
        removeButton.setGraphic(new ImageView(removeImage));
	ShapeButtons.getChildren().add(removeButton);
        
        rectButton = new Button();
        Image rectImage = new Image("file:./images/Rect.png");
        rectButton.setGraphic(new ImageView(rectImage));
        rectButton.setOnAction(e -> {
	    pageEditController.addRect();
	});
	ShapeButtons.getChildren().add(rectButton);
        
        ellipseButton = new Button();
        Image ellipseImage = new Image("file:./images/Ellipse.png");
        ellipseButton.setGraphic(new ImageView(ellipseImage));
	ShapeButtons.getChildren().add(ellipseButton);
        
        frontButton = new Button();
        Image frontImage = new Image("file:./images/MoveToFront.png");
        frontButton.setGraphic(new ImageView(frontImage));
	FrontBack.getChildren().add(frontButton);
        
        backButton = new Button();
        Image backImage = new Image("file:./images/MoveToBack.png");
        backButton.setGraphic(new ImageView(backImage));
	FrontBack.getChildren().add(backButton);
        
        // FOR THE BUTTONS SPACES
        ButtonPane.setTop(ShapeButtons);
        ButtonPane.setBottom(FrontBack);
        
        // BACKGROUND COLOR
        BackGroundColor.setPadding(new Insets(15));
        BackGroundColor.setSpacing(15);
        
        bgColor = new Label("Background Color");
        BackGroundColor.getChildren().add(bgColor);
        
        bgcButton = new Button("background color");
        BackGroundColor.getChildren().add(bgcButton);
        
        // FILL COLOR
        FillColor.setPadding(new Insets(15));
        FillColor.setSpacing(15);
        
        fColor = new Label("Fill Color");
        FillColor.getChildren().add(fColor);
        
        fcButton = new Button("fill color");
        FillColor.getChildren().add(fcButton);
        
        // OUTLINE COLOR
        OutlineColor.setPadding(new Insets(15));
        OutlineColor.setSpacing(15);
        
        otColor = new Label("Outline Color");
        OutlineColor.getChildren().add(otColor);
        
        otcButton = new Button("outline color");
        OutlineColor.getChildren().add(otcButton);
        
        // FOR COLOR SPACES
        ColorPane.setTop(BackGroundColor);
        ColorPane.setCenter(FillColor);
        ColorPane.setBottom(OutlineColor);

        // FOR THICKNESS TOOL
        Thickness.setPadding(new Insets(15));
        Thickness.setSpacing(15);
        
        otThick = new Label("Outline Thickness");
        Thickness.getChildren().add(otThick);
        
        outlineSlider = new Slider();
        Thickness.getChildren().add(outlineSlider);
        
        // FOR SNAP SHOT
        Snapshot.setPadding(new Insets(15));
        
        
        SnapshotButton = new Button();
        Image ssImage = new Image("file:./images/Snapshot.png");
        SnapshotButton.setGraphic(new ImageView(ssImage));
	Snapshot.getChildren().add(SnapshotButton);
        
        
        // FOR BOTTOM 2 TOOLS
        BottomTool.setTop(Thickness);
        BottomTool.setCenter(Snapshot);
        
        // SET UP THE SPACES FOR TOOLS
        tagToolbar.setTop(ButtonPane);
        tagToolbar.setCenter(ColorPane);
        tagToolbar.setBottom(BottomTool);

	// PUT THEM IN THE LEFT
	leftPane.setTop(tagToolbar);

        
        // AND NOW PUT IT IN THE WORKSPACE
	poseWorkSpace.setLeft(leftPane);
	poseWorkSpace.setCenter(rightPane);

	// AND FINALLY, LET'S MAKE THE SPLIT PANE THE WORKSPACE
        workspace.setCenter(poseWorkSpace);

        // NOTE THAT WE HAVE NOT PUT THE WORKSPACE INTO THE WINDOW,
	// THAT WILL BE DONE WHEN THE USER EITHER CREATES A NEW
	// COURSE OR LOADS AN EXISTING ONE FOR EDITING
	workspaceActivated = false;
        
    }
    
    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    @Override
    public void initStyle() {
	// NOTE THAT EACH CLASS SHOULD CORRESPOND TO
	// A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
	// CSS FILE
        rightPane.getStyleClass().add(CLASS_RENDER_CANVAS);
        leftPane.getStyleClass().add(CLASS_BORDER_PANE);
        ShapeButtons.getStyleClass().add(CLASS_MAX_PANE);
        FrontBack.getStyleClass().add(CLASS_MAX_PANE);
        BackGroundColor.getStyleClass().add(CLASS_MAX_PANE);
        FillColor.getStyleClass().add(CLASS_MAX_PANE);
        OutlineColor.getStyleClass().add(CLASS_MAX_PANE);
        bgColor.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        fColor.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        otColor.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        Thickness.getStyleClass().add(CLASS_MAX_PANE);
        otThick.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        Snapshot.getStyleClass().add(CLASS_MAX_PANE);
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {

    }
}
