package vutshila.labs.bpelgen.core.eventb;

public class EventbElement {

	protected String name;
	protected String source;

	public EventbElement(String name, String source) {
		this.name = name;
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
