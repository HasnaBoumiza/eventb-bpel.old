/**
 * 
 */
package za.vutshilalabs.bpelgen.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ernest Mashele <mashern@tuks.co.za>
 * 
 */
public class BpelgenContent implements ITreeContentProvider {

	public BpelgenContent() {
		super();
	}

	@Override
	public void dispose() {
		// ignore

	}

	@Override
	public Object[] getChildren(Object parent) {
		IWorkingSet[] workingSets = PlatformUI.getWorkbench()
				.getWorkingSetManager().getAllWorkingSets();
		List<IWorkingSet> sets = new ArrayList<IWorkingSet>();

		for (int i = 0; i < workingSets.length; i++) {
			IWorkingSet workingSet = workingSets[i];
			if (!workingSet.isAggregateWorkingSet()) {
				sets.add(workingSet);
			}
		}
		return sets.toArray(new IWorkingSet[0]);
	}

	@Override
	public Object[] getElements(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

}
