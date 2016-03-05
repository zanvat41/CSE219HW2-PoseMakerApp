package pm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
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
    
    // THE CANVAS
    Pane rightPane;
    
    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;

    Button aButton;
    Button bButton;
    
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
	//tagToolbarScrollPane = new ScrollPane(tagToolbar);
	//tagToolbarScrollPane.setFitToHeight(true);


	// LOAD ALL SHAPES
	FileManager fileManager = (FileManager) app.getFileComponent();
	DataManager dataManager = (DataManager) app.getDataComponent();

        aButton = new Button("X");
	aButton.setMaxWidth(75);
	aButton.setMinWidth(75);
	aButton.setPrefWidth(75);
	tagToolbar.getChildren().add(aButton);
        
        bButton = new Button("X");
	bButton.setMaxWidth(75);
	bButton.setMinWidth(75);
	bButton.setPrefWidth(75);
	//rightPane.getChildren().add(bButton);
        

	// AND NOW THE REGION FOR EDITING TAG PROPERTIES
	//tagEditorPane = new GridPane();
	//tagEditorScrollPane = new ScrollPane(tagEditorPane);


	// PUT THEM IN THE LEFT
	leftPane.setCenter(tagToolbar);
	//leftPane.setBottom(tagEditorScrollPane);
        
        // AND NOW PUT IT IN THE WORKSPACE
	poseWorkSpace.setLeft(leftPane);
	poseWorkSpace.setCenter(rightPane);

	// AND FINALLY, LET'S MAKE THE SPLIT PANE THE WORKSPACE
        //workspace.setLeft(leftPane);
        //workspace.setRight(rightPane);
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
        //tagToolbar.getStyleClass().add(CLASS_MAX_PANE);
        leftPane.getStyleClass().add(CLASS_BORDER_PANE);
        rightPane.getStyleClass().add(CLASS_RENDER_CANVAS);
        //poseWorkSpace.getStyleClass().add(CLASS_BORDER_PANE);
    
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {

    }
}
