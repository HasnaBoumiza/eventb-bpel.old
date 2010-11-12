package za.vutshilalabs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.rodinp.core.IRodinProject;

import za.vutshilalabs.bpelgen.core.RodinHelper;

/**
 * 
 * @author Mashele Ernest <mashern@tuks.co.za>
 * 
 */

public class Updator {

	public static void update(IFile file) {
		IProject project = file.getProject();
		IRodinProject rodinProject = RodinHelper.getRodinProject(project);

		if (file.getFileExtension().equals("wsdl")) {

			WSDLTranslator wsdlTranslator = new WSDLTranslator();
			try {
				wsdlTranslator.init(file, rodinProject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (file.getFileExtension().equals("bpel")) {
			BPELTranslator bpelTranslator = new BPELTranslator();
			try {
				bpelTranslator.init(file, rodinProject);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}

	}

}
