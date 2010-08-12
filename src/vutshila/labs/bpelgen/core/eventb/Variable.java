package vutshila.labs.bpelgen.core.eventb;

public class Variable extends EventbElement {

	public enum Scope {
		PARAMETER, CONSTANT, CARRIERSET
	}

	private String type;
	private Scope scope;

	public Variable(String name, String source, String type, Scope scope) {
		super(name, source);
		this.type = type;
		this.scope = scope;
	}

	public Scope getScope() {
		return scope;
	}

	public String getType() {
		return type;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setType(String type) {
		this.type = type;
	}

}
