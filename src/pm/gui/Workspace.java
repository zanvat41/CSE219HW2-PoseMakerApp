package pm.gui;

import java.io.IOException;
import saf.ui.AppGUI;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class Workspace extends AppWorkspaceComponent {

    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

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
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {

    }
}
