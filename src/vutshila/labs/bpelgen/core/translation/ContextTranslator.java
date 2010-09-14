/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import vutshila.labs.bpelgen.core.EBConstant;
import vutshila.labs.bpelgen.core.XMLtool;

/**
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class ContextTranslator {

	private IContextRoot context;
	private Document document;

	public void init(IMachineRoot machine, IContextRoot context) {

		XMLtool xml = new XMLtool(true, null);
		document = xml.getDocument();
		this.context = context;
		Comment generated = document.createComment(EBConstant.GENERATED);
		document.appendChild(generated);
	}

	/**
	 * Creates xsd types corresponding to Event-B build-in types. All sets
	 * ending with Type are considered an a xsd ComplexType
	 */
	public boolean createTypes() {

		ICarrierSet[] sets;
		IAxiom[] axioms;

		boolean noErrors = true;
		try {
			sets = context.getCarrierSets();
			axioms = context.getAxioms();

			for (ICarrierSet set : sets) {
				String setName = set.getIdentifierString();

				if (setName.endsWith(EBConstant.TYPE)) {
					// Types
					
					for (IAxiom axiom : axioms) {
						PredicateString ps = new PredicateString();
						if (ps.createPredicate(axiom.getPredicateString())) {
							if (ps.getInput().equals(setName)) {
								System.out.println("ComplexType name: " + setName);
								// Testing xsd types
								String type = "";
								for (int i = 0; i < EBConstant.EVENTB_TYPES.length; i++) {
									if (EBConstant.EVENTB_TYPES[i].equals(ps
											.getOutput())) {
										type = EBConstant.XSD_TYPES[i];
										break;
									}
								}
								if (type.equals(""))
									noErrors = false;
								
								System.out.printf(
										"element name: %s, type: %s\n", ps
												.getOperation(), type);
							}
						} else {
							noErrors = false;
						}
					}

				} else if (setName.endsWith(EBConstant.MESSAGE)) {
					// Messages
					System.out.println("Message name: " + setName);
					for (IAxiom axiom : axioms) {
						PredicateString ps = new PredicateString();
						if (ps.createPredicate(axiom.getPredicateString())) {
							if (ps.getInput().equals(setName)
									&& ps.getOutput().endsWith(EBConstant.TYPE)) {
								System.out.printf("part name: %s, type: %s\n",
										ps.getOperation(), ps.getOutput());
							}
						} else {
							noErrors = false;
						}
					}
				} else {
					noErrors = false;
				}
			}

			// Operations
			for (IAxiom axiom : axioms) {
				PredicateString ps = new PredicateString();
				if (ps.createPredicate(axiom.getPredicateString())) {
					if (ps.getInput().endsWith(EBConstant.MESSAGE)
							&& ps.getOutput().endsWith(EBConstant.MESSAGE)) {
						System.out
								.printf(
										"operation name: %s, input message: %s, output message: %s\n",
										ps.getOperation(), ps.getInput(), ps
												.getOutput());
						// TODO using the same operation create a binding
						// element
					}
				} else {
					noErrors = false;
				}
			}

		} catch (RodinDBException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return noErrors;
	}

}
