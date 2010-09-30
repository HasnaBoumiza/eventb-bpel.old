package za.vutshilalabs.bpelgen.core.translation;

import java.util.ArrayList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.vutshilalabs.bpelgen.core.EBConstant;
import za.vutshilalabs.bpelgen.core.XMLtool;

/**
 * creates a Bpel document from Event-B machine
 * 
 * @author Kabelo Ramongane <djemba@tuks.co.za>
 * @author Lovewell Ramuthivheli <lovewell@tuks.co.za>
 * 
 */
public class MachineTranslator {

	private IMachineRoot machine;
	private Document document;
	private final int INDEX = 1;
	Element process;
	IEvent[] events;

	private void createActivity(Element element, IEvent event) {
		try {
			if (event.getLabel().startsWith("rec_")) {

				Element recieve = document.createElement("recieve");
				recieve.setAttribute("name", event.getLabel());
				recieve.setAttribute("createInstance", "ADD");
				recieve.setAttribute("partnerLink", "ADD");
				recieve.setAttribute("operation", "ADD");
				element.appendChild(recieve);
			} else if (event.getLabel().startsWith("rep_")) {
				Element reply = document.createElement("reply");
				reply.setAttribute("name", event.getLabel());
				reply.setAttribute("createInstance", "ADD");
				reply.setAttribute("partnerLink", "ADD");
				reply.setAttribute("operation", "ADD");
				element.appendChild(reply);
			} else if (event.getLabel().startsWith("inv_")) {
				Element invoke = document.createElement("invoke");
				invoke.setAttribute("name", event.getLabel());
				invoke.setAttribute("createInstance", "ADD");
				invoke.setAttribute("partnerLink", "ADD");
				invoke.setAttribute("operation", "ADD");
				element.appendChild(invoke);

			} else {
				; // TODO handle other event naming convensions
			}
		} catch (Exception e) {
		}

	}

	/**
	 * 
	 * @throws RodinDBException
	 * @throws DOMException
	 */
	private void createAssign() throws RodinDBException, DOMException {

		String[] frm = {};

		for (IEvent event : events) {
			IAction[] actions = event.getActions();
			Element scope = document.createElement("scope");
			scope.setAttribute("name", event.getLabel());
			Element documentation = document.createElement("documentation");
			documentation.setTextContent("The following is the logic of "
					+ event.getLabel() + " Action");
			scope.appendChild(documentation);

			for (IAction action : actions) {
				Element assign = document.createElement("assign");
				Element copy = document.createElement("copy");

				Element from = document.createElement("from");
				Element to = document.createElement("to");

				frm = action.getAssignmentString().split(" ");

				String temp = "";

				for (int i = 2; i < frm.length; i++) {

					if (frm[i].equals("o")) {
						temp = "-";

					} else {
						temp = temp + " " + frm[i];
					}
				}

				from.setTextContent(temp);
				to.setTextContent(frm[0]);
				copy.appendChild(from);
				copy.appendChild(to);
				assign.appendChild(copy);
				scope.appendChild(assign);
				process.appendChild(scope);
				System.out.println(action.getAssignmentString());
			}

		}

	}

	/**
	 * creates the BPEL elements corresponding to their Event-B equivalents
	 * 
	 * @param servName
	 *            ; the name of the machine
	 * @return the success of the creation
	 */
	private boolean createElements(final String servName) {
		try {
			events = machine.getEvents();

			Comment generated = document.createComment(EBConstant.GENERATED);
			document.appendChild(generated);
			createProcess(servName);
			createVariables();
			createSimpleActivity();
			createSequenceOfSimpleActivities();
			createAssign();
			return true;
		} catch (Exception e) {
			System.err.printf("failed getting Events, exception: %s\n",
					e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public void createFile(IFile file) {
		TransformerFactory factory;
		Source source;
		Transformer transformer;
		Result result;
		try {
			factory = TransformerFactory.newInstance();
			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			source = new DOMSource(document);
			result = new StreamResult(file.getLocation().toFile());
			// result = new StreamResult(System.out);
			transformer.transform(source, result);
			file.refreshLocal(0, null);

		} catch (Exception ex) {
			System.err.printf("failed creating file, exception: %s",
					ex.getMessage());
		}

	}

	/**
	 * creates the process element giving
	 * 
	 * @param processName
	 *            , the name of the process being created
	 */
	private void createProcess(String processName) {
		process = document.createElement("process");
		process.setAttribute("name", processName);
		process.setAttribute("targetNamespace", EBConstant.targetNS);
		process.setAttribute("xmlns", EBConstant.xmlns);
		document.appendChild(process);
	}

	/**
	 * @throws RodinDBException
	 * 
	 */
	private void createSequenceOfSimpleActivities() throws RodinDBException {
		ArrayList<IEvent> sequence = sequenceOfEvents(events);
		Element sequenceElement = document.createElement("sequence");
		sequenceElement.setAttribute("name", "GIVE_NAME_OF_SEQUENCE");

		for (int i = 0; i < sequence.size(); i++) {
			createActivity(sequenceElement, sequence.get(i));
			process.appendChild(sequenceElement);
		}
	}

	/**
	 * 
	 * @throws RodinDBException
	 * @throws DOMException
	 */
	private void createSimpleActivity() throws RodinDBException, DOMException {

		for (IEvent event : events) {

			IAction[] actions = event.getActions();

			if (event.getLabel().startsWith("rec_")) {
				Element recieve = document.createElement("recieve");
				recieve.setAttribute("name", event.getLabel());
				recieve.setAttribute("partnerLink", "ADD_PARTNERLINK");

				// getting all the actions
				for (IAction action : actions) {

					String input = action.getAssignmentString();
					String tempBraces = "";
					for (int i = 0; i < input.length(); i++) {
						if (input.charAt(i) == '(' || input.charAt(i) == ')') {
							tempBraces = tempBraces + input.charAt(i);
						}

					}

					if (tempBraces.equals("()")) {
						String[] funct = input.split(EBConstant.COLON_EQUALS);
						int indexOfLeftBracket = funct[INDEX].indexOf("(");
						int indexOfRightBracket = funct[INDEX].indexOf(")");
						String operation = funct[INDEX].substring(0,
								indexOfLeftBracket).trim();
						String variable = funct[INDEX].substring(
								indexOfLeftBracket + 1, indexOfRightBracket)
								.trim();
						System.out.println(operation);

						recieve.setAttribute("variable", variable);
						recieve.setAttribute("operation", operation);
					} else
						;

				}

				process.appendChild(recieve);

			} else if (event.getLabel().startsWith("rep_")) {
				Element reply = document.createElement("reply");
				reply.setAttribute("name", event.getLabel());
				reply.setAttribute("partnerLink", "ADD_PARTNERLINK");

				// getting all the actions
				for (IAction action : actions) {

					String input = action.getAssignmentString();
					String tempBraces = "";
					for (int i = 0; i < input.length(); i++) {
						if (input.charAt(i) == '(' || input.charAt(i) == ')') {
							tempBraces = tempBraces + input.charAt(i);
						}

					}

					if (tempBraces.equals("()")) {
						String[] funct = input.split(EBConstant.COLON_EQUALS);
						int indexOfLeftBracket = funct[INDEX].indexOf("(");
						int indexOfRightBracket = funct[INDEX].indexOf(")");
						String operation = funct[INDEX].substring(0,
								indexOfLeftBracket).trim();
						String variable = funct[INDEX].substring(
								indexOfLeftBracket + 1, indexOfRightBracket)
								.trim();
						System.out.println(operation);

						reply.setAttribute("inputVariable", variable);
						reply.setAttribute("operation", operation);
					} else
						;

				}

				process.appendChild(reply);
			} else if (event.getLabel().startsWith("inv_")) {
				Element invoke = document.createElement("invoke");
				invoke.setAttribute("name", event.getLabel());
				invoke.setAttribute("partnerLink", "ADD_PARTNERLINK");

				// invoke.setAttribute("inputVariable", event.);

				// getting all the actions
				for (IAction action : actions) {

					String input = action.getAssignmentString();
					String tempBraces = "";
					for (int i = 0; i < input.length(); i++) {
						if (input.charAt(i) == '(' || input.charAt(i) == ')') {
							tempBraces = tempBraces + input.charAt(i);
						}
					}
					if (tempBraces.equals("()")) {
						String[] funct = input.split(EBConstant.COLON_EQUALS);
						int indexOfLeftBracket = funct[INDEX].indexOf("(");
						int indexOfRightBracket = funct[INDEX].indexOf(")");
						String operation = funct[INDEX].substring(0,
								indexOfLeftBracket).trim();
						String variable = funct[INDEX].substring(
								indexOfLeftBracket + 1, indexOfRightBracket)
								.trim();
						System.out.println(operation);

						invoke.setAttribute("variable", variable);
						invoke.setAttribute("operation", operation);
					} else
						;
				}
				process.appendChild(invoke);

			} else {
				; // TODO handle other event naming convensions
			}
		}
	}

	/**
	 * 
	 * @throws RodinDBException
	 */
	private void createVariables() throws RodinDBException {
		Element variables = document.createElement("variables");
		IInvariant[] invariants;
		Element variable;

		invariants = machine.getInvariants();
		for (IInvariant invariant : invariants) {

			String predicate = invariant.getPredicateString();
			int position = predicate.indexOf(EBConstant.MATH_ELEMENT);
			String second = predicate.substring(position + 1).trim();
			String first = predicate.substring(0, position).trim();

			variable = document.createElement("variable");
			variable.setAttribute("name", first);
			if (second.endsWith(EBConstant.MESSAGE)) {
				variable.setAttribute("messageType", second);
			} else {
				String type = second;
				for (int i = 0; i < EBConstant.EVENTB_TYPES.length; i++) {
					if (EBConstant.EVENTB_TYPES[i].equals(second)) {
						type = EBConstant.XSD_TYPES[i];
						break;
					}
				}
				variable.setAttribute("type", type);
			}
			variables.appendChild(variable);
		}
		process.appendChild(variables);

	}

	/**
	 * 
	 * @param machine
	 * @param context
	 */
	public void init(IMachineRoot machine) {

		XMLtool xml = new XMLtool(true, null);
		this.machine = machine;
		document = xml.getDocument();
		createElements(machine.getElementName());

	}

	/**
	 * 
	 * @param events
	 * @return sorted ArrayList
	 * @throws RodinDBException
	 */
	private ArrayList<IEvent> sequenceOfEvents(IEvent[] events)
			throws RodinDBException {
		ArrayList<IEvent> e = new ArrayList<IEvent>();
		IGuard[] guard;
		for (IEvent event : events) {
			guard = event.getGuards();

			for (int i = 0; i < guard.length; i++) {
				String label = guard[i].getPredicateString();
				if (label.startsWith("seqEvt_")) {

					e.add(event);
				}
			}
		}
		return e;
	}

}
