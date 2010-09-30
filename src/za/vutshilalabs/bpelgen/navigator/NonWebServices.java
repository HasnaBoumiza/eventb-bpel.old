package za.vutshilalabs.bpelgen.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class NonWebServices extends ViewerFilter {

	public NonWebServices() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IResource) {
			if (element instanceof IFile) {
				if (((IFile) element).getFileExtension().endsWith("wsdl"))
					return true;
				else if (((IFile) element).getFileExtension().endsWith("bpel"))
					return true;
				else if (((IFile) element).getFileExtension().endsWith("xsd"))
					return true;
			}

			if (element instanceof IProject) {
				return true;
			}
		}
		return false;
	}

}
