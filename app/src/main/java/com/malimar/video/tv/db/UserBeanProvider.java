package com.malimar.video.tv.db;

import android.content.Context;

import com.db4o.query.Query;
import com.malimar.video.tv.model.UserBean;

import java.util.Iterator;
import java.util.List;


public class UserBeanProvider extends DB4oHelper {

	public final static String TAG = "UserBeanProvider";
	private static UserBeanProvider provider = null;

	private UserBeanProvider(Context ctx) {
		super(ctx);
	}

	public static synchronized UserBeanProvider getInstance(Context ctx) {
		if (provider == null)
			provider = new UserBeanProvider(ctx);
		return provider;
	}

	public synchronized void store(UserBean model) {
		Query query = db().query();
		query.constrain(UserBean.class);

		@SuppressWarnings("rawtypes")
		List results = query.execute();

		@SuppressWarnings("rawtypes")
		Iterator iterator = results.iterator();

		while (iterator.hasNext()) {
			UserBean saveBean = (UserBean) iterator.next();
			delete(saveBean);
		}

		db().store(model);
		close();
	}

	private synchronized void delete(UserBean model) {
		db().delete(model);
	}

	public synchronized UserBean load() {
		UserBean bean = new UserBean();
		Query query = db().query();
		query.constrain(UserBean.class);

		@SuppressWarnings("rawtypes")
		List results = query.execute();

		@SuppressWarnings("rawtypes")
		Iterator iterator = results.iterator();

		while (iterator.hasNext()) {
			bean = (UserBean) iterator.next();

		}

		close();
		return bean;
	}
}
