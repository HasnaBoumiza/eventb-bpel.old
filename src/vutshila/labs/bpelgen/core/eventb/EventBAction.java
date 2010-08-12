package vutshila.labs.bpelgen.core.eventb;

public class EventBAction extends EventbElement {

	private String label;
	private String assignment;

	public EventBAction(String name, String source, String label,
			String assignment) {
		super(name, source);
		this.label = label;
		this.assignment = assignment;
	}

	public String getAssignment() {
		return assignment;
	}

	public String getLabel() {
		return label;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
