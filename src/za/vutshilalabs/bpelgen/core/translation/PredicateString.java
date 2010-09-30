package za.vutshilalabs.bpelgen.core.translation;

import za.vutshilalabs.bpelgen.core.EBConstant;

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

	public boolean equals(Object obj) {
		if (obj instanceof PredicateString) {
			return operation.equals(((PredicateString) obj).operation)
					&& input.equals(((PredicateString) obj).input)
					&& output.equals(((PredicateString) obj).output);
		}
		return false;
	}

	public String getInput() {
		return input;
	}

	public String getOperation() {
		return operation;
	}

	public String getOutput() {
		return output;
	}

	/**
	 * get the RAW format Predicate string
	 * 
	 * @return
	 */
	public String getPredicateString() {
		String pred1 = operation.concat(" ").concat(EBConstant.MATH_ELEMENT)
				.concat(" ");
		String pred2 = input.concat(" ").concat(EBConstant.MATH_ARROW)
				.concat(" ").concat(output);
		return pred1.concat(pred2);
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
