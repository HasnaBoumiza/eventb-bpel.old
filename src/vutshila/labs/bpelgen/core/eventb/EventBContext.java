package vutshila.labs.bpelgen.core.eventb;

import java.util.ArrayList;
import java.util.List;

public class EventBContext {
	private List<Axiom> axioms = null;
	private List<Variable> constants = null;
	private List<Variable> carrierSets = null;

	private boolean contextAccurate;
	private String configuration;

	public EventBContext() {
		this.contextAccurate = false;
		this.configuration = null;

		axioms = new ArrayList<Axiom>(0);
		constants = new ArrayList<Variable>(0);
		carrierSets = new ArrayList<Variable>(0);
	}

	public void addAxiom(Axiom axiom) {
		axioms.add(axiom);
	}

	public void addVariable(Variable variable) {

		switch (variable.getScope()) {
		case CONSTANT:
			constants.add(variable);
			break;
		case CARRIERSET:
			carrierSets.add(variable);
			break;
		default:
			break;
		}
	}

	public Axiom[] getAxiomsAsArray() {
		Axiom[] list = new Axiom[axioms.size()];
		return axioms.toArray(list);
	}

	public Variable[] getCarrierSetsAsArray() {
		Variable[] list = new Variable[carrierSets.size()];
		return carrierSets.toArray(list);
		
	}

	public String getConfiguration() {
		return configuration;
	}

	public Variable[] getConstantsAsArray() {
		Variable[] list = new Variable[constants.size()];
		return constants.toArray(list);
	}

	public boolean isContextAccurate() {
		return contextAccurate;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public void setContextAccurate(boolean contextAccurate) {
		this.contextAccurate = contextAccurate;
	}

}
