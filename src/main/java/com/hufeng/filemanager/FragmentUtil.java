package com.hufeng.filemanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by feng on 2014-07-10.
 */
public class FragmentUtil {

    public static Fragment replaceFragment(FragmentManager manager, int container_id, Class<?> fragment_class, String tag, boolean reverse_enabled, Object... args) {
        Fragment fragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            try {
                int len = args == null ? 0: args.length;
                Class[] cAgs = (len == 0)? null : new Class[len];
                for (int i=0; i<len; i++) {
                    cAgs[i] = args[i].getClass();
                }
                Method method = fragment_class.getMethod("newFragment", cAgs);
                if (method != null) {
                    try {
                        fragment = (Fragment)method.invoke(fragment_class, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            transaction.replace(container_id, fragment, tag);
            if (reverse_enabled) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        } else {
            if (fragment.isDetached() ) {
                transaction.attach(fragment);
                transaction.commit();
            }
        }
        return fragment;
    }

}
