package za.vutshilalabs.bpelgen.popup.actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.xml.sax.InputSource;

import za.vutshilalabs.bpelgen.Activator;

public class ValidateAction implements IObjectActionDelegate {

	private static final String BPEL = "bpel";
	private static final String BPEL_SCHEMA_FILE = "schemas/ws-bpel_executable.xsd";
	private static final String WSDL = "wsdl";
	private static final String WSDL_SCHEMA_FILE = "schemas/wsdl.xsd";
	private static final String XML_SCHEMA_FILE = "schemas/xml.xsd";

	private Shell shell;

	/**
	 * Get the Schema for Validation
	 * 
	 * @param source
	 * @return
	 */
	private File getSchemaFile(IFile source) {
		Bundle bundle = Activator.getDefault().getBundle();
		String resource = null;
		if (source.getFileExtension().equals(WSDL)) {
			resource = WSDL_SCHEMA_FILE;
		} else if (source.getFileExtension().equals(BPEL)) {
			resource = BPEL_SCHEMA_FILE;
		}

		URL sourceUrl = bundle.getResource(resource);
		URL xmlUrl = bundle.getResource(XML_SCHEMA_FILE); // imported by BPEL Schema
		
		try {
			URL sourceFullPath = FileLocator.toFileURL(sourceUrl);
			URL xmlFullPath = FileLocator.toFileURL(xmlUrl);
			new File(xmlFullPath.getFile()); // extract xml.xsd
			return new File(sourceFullPath.getFile());
		} catch (IOException e) {
			Activator.errorDialog(e, "Error loading XML Schema file");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object obj = ss.getFirstElement();

			if (obj instanceof IFile) {
				IFile xml = (IFile) obj;
				String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
				SchemaFactory factory = SchemaFactory.newInstance(language);

				String ext = xml.getFileExtension();
				if (ext.equals(BPEL) || ext.equals(WSDL)) {
					try {
						Schema schema = factory.newSchema(getSchemaFile(xml));
						Validator validator = schema.newValidator();
						// validator.setErrorHandler(new XMLErrorHandler());
						SAXSource source = new SAXSource(new InputSource(
								new java.io.FileInputStream(xml.getLocation()
										.toString())));
						validator.validate(source);

						String message = "The validation of " + xml.getName()
								+ " completed without errors.\nDetected as "
								+ ext.toUpperCase() + " document.";
						MessageDialog.openInformation(shell,
								"Sercive Generator > Validator results",
								message);

					} catch (Exception e) {
						System.out.printf(
								"failed initialising schema, exception: %s \n",
								e.getMessage());
						String message = "The validation of " + xml.getName()
								+ " failed.\nTested as " + ext.toUpperCase()
								+ " document.";
						IStatus status = new Status(IStatus.ERROR,
								Activator.getId(), IStatus.OK, e.getMessage(),
								e);
						ErrorDialog.openError(shell,
								"Sercive Generator > Validator results",
								message, status);
					}
				}
			}

		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
}
