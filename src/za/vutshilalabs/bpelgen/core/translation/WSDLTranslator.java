/**
 * 
 */
package za.vutshilalabs.bpelgen.core.translation;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
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

	private static final String COLON = ":";
	private static final String ELEMENT = "element";
	private static final String INPUT = "input";
	private static final String MSG = "Msg";
	private static final String NAME = "name";
	private static final Namespace NS = Namespace
			.getNamespace("http://schemas.xmlsoap.org/wsdl/");
	private static final String OUTPUT = "output";
	private static final String PORT_TYPE = "portType";
	private static final String TYPE = "type";
	private static final String UNDERSCORE = "_";
	private static final String WSDL_MESSAGE = "message";
	private static final String XSD_PREFIX = "xs:";
	private IInternalElement context;
	private IRodinFile contextFile;
	private Element def;
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

	private String getType(String type) {
		String toLook = type.startsWith(XSD_PREFIX) ? type : XSD_PREFIX
				.concat(type);
		for (int i = 0; i < IGlobalConstants.EVENTB_TYPES.length; i++) {
			if (IGlobalConstants.XSD_TYPES[i].equals(toLook)) {
				return IGlobalConstants.EVENTB_TYPES[i];
			}
		}

		return "";
	}

	/**
	 * create the Context file
	 * 
	 * @param contextName
	 * @param project
	 * @throws IOException
	 * @throws JDOMException
	 * @throws CoreException 
	 */
	public void init(final IFile wsdlFile, final IRodinProject project)
			throws JDOMException, IOException, CoreException {

		SAXBuilder builder = new SAXBuilder();
		document = builder.build(wsdlFile.getLocation().toFile());
		def = document.getRootElement();

		String contextName = FileManager.getFilename(wsdlFile, project).concat(
				IGlobalConstants.CONTEXT_EXTENSION);
		wsdlFile.setPersistentProperty(IGlobalConstants.CONTEXT,contextName);
		contextFile = RodinHelper.createRodinConstruct(contextName, project);
		context = contextFile.getRoot();

		processMessages();
		processTypes(wsdlFile);
		processPortType();
		contextFile.save(null, true);
	}

	/**
	 * Prepare message name to support backward translation
	 * 
	 * @param messageName
	 * @return
	 */
	private String prepareMessage(String messageName) {
		if (messageName.endsWith(MSG)) {
			return messageName.replace(MSG, IGlobalConstants.MESSAGE);
		} else if (messageName.endsWith(IGlobalConstants.MESSAGE)) {
			return messageName;
		} else {
			return messageName.concat(IGlobalConstants.MESSAGE);
		}
	}

	/**
	 * Create Event-B elements from WSDL messages
	 * 
	 * @throws RodinDBException
	 */
	@SuppressWarnings("rawtypes")
	private void processMessages() throws RodinDBException {

		List messages = def.getChildren(WSDL_MESSAGE, NS);

		for (int i = 0; i < messages.size(); i++) {
			Element message = (Element) messages.get(i);
			String messageName = message.getAttributeValue(NAME);
			messageName = prepareMessage(removeNamespace(messageName));

			RodinHelper.createCarrierSet(context, messageName);

			List parts = message.getChildren();

			for (int k = 0; k < parts.size(); k++) {
				Element part = (Element) parts.get(k);
				String name = removeNamespace(part.getAttributeValue(NAME));
				String type = part.getAttributeValue(TYPE);
				String element = part.getAttributeValue(ELEMENT);

				if (type != null) {
					type = removeNamespace(addSuffix(type,
							IGlobalConstants.TYPE));
					RodinHelper.createCarrierSet(context, type);
					RodinHelper.createConstant(context, name);
					RodinHelper.createAxiom(context, name, messageName, type);
				} else {
					element = removeNamespace(addSuffix(element,
							IGlobalConstants.TYPE));
					RodinHelper.createCarrierSet(context, element);
					RodinHelper.createConstant(context, name);
					RodinHelper
							.createAxiom(context, name, messageName, element);
				}

			}

		}
	}

	/**
	 * Create Event-B elements from PortType operations. Assuming there is only
	 * one portType
	 * 
	 * @throws RodinDBException
	 */
	@SuppressWarnings("rawtypes")
	private void processPortType() throws RodinDBException {

		List portTypes = def.getChildren(PORT_TYPE, NS);
		// handle only one PortType
		Element pt0 = (Element) portTypes.get(0);
		List operations = pt0.getChildren();

		for (int i = 0; i < operations.size(); i++) {
			Element operation = (Element) operations.get(i);
			String name = operation.getAttributeValue(NAME);
			name = removeNamespace(name);

			List io = operation.getChildren();
			String input = "";
			String output = "";

			System.out.println("operationName "
					+ operation.getAttributeValue(NAME));
			for (int j = 0; j < io.size(); j++) {
				Element el = (Element) io.get(j);
				String elName = el.getName();
				String messageName = el.getAttributeValue(WSDL_MESSAGE);

				if (elName.equals(INPUT)) {
					input = prepareMessage(messageName);
				} else if (elName.equals(OUTPUT)) {
					output = prepareMessage(messageName);
				}
			}

			input = removeNamespace(input);
			output = removeNamespace(output);

			RodinHelper.createConstant(context, name);
			RodinHelper.createAxiom(context, name, input, output);
		}
	}

	/**
	 * Create Event-B elements from WSDL types
	 * 
	 * @throws RodinDBException
	 */
	private void processTypes(final IFile wsdlFile) throws RodinDBException {
		XMLtool tool = new XMLtool(false, wsdlFile);
		org.w3c.dom.Document doc = tool.getDocument();
		NodeList elements = doc.getDocumentElement().getElementsByTagName(
				ELEMENT);

		for (int i = 0; i < elements.getLength(); i++) {
			org.w3c.dom.Element element = (org.w3c.dom.Element) elements
					.item(i);
			if (element.getAttribute(TYPE).isEmpty()) {
				String setName = element.getAttribute(NAME);
				setName = removeNamespace(addSuffix(setName,
						IGlobalConstants.TYPE));

				RodinHelper.createCarrierSet(context, setName);

				NodeList internalElements = element
						.getElementsByTagName(ELEMENT);
				for (int k = 0; k < internalElements.getLength(); k++) {
					org.w3c.dom.Element inElement = (org.w3c.dom.Element) internalElements
							.item(k);
					String constantName = inElement.getAttribute(NAME);
					String bType = getType(inElement.getAttribute(TYPE));

					// for unsupported types, e.g strings
					if (bType.isEmpty() || bType.equals("STRING")) {
						bType = removeNamespace(inElement.getAttribute(TYPE))
								.toUpperCase();
						RodinHelper.createCarrierSet(context, bType);
					}

					// handle element with same name from different elements
					if (RodinHelper.createConstant(context, constantName)) {
						RodinHelper.createAxiom(context, constantName, setName,
								bType);
					} else {
						constantName = setName.concat(UNDERSCORE).concat(
								constantName);
						RodinHelper.createConstant(context, constantName);
						RodinHelper.createAxiom(context, constantName, setName,
								bType);
					}

				}
			}
		}
	}

	/**
	 * Remove namespace prefix
	 * 
	 * @param element
	 * @return
	 */
	private String removeNamespace(String element) {
		int pos = element.indexOf(COLON);
		pos = pos > 0 ? pos + 1 : 0;
		return element.substring(pos);
	}
}
