
package com.modularit.beans;

import static com.modularit.beans.BeanUtils.property;
import static com.modularit.beans.BeanUtils.hasProperty;

/**
 * A re-usable definition of a property which can be used to re-usably inspect and mutate objects for a given property name. For Example:
 * 
 * <pre>
 *  BeanPropertyDescription surname = BeanUtils.property("surname");
 *  if ( surname.existsOn(aPerson) ) {
 *     System.out.println("Hello " + surname.getValue(aPerson) 
 *  }
 * </pre>
 * @author Stewart Bissett
 */
public class BeanPropertyDescriptor<P> {

	private final String name;
	private final Class<P> type;

	public BeanPropertyDescriptor(final String name, final Class<P> type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Retutn the name of the property
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the type of the property
	 */
	public Class<P> getType() {
		return type;
	}

	/**
	 * Test if this property exists on the supplied object instance. For Example: </p>
	 * 
	 * <pre>
	 *  BeanPropertyDescription surname = BeanUtils.property("surname");
	 *  if ( surname.existsOn(aPerson) ) {
	 *     System.out.println("Hello " + surname.getValue(aPerson) 
	 *  }
	 * </pre>
	 * 
	 * @param instance
	 *            the object instance to test to see if it has this property
	 */
	public boolean existsOn(final Object instance) {
		return hasProperty(instance, name);
	}

	/**
	 * Read the property from the supplied supplied object instance. For Example: </p>
	 * 
	 * <pre>
	 *  BeanPropertyDescription surname = BeanUtils.property("surname");
	 *  if ( surname.existsOn(aPerson) ) {
	 *     System.out.println("Hello " + surname.getValue(aPerson) 
	 *  }
	 * </pre>
	 * @param instance
	 *            the object instance to read this property from
	 */
	@SuppressWarnings("unchecked")
	public P getValue(final Object instance) {
		BeanProperty property = property(instance, name);
		if (property != null) {
			return (P) property.getValue();
		}
		return null;
	}

	/**
	 * Set the property on the supplied supplied object instance. For Example: </p>
	 * 
	 * <pre>
	 * BeanPropertyDescription surname = BeanUtils.property(&quot;surname&quot;);
	 * if (surname.existsOn(aPerson)) {
	 * 	surname.setValue(aPerson, &quot;Smith&quot;);
	 * }
	 * </pre>
	 * @param instance
	 *            the object instance to set this property on
	 * @param value
	 *            the value to set
	 */
	public void setValue(final Object instance, final P value) {
		BeanProperty property = property(instance, name);
		if (property != null) {
			property.setValue(value);
		} else {
			throw new BeanPropertyNotFound("Property '" + name + " was not found on '" + instance + "'");
		}
	}
}
