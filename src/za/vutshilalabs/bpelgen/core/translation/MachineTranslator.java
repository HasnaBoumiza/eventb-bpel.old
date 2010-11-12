package za.vutshilalabs.bpelgen.core.translation;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.vutshilalabs.bpelgen.core.IGlobalConstants;
import za.vutshilalabs.bpelgen.core.XMLtool;

/**
 * Creates a Bpel document from Event-B machine
 * 
 * @author Lovewell Ramuthivheli <lovewell@tuks.co.za>
 * 
 */
public class MachineTranslator {

	private static final String XSD_NS = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	private static final String STUB = "stub";
	private Element process;
	private IMachineRoot machine;
	private Document document;

	private static final String REPLY = "REP";
	private static final String RECEIVE = "REC";
	private static final String INVOKE = "INV";
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

	private void createProcess(String processName) {
		process = document.createElement("process");
		process.setAttribute("name", processName);
		process.setAttribute("targetNamespace", IGlobalConstants.targetNS);
		process.setAttribute("xmlns", IGlobalConstants.NS);
		process.setAttribute("xmlns:xs", XSD_NS);
		document.appendChild(process);
	}

	public void init(IMachineRoot machine) {

		XMLtool xml = new XMLtool(true, null);
		this.machine = machine;
		document = xml.getDocument();
		createElements(machine.getElementName());

	}

	private boolean createElements(final String servName) {
		try {

			Comment generated = document
					.createComment(IGlobalConstants.GENERATED);
			document.appendChild(generated);
			createProcess(servName);
			createVariables();
			findSequences();

			return true;
		} catch (Exception e) {
			System.err.printf("failed getting Events, exception: %s\n",
					e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	private void createVariables() throws RodinDBException {
		Element variables = document.createElement("variables");
		IInvariant[] invariants;
		Element variable;

		invariants = machine.getInvariants();
		for (IInvariant invariant : invariants) {

			String predicate = invariant.getPredicateString();
			int position = predicate.indexOf(IGlobalConstants.MATH_ELEMENT);
			String second = predicate.substring(position + 1).trim();
			String first = predicate.substring(0, position - 1).trim();

			if (!first.endsWith("_Variant")) {
				if (!first.contains("flow")) {
					variable = document.createElement("variable");
					variable.setAttribute("name", first);
					if (second.endsWith(IGlobalConstants.MESSAGE)) {
						variable.setAttribute("messageType", second);
					} else {
						String type = getType(second);
						variable.setAttribute("type", type);
					}
					variables.appendChild(variable);
				}
			}
		}
		process.appendChild(variables);

	}

	private String getType(String type) {
		for (int i = 0; i < IGlobalConstants.EVENTB_TYPES.length; i++) {
			if (IGlobalConstants.EVENTB_TYPES[i].equals(type)) {
				return IGlobalConstants.XSD_TYPES[i];
			}
		}

		return "xs:anyType";
	}

	private void createActivity(Element parent, IEvent event)
			throws DOMException, RodinDBException {
		System.out.println( "creating activities " + event.getLabel());
		if (event.getLabel().endsWith(RECEIVE)) {
			Element recieve = document.createElement("receive");
			recieve.setAttribute("name", event.getLabel());
			recieve.setAttribute("partnerLink", STUB);
			recieve.setAttribute("operation", STUB);
			parent.appendChild(recieve);
		} else if (event.getLabel().endsWith(REPLY)) {
			Element reply = document.createElement("reply");
			reply.setAttribute("name", event.getLabel());
			reply.setAttribute("partnerLink", STUB);
			reply.setAttribute("operation", STUB);
			parent.appendChild(reply);
		} else if (event.getLabel().endsWith(INVOKE)) {
			Element invoke = document.createElement("invoke");
			invoke.setAttribute("name", event.getLabel());
			invoke.setAttribute("partnerLink", STUB);
			invoke.setAttribute("operation", STUB);
			parent.appendChild(invoke);

		}
	}

	private void findSequences() throws Exception {
		String vars[] = getVariants();
		for (String s: vars) {
			IEvent events[] = machine.getEvents();
			HashMap<Integer, IEvent> seqs = new HashMap<Integer, IEvent>(0);

			for (IEvent e : events) {
				IGuard guards[] = e.getGuards();

				for (IGuard guard : guards) {
					
					if (guard.getPredicateString().startsWith(s)){
						String pd = guard.getPredicateString().trim();
						int pos = Integer.parseInt(pd.substring(pd.length()-1));
						seqs.put(pos, e);
						break;
					}
				}
			}
			
			int size = seqs.size();
			
			if (size > 0) {
				
				Element sequence = document.createElement("sequence");
				IEvent myEvent = seqs.get(0);
				sequence.setAttribute("name", myEvent.getLabel());
				process.appendChild(sequence);
				
				for (int i = size-1; i > 0; i--){
					IEvent e = seqs.get(i);
					createActivity(sequence, e);
				}
			}
			
		}

	}
	
	

	private String[] getVariants() throws Exception{
		IEvent events[] = machine.getEvents();
		ArrayList<String> vars = new ArrayList<String>(0);
		
		for (IEvent e : events) {
			IGuard guards[] = e.getGuards();

			for (IGuard guard : guards) {
				System.out.println( guard.getPredicateString());
				if (guard.getPredicateString().contains("_Variant")){
					String var = guard.getPredicateString().split(" ")[0];
					if (!vars.contains(var)){
						vars.add(var);
					}
				}
			}
		}
		String s[] = new String[vars.size()];
		return vars.toArray(s);
	}

}
