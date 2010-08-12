package vutshila.labs.bpelgen.core.eventb;

public class MachineVariable extends EventbElement {

	private boolean variableAbstract;
	private boolean variableConcrete;
	private String type;

	public MachineVariable(String name, String source, String type,
			boolean variableAbstract, boolean variableConcrete) {
		super(name, source);
		this.type = type;
		this.variableAbstract = variableAbstract;
		this.variableConcrete = variableConcrete;
	}

	public String getType() {
		return type;
	}

	public boolean isMachineAbstract() {
		return variableAbstract;
	}

	public boolean isVariableConcrete() {
		return variableConcrete;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVariableAbstract(boolean variableAbstract) {
		this.variableAbstract = variableAbstract;
	}

	public void setVariableConcrete(boolean variableConcrete) {
		this.variableConcrete = variableConcrete;
	}

}
