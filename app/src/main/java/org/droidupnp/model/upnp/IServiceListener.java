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

import android.content.ServiceConnection;

import java.util.Collection;

public interface IServiceListener
{
    //listener
    public void addListener(IRegistryListener registryListener);

    public void removeListener(IRegistryListener registryListener);

    public void clearListener();

    //device
    public Collection<IUpnpDevice> getDeviceList();

    public Collection<IUpnpDevice> getFilteredDeviceList(ICallableFilter filter);

    //service connexion
    public ServiceConnection getServiceConnexion();

    public void refresh();
}
