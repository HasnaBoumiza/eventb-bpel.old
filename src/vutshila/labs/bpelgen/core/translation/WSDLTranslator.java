/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import vutshila.labs.bpelgen.core.EBConstant;
import vutshila.labs.bpelgen.core.RodinHelper;
import vutshila.labs.bpelgen.core.XMLtool;

/**
 * Translate WSDL to Event-b contextFile file
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
@SuppressWarnings("unused")
public class WSDLTranslator {
	private IRodinFile contextFile;
	private Document document;
	private Element definitions;
	private IInternalElement context;

	/**
	 * create the Context file
	 * 
	 * @param contextName
	 * @param project
	 * @throws RodinDBException
	 */
	public void init(final IFile wsdlFile, final IRodinProject project)
			throws RodinDBException {
		XMLtool tool = new XMLtool(false, wsdlFile);
		document = tool.getDocument();
		definitions = document.getDocumentElement();
		String contextName = wsdlFile.getName().replace(
				EBConstant.WSDL_EXTENSION, "GN").concat(
				EBConstant.CONTEXT_EXTENSION);

		contextFile = RodinHelper.createRodinConstruct(contextName, project);
		context = contextFile.getRoot();
		extractMessages();
		extractPortType();
		extractTypes();
		contextFile.save(null, true);
	}

	/**
	 * Create Event-B Context elements from WSDL messages
	 * 
	 * @throws RodinDBException
	 */
	void extractMessages() throws RodinDBException {
		final NodeList messages = definitions.getElementsByTagName("message");

		for (int i = 0; i < messages.getLength(); i++) {
			Element message = (Element) messages.item(i);
			String messageName = addSuffix(message.getAttribute("name"),
					EBConstant.MESSAGE);
			RodinHelper.createCarrierSet(context, messageName);
			NodeList parts = message.getElementsByTagName("part");

			for (int k = 0; k < parts.getLength(); k++) {
				Element part = (Element) parts.item(k);
				String name = part.getAttribute("name");
				String type = addSuffix(part.getAttribute("type"),
						EBConstant.TYPE);
				RodinHelper.createCarrierSet(context, type);
				RodinHelper.createConstant(context, name);
				RodinHelper
						.createAxiom(context, name, messageName, type, false);
			}
		}
	}

	/**
	 * Create Event-B elements from WSDL PortType's operation
	 * 
	 * @throws RodinDBException
	 */
	private void extractPortType() throws RodinDBException {
		final NodeList operations = definitions
				.getElementsByTagName("operation");

		for (int i = 0; i < operations.getLength(); i++) {
			Element operation = (Element) operations.item(i);
			String operationName = operation.getAttribute("name");

			Element input = (Element) operation.getElementsByTagName("input")
					.item(0);
			String inputMessage = addSuffix(input.getAttribute("message"),
					EBConstant.MESSAGE);

			Element output = (Element) operation.getElementsByTagName("output")
					.item(0);
			String outputMessage = addSuffix(output.getAttribute("message"),
					EBConstant.MESSAGE);

			RodinHelper.createConstant(context, operationName);
			RodinHelper.createAxiom(context, operationName, inputMessage,
					outputMessage, false);
		}
	}

	/**
	 * Create Event-B context element from WSDL types
	 * 
	 * @throws RodinDBException
	 */
	private void extractTypes() throws RodinDBException {
		final NodeList types = definitions
				.getElementsByTagName("xs:ComplexType");
		for (int i = 0; i < types.getLength(); i++) {
			Element type = (Element) types.item(i);
			String complexTypeName = addSuffix(type.getAttribute("name"),
					EBConstant.TYPE);
			RodinHelper.createCarrierSet(context, complexTypeName);

			NodeList elements = type.getElementsByTagName("xs:element");
			
			for (int k = 0; k < elements.getLength(); k++) {
				Element element = (Element) elements.item(k);
				String elementName = element.getAttribute("name");
				String elementType = element.getAttribute("type");

				for (int j = 0; j < EBConstant.XSD_TYPES.length; j++) {
					if (elementType.equals(EBConstant.XSD_TYPES[j])) {
						elementType = EBConstant.EVENTB_TYPES[j];
						break;
					}
				}
				RodinHelper.createConstant(context, elementName);
				RodinHelper.createAxiom(context, elementName, complexTypeName,
						elementType, false);
			}
		}
	}

	/**
	 * Add a suffix if it does not exist
	 * 
	 * @param suffix
	 * @param text
	 * @return
	 */
	private String addSuffix(String text, String suffix) {
		return text.endsWith(suffix) ? text : text.concat(suffix);
	}

}
