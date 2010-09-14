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
import org.eventb.core.IExtendsContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import vutshila.labs.bpelgen.core.EBConstant;
import vutshila.labs.bpelgen.core.RodinHelper;

/**
 * Translate WSDL to Event-b contextFile file
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
@SuppressWarnings("unused")
public class WSDLTranslator {
	private IRodinFile contextFile;

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
		contextFile = RodinHelper.createRodinConstruct(checkedName, project);
		
		IInternalElement root = contextFile.getRoot();
		RodinHelper.createCarrierSet(root, "POMessage");
		RodinHelper.createCarrierSet(root, "CustomerInfoType");
		RodinHelper.createConstant(root, "customerInfo");
		RodinHelper.createAxiom(root, "customerInfo", "POMessage", "CustomerInfoType", true);
		contextFile.save(null, true);
		
		return contextFile != null;
	}

}
