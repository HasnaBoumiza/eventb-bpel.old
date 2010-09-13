/**
 * 
 */
package vutshila.labs.bpelgen.core;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class RodinHelper {

	/**
	 * Create a Rodin File construct (e.g. Machine, Context), code from
	 * http://wiki.event-b.org/
	 * 
	 * @param filename
	 * @param project
	 * @param comment
	 * @return rodinFile the created Rodin File
	 */
	public static IRodinFile createRodinConstruct(final String filename,
			final IRodinProject project, final String comment) {

		if (null == project) {
			return null;
		}

		try {
			final IRodinFile rodinFile = project.getRodinFile(filename);
			rodinFile.create(true, null);
			rodinFile.getResource().setDerived(true);

			if (rodinFile instanceof ICommentedElement) {
				if (comment != null && !comment.trim().equals("")) {
					((ICommentedElement) rodinFile).setComment(comment, null);
				}
			}

			return rodinFile;

		} catch (RodinDBException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a Rodin element ,code from http://wiki.event-b.org/
	 * 
	 * @param type
	 * @param name
	 * @param parent
	 * @param rodinFile
	 * @param comment
	 * @return rodinElement the created element
	 */
	@SuppressWarnings("unchecked")
	public static IInternalElement createRodinElement(
			final IInternalElementType type, final String name,
			final IInternalElement parent, final IRodinFile rodinFile,
			final String comment) {

		if (null == parent)
			return null;

		try {
			final IInternalElement rodinElement = parent.getInternalElement(
					type, name);
			rodinElement.create(null, null);

			if (rodinElement instanceof ILabeledElement) {
				((ILabeledElement) rodinElement).setLabel(name, null);
			}
			if (rodinElement instanceof IIdentifierElement) {
				((IIdentifierElement) rodinElement).setIdentifierString(name,
						null);
			}

			if (rodinElement instanceof ICommentedElement) {
				((ICommentedElement) rodinElement).setComment(comment, null);
			}

			if (rodinElement instanceof IRefinesEvent) {
				((IRefinesEvent) rodinElement)
						.setAbstractEventLabel(name, null);
			}

			return rodinElement;

		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
