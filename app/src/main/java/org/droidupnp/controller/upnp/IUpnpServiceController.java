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

package org.droidupnp.controller.upnp;

import android.app.Activity;

import org.droidupnp.model.upnp.ContentDirectoryDiscovery;
import org.droidupnp.model.upnp.IServiceListener;
import org.droidupnp.model.upnp.IUpnpDevice;
import org.droidupnp.model.upnp.RendererDiscovery;
import org.fourthline.cling.model.meta.LocalDevice;

import java.util.Observer;

public interface IUpnpServiceController
{
    //renderer
    public void setSelectedRenderer(IUpnpDevice renderer);

    public void setSelectedRenderer(IUpnpDevice renderer, boolean force);

    public IUpnpDevice getSelectedRenderer();

    public RendererDiscovery getRendererDiscovery();

    public void addSelectedRendererObserver(Observer o);

    public void delSelectedRendererObserver(Observer o);

    //content
    public void setSelectedContentDirectory(IUpnpDevice contentDirectory);

    public void setSelectedContentDirectory(IUpnpDevice contentDirectory, boolean force);

    public IUpnpDevice getSelectedContentDirectory();

    public ContentDirectoryDiscovery getContentDirectoryDiscovery();

    public void addSelectedContentDirectoryObserver(Observer o);

    public void delSelectedContentDirectoryObserver(Observer o);

    //listener
    public IServiceListener getServiceListener();

    //device
    public void addDevice(LocalDevice localDevice);

    public void removeDevice(LocalDevice localDevice);

    //control
    public void pause();

    public void resume(Activity activity);
}
