/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Parsers.toLatitude;
import static io.jenetics.jpx.Parsers.toLongitude;
import static io.jenetics.jpx.XMLReader.attr;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Two lat/lon pairs defining the extent of an element.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Bounds implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Latitude _minLatitude;
	private final Longitude _minLongitude;
	private final Latitude _maxLatitude;
	private final Longitude _maxLongitude;

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private Bounds(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		_minLatitude = requireNonNull(minLatitude);
		_minLongitude = requireNonNull(minLongitude);
		_maxLatitude = requireNonNull(maxLatitude);
		_maxLongitude = requireNonNull(maxLongitude);
	}

	/**
	 * Return the minimum latitude.
	 *
	 * @return the minimum latitude
	 */
	public Latitude getMinLatitude() {
		return _minLatitude;
	}

	/**
	 * Return the minimum longitude.
	 *
	 * @return the minimum longitude
	 */
	public Longitude getMinLongitude() {
		return _minLongitude;
	}

	/**
	 * Return the maximum latitude.
	 *
	 * @return the maximum latitude
	 */
	public Latitude getMaxLatitude() {
		return _maxLatitude;
	}

	/**
	 * Return the maximum longitude
	 *
	 * @return the maximum longitude
	 */
	public Longitude getMaxLongitude() {
		return _maxLongitude;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*Objects.hashCode(_minLatitude) + 37;
		hash += 31*Objects.hashCode(_minLongitude) + 37;
		hash += 31*Objects.hashCode(_maxLatitude) + 37;
		hash += 31*Objects.hashCode(_maxLongitude) + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof  Bounds &&
			Objects.equals(((Bounds)obj)._minLatitude, _minLatitude) &&
			Objects.equals(((Bounds)obj)._minLongitude, _minLongitude) &&
			Objects.equals(((Bounds)obj)._maxLatitude, _maxLatitude) &&
			Objects.equals(((Bounds)obj)._maxLongitude, _maxLongitude);
	}

	@Override
	public String toString() {
		return format(
			"[%s, %s][%s, %s]",
			_minLatitude,
			_minLongitude,
			_maxLatitude,
			_maxLongitude
		);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @return a new {@code Bounds} object with the given extent
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Bounds of(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		return new Bounds(minLatitude, minLongitude, maxLatitude, maxLongitude);
	}

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitudeDegree the minimum latitude
	 * @param minLongitudeDegree the minimum longitude
	 * @param maxLatitudeDegree the maximum latitude
	 * @param maxLongitudeDegree the maximum longitude
	 * @return a new {@code Bounds} object with the given extent
	 * @throws IllegalArgumentException if the latitude values are not within
	 *         the range of {@code [-90..90]}
	 * @throws IllegalArgumentException if the longitudes value are not within
	 *         the range of {@code [-180..180]}
	 */
	public static Bounds of(
		final double minLatitudeDegree,
		final double minLongitudeDegree,
		final double maxLatitudeDegree,
		final double maxLongitudeDegree
	) {
		return new Bounds(
			Latitude.ofDegrees(minLatitudeDegree),
			Longitude.ofDegrees(minLongitudeDegree),
			Latitude.ofDegrees(maxLatitudeDegree),
			Longitude.ofDegrees(maxLongitudeDegree)
		);
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	/**
	 * Writes this {@code Link} object to the given XML stream {@code writer}.
	 *
	 * @param writer the XML data sink
	 * @throws XMLStreamException if an error occurs
	 */
	void write(final XMLStreamWriter writer) throws XMLStreamException {
		final XMLWriter xml = new XMLWriter(writer);

		xml.write("bounds",
			xml.attr("minlat", _minLatitude),
			xml.attr("minlon", _minLongitude),
			xml.attr("maxlat", _maxLatitude),
			xml.attr("maxlon", _maxLongitude)
		);
	}

	static XMLReader<Bounds> reader() {
		final XML.Function<Object[], Bounds> creator = a -> Bounds.of(
			toLatitude(a[0], "Bounds.minlat"),
			toLongitude(a[1], "Bounds.minlon"),
			toLatitude(a[2], "Bounds.maxlat"),
			toLongitude(a[3], "Bounds.maxlon")
		);

		return XMLReader.of(creator, "bounds",
			attr("minlat"), attr("minlon"),
			attr("maxlat"), attr("maxlon")
		);
	}

}
