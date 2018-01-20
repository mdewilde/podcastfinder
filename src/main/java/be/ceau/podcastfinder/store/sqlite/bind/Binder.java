/*
	Copyright 2018 Marceau Dewilde <m@ceau.be>
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
		https://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package be.ceau.podcastfinder.store.sqlite.bind;

import org.skife.jdbi.v2.SQLStatement;

/**
 * Interface describing logic to bind fields of an object to an SQL statement.
 */
public interface Binder<T> {

	/**
	 * Bind fields of the given instance to the given {@link SQLStatement}
	 * 
	 * @param statement
	 *            a {@link SQLStatement}, not {@code null}
	 * @param t
	 *            instance, not {@code null}
	 */
	public void bind(SQLStatement<?> statement, T t);

}
