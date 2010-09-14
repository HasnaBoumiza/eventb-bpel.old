package vutshila.labs.bpelgen.core.translation;

import vutshila.labs.bpelgen.core.EBConstant;

/**
 * 
 * @author Mashele Ernest
 * @author Kabelo Ramongane
 * 
 */

public class PredicateString {

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
		if (obj instanceof PredicateString) {
			return operation.equals(((PredicateString) obj).operation)
					&& input.equals(((PredicateString) obj).input)
					&& output.equals(((PredicateString) obj).output);
		}
		return false;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean createPredicate(String predicate) {
		if ((predicate.contains(EBConstant.MATH_ELEMENT))
				&& (predicate.contains(EBConstant.MATH_ARROW))) {
			int epos = predicate.indexOf(EBConstant.MATH_ELEMENT);
			int arrowpos = predicate.indexOf(EBConstant.MATH_ARROW);

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

	public void createPredicateString(String operation, String input,
			String output) {
		setInput(input);
		setOutput(output);
		setOperation(operation);
	}

	/**
	 * get the RAW format Predicate string
	 * 
	 * @return
	 */
	public String getPredicateString() {
		String pred1 = operation.concat(EBConstant.MATH_ELEMENT);
		String pred2 = (input.concat(EBConstant.MATH_ARROW)).concat(output);
		return pred1.concat(pred2);
	}
}
