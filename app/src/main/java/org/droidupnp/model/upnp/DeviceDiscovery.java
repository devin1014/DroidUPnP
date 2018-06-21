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

package org.droidupnp.model.upnp;

import android.util.Log;

import org.droidupnp.MainActivity;

import java.util.ArrayList;
import java.util.Collection;

public abstract class DeviceDiscovery
{
    protected static final String TAG = "DeviceDiscovery";

    private final BrowsingRegistryListener browsingRegistryListener;

    protected boolean extendedInformation;

    private final ArrayList<IDeviceDiscoveryObserver> observerList;

    public DeviceDiscovery(IServiceListener serviceListener)
    {
        this(serviceListener, false);
    }

    public DeviceDiscovery(IServiceListener serviceListener, boolean extendedInformation)
    {
        browsingRegistryListener = new BrowsingRegistryListener();
        this.extendedInformation = extendedInformation;
        observerList = new ArrayList<>();
    }

    public void resume(IServiceListener serviceListener)
    {
        serviceListener.addListener(browsingRegistryListener);
    }

    public void pause(IServiceListener serviceListener)
    {
        serviceListener.removeListener(browsingRegistryListener);
    }

    // ------------------------------------------------------------------------
    // ---- IRegistryListener imp
    // ------------------------------------------------------------------------
    public class BrowsingRegistryListener implements IRegistryListener
    {
        @Override
        public void deviceAdded(final IUpnpDevice device)
        {
            Log.v(TAG, "New device detected : " + device.getDisplayString());

            if (device.isFullyHydrated() && filter(device))
            {
                if (isSelected(device))
                {
                    Log.i(TAG, "Reselect device to refresh it");
                    select(device, true);
                }

                notifyAdded(device);
            }
        }

        @Override
        public void deviceRemoved(final IUpnpDevice device)
        {
            Log.v(TAG, "Device removed : " + device.getFriendlyName());

            if (filter(device))
            {
                if (isSelected(device))
                {
                    Log.i(TAG, "Selected device have been removed");
                    removed(device);
                }

                notifyRemoved(device);
            }
        }
    }

    public void addObserver(IDeviceDiscoveryObserver o)
    {
        observerList.add(o);

        final Collection<IUpnpDevice> upnpDevices = MainActivity.upnpServiceController.getServiceListener().getFilteredDeviceList(getCallableFilter());

        for (IUpnpDevice d : upnpDevices)
        {
            o.addedDevice(d);
        }
    }

    public void removeObserver(IDeviceDiscoveryObserver o)
    {
        observerList.remove(o);
    }

    public void notifyAdded(IUpnpDevice device)
    {
        for (IDeviceDiscoveryObserver o : observerList)
        {
            o.addedDevice(device);
        }
    }

    public void notifyRemoved(IUpnpDevice device)
    {
        for (IDeviceDiscoveryObserver o : observerList)
        {
            o.removedDevice(device);
        }
    }

    /**
     * Filter device you want to add to this device list fragment
     *
     * @param device the device to test
     * @return add it or not
     */
    protected boolean filter(IUpnpDevice device)
    {
        ICallableFilter filter = getCallableFilter();
        filter.setDevice(device);
        try
        {
            return filter.call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a callable device filter
     */
    protected abstract ICallableFilter getCallableFilter();

    /**
     * Filter to know if device is selected
     */
    protected abstract boolean isSelected(IUpnpDevice d);

    /**
     * Select a device
     */
    protected abstract void select(IUpnpDevice device);

    /**
     * Select a device
     */
    protected abstract void select(IUpnpDevice device, boolean force);

    /**
     * Callback when device removed
     */
    protected abstract void removed(IUpnpDevice d);
}
