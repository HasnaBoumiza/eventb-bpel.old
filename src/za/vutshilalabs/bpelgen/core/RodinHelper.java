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
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
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

	private static final Object ID = "id";

	/**
	 * check for unAllowed names
	 * 
	 * @param id
	 * @return
	 */
	private static String clean(String id) {
		if (id.equals(ID)) {
			return id.toUpperCase();
		}
		return id;
	}

	/**
	 * @param event
	 * @param assignment
	 * @param comment
	 * @throws RodinDBException
	 */
	public static void createAction(final IEvent event,
			final String assignment, final String comment)
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
			IAction action = event
					.createChild(IAction.ELEMENT_TYPE, null, null);
			action.setLabel("act" + (len + 1), null);
			action.setComment(comment, null);
			action.setAssignmentString(assignment, null);
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
			ps.createPredicateString(clean(firstTerm), clean(middleTerm),
					clean(lastTerm));
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
				if (set.getIdentifierString().equals(clean(name))) {
					return;
				}
			}

			ICarrierSet carrierSet = contextRoot.createChild(
					ICarrierSet.ELEMENT_TYPE, null, null);
			carrierSet.setIdentifierString(clean(name), null);
		}
	}

	/**
	 * 
	 * @param contextRoot
	 * @param name
	 * @throws RodinDBException
	 */
	public static boolean createConstant(final IInternalElement contextRoot,
			final String name) throws RodinDBException {
		if (contextRoot instanceof IContextRoot) {
			IContextRoot croot = (IContextRoot) contextRoot;
			IConstant constants[] = croot.getConstants();

			for (IConstant cons : constants) {
				if (cons.getIdentifierString().equals(clean(name))) {
					return false;
				}
			}

			IConstant constant = contextRoot.createChild(
					IConstant.ELEMENT_TYPE, null, null);
			constant.setIdentifierString(clean(name), null);
			return true;
		}
		return false;
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
			if (event.getLabel().equals(clean(label))) {
				hasEvent = true;
				break;
			}
		}

		if (!hasEvent) {
			IEvent event = machine.createChild(IEvent.ELEMENT_TYPE, null, null);
			event.setLabel(clean(label), null);
			event.setExtended(false, null);
			event.setConvergence(IConvergenceElement.Convergence.valueOf(0),
					null);
			return event;
		}

		return null;
	}

	/**
	 * Create non-Ordinary event
	 * 
	 * @param machineRoot
	 * @param label
	 * @param convergance
	 * @return
	 * @throws RodinDBException
	 */
	public static IEvent createEvent(final IInternalElement machine,
			final String label, Convergence convergance)
			throws RodinDBException {

		IEvent events[] = ((IMachineRoot) machine).getEvents();
		boolean hasEvent = false;
		for (IEvent event : events) {
			if (event.getLabel().equals(clean(label))) {
				hasEvent = true;
				break;
			}
		}

		if (!hasEvent) {
			IEvent event = machine.createChild(IEvent.ELEMENT_TYPE, null, null);
			event.setLabel(clean(label), null);
			event.setExtended(false, null);
			event.setConvergence(convergance, null);
			return event;
		}

		return null;
	}

	/**
	 * Create Event parameter
	 * 
	 * @param event
	 * @param name
	 * @param comment
	 * @throws RodinDBException
	 */
	public static void createParameter(final IEvent event, final String name,
			final String comment) throws RodinDBException {
		IParameter params[] = event.getParameters();
		boolean exist = false;
		for (IParameter param : params) {
			if (param.getIdentifierString().equals(name)) {
				exist = false;
				break;
			}
		}

		if (!exist) {
			IParameter param = event.createChild(IParameter.ELEMENT_TYPE, null,
					null);
			param.setIdentifierString(name, null);

			if (comment != null) {
				param.setComment(comment, null);
			}
		}
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
				if (variables[i].getIdentifierString().equals(
						clean(variableName))) {
					hasVariable = true;
					break;
				}
			}

			if (!hasVariable) {
				variable = machineRoot.createChild(IVariable.ELEMENT_TYPE,
						null, null);
				variable.setIdentifierString(clean(variableName), null);
				

				invariant = machineRoot.createChild(IInvariant.ELEMENT_TYPE,
						null, null);
				String predicate = variableName.concat(" ")
						.concat(IGlobalConstants.MATH_ELEMENT).concat(" ")
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

	public static void createGuard(IEvent event, final String predicate)
			throws RodinDBException {
		IGuard guards[] = event.getGuards();

		boolean exist = false;

		for (IGuard g : guards) {
			if (g.getPredicateString().equals(predicate)) {
				exist = true;
				break;
			}
		}

		if (!exist) {
			IGuard guard = event.createChild(IGuard.ELEMENT_TYPE, null, null);
			guard.setLabel("grd" + guards.length, null);
			guard.setPredicateString(predicate, null);
		}
	}
}