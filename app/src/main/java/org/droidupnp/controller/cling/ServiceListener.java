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

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.droidupnp.MainActivity;
import org.droidupnp.model.cling.CDevice;
import org.droidupnp.model.cling.CRegistryListener;
import org.droidupnp.model.mediaserver.MediaServer;
import org.droidupnp.model.upnp.ICallableFilter;
import org.droidupnp.model.upnp.IRegistryListener;
import org.droidupnp.model.upnp.IServiceListener;
import org.droidupnp.model.upnp.IUpnpDevice;
import org.droidupnp.view.SettingsActivity;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

public class ServiceListener implements IServiceListener
{
    private static final String TAG = "Cling.ServiceListener";

    private AndroidUpnpService mAndroidUpnpService;
    private ArrayList<IRegistryListener> mWaitingListener;

    private Context mContext;
    private MediaServer mMediaServer = null;

    ServiceListener(Context ctx)
    {
        mContext = ctx;
        mWaitingListener = new ArrayList<>();
    }

    // ------------------------------------------------------------------------------
    // ---- listener
    // ------------------------------------------------------------------------------
    @Override
    public void addListener(IRegistryListener registryListener)
    {
        Log.d(TAG, "addListener:" + registryListener);

        if (mAndroidUpnpService != null)
        {
            addListenerSafe(registryListener);
        }
        else
        {
            mWaitingListener.add(registryListener);
        }
    }

    private void addListenerSafe(IRegistryListener registryListener)
    {
        Log.d(TAG, "Add Listener Safe !");

        // Get ready for future device advertisements
        mAndroidUpnpService.getRegistry().addListener(new CRegistryListener(registryListener));

        // Now add all devices to the list we already know about
        for (Device device : mAndroidUpnpService.getRegistry().getDevices())
        {
            registryListener.deviceAdded(new CDevice(device));
        }
    }

    @Override
    public void removeListener(IRegistryListener registryListener)
    {
        Log.d(TAG, "remove listener");

        if (mAndroidUpnpService != null)
        {
            removeListenerSafe(registryListener);
        }
        else
        {
            mWaitingListener.remove(registryListener);
        }
    }

    private void removeListenerSafe(IRegistryListener registryListener)
    {
        Log.d(TAG, "remove listener Safe");

        mAndroidUpnpService.getRegistry().removeListener(new CRegistryListener(registryListener));
    }

    @Override
    public void clearListener()
    {
        mWaitingListener.clear();
    }

    // ------------------------------------------------------------------------------
    // ---- device
    // ------------------------------------------------------------------------------
    @Override
    public Collection<IUpnpDevice> getDeviceList()
    {
        ArrayList<IUpnpDevice> deviceList = new ArrayList<>();

        if (mAndroidUpnpService != null && mAndroidUpnpService.getRegistry() != null)
        {
            for (Device device : mAndroidUpnpService.getRegistry().getDevices())
            {
                deviceList.add(new CDevice(device));
            }
        }

        return deviceList;
    }

    @Override
    public Collection<IUpnpDevice> getFilteredDeviceList(ICallableFilter filter)
    {
        ArrayList<IUpnpDevice> deviceList = new ArrayList<>();

        try
        {
            if (mAndroidUpnpService != null && mAndroidUpnpService.getRegistry() != null)
            {
                for (Device device : mAndroidUpnpService.getRegistry().getDevices())
                {
                    IUpnpDevice upnpDevice = new CDevice(device);

                    filter.setDevice(upnpDevice);

                    if (filter.call())
                    {
                        deviceList.add(upnpDevice);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return deviceList;
    }

    @Override
    public ServiceConnection getServiceConnexion()
    {
        return mServiceConnection;
    }

    public AndroidUpnpService getUpnpService()
    {
        return mAndroidUpnpService;
    }

    @Override
    public void refresh()
    {
        if (mAndroidUpnpService != null)
        {
            mAndroidUpnpService.getControlPoint().search();
        }
    }

    // ------------------------------------------------------------------------------
    // ---- ServiceConnection
    // ------------------------------------------------------------------------------
    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.i(TAG, "ServiceConnection onServiceConnected");

            mAndroidUpnpService = (AndroidUpnpService) service;

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            if (sharedPref.getBoolean(SettingsActivity.CONTENTDIRECTORY_SERVICE, true))
            {
                try
                {
                    // Local content directory
                    if (mMediaServer == null)
                    {
                        mMediaServer = new MediaServer(MainActivity.getLocalIpAddress(mContext), mContext);
                        mMediaServer.start();
                    }
                    else
                    {
                        mMediaServer.restart();
                    }
                    mAndroidUpnpService.getRegistry().addDevice(mMediaServer.getDevice());
                }
                catch (UnknownHostException e1)
                {
                    Log.e(TAG, "Creating demo device failed");
                    Log.e(TAG, "exception", e1);
                }
                catch (ValidationException e2)
                {
                    Log.e(TAG, "Creating demo device failed");
                    Log.e(TAG, "exception", e2);
                }
                catch (IOException e3)
                {
                    Log.e(TAG, "Starting http server failed");
                    Log.e(TAG, "exception", e3);
                }
            }
            else if (mMediaServer != null)
            {
                mMediaServer.stop();
                mMediaServer = null;
            }

            for (IRegistryListener registryListener : mWaitingListener)
            {
                addListenerSafe(registryListener);
            }

            // Search asynchronously for all devices, they will respond soon
            mAndroidUpnpService.getControlPoint().search();
        }

        @Override
        public void onServiceDisconnected(ComponentName className)
        {
            Log.i(TAG, "Service disconnected");
            mAndroidUpnpService = null;
        }
    };
}
