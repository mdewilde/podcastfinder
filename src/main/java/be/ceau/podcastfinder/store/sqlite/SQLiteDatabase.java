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
package be.ceau.podcastfinder.store.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

class SQLiteDatabase implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SQLiteDatabase.class);

	private static final String CREATE_TABLE_PODCASTS =
			"CREATE TABLE IF NOT EXISTS podcasts (uri TEXT NOT NULL UNIQUE, name TEXT, language TEXT, description TEXT, insertDate TEXT, podcastId INTEGER PRIMARY KEY NOT NULL)";

	private static final String CREATE_TABLE_STATUS =
			"CREATE TABLE IF NOT EXISTS status (podcastId INTEGER NOT NULL, date TEXT NOT NULL, items INTEGER, lastUpdate TEXT, hash INTEGER, bytes INTEGER, FOREIGN KEY(podcastId) REFERENCES podcasts(podcastId))";

	private static final String CREATE_TABLE_ERRORS =
			"CREATE TABLE IF NOT EXISTS errors (podcastId INTEGER NOT NULL, date TEXT NOT NULL, error TEXT NOT NULL, message TEXT NOT NULL, FOREIGN KEY(podcastId) REFERENCES podcasts(podcastId))";

	private final SQLiteDataSource ds;
	private final Handle handle;
	private final Connection connection;

	private static final Lock LOCK = new ReentrantLock();
	private static SQLiteDatabase instance;
	private static int users = 0;

	static SQLiteDatabase getInstance() {
		LOCK.lock();
		try {
			if (instance == null) {
				instance = new SQLiteDatabase();
			}
			users++;
			return instance;
		} finally {
			LOCK.unlock();
		}
	}

	private SQLiteDatabase() {
		ds = new SQLiteDataSource();
		ds.setUrl(getUrl());
		ds.setDatabaseName("podcasts");
		ds.setFullSync(false);
		try {
			connection = ds.getConnection();
			connection.setAutoCommit(true);
			connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
			handle = DBI.open(connection);
			prepareDatabase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getUrl() {
		return new StringBuilder()
				.append("jdbc:sqlite:")
				.append(System.getProperty("user.home"))
				.append("/podcastfinder")
				.append("/podcastfinder.sqlite")
				.toString();
	}

	private void prepareDatabase() throws SQLException {
		handle.createStatement(CREATE_TABLE_PODCASTS).execute();
		handle.createStatement(CREATE_TABLE_STATUS).execute();
		handle.createStatement(CREATE_TABLE_ERRORS).execute();
	}

	<T> T execute(Function<Handle, T> function) {
		LOCK.lock();
		try {
			return function.apply(handle);
		} finally {
			LOCK.unlock();
		}
	}
	
	void execute(Consumer<Handle> consumer) {
		LOCK.lock();
		try {
			consumer.accept(handle);
		} finally {
			LOCK.unlock();
		}
	}

	public void close() {
		LOCK.lock();
		try {
			users--;
			if (users == 0) { 
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("close()", e);
				}
				instance = null;
			}
		} finally {
			LOCK.unlock();
		}
	}

}
