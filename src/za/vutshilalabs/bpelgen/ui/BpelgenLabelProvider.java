package za.vutshilalabs.bpelgen.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ernest Mashele <mashern@tuks.co.za>
 * 
 */

public class BpelgenLabelProvider implements ILabelProvider {

	public static Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener element) {
		// ignore

	}

	@Override
	public void dispose() {
		// ignore

	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IFile) {
			if (((IFile) element).getFileExtension().equals("wsdl")
					|| ((IFile) element).getFileExtension().equals("bpel")
					|| ((IFile) element).getFileExtension().equals("xsd")) {
				return getImage();

			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IFile) {
			return ((IFile) element).getName();
		}
		return null;
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// ignore
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

}
