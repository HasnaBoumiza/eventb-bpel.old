package vutshila.labs.bpelgen.core.eventb;

import java.util.HashSet;
import java.util.Set;

//TODO IMPLEMENT equals() for Sets

public class EventBEvent extends EventbElement {

	private boolean eventAccurate;
	private boolean extended;
	private String label;
	private int convergence;
	private Set<EventBAction> actions;
	private Set<Variable> parameters;
	private Set<EventBGuard> guards;

	public EventBEvent(String name, String source) {
		super(name, source);
		actions = new HashSet<EventBAction>(0);
		parameters = new HashSet<Variable>(0);
		guards = new HashSet<EventBGuard>(0);
	}

	public boolean addAction(EventBAction action) {
		return actions.add(action);
	}

	public boolean addGuard(EventBGuard guard) {
		return guards.add(guard);
	}

	public boolean addParameter(Variable parameter) {
		return parameters.add(parameter);
	}

	public EventBAction[] getActionsAsArray() {
		EventBAction aActions[] = new EventBAction[actions.size()];
		return actions.toArray(aActions);
	}

	public int getConvergence() {
		return convergence;
	}

	public EventBGuard[] getGuardsAsArray() {
		EventBGuard aGuards[] = new EventBGuard[guards.size()];
		return guards.toArray(aGuards);
	}

	public String getLabel() {
		return label;
	}

	public Variable[] getParametersAsArray() {
		Variable aParameters[] = new Variable[parameters.size()];
		return parameters.toArray(aParameters);
	}

	public boolean isEventAccurate() {
		return eventAccurate;
	}

	public boolean isEventExtended() {
		return extended;
	}

	public void setConvergence(int convergence) {
		this.convergence = convergence;
	}

	public void setEventAccurate(boolean eventAccurate) {
		this.eventAccurate = eventAccurate;
	}

	public void setEventExtended(boolean extended) {
		this.extended = extended;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
