
package com.modularit.beans;

import static org.apache.commons.lang.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ObjectUtils;

/**
 * Builder object for instantiating and populating objects which follow the Java beans standards conventions for getter/setters
 * 
 * @author Stewart Bissett
 */
public class BeanBuilder<T> {

	/**
	 * Return an instance of a {@link BeanBuilder} for the given type which can then be populated with values either manually or automatically. For example:
	 * 
	 * <pre>
	 * BeanUtils.anInstanceOf(Person.class).populatedWith(BeanUtils.randomValues()).build();
	 * </pre>
	 * @param type
	 *            the type to return the {@link BeanBuilder} for
	 */
	public static <T> BeanBuilder<T> anInstanceOf(final Class<T> type) {
		return new BeanBuilder<T>(type);
	}

	/**
	 * Interface to be implement by classes which can return values to the requested types
	 * 
	 * @author Stewart Bissett
	 */
	public interface BeanPropertyValue {

		/**
		 * Return a {@link String} instance or <code>null</code>
		 */
		public String stringValue();

		/**
		 * Return an {@link Integer} instance or <code>null</code>
		 */
		public Integer intValue();

		/**
		 * Return a {@link Short} instance or <code>null</code>
		 */
		public Short shortValue();

		/**
		 * Return a {@link Long} instance or <code>null</code>
		 */
		public Long longValue();

		/**
		 * Return a {@link Double} instance or <code>null</code>
		 */
		public Double doubleValue();

		/**
		 * Return a {@link Float} instance or <code>null</code>
		 */
		public Float floatValue();

		/**
		 * Return a {@link Boolean} instance or <code>null</code>
		 */
		public Boolean booleanValue();

		/**
		 * Return a {@link Date} instance or <code>null</code>
		 */
		public Date dateValue();

		/**
		 * Return a {@link BigDecimal} instance or <code>null</code>
		 */
		public BigDecimal bigDecimalValue();

		/**
		 * Return a {@link Byte} instance or <code>null</code>
		 */
		public Byte byteValue();

		/**
		 * Return a {@link Character} instance or <code>null</code>
		 */
		public Character charValue();
	}

	private final Map<String, Object> properties = new HashMap<String, Object>();
	private final Map<String, Object> paths = new HashMap<String, Object>();
	private final Class<T> type;
	private BeanPropertyValue values;
	private int minCollectionSize = 1, maxCollectionSize = 5;

	public BeanBuilder(final Class<T> type) {
		this.type = type;
	}

	public BeanBuilder<T> populatedWith(final BeanPropertyValue values) {
		this.values = values;
		return this;
	}

	public BeanBuilder<T> withPropertyValue(final String propertyName, final Object value) {
		this.properties.put(propertyName, value);
		return this;
	}

	public BeanBuilder<T> withPathValue(final String path, final Object value) {
		this.paths.put(path, value);
		return this;
	}

	public BeanBuilder<T> withCollectionSize(final int min, final int max) {
		this.minCollectionSize = min;
		this.maxCollectionSize = max;
		return this;
	}

	public T build() {
		T instance = createNewInstance();
		BeanUtils.visitAll(instance, new BeanVisitor() {

			public void visit(final BeanProperty property, final Object current, final String path, final Object[] stack) {
				Object value = paths.get(path);
				if (value == null) {
					value = properties.get(property.getName());
					if (value == null) {
						value = createValue(property);
					}
				}
				property.setValue(current, value);
			}
		});
		return instance;
	}

	private T createNewInstance() {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new BeanBuilderException("Failed to instantiate '" + type + "'. Error [" + e.getMessage() + "]", e);
		} catch (IllegalAccessException e) {
			throw new BeanBuilderException(e);
		}
	}

	private Object createValue(final BeanProperty property) {
		if (property.isArray()) {
			return createArray(property.getType().getComponentType());
		} else if (property.isMap()) {
			return createMap(property.getTypeParameter(0), property.getTypeParameter(1));
		} else if (property.isSet()) {
			return createSet(property.getTypeParameter(0));
		} else if (property.isList() || property.isCollection()) {
			return createList(property.getTypeParameter(0));
		} else {
			return createValue(property.getType());
		}
	}

	@SuppressWarnings("unchecked")
	private <V> V createValue(final Class<V> type) {
		if (isType(type, String.class)) {
			return (V) values.stringValue();
		} else if (isType(type, Integer.class)) {
			return (V) values.intValue();
		} else if (isType(type, int.class)) {
			return (V) ObjectUtils.defaultIfNull(values.intValue(), 0);
		} else if (isType(type, Short.class)) {
			return (V) values.shortValue();
		} else if (isType(type, short.class)) {
			return (V) ObjectUtils.defaultIfNull(values.shortValue(), 0);
		} else if (isType(type, Long.class)) {
			return (V) values.longValue();
		} else if (isType(type, long.class)) {
			return (V) ObjectUtils.defaultIfNull(values.longValue(), 0L);
		} else if (isType(type, Double.class)) {
			return (V) values.doubleValue();
		} else if (isType(type, double.class)) {
			return (V) defaultIfNull(values.doubleValue(), 0.0);
		} else if (isType(type, Float.class)) {
			return (V) values.floatValue();
		} else if (isType(type, float.class)) {
			return (V) defaultIfNull(values.floatValue(), 0.0);
		} else if (isType(type, Boolean.class)) {
			return (V) values.booleanValue();
		} else if (isType(type, boolean.class)) {
			return (V) defaultIfNull(values.booleanValue(), false);
		} else if (isType(type, Byte.class)) {
			return (V) values.byteValue();
		} else if (isType(type, byte.class)) {
			return (V) defaultIfNull(values.byteValue(), (byte) 0);
		} else if (isType(type, Character.class)) {
			return (V) values.charValue();
		} else if (isType(type, char.class)) {
			return (V) defaultIfNull(values.charValue(), (char) 0);
		} else if (isType(type, Date.class)) {
			return (V) values.dateValue();
		} else if (isType(type, BigDecimal.class)) {
			return (V) values.bigDecimalValue();
		} else {
			try {
				return type.newInstance();
			} catch (Exception e) {
				throw new BeanBuilderException("Failed to instantiate instance of '" + type.getCanonicalName() + "'", e);
			}
		}
	}

	private <E> Object createArray(final Class<E> type) {
		int size = collectionSize();
		Object array = Array.newInstance(type, size);
		for (int i = 0; i < size; ++i) {
			Array.set(array, i, createValue(type));
		}
		return array;
	}

	private <E> Set<E> createSet(final Class<E> type) {
		Set<E> set = new HashSet<E>();
		for (int i = 0; i < collectionSize(); ++i) {
			set.add(createValue(type));
		}
		return set;
	}

	private <E> List<E> createList(final Class<E> type) {
		List<E> list = new ArrayList<E>();
		for (int i = 0; i < collectionSize(); ++i) {
			list.add(createValue(type));
		}
		return list;
	}

	private <K, V> Map<K, V> createMap(final Class<K> keyType, final Class<V> valueType) {
		Map<K, V> map = new HashMap<K, V>();
		for (int i = 0; i < collectionSize(); ++i) {
			map.put(createValue(keyType), createValue(valueType));
		}
		return map;
	}

	private int collectionSize() {
		int colllectionSize = Integer.MIN_VALUE;
		while (colllectionSize < minCollectionSize) {
			colllectionSize = nextInt(maxCollectionSize);
		}
		return colllectionSize;
	}

	private boolean isType(final Class<?> type, final Class<?>... options) {
		for (Class<?> option : options) {
			if (option.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

}
