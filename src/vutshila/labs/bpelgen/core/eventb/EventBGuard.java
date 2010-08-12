package vutshila.labs.bpelgen.core.eventb;

public class EventBGuard extends EventbElement {

	private String label;
	private String predicate;
	private boolean theorem;

	public EventBGuard(String name, String source, String label,
			String predicate, boolean theorem) {
		// TODO Auto-generated constructor stub
		super(name, source);
		this.label = label;
		this.predicate = predicate;
		this.theorem = theorem;
	}

	public void addTheorem(boolean theorem) {
		this.theorem = theorem;
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

}
