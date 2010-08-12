package vutshila.labs.bpelgen.core.translation;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import vutshila.labs.bpelgen.core.eventb.EventBEvent;
import vutshila.labs.bpelgen.core.eventb.Machine;
import vutshila.labs.bpelgen.core.eventb.MachineVariable;
import vutshila.labs.bpelgen.core.eventb.Variable;

public class BpelDocumentCreator extends DocumentCreator {

	private final int FILE_EXT_LEN = 4;
	private final String BPEL_EXT = ".bpel";
	private Machine machine;
	private Document document = null;
	private Element root = null;

	public BpelDocumentCreator(Machine machine) {
		this.machine = machine;
		name = machine.getName();
		name = name.substring(0, name.length() - FILE_EXT_LEN);
		String serviceName = name.concat("Services");
		document = DocumentHelper.createDocument();
		root = document.addElement("process").addAttribute("name", serviceName);

	}

	private void addNameSpaces() {
		Namespace bpelns = new Namespace("",
				"http://docs.oasis-open.org/wsbpel/2.0/process/abstract");
		Namespace xsd = new Namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		root.add(bpelns);
		root.add(xsd);
	}

	@Override
	public boolean createDocument() {
		// TODO Auto-generated method stub
		createProcess();
		return false;
	}

	@Override
	public boolean createFile() {
		// TODO Auto-generated method stub
		FileWriter bpelWriter = null;
		XMLWriter writer = null;

		try {
			bpelWriter = new FileWriter(name.concat(BPEL_EXT));
			OutputFormat format = OutputFormat.createPrettyPrint();
			writer = new XMLWriter(bpelWriter, format);
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}

		return true;
	}

	private void createProcess() {
		addNameSpaces();
		createVariables();
		createActivities();
	}

	private void createActivities() {
		// TODO Auto-generated method stub
		Element sequence = root.addElement("sequence");

		EventBEvent events[] = machine.getEventsAsArray();

		for (EventBEvent event : events) {
			// TODO: check whether it is a method call or initialises
			String eventName = event.getLabel();
			if (!eventName.equalsIgnoreCase("initialisation")) {
				Element invoke = sequence.addElement("invoke");
				invoke.addAttribute("name", eventName);
				invoke.addAttribute("partnerLink", "");
				invoke.addAttribute("portType", "");

				// Get it from action's assignment statement
				Variable[] parameters = event.getParametersAsArray();
				invoke.addAttribute("operation", "");
				for (Variable parameter : parameters) {
					invoke.addAttribute("inputVariable", parameter.getName());
				}

			}

		}
	}

	private void createVariables() {
		MachineVariable[] mVars = machine.getVariablesAsArray();
		Element variables = root.addElement("variables");
		for (MachineVariable machineVar : mVars) {
			Element element = variables.addElement("variable");
			element.addAttribute("name", machineVar.getName());
			element.addAttribute("messageType", machineVar.getType());
		}

	}

}
