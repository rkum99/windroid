/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.identifyable;

/**
 * An Iterable Node
 * 
 * @author mac
 * @version $Id$
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
	 * @param node
	 * @throws NullPointerException
	 *             if null passed in
	 */
	public void add(final T node);

	/**
	 * returns true if node represents a leaf in a tree.
	 * 
	 * @return
	 */
	public boolean isLeaf();

}
