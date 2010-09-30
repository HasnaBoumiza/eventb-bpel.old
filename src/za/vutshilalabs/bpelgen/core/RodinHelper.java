/**
 * 
 */
package za.vutshilalabs.bpelgen.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.core.translation.PredicateString;

/**
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class RodinHelper {

	/**
	 * Create IAction
	 * 
	 * @param event
	 * @param assignment
	 * @throws RodinDBException
	 */
	public static void createAction(final IEvent event, final String assignment)
			throws RodinDBException {
		IAction actions[] = event.getActions();
		int len = actions.length;
		boolean hasAction = false;

		for (IAction action : actions) {
			if (action.getAssignmentString().equals(assignment)) {
				hasAction = true;
				break;
			}
		}

		if (!hasAction) {
			IAction action = event.getRoot().createChild(IAction.ELEMENT_TYPE,
					null, null);
			action.setAssignmentString(assignment, null);
			action.setLabel("act" + (len + 1), null);
		}

	}

	/**
	 * Create Axiom
	 * 
	 * @param contextRoot
	 * @param firstTerm
	 * @param middleTerm
	 * @param lastTerm
	 * @throws RodinDBException
	 */
	public static void createAxiom(final IInternalElement contextRoot,
			final String firstTerm, final String middleTerm,
			final String lastTerm) throws RodinDBException {

		if (contextRoot instanceof IContextRoot) {
			PredicateString ps = new PredicateString();
			ps.createPredicateString(firstTerm, middleTerm, lastTerm);
			String predicate = ps.getPredicateString();

			IContextRoot croot = (IContextRoot) contextRoot;
			IAxiom[] axioms = croot.getAxioms();
			int count = axioms.length;

			for (IAxiom a : axioms) {
				PredicateString p = new PredicateString();
				p.createPredicate(a.getPredicateString());

				if (p.equals(ps)) {
					return;
				}
			}

			IAxiom axiom = contextRoot.createChild(IAxiom.ELEMENT_TYPE, null,
					null);
			axiom.setPredicateString(predicate, null);
			axiom.setLabel("axm" + (count + 1), null);
		}

	}

	/**
	 * 
	 * @param contextRoot
	 * @throws RodinDBException
	 */
	public static void createCarrierSet(final IInternalElement contextRoot,
			final String name) throws RodinDBException {
		if (contextRoot instanceof IContextRoot) {
			IContextRoot croot = (IContextRoot) contextRoot;
			ICarrierSet[] sets = croot.getCarrierSets();

			for (ICarrierSet set : sets) {
				if (set.getIdentifierString().equals(name)) {
					return;
				}
			}

			ICarrierSet carrierSet = contextRoot.createChild(
					ICarrierSet.ELEMENT_TYPE, null, null);
			carrierSet.setIdentifierString(name, null);
		}
	}

	/**
	 * 
	 * @param contextRoot
	 * @param name
	 * @throws RodinDBException
	 */
	public static void createConstant(final IInternalElement contextRoot,
			final String name) throws RodinDBException {
		if (contextRoot instanceof IContextRoot) {
			IContextRoot croot = (IContextRoot) contextRoot;
			IConstant constants[] = croot.getConstants();

			for (IConstant cons : constants) {
				if (cons.getIdentifierString().equals(name)) {
					return;
				}
			}

			IConstant constant = contextRoot.createChild(
					IConstant.ELEMENT_TYPE, null, null);
			constant.setIdentifierString(name, null);
		}
	}

	/**
	 * Create IEvent element
	 * 
	 * @param machine
	 * @param label
	 * @return
	 * @throws RodinDBException
	 */
	public static IEvent createEvent(final IInternalElement machine,
			final String label) throws RodinDBException {

		IEvent events[] = ((IMachineRoot) machine).getEvents();
		boolean hasEvent = false;
		for (IEvent event : events) {
			if (event.getLabel().equals(label)) {
				hasEvent = true;
				break;
			}
		}

		if (!hasEvent) {
			IEvent event = machine.createChild(IEvent.ELEMENT_TYPE, null, null);
			event.setLabel(label, null);
			event.setExtended(false, null);
			event.setConvergence(IConvergenceElement.Convergence.valueOf(0),
					null);
			return event;
		}

		return null;
	}

	public static IRodinFile createRodinConstruct(final String filename,
			final IRodinProject project) throws RodinDBException {
		if (project == null)
			return null;

		final IRodinFile rodinFile = project.getRodinFile(filename);

		RodinCore.run(new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				// do not overwrite existing file
				rodinFile.create(false, monitor);
				rodinFile.getResource().setDerived(true, null);

				final IInternalElement rodinRoot = rodinFile.getRoot();
				((IConfigurationElement) rodinRoot).setConfiguration(
						IConfigurationElement.DEFAULT_CONFIGURATION, monitor);

				if (rodinRoot instanceof IMachineRoot) {
					final IEvent init = rodinRoot.createChild(
							IEvent.ELEMENT_TYPE, null, monitor);
					init.setLabel(IEvent.INITIALISATION, monitor);
					init.setConvergence(
							IConvergenceElement.Convergence.ORDINARY, monitor);
					init.setExtended(false, monitor);
				}

				rodinFile.save(null, false);
			}
		}, null);

		return rodinFile;
	}

	/**
	 * Create a Variable + corresponding Invariant
	 * 
	 * @param machineRoot
	 * @param variableName
	 * @param variableType
	 * @throws RodinDBException
	 */
	public static void createVariables(IInternalElement machineRoot,
			final String variableName, final String variableType)
			throws RodinDBException {

		if (machineRoot instanceof IMachineRoot) {

			IInvariant invariant;
			IVariable variable;
			IVariable[] variables = ((IMachineRoot) machineRoot).getVariables();
			IInvariant[] invariants = ((IMachineRoot) machineRoot)
					.getInvariants();
			int count = invariants.length;
			boolean hasVariable = false;

			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getIdentifierString().equals(variableName)) {
					hasVariable = true;
					break;
				}
			}

			if (!hasVariable) {
				variable = machineRoot.createChild(IVariable.ELEMENT_TYPE,
						null, null);
				variable.setIdentifierString(variableName, null);

				invariant = machineRoot.createChild(IInvariant.ELEMENT_TYPE,
						null, null);
				String predicate = variableName.concat(" ")
						.concat(EBConstant.MATH_ELEMENT).concat(" ")
						.concat(variableType);
				invariant.setPredicateString(predicate, null);
				invariant.setLabel("inv" + (count + 1), null);
			}
		}

	}

	/**
	 * Get a Rodin project reference
	 * 
	 * @param project
	 * @return
	 */
	public static IRodinProject getRodinProject(IProject project) {

		IWorkspaceRoot wroot = project.getWorkspace().getRoot();
		IRodinDB rodinDB = RodinCore.valueOf(wroot);
		return rodinDB.getRodinProject(project.getName());
	}

	public static void linkContext(final IInternalElement machineRoot,
			final String contextName) throws RodinDBException {
		if (machineRoot instanceof IMachineRoot) {
			ISeesContext seesContext = machineRoot.createChild(
					ISeesContext.ELEMENT_TYPE, null, null);
			seesContext.setSeenContextName(contextName, null);
		}
	}
}