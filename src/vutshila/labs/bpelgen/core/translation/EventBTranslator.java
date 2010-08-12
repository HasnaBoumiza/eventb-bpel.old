package vutshila.labs.bpelgen.core.translation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;

import vutshila.labs.bpelgen.core.eventb.Axiom;
import vutshila.labs.bpelgen.core.eventb.EventBAction;
import vutshila.labs.bpelgen.core.eventb.EventBContext;
import vutshila.labs.bpelgen.core.eventb.EventBEvent;
import vutshila.labs.bpelgen.core.eventb.EventBGuard;
import vutshila.labs.bpelgen.core.eventb.Invariant;
import vutshila.labs.bpelgen.core.eventb.Machine;
import vutshila.labs.bpelgen.core.eventb.MachineVariable;
import vutshila.labs.bpelgen.core.eventb.SeesContext;
import vutshila.labs.bpelgen.core.eventb.Variable;
import vutshila.labs.bpelgen.core.eventb.Variable.Scope;

/**
 * 
 * @author Ernest Mashele <mashern@tuks.co.za>
 * @author Kabelo Ramongane <djemba@tuks.co.za>
 * 
 */

// TODO: change class and method names
public class EventBTranslator {

    private Document machineDocument = null;
    private Document contextDocument = null;
    private String dir = null;
    private EventBContext context = new EventBContext();
    private Machine machine = new Machine();

    @SuppressWarnings("unchecked")
    private EventBAction[] createActions() {
	String actionPath = "/org.eventb.core.scMachineFile/org.eventb.core.scEvent/org.eventb.core.scAction";
	List<Node> list = null;
	List<EventBAction> actions = new ArrayList<EventBAction>(0);

	list = machineDocument.selectNodes(actionPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element actionElement = (Element) iter.next();

	    QName actionNameAtr = new QName("name");
	    QName actionLabelAtr = new QName("org.eventb.core.label");
	    QName actionAssignmentAtr = new QName("org.eventb.core.assignment");
	    QName actionSourceAtr = new QName("org.eventb.core.source");

	    String actionName = actionElement.attributeValue(actionNameAtr);
	    String actionLabel = actionElement.attributeValue(actionLabelAtr);
	    String actionSource = actionElement.attributeValue(actionSourceAtr);
	    String actionAssignment = actionElement
		    .attributeValue(actionAssignmentAtr);

	    EventBAction action = new EventBAction(actionName, actionSource,
		    actionLabel, actionAssignment);
	    actions.add(action);
	}

	EventBAction[] temp = new EventBAction[actions.size()];
	temp = actions.toArray(temp);
	return temp;
    }

    @SuppressWarnings("unchecked")
    private void createAxioms() {

	String axiomPath = "/org.eventb.core.scContextFile/org.eventb.core.scAxiom";
	List<Node> list = null;
	list = contextDocument.selectNodes(axiomPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element axiomElement = (Element) iter.next();

	    QName axiomNameAtr = new QName("name");
	    QName axiomLabelAtr = new QName("org.eventb.core.label");
	    QName axiomPredicateAtr = new QName("org.eventb.core.predicate");
	    QName axiomTheoremAtr = new QName("org.eventb.core.theorem");
	    QName axiomSourceAtr = new QName("org.eventb.core.source");

	    String axiomName = axiomElement.attributeValue(axiomNameAtr);
	    String axiomLabel = axiomElement.attributeValue(axiomLabelAtr);
	    String axiomTheorem = axiomElement.attributeValue(axiomTheoremAtr);
	    String axiomSource = axiomElement.attributeValue(axiomSourceAtr);
	    String axiomPredicate = axiomElement
		    .attributeValue(axiomPredicateAtr);

	    Axiom axiom = new Axiom(axiomName, axiomSource);
	    axiom.setPredicate(axiomPredicate);
	    axiom.setLabel(axiomLabel);
	    axiom.setTheorem(stringToBoolean(axiomTheorem));

	    context.addAxiom(axiom);
	}
    }

    @SuppressWarnings("unchecked")
    private void createCarrierSets() {
	String carrierSetPath = "/org.eventb.core.scContextFile/org.eventb.core.scCarrierSet";
	List<Node> list = contextDocument.selectNodes(carrierSetPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element carrierSetElement = (Element) iter.next();

	    QName carrierSetNameAtr = new QName("name");
	    QName carrierSetTypeAtr = new QName("org.eventb.core.type");
	    QName carrierSetSourceAtr = new QName("org.eventb.core.source");

	    String carrierSetName = carrierSetElement
		    .attributeValue(carrierSetNameAtr);
	    String carrierSetType = carrierSetElement
		    .attributeValue(carrierSetTypeAtr);
	    String carrierSetSource = carrierSetElement
		    .attributeValue(carrierSetSourceAtr);

	    Variable carrierSet = new Variable(carrierSetName,
		    carrierSetSource, carrierSetType, Scope.CARRIERSET);

	    context.addVariable(carrierSet);

	}
    }

    @SuppressWarnings("unchecked")
    private void createConstants() {
	String constantPath = "/org.eventb.core.scContextFile/org.eventb.core.scConstant";
	List<Node> list = null;

	list = contextDocument.selectNodes(constantPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element constantElement = (Element) iter.next();

	    QName constantNameAtr = new QName("name");
	    QName constantTypeAtr = new QName("org.eventb.core.type");
	    QName constantSourceAtr = new QName("org.eventb.core.source");

	    String constantName = constantElement
		    .attributeValue(constantNameAtr);
	    String constantType = constantElement
		    .attributeValue(constantTypeAtr);
	    String constantSource = constantElement
		    .attributeValue(constantSourceAtr);

	    Variable constant = new Variable(constantName, constantSource,
		    constantType, Scope.CONSTANT);

	    context.addVariable(constant);

	}
    }

    public void createEventB() {

	SeesContext seesContext = createSeesContext();
	machine.setSeesContext(seesContext);

	String eventContextFile = seesContext.getTarget();
	int lastPositionOfForwardSlash = eventContextFile.lastIndexOf("/");
	if (lastPositionOfForwardSlash != -1) {
	    eventContextFile = eventContextFile
		    .substring(lastPositionOfForwardSlash + 1);
	}

	// TODO: get context path from sees context (target)
	loadContextDocument(dir.concat(eventContextFile));
	QName isAccurate = new QName("org.eventb.core.accurate");
	QName configuration = new QName("org.eventb.core.configuration");
	Element root = contextDocument.getRootElement();
	String status = root.attributeValue(isAccurate);
	String config = root.attributeValue(configuration);
	context.setContextAccurate(stringToBoolean(status));
	context.setConfiguration(config);

	createAxioms();
	createCarrierSets();
	createConstants();

	createInvariants();
	createVariables();
	createEvents();
    }

    @SuppressWarnings("unchecked")
    private void createEvents() {
	String eventPath = "/org.eventb.core.scMachineFile/org.eventb.core.scEvent";
	List<Node> list = machineDocument.selectNodes(eventPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element eventElement = (Element) iter.next();

	    QName eventNameAtr = new QName("name");
	    QName eventAccurateAtr = new QName("org.eventb.core.accurate");
	    QName convergenceAtr = new QName("org.eventb.core.convergence");
	    QName eventExtendeAtr = new QName("org.eventb.core.extended");
	    QName eventLabelAtr = new QName("org.eventb.core.label");
	    QName variableSourceAtr = new QName("org.eventb.core.source");

	    String name = eventElement.attributeValue(eventNameAtr);
	    String isAccurate = eventElement.attributeValue(eventAccurateAtr);
	    String isExtended = eventElement.attributeValue(eventExtendeAtr);
	    String source = eventElement.attributeValue(variableSourceAtr);
	    String convergence = eventElement.attributeValue(convergenceAtr);
	    String label = eventElement.attributeValue(eventLabelAtr);

	    EventBEvent event = new EventBEvent(name, source);
	    event.setConvergence(Integer.parseInt(convergence));
	    event.setEventAccurate(stringToBoolean(isAccurate));
	    event.setEventExtended(stringToBoolean(isExtended));
	    event.setLabel(label);

	    // Guards
	    EventBGuard[] guards;
	    guards = createGuards();

	    for (EventBGuard guard : guards) {
		event.addGuard(guard);
	    }
	    // Actions

	    EventBAction[] actions = createActions();
	    for (EventBAction action : actions) {
		event.addAction(action);
	    }

	    Variable[] parameters = createParameters();

	    for (Variable parameter : parameters) {
		event.addParameter(parameter);
	    }

	    machine.addEvent(event);

	}
    }

    @SuppressWarnings("unchecked")
    private EventBGuard[] createGuards() {
	String guardPath = "/org.eventb.core.scMachineFile/org.eventb.core.scEvent/org.eventb.core.scGuard";
	List<Node> list = null;
	ArrayList<EventBGuard> guards = new ArrayList<EventBGuard>(0);

	list = machineDocument.selectNodes(guardPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element guardElement = (Element) iter.next();

	    QName guardNameAtr = new QName("name");
	    QName guardLabelAtr = new QName("org.eventb.core.label");
	    QName guardPredicateAtr = new QName("org.eventb.core.predicate");
	    QName guardTheoremAtr = new QName("org.eventb.core.theorem");
	    QName guardSourceAtr = new QName("org.eventb.core.source");

	    String guardName = guardElement.attributeValue(guardNameAtr);
	    String guardLabel = guardElement.attributeValue(guardLabelAtr);
	    String guardTheorem = guardElement.attributeValue(guardTheoremAtr);
	    String guardSource = guardElement.attributeValue(guardSourceAtr);
	    String guardPredicate = guardElement
		    .attributeValue(guardPredicateAtr);

	    EventBGuard guard = new EventBGuard(guardName, guardSource,
		    guardLabel, guardPredicate, stringToBoolean(guardTheorem));
	    guards.add(guard);
	}

	EventBGuard[] temp = new EventBGuard[guards.size()];
	temp = guards.toArray(temp);
	return temp;

    }

    @SuppressWarnings("unchecked")
    private void createInvariants() {
	String invariantPath = "/org.eventb.core.scMachineFile/org.eventb.core.scInvariant";
	List<Node> list = null;

	list = machineDocument.selectNodes(invariantPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element invariantElement = (Element) iter.next();

	    QName invariantNameAtr = new QName("name");
	    QName invariantLabelAtr = new QName("org.eventb.core.label");
	    QName invariantPredicateAtr = new QName("org.eventb.core.predicate");
	    QName invariantTheoremAtr = new QName("org.eventb.core.theorem");
	    QName invariantSourceAtr = new QName("org.eventb.core.source");

	    String invariantName = invariantElement
		    .attributeValue(invariantNameAtr);
	    String invariantLabel = invariantElement
		    .attributeValue(invariantLabelAtr);
	    String invariantTheorem = invariantElement
		    .attributeValue(invariantTheoremAtr);
	    String invariantSource = invariantElement
		    .attributeValue(invariantSourceAtr);
	    String invariantPredicate = invariantElement
		    .attributeValue(invariantPredicateAtr);

	    Invariant invariant = new Invariant(invariantName, invariantSource,
		    invariantLabel, invariantPredicate,
		    stringToBoolean(invariantTheorem));
	    machine.addInvariant(invariant);
	}
    }

    @SuppressWarnings("unchecked")
    private Variable[] createParameters() {
	String parameterPath = "/org.eventb.core.scMachineFile/org.eventb.core.scEvent/org.eventb.core.scParameter";
	List<Node> list = null;

	ArrayList<Variable> parameters = new ArrayList<Variable>(0);

	list = machineDocument.selectNodes(parameterPath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element parameterElement = (Element) iter.next();

	    QName parameterNameAtr = new QName("name");
	    QName parameterTypeAtr = new QName("org.eventb.core.type");
	    QName parameterSourceAtr = new QName("org.eventb.core.source");

	    String parameterName = parameterElement
		    .attributeValue(parameterNameAtr);
	    String parameterType = parameterElement
		    .attributeValue(parameterTypeAtr);
	    String parameterSource = parameterElement
		    .attributeValue(parameterSourceAtr);

	    Variable parameter = new Variable(parameterName, parameterSource,
		    parameterType, Scope.PARAMETER);
	    parameters.add(parameter);

	}
	Variable[] temp = new Variable[parameters.size()];
	temp = parameters.toArray(temp);
	return temp;
    }

    @SuppressWarnings("unchecked")
    private SeesContext createSeesContext() {

	SeesContext seesContext = new SeesContext();

	String contextRootPath = "/org.eventb.core.scMachineFile/org.eventb.core.scSeesContext";

	List<Node> list = machineDocument.selectNodes(contextRootPath);
	QName seesContextName = new QName("name");
	QName seesContextTarget = new QName("org.eventb.core.scTarget");
	QName seesContextSource = new QName("org.eventb.core.source");

	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element element = (Element) iter.next();
	    seesContext.setName(element.attributeValue(seesContextName));
	    seesContext.setTarget(element.attributeValue(seesContextTarget));
	    seesContext.setSource(element.attributeValue(seesContextSource));
	}

	return seesContext;
    }

    @SuppressWarnings("unchecked")
    private void createVariables() {
	String variablePath = "/org.eventb.core.scMachineFile/org.eventb.core.scVariable";
	List<Node> list = machineDocument.selectNodes(variablePath);
	Iterator<Node> iter = list.iterator();
	while (iter.hasNext()) {
	    Element invariantElement = (Element) iter.next();

	    QName variableNameAtr = new QName("name");
	    QName variableIsAbstractAtr = new QName("org.eventb.core.abstract");
	    QName variableTypeAtr = new QName("org.eventb.core.type");
	    QName variableIsConcreteAtr = new QName("org.eventb.core.concrete");
	    QName variableSourceAtr = new QName("org.eventb.core.source");

	    String name = invariantElement.attributeValue(variableNameAtr);
	    String isAbstract = invariantElement
		    .attributeValue(variableIsAbstractAtr);
	    String isConcrete = invariantElement
		    .attributeValue(variableIsConcreteAtr);
	    String source = invariantElement.attributeValue(variableSourceAtr);
	    String type = invariantElement.attributeValue(variableTypeAtr);

	    MachineVariable variable = new MachineVariable(name, source, type,
		    stringToBoolean(isAbstract), stringToBoolean(isConcrete));
	    machine.addVariable(variable);
	}

    }

    public EventBContext getContext() {
	return context;
    }

    public Machine getMachine() {
	return machine;
    }

    public boolean getMachineStatus() {
	QName isAccurate = new QName("org.eventb.core.accurate");
	Element root = machineDocument.getRootElement();
	String status = root.attributeValue(isAccurate);

	if (status.equals("true")) {
	    return true;
	}

	return false;
    }

    private void loadContextDocument(String contextFileName) {
	File contextXML = new File(contextFileName);
	SAXReader reader = new SAXReader();

	try {
	    contextDocument = reader.read(contextXML);
	} catch (DocumentException e) {
	    System.err.println(e.getMessage());
	}

    }

    /*
     * loads the machine using the eclipse from eclipse IFile
     * 
     * @param machineFile
     */
    public void loadMachineDocument(IFile machineFile, String machineName) {
	File machineXML = machineFile.getLocation().toFile();
	System.out.println(machineFile.getLocation());
	dir = machineXML.getAbsolutePath();
	int pos = dir.lastIndexOf(File.separator);
	dir = dir.substring(0, pos + 1);

	machine.setName(machineName);
	SAXReader reader = new SAXReader();

	try {
	    machineDocument = reader.read(machineXML);
	} catch (DocumentException e) {
	    System.err.println(e.getMessage());
	}
    }

    public void loadMachineDocument(String machineFileName) {

	File machineXML = new File(machineFileName);
	dir = machineXML.getAbsolutePath();
	int pos = dir.lastIndexOf(File.separator);
	dir = dir.substring(0, pos + 1);

	// TODO: set the machine name
	machine.setName(machineXML.getName());
	SAXReader reader = new SAXReader();

	try {
	    machineDocument = reader.read(machineXML);
	} catch (DocumentException e) {
	    System.err.println(e.getMessage());
	}

    }

    private boolean stringToBoolean(String bool) {
	return bool.equals("true");
    }

    public void cookWSDL() {
	WsdlDocumentCreator creator = new WsdlDocumentCreator(machine, context);
	creator.createDocument();
	creator.createFile();
    }

    public void cookBPEL() {
	BpelDocumentCreator creator = new BpelDocumentCreator(machine);
	creator.createDocument();
	creator.createFile();
    }
}
