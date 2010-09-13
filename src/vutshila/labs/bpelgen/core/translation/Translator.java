/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vutshila.labs.bpelgen.BpelgenPlugin;
import vutshila.labs.bpelgen.core.EBConstant;
import vutshila.labs.bpelgen.core.XMLtool;

/**
 * @author Ernest Mashele
 * 
 */
public class Translator {

	/**
	 * Create BPEL and WSDL file
	 * 
	 * @param machine
	 */
	public static void translateEventb(IMachineRoot machine) {

		// IProject project = machine.getEventBProject().getRodinProject()
		// .getProject();
		//
		// // XXX move to a function in Translater class
		// IFile bcmFile = project
		// .getFile(machine.getElementName().concat(".bcm"));
		// XMLtool xml = new XMLtool(false, bcmFile);
		// Document doc = xml.getDocument();
		// Element root = doc.getDocumentElement();
		// String accurate = root.getAttribute(VLConstants.EVENTB_ACCURATE);
		// // XXX test this before translation
		// boolean isAccurate = accurate.equals("true") ? true : false;
		//
		// if (isAccurate) {
		// // Create BPEL file
		// IFile bpelFile = project.getFile(machine.getElementName().concat(
		// ".bpel"));
		// BPELwriter writer = new BPELwriter();
		// writer.init(machine);
		// writer.createFile(bpelFile);
		//
		// Element seesContext = (Element) root.getElementsByTagName(
		// VLConstants.EVENTB_CONTEXT).item(0);
		// String targetSrc = seesContext
		// .getAttribute(VLConstants.EVENTB_TARGET);
		// // Get the context file
		// String target = targetSrc.substring(targetSrc.lastIndexOf("/") + 1,
		// targetSrc.length() - 4);
		// // Get the context file
		// IContextRoot context = machine.getEventBProject().getContextRoot(
		// target);
		IContextRoot context = machine.getEventBProject().getContextRoot(
				"PurchaseOrderC");
		ContextTranslator trans = new ContextTranslator();
		trans.init(machine, context);
		trans.createTypes();
		// // Create WSDL file
		// IFile wsdlFile = project.getFile(machine.getElementName().concat(
		// ".wsdl"));
		// WSDLwriter wsdlWriter = new WSDLwriter(machine, context);
		// wsdlWriter.createFile(wsdlFile);
		// // Validate files
		// } else {
		// BpelgenPlugin
		// .logError(new Exception("Invalid event-b Machine"),
		// "There seems to be an error in the Machine you are trying to translate");
		//
		// }
	}

	/**
	 * Handle other machine files types
	 * 
	 * @param machineFile
	 */
	public static void translateEventb(IFile machineFile) {
		// TODO Auto-generated method stub
		IProject project = machineFile.getProject();

		if (machineFile.getFileExtension().equals("bcm")) {
			// Handle .bcm file
			machineFile = project.getFile(machineFile.getName().replace("bcm",
					"bum"));
		}

		IWorkspaceRoot wroot = project.getWorkspace().getRoot();
		IRodinDB rodinDB = RodinCore.valueOf(wroot);
		IRodinProject rodinProject = rodinDB.getRodinProject(project.getName());

		IMachineRoot machine = (IMachineRoot) rodinProject.getRodinFile(
				machineFile.getName()).getRoot();

		translateEventb(machine);
	}
}
