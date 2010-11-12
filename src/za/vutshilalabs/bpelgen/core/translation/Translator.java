/**
 *
 */
package za.vutshilalabs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.vutshilalabs.bpelgen.core.IGlobalConstants;
import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.XMLtool;

/**
 * @author Ernest Mashele
 * 
 */
public class Translator {

	/**
	 * Handle other machine files types
	 * 
	 * @param machineFile
	 */
	public static void translateEventb(IFile machineFile) {
		IProject project = machineFile.getProject();

		if (machineFile.getFileExtension().equals("bcm")) {
			// Handle .bcm file
			machineFile = project.getFile(machineFile.getName().replace("bcm",
					"bum"));
		}

		IRodinProject rodinProject = RodinHelper.getRodinProject(project);

		IMachineRoot machine = (IMachineRoot) rodinProject.getRodinFile(
				machineFile.getName()).getRoot();

		translateEventb(machine);
	}

	/**
	 * Create BPEL and WSDL file
	 * 
	 * @param machine
	 */
	public static void translateEventb(IMachineRoot machine) {

		
		IProject project = machine.getEventBProject().getRodinProject()
				.getProject();
		IFile bcmFile = project
				.getFile(machine.getElementName().concat(".bcm"));
		XMLtool xml = new XMLtool(false, bcmFile);
				Document doc = xml.getDocument();
		Element root = doc.getDocumentElement();
		String accurate = root.getAttribute(IGlobalConstants.EVENTB_ACCURATE);

		boolean isAccurate = accurate.equals("true") ? true : false;

		if (isAccurate) {
			// Create BPEL file
			String bpelFilename = FileManager.getBPELName(machine, machine.getRodinProject());
			IFile bpelFile = project.getFile(bpelFilename);
			MachineTranslator machineTrans = new MachineTranslator();
			machineTrans.init(machine);
			machineTrans.createFile(bpelFile);
			try {
				machine.getResource().setPersistentProperty(
						IGlobalConstants.BPEL, bpelFile.getName());
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

			Element seesContext = (Element) root.getElementsByTagName(
					IGlobalConstants.EVENTB_SEES_CONTEXT).item(0);
			String targetSrc = seesContext
					.getAttribute(IGlobalConstants.EVENTB_TARGET);
			// Get the context file
			String target = targetSrc.substring(targetSrc.lastIndexOf("/") + 1,
					targetSrc.length() - 4);
			// Get the context file
			IContextRoot context = machine.getEventBProject().getContextRoot(
					target);
			String wsdlFilename = FileManager.getWSDLName(context, context.getRodinProject());
			IFile wsdlFile = project.getFile(wsdlFilename);
			ContextTranslator wsdlW = new ContextTranslator();
			try {
				wsdlW.init(context, machine.getElementName());
				wsdlW.createFile(wsdlFile, null);
				context.getResource().setPersistentProperty(
						IGlobalConstants.WSDL, wsdlFile.getName());
				wsdlFile.setPersistentProperty(IGlobalConstants.CONTEXT,
						context.getElementName());
				bpelFile.setPersistentProperty(IGlobalConstants.CONTEXT,
						context.getElementName());
			} catch (Exception e) {
				System.err.printf("failed creating WSDL file. exception: %s\n",
						e.getMessage());
				e.printStackTrace();
			}

		}
	}
}
