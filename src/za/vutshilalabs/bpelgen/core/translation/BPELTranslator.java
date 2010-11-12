package za.vutshilalabs.bpelgen.core.translation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.InputDialog;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.Activator;
import za.vutshilalabs.bpelgen.core.IGlobalConstants;
import za.vutshilalabs.bpelgen.core.RodinHelper;

public class BPELTranslator {

	private static final String COLON = ":";
	private static final String ELEMENT = "element";
	private static final String MSG = "Msg";
	private static final String MSGTYPE = "messageType";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String XSD_PREFIX = "xs:";
	private static final String ONE = "1";

	// contants that will be used as suffixes to the translated elements
	private static final String SEQ = "SEQ";
	private static final String REPLY = "REP";
	private static final String RECEIVE = "REC";
	private static final String INVOKE = "INV";
	private static final String FLOW = "FLOW";

	private Document document;
	private IInternalElement machine;
	private Element process;
	private Convergence convergence = IConvergenceElement.Convergence.ORDINARY;

	// global containers used in the naming of unnamed structured elements
	private ArrayList<Element> sequences = new ArrayList<Element>(0);
	private ArrayList<Element> flows = new ArrayList<Element>(0);

	/**
	 * Add a suffix if it does not exist
	 * 
	 * @param suffix
	 * @param text
	 * @return the text with the added suffix to meet the modeling convention
	 */
	private String addSuffix(String text, String suffix) {
		text = removeNamespace(text);
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
	 * @throws RodinDBException
	 * 
	 */
	private IEvent createInvoke(Element element) throws RodinDBException {
		String name = element.getAttributeValue(NAME);
		name = addSuffix(name, INVOKE);
		return RodinHelper.createEvent(machine, name, convergence);

	}

	/**
	 * @throws RodinDBException
	 * 
	 */
	private IEvent createReceive(Element element) throws RodinDBException {
		String name = element.getAttributeValue(NAME);
		name = addSuffix(name, RECEIVE);
		return RodinHelper.createEvent(machine, name, convergence);
	}

	private IEvent createReply(Element element) throws RodinDBException {
		String name = element.getAttributeValue(NAME);
		name = addSuffix(name, REPLY);
		return RodinHelper.createEvent(machine, name, convergence);
	}

	private IEvent createSequence(Element sequence) throws RodinDBException {

		sequences.add(sequence);
		ArrayList<IEvent> sequenceChildren = new ArrayList<IEvent>(0);
		ArrayList<Integer> guardValue = new ArrayList<Integer>(0); // the value
																	// to
																	// initialise
																	// the
																	// firing of
																	// guards
		String variantName = sequence.getAttributeValue(NAME);
		int decreasingVariant = 1;
		String variantType = "xs:int";

		IEvent event = createSequenceEvent(sequence);

		@SuppressWarnings("rawtypes")
		List list = sequence.getChildren();

		if (variantName == null) {
			variantName = "sequence" + sequences.size();
		}

		variantName = variantName.concat("_Variant");
		// createVariant( variantName );

		// create the guard for the sequence event
		String predicate = variantName + " = " + 0;
		RodinHelper.createGuard(event, predicate);

		for (int k = 0; k < list.size(); k++) {
			Element e = (Element) list.get(k);

			if (e.getName().equals("receive")) {
				decreasingVariant++;
				guardValue.add(decreasingVariant);
				IEvent evnt = createReceive(e);
				String ass = createVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				sequenceChildren.add(evnt);

			} else if (e.getName().equals("invoke")) {
				decreasingVariant++;
				guardValue.add(decreasingVariant);
				IEvent evnt = createInvoke(e);
				String ass = createVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				sequenceChildren.add(evnt);

			} else if (e.getName().equals("reply")) {
				decreasingVariant++;
				guardValue.add(decreasingVariant);
				IEvent evnt = createReply(e);
				String ass = createVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				sequenceChildren.add(evnt);
			} else if (e.getName().equals("sequence")) {
				decreasingVariant++;
				guardValue.add(decreasingVariant);
				IEvent evnt = createSequence(e);
				String ass = createVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				sequenceChildren.add(evnt);
			} else if (e.getName().equals("flow")) {
				decreasingVariant++;
				IEvent evnt = createFlow(e);
				sequenceChildren.add(evnt);
			}

		}

		variantType = variantSet(decreasingVariant);
		RodinHelper.createVariables(machine, variantName, variantType);
		initialiseGuards(variantName, sequenceChildren, decreasingVariant);

		return event;

	}// end createSequence(e)

	private IEvent createFlow(Element flow) throws RodinDBException {
		String variantName = flow.getAttributeValue(NAME);
		ArrayList<IEvent> flowsEvents = new ArrayList<IEvent>(0);
		ArrayList<String> variantNames = new ArrayList<String>(0);

		if (variantName == null) {
			variantName = "flow";
			variantName = variantName.concat("" + flows.size());
		}

		// create the flow event
		IEvent event = createFlowEvent(flow);

		String choice = "{ 0 , 1 }";
		int numChildren = 0;

		@SuppressWarnings("rawtypes")
		List flowChildren = flow.getChildren();
		for (int k = 0; k < flowChildren.size(); k++) {
			Element e = (Element) flowChildren.get(k);

			if (e.getName().equals("receive")) {
				numChildren++;
				variantName = variantName.concat("" + numChildren);
				IEvent evnt = createReceive(e);
				String ass = flowVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				variantNames.add(variantName);
				flowsEvents.add(evnt);

			} else if (e.getName().equals("invoke")) {
				numChildren++;
				variantName = variantName.concat("" + numChildren);
				IEvent evnt = createReceive(e);
				String ass = flowVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				variantNames.add(variantName);
				flowsEvents.add(evnt);
			} else if (e.getName().equals("reply")) {
				numChildren++;
				variantName = variantName.concat("" + numChildren);
				IEvent evnt = createReceive(e);
				String ass = flowVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				variantNames.add(variantName);
				flowsEvents.add(evnt);
			} else if (e.getName().equals("sequence")) {
				numChildren++;
				variantName = variantName.concat("" + numChildren);
				IEvent evnt = createReceive(e);
				String ass = flowVariantAssignment(variantName);
				RodinHelper.createAction(evnt, ass,
						"point to the next in line for execution");
				variantNames.add(variantName);
				flowsEvents.add(evnt);
			} else if (e.getName().equals("flow")) {
				// numChildren++;
				// variantName = variantName.concat("" + numChildren);
				// IEvent evnt = createFlow(e);
				// String ass = flowVariantAssignment( variantName );
				// RodinHelper.createAction(evnt, ass,
				// "the decreasing variant");
				// variantNames.add(variantName);
				// flowsEvents.add(evnt);
			}
		}

		initialiseGuards(flowsEvents, variantNames);

		for (int i = 0; i < variantNames.size(); i++) {
			RodinHelper.createVariables(machine, variantNames.get(i), choice);
		}

		for (int i = 0; i < flowsEvents.size(); i++) {
			// create the guard for the sequence event
			//IEvent e = flowsEvents.get(i);
			//IAction[] actions = e.getActions();
			String predicate = variantName + " = " + 0;
			RodinHelper.createGuard(event, predicate);
		}
		// createFlowVariants( variantNames );
		return event;
	}

	private IEvent createSequenceEvent(Element element) throws RodinDBException {
		String name = element.getAttributeValue(NAME);
		if (name == null) {
			name = "sequence";
			name = "sequence" + sequences.size();
		}
		name = addSuffix(name, SEQ);
		return RodinHelper.createEvent(machine, name, convergence);
	}

	private IEvent createFlowEvent(Element flow) throws RodinDBException {
		String name = flow.getAttributeValue(NAME);
		if (name == null) {
			name = "flow";
			name = "flow" + flows.size();
		}
		name = addSuffix(name, FLOW);
		flows.add(flow);
		return RodinHelper.createEvent(machine, name, convergence);
	}

	private void createVariables(Element variables) throws RodinDBException {

		@SuppressWarnings("rawtypes")
		List variablesList = variables.getChildren();

		for (int i = 0; i < variablesList.size(); i++) {
			Element variable = (Element) variablesList.get(i);
			String vName = null;
			String vType = null;
			String type = null;
			String messageType = null;
			String elementName = null;

			vName = variable.getAttributeValue(NAME);
			type = variable.getAttributeValue(TYPE);
			messageType = variable.getAttributeValue(MSGTYPE);
			elementName = variable.getAttributeValue(ELEMENT);

			// Element
			if (elementName != null) {
				vType = elementName;
			} else if (type != null) {
				type = getType(removeNamespace(type));
				if (type.isEmpty()) {
					type = variable.getAttributeValue(TYPE);
					type = removeNamespace(type).toUpperCase();
				}
				vType = type;
			} else if (messageType != null) {
				messageType = removeNamespace(prepareMessage(messageType));
				vType = messageType;
			}

			vName = vName.replace("Message", "VAR");
			RodinHelper.createVariables(machine, vName, vType);
		}

	}

	private void createMachineConstructs(Element process)
			throws RodinDBException {
		@SuppressWarnings("rawtypes")
		List processChildren = process.getChildren();

		for (int i = 0; i < processChildren.size(); i++) {
			Element element = (Element) processChildren.get(i);
			if (element.getName().equals("receive")) {
				createReceive(element);
			} else if (element.getName().equals("reply")) {
				createReply(element);
			} else if (element.getName().equals("invoke")) {
				createInvoke(element);
			} else if (element.getName().equals("sequence")) {
				createSequence(element);
			} else if (element.getName().equals("variables")) {
				createVariables(element);
			} else if (element.getName().equals("flow ")) {
				// createFlow( element );
			}
		}

	}

	public void init(final IFile bpelFile, final IRodinProject project)
			throws JDOMException, IOException, CoreException {

		String context = bpelFile
				.getPersistentProperty(IGlobalConstants.CONTEXT);

		if (context == null) {
			// Ask user for associated WSDL filename.

			InputDialog dialog = new InputDialog(Activator.getShell(),
					"Service Generator - Associated WSDL",
					"Please enter the name of WSDL fle.", "", null);

			if (dialog.open() == IStatus.OK) {
				context = dialog.getValue();
				IFile wsdl = project.getProject().getFile(context);

				if ((wsdl.exists()) && (wsdl.getFileExtension().equals("wsdl"))) {
					Updator.update(wsdl);
					context = wsdl
							.getPersistentProperty(IGlobalConstants.CONTEXT);
					context = context.replace(
							IGlobalConstants.CONTEXT_EXTENSION, "");
				} else {
					Activator.errorDialog(new Exception("Could not translate."
							+ ". WSDL not found."), "Translation Error!");
					return;
				}
			} else {
				Activator.errorDialog(new Exception("Could not translate. "	+ ". Operation cancelled by user."),
						"Translation Error!");
				return;
			}
		}

		SAXBuilder builder = new SAXBuilder();
		document = builder.build(bpelFile.getLocation().toFile());
		process = (Element) document.getRootElement();

		String machineName = FileManager.getFilename(bpelFile, project).concat(
				IGlobalConstants.MACHINE_EXTENSION);

		IRodinFile machineFile = RodinHelper.createRodinConstruct(machineName,
				project);
		machine = machineFile.getRoot();

		// CREATING MACHINE CONSTRUCTS
		createMachineConstructs(process);
		
		RodinHelper.linkContext(machine, context);
		machineFile.save(null, true);
		project.getResource().refreshLocal(IResource.PROJECT, null);
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

	private String createVariantAssignment(String variantName) {
		String assignment = null;
		StringBuffer ass = new StringBuffer();
		assignment = ass.append(
				variantName + " " + IGlobalConstants.COLON_EQUALS + " "
						+ variantName + " " + IGlobalConstants.MINUS + " "
						+ ONE).toString();

		return assignment;
	}

	private String flowVariantAssignment(String name) {
		String assignment = null;

		// refactor this code to be elegant
		StringBuffer ass = new StringBuffer();
		assignment = ass.append(
				name + " " + IGlobalConstants.COLON_EQUALS + " " + 0)
				.toString();
		return assignment;
	}

	public String variantSet(int setSize) {
		return IGlobalConstants.XSD_TYPES[0];
	}

	private void initialiseGuards(String variantName,
			ArrayList<IEvent> sequenceChildren, int size)
			throws RodinDBException {
		int guardVal = size - 1;
		for (int i = 0; i < sequenceChildren.size(); i++) {
			IEvent e = sequenceChildren.get(i);
			String predicate = variantName + " " + " = " + " " + guardVal;
			guardVal--;
			RodinHelper.createGuard(e, predicate);
			if (guardVal < 1)
				break;
		}
	}

	private void initialiseGuards(ArrayList<IEvent> events,
			ArrayList<String> names) throws RodinDBException {

		for (int i = 0; i < events.size(); i++) {
			StringBuffer ass = new StringBuffer();
			IEvent event = events.get(i);
			String predicate = null;

			predicate = ass.append(names.get(i) + " " + " = " + " " + 1)
					.toString();
			RodinHelper.createGuard(event, predicate);
		}
	}
}