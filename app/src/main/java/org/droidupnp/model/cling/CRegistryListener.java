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

package org.droidupnp.model.cling;

import org.droidupnp.model.upnp.IRegistryListener;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class CRegistryListener extends DefaultRegistryListener
{
    private final IRegistryListener mRegistryListener;

    public CRegistryListener(IRegistryListener registryListener)
    {
        mRegistryListener = registryListener;
    }

    /* Discovery performance optimization for very slow Android devices! */
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device)
    {
        mRegistryListener.deviceAdded(new CDevice(device));
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex)
    {
        mRegistryListener.deviceRemoved(new CDevice(device));
    }

    /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device)
    {
        mRegistryListener.deviceAdded(new CDevice(device));
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device)
    {
        mRegistryListener.deviceRemoved(new CDevice(device));
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device)
    {
        mRegistryListener.deviceAdded(new CDevice(device));
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device)
    {
        mRegistryListener.deviceRemoved(new CDevice(device));
    }
}
