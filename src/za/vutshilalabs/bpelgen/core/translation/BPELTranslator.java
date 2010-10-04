package za.vutshilalabs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import za.vutshilalabs.bpelgen.core.IGlobalConstants;
import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.XMLtool;

/**
 * 
 * @author Ernest Mashele <mashern@tuks.co.za>
 * @author Kabelo Ramongane <djemba@tuks.co.za>
 */

public class BPELTranslator {
	private Document document;
	private IInternalElement machine;
	private Element process;

	private void createInvokes() throws RodinDBException {
		NodeList invokes = process.getElementsByTagName("invoke");
		for (int i = 0; i < invokes.getLength(); i++) {
			Element invoke = (Element) invokes.item(i);
			String name = invoke.getAttribute("name");
			// String operation = invoke.getAttribute("operation");
			// String variable = invoke.getAttribute("inputVariable");

			RodinHelper.createEvent(machine, name);
		}
	}

	/**
	 * @throws RodinDBException
	 * 
	 */
	private void createReceives() throws RodinDBException {
		NodeList receives = process.getElementsByTagName("receive");
		for (int i = 0; i < receives.getLength(); i++) {
			Element receive = (Element) receives.item(i);
			String name = receive.getAttribute("name");
			// String operation = receive.getAttribute("operation");
			// String variable = receive.getAttribute("variable");
			RodinHelper.createEvent(machine, name);

		}
	}

	private void createReplies() throws RodinDBException {
		NodeList replies = process.getElementsByTagName("reply");

		for (int i = 0; i < replies.getLength(); i++) {
			Element reply = (Element) replies.item(i);
			String name = reply.getAttribute("name");
			// String operation = reply.getAttribute("operation");
			// String variable = reply.getAttribute("variable");
			RodinHelper.createEvent(machine, name);

		}
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

			if (variableType.isEmpty() || variableType.equals(null)) {
				variableType = variable.getAttribute("type");
				for (int k = 0; k < IGlobalConstants.XSD_TYPES.length; k++) {
					if (variableType.equals(IGlobalConstants.XSD_TYPES[k])) {
						variableType = IGlobalConstants.EVENTB_TYPES[k];
						break;
					}
				}
			}
			RodinHelper.createVariables(machine, variableName, variableType);
		}
	}

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
		String machineName = filename
				.substring(0, filename.indexOf(IGlobalConstants.PERIOD)).concat("GN")
				.concat(IGlobalConstants.MACHINE_EXTENSION);

		IRodinFile machineFile = RodinHelper.createRodinConstruct(machineName,
				project);
		machine = machineFile.getRoot();
		// FIXME remove hard coded
		RodinHelper.linkContext(machine, "PurchaseOrderC");

		createVariables();
		createReceives();
		createInvokes();
		createReplies();
		machineFile.save(null, true);
	}
}
