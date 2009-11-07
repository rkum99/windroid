package de.macsystems.windroid.identifyable;

/**
 * An Iterable Node
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface Node<T> extends Iterable<T>
{
	/**
	 * Returns no. of leafs in the node.
	 * 
	 * @return
	 */
	public int getSize();

	/**
	 * Adds T as a leaf.
	 * 
	 * @param t
	 * @throws NullPointerException
	 *             if null passed in
	 */
	public void add(final T t);

	/**
	 * returns true if node represents a leaf in a tree.
	 * 
	 * @return
	 */
	public boolean isLeaf();

}
