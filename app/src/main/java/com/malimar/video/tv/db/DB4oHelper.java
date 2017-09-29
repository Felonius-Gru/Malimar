package com.malimar.video.tv.db;

import android.content.Context;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.malimar.video.tv.model.UserBean;

import java.io.IOException;


public class DB4oHelper {

	private static ObjectContainer oc = null;
	private Context context;



	public DB4oHelper(Context ctx) {
		context = ctx;
	}
	/**
	 * Create, open and close the database

	 */
	public ObjectContainer db() {

		try {
			if (oc == null || oc.ext().isClosed()) {
				oc = Db4oEmbedded.openFile(dbConfig(), db4oDBFullPath(context));
			}

			return oc;

		} catch (Exception ie) {
			Log.e(DB4oHelper.class.getName(), ie.toString());
			return null;
		}
	}

	/**
	 * Configure the behavior of the database
	 */

	private EmbeddedConfiguration dbConfig() throws IOException {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		// configuration.common().objectClass(FavouriteModel.class).objectField("_sessionKey").indexed(true);
		configuration.common().objectClass(UserBean.class)
				.cascadeOnUpdate(true);
		configuration.common().objectClass(UserBean.class)
				.cascadeOnActivate(true);
		configuration.common().objectClass(UserBean.class)
				.cascadeOnDelete(true);
		return configuration;
	}

	/**
	 * Returns the path for the database location
	 */

	private String db4oDBFullPath(Context ctx) {
		return ctx.getDir("data", 0) + "/" + "userbean.db4o";
	}

	/**
	 * Closes the database
	 */

	public void close() {
		if (oc != null)
			oc.close();
	}
}
