/**
 * 
 */
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
 * Translate WSDL to Event-b contextFile file
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class WSDLTranslator {
	private IInternalElement context;
	private IRodinFile contextFile;
	private Element definitions;
	private Document document;

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

	/**
	 * Create Event-B Context elements from WSDL messages
	 * 
	 * @throws RodinDBException
	 */
	private void extractMessages() throws RodinDBException {
		final NodeList messages = definitions.getElementsByTagName("message");

		for (int i = 0; i < messages.getLength(); i++) {
			Element message = (Element) messages.item(i);
			String messageName = addSuffix(message.getAttribute("name"),
					IGlobalConstants.MESSAGE);
			RodinHelper.createCarrierSet(context, messageName);
			NodeList parts = message.getElementsByTagName("part");

			for (int k = 0; k < parts.getLength(); k++) {
				Element part = (Element) parts.item(k);
				String name = part.getAttribute("name");
				String type = addSuffix(part.getAttribute("type"),
						IGlobalConstants.TYPE);
				RodinHelper.createCarrierSet(context, type);
				RodinHelper.createConstant(context, name);
				RodinHelper.createAxiom(context, name, messageName, type);
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
					IGlobalConstants.MESSAGE);

			Element output = (Element) operation.getElementsByTagName("output")
					.item(0);
			String outputMessage = addSuffix(output.getAttribute("message"),
					IGlobalConstants.MESSAGE);

			RodinHelper.createConstant(context, operationName);
			RodinHelper.createAxiom(context, operationName, inputMessage,
					outputMessage);
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
					IGlobalConstants.TYPE);
			RodinHelper.createCarrierSet(context, complexTypeName);

			NodeList elements = type.getElementsByTagName("xs:element");

			for (int k = 0; k < elements.getLength(); k++) {
				Element element = (Element) elements.item(k);
				String elementName = element.getAttribute("name");
				String elementType = element.getAttribute("type");

				for (int j = 0; j < IGlobalConstants.XSD_TYPES.length; j++) {
					if (elementType.equals(IGlobalConstants.XSD_TYPES[j])) {
						elementType = IGlobalConstants.EVENTB_TYPES[j];
						break;
					}
				}
				RodinHelper.createConstant(context, elementName);
				RodinHelper.createAxiom(context, elementName, complexTypeName,
						elementType);
			}
		}
	}

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
		String contextName = wsdlFile.getName()
				.replace(IGlobalConstants.WSDL_EXTENSION, "GN")
				.concat(IGlobalConstants.CONTEXT_EXTENSION);

		contextFile = RodinHelper.createRodinConstruct(contextName, project);
		context = contextFile.getRoot();
		extractMessages();
		extractPortType();
		extractTypes();
		contextFile.save(null, true);
	}

}
