package za.vutshilalabs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import za.vutshilalabs.bpelgen.core.EBConstant;
import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.XMLtool;

/**
 * 
 * @author Ernest Mashele <mashern@tuks.co.za>
 * 
 */

public class BPELTranslator {
	private Document document;
	private Element process;
	private IInternalElement machine;

	/**
	 * Translate BPEL to Event-B Machine component
	 * 
	 * @param bpelFile
	 * @param project
	 * @throws RodinDBException
	 */
	public void init(final IFile bpelFile, final IRodinProject project)
			throws RodinDBException {

		XMLtool xml = new XMLtool(false, bpelFile);
		document = xml.getDocument();
		process = document.getDocumentElement();
		String filename = bpelFile.getName();
		// TODO check generated file's name
		String machineName = filename.substring(0,
				filename.indexOf(EBConstant.PERIOD)).concat("GN");

		IRodinFile machineFile = RodinHelper.createRodinConstruct(machineName,
				project);
		machine = machineFile.getRoot();

		createVariables();
		machineFile.save(null, true);
	}

	/**
	 * Create VARIABLES and INVARIANTS elements
	 * 
	 * @throws RodinDBException
	 */
	private void createVariables() throws RodinDBException {
		NodeList variables = process.getElementsByTagName("variable");
		for (int i = 0; i < variables.getLength(); i++) {
			Element variable = (Element) variables.item(i);
			String variableName = variable.getAttribute("name");
			String variableType = variable.getAttribute("messageType");
			RodinHelper.createVariables(machine, variableName, variableType);
		}
	}
}
