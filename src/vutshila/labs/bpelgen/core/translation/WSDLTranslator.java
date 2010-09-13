/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import vutshila.labs.bpelgen.core.EBConstant;
import vutshila.labs.bpelgen.core.RodinHelper;

/**
 * Translate WSDL to Event-b context file
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
@SuppressWarnings("unused")
public class WSDLTranslator {
	private IRodinFile context;

	/**
	 * create the Context file
	 * 
	 * @param contextName
	 * @param project
	 * @return
	 * @throws RodinDBException
	 */
	public boolean init(final String contextName, final IRodinProject project)
			throws RodinDBException {

		String checkedName = contextName.endsWith(EBConstant.CONTEXT_EXTENSION) ? contextName
				: contextName.concat(EBConstant.CONTEXT_EXTENSION);
		context =  RodinHelper.createRodinConstruct(checkedName,
				project, null);
		
//		IContextRoot r = (IContextRoot) context;
//		r.setConfiguration(EBConstant.CONFIGURATION, null);
		System.out.println("working on it");
//		ICarrierSet set = (ICarrierSet) RodinHelper.createRodinElement(
//				ICarrierSet.ELEMENT_TYPE, "POMessage", context,
//				(IRodinFile) context, null);
//
//		ICarrierSet set2 = (ICarrierSet) RodinHelper.createRodinElement(
//				ICarrierSet.ELEMENT_TYPE, "CustomerInfoType", context,
//				(IRodinFile) context, null);
//
//		IConstant constant = (IConstant) RodinHelper.createRodinElement(
//				IConstant.ELEMENT_TYPE, "customerInfoPOMessage", context,
//				(IRodinFile) context, null);
//
//		IAxiom axiom = (IAxiom) RodinHelper.createRodinElement(
//				IAxiom.ELEMENT_TYPE, "axm1", context, (IRodinFile) context,
//				null);
//		PredicateString s = new PredicateString();
//		s.createPredicateString("customerInfoPOMessage", "POMessage",
//				"CustomerInfoType");
//		axiom.setPredicateString(s.getPredicateString(), null);

//		// Save changes
		RodinCore.run(new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Generating file", 10);
				project.save(null, true);
				monitor.done();
			}

		}, null);

		return context != null;
	}

	public void testContext(final IRodinProject rodinProject){
//			throws RodinDBException {
//		System.out.println("working on it");
//		ICarrierSet set = (ICarrierSet) RodinHelper.createRodinElement(
//				ICarrierSet.ELEMENT_TYPE, "POMessage", context,
//				(IRodinFile) context, null);
//
//		ICarrierSet set2 = (ICarrierSet) RodinHelper.createRodinElement(
//				ICarrierSet.ELEMENT_TYPE, "CustomerInfoType", context,
//				(IRodinFile) context, null);
//
//		IConstant constant = (IConstant) RodinHelper.createRodinElement(
//				IConstant.ELEMENT_TYPE, "customerInfoPOMessage", context,
//				(IRodinFile) context, null);
//
//		IAxiom axiom = (IAxiom) RodinHelper.createRodinElement(
//				IAxiom.ELEMENT_TYPE, "axm1", context, (IRodinFile) context,
//				null);
//		PredicateString s = new PredicateString();
//		s.createPredicateString("customerInfoPOMessage", "POMessage",
//				"CustomerInfoType");
//		axiom.setPredicateString(s.getPredicateString(), null);
//
//		// Save changes
//		RodinCore.run(new IWorkspaceRunnable() {
//
//			@Override
//			public void run(IProgressMonitor monitor) throws CoreException {
//				monitor.beginTask("Generating file", 10);
//				rodinProject.save(null, true);
//				monitor.done();
//			}
//
//		}, null);
	}

}
