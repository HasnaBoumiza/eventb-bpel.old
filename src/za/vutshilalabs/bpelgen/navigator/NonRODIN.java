package za.vutshilalabs.bpelgen.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rodinp.core.RodinCore;

public class NonRODIN extends ViewerFilter {

	public NonRODIN() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject) {
			return RodinCore.valueOf((IProject) element).exists();
		}
		return true;
	}

}
