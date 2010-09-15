package vutshila.labs.bpelgen.core.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import vutshila.labs.bpelgen.core.eventb.Axiom;
import vutshila.labs.bpelgen.core.eventb.EventBContext;
import vutshila.labs.bpelgen.core.eventb.Machine;

public class WsdlDocumentCreator extends DocumentCreator {
    public final String MESSAGE_SUF = "MSG";
    private EventBContext context;

    private WSDLFactory factory;
    private WSDLWriter writer;
    private Definition definition;

    public WsdlDocumentCreator(Machine machine, EventBContext context) {
	name = machine.getName();
	this.context = context;

	try {
	    factory = WSDLFactory.newInstance();
	    writer = factory.newWSDLWriter();
	    definition = factory.newDefinition();
	} catch (WSDLException e) {
	    System.err.println(e.getMessage());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see vlabs.translation.DocumentCreator#createDocument() Create WSDL
     * document from Event-B context
     */
    @Override
    public boolean createDocument() {
	String tns = "http://localhost";
	String xsd = "http://www.w3.org/2001/XMLSchema";

	// Set namespaces
	definition.setQName(new QName(name.substring(0, name.length() - 4)
		.concat("Service")));
	definition.setTargetNamespace(tns);
	definition.addNamespace("tns", tns);
	definition.addNamespace("xsd", xsd);

	HashTable<PredicateString> messageTable = new HashTable<PredicateString>();
	ArrayList<PredicateString> operationList = new ArrayList<PredicateString>(
		0);

	for (Axiom axiom : context.getAxiomsAsArray()) {
	    String predicateString = axiom.getPredicate();
	    PredicateString p = new PredicateString();
	    if (p.createPredicate(predicateString)) {
		if (p.getOperation().endsWith(MESSAGE_SUF)) {
		    // TODO: insert to hash table
		    messageTable.insert(p.getInput(), p);
		} else {
		    operationList.add(p);
		}
	    }
	}

	// Create PortTypes and Messages
	PortType portType = definition.createPortType();
	portType.setQName(new QName("CHANGE_ME"));

	for (PredicateString ePredicate : operationList) {

	    Operation operation = definition.createOperation();
	    Input in = definition.createInput();
	    Output out = definition.createOutput();

	    // Operation input
	    {
		String inString = ePredicate.getInput();
		List<PredicateString> messages = messageTable
			.getHeapLocation(inString);

		Message msg = definition.createMessage();
		msg.setQName(new QName(inString));
		// Create parts for the message

		for (PredicateString p : messages) {
		    Part part = definition.createPart();
		    part.setName(p.getOperation());
		    part.setTypeName(new QName(p.getOutput()));

		    msg.addPart(part);
		}
		msg.setUndefined(false);
		definition.addMessage(msg);
		in.setMessage(msg);
	    }
	    // Operation output
	    {
		String outString = ePredicate.getOutput();
		List<PredicateString> messages = messageTable
			.getHeapLocation(outString);

		Message msg = definition.createMessage();
		msg.setQName(new QName(outString));
		// Create parts for the message

		for (PredicateString p : messages) {
		    Part part = definition.createPart();
		    part.setName(p.getOperation());
		    part.setTypeName(new QName(p.getOutput()));

		    msg.addPart(part);
		}
		msg.setUndefined(false);
		definition.addMessage(msg);
		out.setMessage(msg);

	    }

	    operation.setName(ePredicate.getOperation());
	    operation.setInput(in);
	    operation.setOutput(out);
	    operation.setUndefined(false);
	    portType.addOperation(operation);

	}
	portType.setUndefined(false);
	definition.addPortType(portType);

	return true;
    }

    @Override
    public boolean createFile() {
	// TODO: update workspace
	// Add comments to file
	Writer output = null;
	File wsdlFile = new File(name.substring(0, name.length() - 4).concat(
		".wsdl"));

	try {
	    output = new BufferedWriter(new FileWriter(wsdlFile));
	    writer.writeWSDL(definition, output);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (WSDLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return false;

    }

    /**
     * For use with the eclipse environment
     * 
     * @return
     */
    public Definition getDefinition() {
	return definition;
    }
}
