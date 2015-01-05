
package org.exparity.beans;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import org.exparity.beans.core.BeanProperty;
import org.exparity.beans.core.BeanPropertyPath;
import org.exparity.beans.core.BeanVisitor;

/**
 * Static repository of {@link BeanVisitor} implementations
 * 
 * @author Stewart Bissett
 */
public abstract class BeanVisitors {

	/**
	 * Print all the properties visited to {@link System#out}
	 */
	public static BeanVisitor print() {
		return print(new OutputStreamWriter(System.out));
	}

	/**
	 * Print all the properties visited to the {@link Writer}
	 */
	public static BeanVisitor print(final Writer writer) {
		return new BeanVisitor() {

			final PrintWriter printer = new PrintWriter(writer);

			public void visit(final BeanProperty property, final Object current, final BeanPropertyPath path, final Object[] stack) {
				printer.println("'" + path + "' = '" + property.getValue() + "'");
				printer.flush();
			}
		};
	}
}
