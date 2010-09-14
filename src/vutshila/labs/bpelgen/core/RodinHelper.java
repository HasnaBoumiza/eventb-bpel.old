/**
 * 
 */
package vutshila.labs.bpelgen.core;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import vutshila.labs.bpelgen.core.translation.PredicateString;

/**
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class RodinHelper {

	/**
	 * Create a Rodin File construct (e.g. Machine, Context) (adapted from
	 * org.eventb.ui.wizards.NewComponentWizard.java doFinish())
	 * 
	 * @param filename
	 * @param project
	 * @param comment
	 * @return rodinFile the created Rodin File
	 * @throws RodinDBException
	 */
	private static int axiomCount = 0;

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
				rodinFile.getResource().setDerived(true);

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
			final String lastTerm, boolean reset) throws RodinDBException {

		if (contextRoot instanceof IContextRoot) {
			if (reset) {
				axiomCount = 0;
			}
			PredicateString ps = new PredicateString();
			ps.createPredicateString(firstTerm, middleTerm, lastTerm);
			String predicate = ps.getPredicateString();

			IAxiom axiom = contextRoot.createChild(IAxiom.ELEMENT_TYPE, null,
					null);
			axiom.setPredicateString(predicate, null);
			axiom.setLabel("axm" + (++axiomCount), null);
			System.out.println(axiomCount);
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
			IConstant constant = contextRoot.createChild(
					IConstant.ELEMENT_TYPE, null, null);
			constant.setIdentifierString(name, null);
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
			ICarrierSet carrierSet = contextRoot.createChild(
					ICarrierSet.ELEMENT_TYPE, null, null);
			carrierSet.setIdentifierString(name, null);
		}
	}
}
