package vutshila.labs.bpelgen.core.translation;

public class EventBPredicate {
	final static String uE = "\u2208";
	final static String uArrow = "\u2192";

	private String operation;
	private String input;
	private String output;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public boolean equals(Object obj) {
		if (obj instanceof EventBPredicate) {
			return operation.equals(((EventBPredicate) obj).operation)
					&& input.equals(((EventBPredicate) obj).input)
					&& output.equals(((EventBPredicate) obj).output);
		}
		return false;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean createPredicate(String predicate) {
		if ((predicate.contains(uE)) && (predicate.contains(uArrow))) {
			int epos = predicate.indexOf(uE);
			int arrowpos = predicate.indexOf(uArrow);

			if (epos < arrowpos) {
				String left = predicate.substring(0, epos);
				String middle = predicate.substring(epos + 1, arrowpos);
				String right = predicate.substring(arrowpos + 1);

				setInput(middle.trim());
				setOperation(left.trim());
				setOutput(right.trim());

				return true;
			}
			return false;

		}

		return false;

	}

}