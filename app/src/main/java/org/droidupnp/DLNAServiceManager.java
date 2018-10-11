package org.droidupnp;

import android.content.Context;

import org.droidupnp.controller.cling.Factory;
import org.droidupnp.controller.upnp.IUpnpServiceController;

/**
 * User: liuwei
 * Date: 2018-06-21
 * Time: 18:16
 */
public class DLNAServiceManager
{
    private static DLNAServiceManager sDLNAServiceManager;

    public static void initialize(Context context)
    {
        if (sDLNAServiceManager == null)
        {
            sDLNAServiceManager = new DLNAServiceManager(context.getApplicationContext());
        }
    }

    public static DLNAServiceManager getInstance()
    {
        return sDLNAServiceManager;
    }

    private final Factory mFactory;
    private final IUpnpServiceController mUpnpServiceController;

    private DLNAServiceManager(Context context)
    {
        mFactory = new org.droidupnp.controller.cling.Factory();

        mUpnpServiceController = mFactory.createUpnpServiceController(context);
    }

    public Factory getFactory()
    {
        return mFactory;
    }

    public IUpnpServiceController getUpnpServiceController()
    {
        return mUpnpServiceController;
    }
}
