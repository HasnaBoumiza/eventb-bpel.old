package za.vutshilalabs.bpelgen.popup.actions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import za.vutshilalabs.bpelgen.Activator;

public class ValidateAction implements IObjectActionDelegate {

	private Shell shell;

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

				if (xml.getFileExtension().equals("bpel")) {
					try {
						Schema schema = factory.newSchema(new File(
								"ws-bpel_executable.xsd"));
						Validator validator = schema.newValidator();
						validator.setErrorHandler(new XMLErrorHandler());
						SAXSource source = new SAXSource(new InputSource(
								new java.io.FileInputStream(xml.getLocation()
										.toString())));
						validator.validate(source);
						Activator.logInfo(xml.getName()
								+ " validated successfully");

						System.out.printf("%s validated\n", xml.getName());
						MessageDialog.openInformation(shell, "BPEL Generator",
								xml.getName() + " validated successfully.");

					} catch (Exception e) {
						System.out.printf(
								"failed initialising schema, exception: %s \n",
								e.getMessage());
						Activator.errorDialog(e, e.getMessage());
					}
				} else if (xml.getFileExtension().equals("wsdl")) {
					getPluginFolder();
					System.out.print("done\n");
					// try {
					// Schema schema = factory.newSchema(new File(
					// Activator.getId().concat("wsdl.xsd")));
					// Validator validator = schema.newValidator();
					// validator.setErrorHandler(new XMLErrorHandler());
					// SAXSource source = new SAXSource(new InputSource(
					// new java.io.FileInputStream(xml.getLocation()
					// .toString())));
					// validator.validate(source);
					// System.out.printf("%s validated\n", xml.getName());
					// MessageDialog.openInformation(shell, "BPEL Generator",
					// xml.getName() + " validated successfully.");
					//
					// } catch (Exception e) {
					// System.out.printf(
					// "failed initialising schema, exception: %s \n",
					// e.getMessage());
					// }

				}
			}

		}

	}

	private File getPluginFolder() {
		URL url = Activator.getDefault().getBundle().getResource("wsdl.xsd");
		
		try {
			System.out.println(url.toURI().getRawPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new File(url.getPath());
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

	private static class XMLErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException exception) throws SAXException {
			String error = "(line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + "):\n"
					+ exception.getMessage();
			Activator.errorDialog(exception, error);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			String error = "(line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + "):\n"
					+ exception.getMessage();
			Activator.errorDialog(exception, error);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			String error = "(line " + exception.getLineNumber() + ", column "
					+ exception.getColumnNumber() + "):\n"
					+ exception.getMessage();
			Activator.errorDialog(exception, error);

		}

	}
}
