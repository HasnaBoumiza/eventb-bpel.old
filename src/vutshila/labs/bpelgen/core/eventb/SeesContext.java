package vutshila.labs.bpelgen.core.eventb;

public class SeesContext extends EventbElement {

	private String target;

	public SeesContext() {
		super(null, null);
		target = null;
		// TODO Auto-generated constructor stub
	}

	public SeesContext(String name, String source, String target) {
		super(name, source);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
