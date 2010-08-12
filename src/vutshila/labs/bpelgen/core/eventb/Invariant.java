package vutshila.labs.bpelgen.core.eventb;

public class Invariant extends EventbElement {

	private String label;
	private String predicate;
	private boolean containsTheorem;

	public Invariant(String name, String source, String label,
			String predicate, boolean containsTheorem) {
		super(name, source);
		this.label = label;
		this.predicate = predicate;
		this.containsTheorem = containsTheorem;
	}

	public void addTheorem(boolean containsTheorem) {
		this.containsTheorem = containsTheorem;
	}

	public String getLabel() {
		return label;
	}

	public String getPredicate() {
		return predicate;
	}

	public boolean hasTheorem() {
		return containsTheorem;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

}
