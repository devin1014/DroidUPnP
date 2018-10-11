/**
 * Copyright (C) 2013 Aurélien Chabot <aurelien@chabot.fr>
 * <p>
 * This file is part of DroidUPNP.
 * <p>
 * DroidUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * DroidUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with DroidUPNP.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.droidupnp.controller.cling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.droidupnp.model.cling.UpnpService;
import org.droidupnp.model.cling.BaseUpnpServiceController;
import org.fourthline.cling.model.meta.LocalDevice;

public class ServiceController extends BaseUpnpServiceController
{
    private static final String TAG = "Cling.ServiceController";

    private Activity mActivity = null;
    private final ServiceListener mUpnpServiceListener;

    public ServiceController(Context ctx)
    {
        super();

        mUpnpServiceListener = new ServiceListener(ctx);
    }

    @Override
    public ServiceListener getServiceListener()
    {
        return mUpnpServiceListener;
    }

    @Override
    public void addDevice(LocalDevice localDevice)
    {
        mUpnpServiceListener.getUpnpService().getRegistry().addDevice(localDevice);
    }

    @Override
    public void removeDevice(LocalDevice localDevice)
    {
        mUpnpServiceListener.getUpnpService().getRegistry().removeDevice(localDevice);
    }

    @Override
    public void pause()
    {
        super.pause();
        mActivity.unbindService(mUpnpServiceListener.getServiceConnexion());
        mActivity = null;
    }

    @Override
    public void resume(Activity activity)
    {
        super.resume(activity);
        Log.d(TAG, "Start upnp service");

        mActivity = activity;
        // This will start the UPnP service if it wasn't already started
        activity.bindService(new Intent(activity, UpnpService.class), mUpnpServiceListener.getServiceConnexion(), Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void finalize()
    {
        pause();
    }
}
