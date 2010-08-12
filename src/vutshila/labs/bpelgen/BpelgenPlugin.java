package vutshila.labs.bpelgen;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BpelgenPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "vutshila.labs.bpelgen";

    // The shared instance
    private static BpelgenPlugin plugin;

    public static IWorkspace workspace;

    /**
     * The constructor
     */
    public BpelgenPlugin() {
	super();
	if (plugin != null) {
	    throw new IllegalStateException("BpelgenPlugin is a singleton");
	}
	plugin = this;
    }

    /**
     * Display an errorDialog
     * 
     * @param exception
     * @param message
     */
    public static void errorDialog(final Exception exception, String message) {
	// XXX: create a messages class
	Shell shell = getShell();
	message = message + " " + exception.getLocalizedMessage();
	IStatus status = new Status(IStatus.ERROR, getId(), IStatus.OK,
		message, exception);
	ErrorDialog.openError(shell, "Bpelgen Error", message, status);

    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static BpelgenPlugin getDefault() {
	return plugin;
    }

    public static String getId() {
	return getDefault().getBundle().getSymbolicName();
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * 
     * @return the workspace instance
     */
    public static Shell getShell() {
	return getDefault().getWorkbench().getActiveWorkbenchWindow()
		.getShell();
    }

    /**
     * log an error in the global log
     * 
     * @param exception
     * @param message
     */
    public static void logError(final Exception exception, final String message) {
	IStatus status = new Status(IStatus.ERROR, getId(), IStatus.ERROR,
		message, exception);
	getDefault().getLog().log(status);
    }

    public static void logInfo(String message) {
	getDefault().getLog().log(
		new Status(IStatus.INFO, getId(), IStatus.OK, message, null));
    }

    /**
     * log a warning to the global log
     * 
     * @param exception
     * @param message
     */
    public static void logWarning(final Exception exception,
	    final String message) {
	IStatus status = new Status(IStatus.WARNING, getId(), IStatus.WARNING,
		message, exception);
	getDefault().getLog().log(status);
    }

    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	workspace = ResourcesPlugin.getWorkspace();
    }

    public void stop(BundleContext context) throws Exception {
	plugin = null;
	workspace = null;
	super.stop(context);
    }
}
