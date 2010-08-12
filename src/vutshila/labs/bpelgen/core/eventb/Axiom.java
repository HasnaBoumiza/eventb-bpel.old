package vutshila.labs.bpelgen.core.eventb;

public class Axiom extends EventbElement {
	private String predicate;
	private String label;
	private boolean theorem;

	public Axiom(String name, String source) {
		super(name, source);
		// TODO Auto-generated constructor stub
	}

	public String getLabel() {
		return label;
	}

	public String getPredicate() {
		return predicate;
	}

	public boolean hasTheorem() {
		return theorem;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public void setTheorem(boolean theorem) {
		this.theorem = theorem;
	}

}
