package vutshila.labs.bpelgen.core.eventb;

import java.util.HashSet;
import java.util.Set;

public class Machine {

	private String name;
	private boolean machineAccurate;

	private String configuration;

	private Set<MachineVariable> variables;

	private Set<EventBEvent> events;

	private Set<Invariant> invariants;

	private SeesContext seesContext;

	public Machine() {
		events = new HashSet<EventBEvent>(0);
		invariants = new HashSet<Invariant>(0);
		variables = new HashSet<MachineVariable>(0);

	}

	public boolean addEvent(EventBEvent event) {
		return events.add(event);
	}

	public boolean addInvariant(Invariant invariant) {
		return invariants.add(invariant);
	}

	public boolean addVariable(MachineVariable machineVariable) {
		return variables.add(machineVariable);
	}

	public String getConfiguration() {
		return configuration;
	}

	public EventBEvent[] getEventsAsArray() {
		EventBEvent[] list = new EventBEvent[events.size()];
		return events.toArray(list);
	}

	public Invariant[] getInvariantsAsArray() {
		Invariant[] list = new Invariant[invariants.size()];
		return invariants.toArray(list);
	}

	public String getName() {
		return name;
	}

	public SeesContext getSeesContext() {
		return seesContext;
	}

	public MachineVariable[] getVariablesAsArray() {
		MachineVariable[] list = new MachineVariable[variables.size()];
		return variables.toArray(list);
	}

	public boolean isMachineAccurate() {
		return machineAccurate;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public void setMachineAccurate(boolean machineAccurate) {
		this.machineAccurate = machineAccurate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSeesContext(SeesContext seesContext) {
		this.seesContext = seesContext;
	}
	
}
