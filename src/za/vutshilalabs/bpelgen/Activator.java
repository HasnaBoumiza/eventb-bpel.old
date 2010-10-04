package za.vutshilalabs.bpelgen;

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
public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "za.vutshilalabs.bpelgen"; //$NON-NLS-1$

	public static void errorDialog(final Exception exception, String message) {
		Shell shell = getShell();
		IStatus status = new Status(IStatus.ERROR, getId(), IStatus.OK,
				exception.getMessage(), exception);
		ErrorDialog.openError(shell, "BPEL Generator Error", message, status);

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
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

	public static void logError(final Exception e, final String message) {
		IStatus status = new Status(IStatus.ERROR, getId(), IStatus.ERROR,
				message, e);
		getDefault().getLog().log(status);
	}

	public static void logInfo(String message) {
		getDefault().getLog().log(
				new Status(IStatus.INFO, getId(), IStatus.OK, message, null));

	}

	/**
	 * The constructor
	 */
	public Activator() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
