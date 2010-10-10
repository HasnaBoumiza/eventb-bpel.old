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
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.vutshilalabs.bpelgen.core.IGlobalConstants;
import za.vutshilalabs.bpelgen.core.XMLtool;

public class oldMachineTranslator {

	private static final String RECEIVE = "receive";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
	private IMachineRoot machine;
	private Document document;
	private final int INDEX = 1;
	private final String AND_OP = "&&";
	Element process;
	Element flow;
	Element sequence;
	IEvent[] events;

	private void createActivity(Element element, IEvent event) {
		try {
			if (event.getLabel().startsWith("rec_")) {

				Element recieve = document.createElement(RECEIVE);
				recieve.setAttribute("name", event.getLabel());
				recieve.setAttribute("partnerLink", "ADD");
				recieve.setAttribute("operation", "ADD");
				element.appendChild(recieve);
			} else if (event.getLabel().startsWith("rep_")) {
				Element reply = document.createElement("reply");
				reply.setAttribute("name", event.getLabel());
				reply.setAttribute("partnerLink", "ADD");
				reply.setAttribute("operation", "ADD");
				element.appendChild(reply);
			} else if (event.getLabel().startsWith("inv_")) {
				Element invoke = document.createElement("invoke");
				invoke.setAttribute("name", event.getLabel());
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
	 * creates the BPEL elements corresponding to their Event-B equivalents
	 * 
	 * @param servName
	 *            ; the name of the machine
	 * @return the success of the creation
	 */
	private boolean createElements(final String servName) {
		try {
			sequence = document.createElement("sequence");
			events = machine.getEvents();
			flow= document.createElement("flow");
			Comment generated = document.createComment(IGlobalConstants.GENERATED);
			document.appendChild(generated);
			createProcess(servName);
			createVariables();
			// createSimpleActivity();
			createSequenceOfSimpleActivities();
			createIf();
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
		process.setAttribute("targetNamespace", IGlobalConstants.targetNS);
		process.setAttribute("xmlns", IGlobalConstants.NS);
		process.setAttribute("xmlns:xs",XSD_NS);
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
	@SuppressWarnings("unused")
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
						String[] funct = input.split(IGlobalConstants.COLON_EQUALS);
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
						String[] funct = input.split(IGlobalConstants.COLON_EQUALS);
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
						String[] funct = input.split(IGlobalConstants.COLON_EQUALS);
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
				;
			}
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
			 Element subflow= document.createElement("flow");
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
				subflow.appendChild(assign);
				scope.appendChild(subflow);
				flow.appendChild(scope);
				sequence.appendChild(flow);
				process.appendChild(sequence);
				//process.appendChild(flow);
				//System.out.println(action.getAssignmentString());
			}
			
		}
		
	}

	/**
	 * 
	 * @throws RodinDBException
	 */
	private void createVariables() throws RodinDBException {
//		Element variables = document.createElement("variables");
//		IInvariant[] invariants;
//		Element variable;
//
//		invariants = machine.getInvariants();
//		for (IInvariant invariant : invariants) {
//
//			String predicate = invariant.getPredicateString();
//			int position = predicate.indexOf(IGlobalConstants.MATH_ELEMENT);
//			String second = predicate.substring(position + 1).trim();
//			String first = predicate.substring(0, position).trim();
//
//			variable = document.createElement("variable");
//			variable.setAttribute("name", first);
//			if (second.endsWith(IGlobalConstants.MESSAGE)) {
//				variable.setAttribute("messageType", second);
//			} else {
//				String type = second;
//				for (int i = 0; i < IGlobalConstants.EVENTB_TYPES.length; i++) {
//					if (IGlobalConstants.EVENTB_TYPES[i].equals(second)) {
//						type = IGlobalConstants.XSD_TYPES[i];
//						break;
//					}
//				}
//				variable.setAttribute("type", type);
//			}
//			variables.appendChild(variable);
//		}
//		process.appendChild(variables);

	}

	private void createIf() throws RodinDBException, DOMException {
		Element subflow= document.createElement("flow");
		for (IEvent event : events) {
			int counter = 0;
			String stGuard = "";
			String transGuard = "";
			IGuard[] guards = event.getGuards();
			Element ifElement = document.createElement("if");
			Element condition = document.createElement("condition");

			if (guards.length == 0) {
				createActivity(process, event);
			} else {

				;
			}

			for (IGuard guard : guards) {
				if (counter == 0) {
					stGuard = guard.getPredicateString();
					transGuard = translateSymbols(stGuard);

					counter++;
				} else if (counter > 0) {
					stGuard = stGuard + AND_OP + guard.getPredicateString();
					System.out.println("From:  "+ stGuard);
					transGuard = translateSymbols(stGuard);
					counter++;
				}
				condition.setTextContent(transGuard);
				ifElement.setAttribute("name", "if_" + event.getLabel());
				ifElement.appendChild(condition);
				subflow.appendChild(ifElement);
				sequence.appendChild(subflow);

			}

			if (event.getLabel().startsWith("rec_")) {

				Element recieve = document.createElement(RECEIVE);
				recieve.setAttribute("name", event.getLabel());
				recieve.setAttribute("partnerLink", "ADD");
				recieve.setAttribute("operation", "ADD");
				ifElement.appendChild(recieve);
			} else if (event.getLabel().startsWith("rep_")) {
				Element reply = document.createElement("reply");
				reply.setAttribute("name", event.getLabel());
				reply.setAttribute("partnerLink", "ADD");
				reply.setAttribute("operation", "ADD");
				ifElement.appendChild(reply);
			} else if (event.getLabel().startsWith("inv_")) {
				Element invoke = document.createElement("invoke");
				invoke.setAttribute("name", event.getLabel());
				invoke.setAttribute("partnerLink", "ADD");
				invoke.setAttribute("operation", "ADD");
				ifElement.appendChild(invoke);

			} else {
				;
			}

		}
		process.appendChild(sequence);
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

	
	
	
	
	
	
	
public String translateSymbols(String expression) {
		String result = "";
		String tempExpression = "";
		try {
			int start = 0;
			for(int i = 0; i < expression.length();i++){
				
				String chars= expression.charAt(i)+"";
				for(int j = 0; j < IGlobalConstants.EVENTB_TYPES.length;j++){
					 
					if(chars.equals(IGlobalConstants.EVENTB_TYPES[j])){
						for(int r = start;r < i ; r ++ ){
							tempExpression = tempExpression + expression.charAt(r);
						}
						tempExpression = tempExpression+IGlobalConstants.TYPES[j];
						
						start = i+1;
					}
				}
				if(i==expression.length()-1){
					for(int k = start; k< expression.length(); k ++){
						tempExpression = tempExpression+expression.charAt(k);
					}
				}
				
			}
			result =tempExpression;
			System.out.println("Found:"+"    :     "+result);
		} catch (Exception ex) {
			
		}
		return result;
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
