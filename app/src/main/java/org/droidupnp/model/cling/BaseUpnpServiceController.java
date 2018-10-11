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

import android.app.Activity;
import android.util.Log;

import org.droidupnp.controller.upnp.IUpnpServiceController;
import org.droidupnp.model.CObservable;
import org.droidupnp.model.upnp.ContentDirectoryDiscovery;
import org.droidupnp.model.upnp.IUpnpDevice;
import org.droidupnp.model.upnp.RendererDiscovery;

import java.util.Observer;

public abstract class BaseUpnpServiceController implements IUpnpServiceController
{
    private static final String TAG = "UpnpServiceController";

    //renderer
    protected IUpnpDevice mSelectedRenderer;
    protected CObservable mRendererObservable;
    private final RendererDiscovery mRendererDiscovery;

    //content
    protected IUpnpDevice mSelectedContentDirectory;
    protected CObservable mContentDirectoryObservable;
    private final ContentDirectoryDiscovery mContentDirectoryDiscovery;

    protected BaseUpnpServiceController()
    {
        mRendererObservable = new CObservable();
        mContentDirectoryObservable = new CObservable();

        mContentDirectoryDiscovery = new ContentDirectoryDiscovery(getServiceListener());
        mRendererDiscovery = new RendererDiscovery(getServiceListener());
    }

    // ------------------------------------------------------------------------------
    // ---- renderer
    // ------------------------------------------------------------------------------
    @Override
    public void setSelectedRenderer(IUpnpDevice renderer)
    {
        setSelectedRenderer(renderer, false);
    }

    @Override
    public void setSelectedRenderer(IUpnpDevice renderer, boolean force)
    {
        // Skip if no change and no forced
        if (!force && renderer != null && mSelectedRenderer != null && mSelectedRenderer.equals(renderer))
        {
            return;
        }

        mSelectedRenderer = renderer;
        mRendererObservable.notifyAllObservers();
    }

    @Override
    public IUpnpDevice getSelectedRenderer()
    {
        return mSelectedRenderer;
    }

    @Override
    public RendererDiscovery getRendererDiscovery()
    {
        return mRendererDiscovery;
    }

    @Override
    public void addSelectedRendererObserver(Observer o)
    {
        Log.i(TAG, "New SelectedRendererObserver");
        mRendererObservable.addObserver(o);
    }

    @Override
    public void delSelectedRendererObserver(Observer o)
    {
        mRendererObservable.deleteObserver(o);
    }

    // ------------------------------------------------------------------------------
    // ---- content
    // ------------------------------------------------------------------------------
    @Override
    public void setSelectedContentDirectory(IUpnpDevice contentDirectory)
    {
        setSelectedContentDirectory(contentDirectory, false);
    }

    @Override
    public void setSelectedContentDirectory(IUpnpDevice contentDirectory, boolean force)
    {
        // Skip if no change and no force
        if (!force && contentDirectory != null && mSelectedContentDirectory != null && mSelectedContentDirectory.equals(contentDirectory))
        {
            return;
        }

        mSelectedContentDirectory = contentDirectory;
        mContentDirectoryObservable.notifyAllObservers();
    }

    @Override
    public IUpnpDevice getSelectedContentDirectory()
    {
        return mSelectedContentDirectory;
    }

    @Override
    public ContentDirectoryDiscovery getContentDirectoryDiscovery()
    {
        return mContentDirectoryDiscovery;
    }


    @Override
    public void addSelectedContentDirectoryObserver(Observer o)
    {
        mContentDirectoryObservable.addObserver(o);
    }

    @Override
    public void delSelectedContentDirectoryObserver(Observer o)
    {
        mContentDirectoryObservable.deleteObserver(o);
    }

    // ------------------------------------------------------------------------------
    // ---- control
    // ------------------------------------------------------------------------------
    @Override
    public void pause()
    {
        mRendererDiscovery.pause(getServiceListener());
        mContentDirectoryDiscovery.pause(getServiceListener());
    }

    @Override
    public void resume(Activity activity)
    {
        mRendererDiscovery.resume(getServiceListener());
        mContentDirectoryDiscovery.resume(getServiceListener());
    }

}