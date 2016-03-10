package pm.data;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import pm.gui.Workspace;
import saf.components.AppDataComponent;
import saf.AppTemplate;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author Zhe Lin
 * @version 1.0
 */
public class DataManager implements AppDataComponent {
    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;

    /**
     * THis constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public DataManager(AppTemplate initApp) throws Exception {
	// KEEP THE APP FOR LATER
	app = initApp;
    }

    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void reset() {
	// NOW MAKE THE NODES
	Workspace workspace = (Workspace) app.getWorkspaceComponent();
        workspace.reloadWorkspace();
        
    }
    
    public AppTemplate getApp() {
        return app;
    }
}
