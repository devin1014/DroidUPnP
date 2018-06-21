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

package org.droidupnp.view;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

import com.android.liuwei.droidupnp.R;

import org.droidupnp.model.upnp.IDeviceDiscoveryObserver;
import org.droidupnp.model.upnp.IUpnpDevice;

public abstract class UpnpDeviceListFragment extends ListFragment implements IDeviceDiscoveryObserver
{
    protected static final String TAG = "UpnpDeviceListFragment";

    private ArrayAdapter<DeviceDisplay> mListAdapter;

    private final boolean mExtendedInformation;

    public UpnpDeviceListFragment()
    {
        this(false);
    }

    public UpnpDeviceListFragment(boolean extendedInformation)
    {
        mExtendedInformation = extendedInformation;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Activity created");
        mListAdapter = new ArrayAdapter<>(getActivity(), R.layout.device_list_item);
        setListAdapter(mListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.sub_device_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreated");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        getListView().setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                showInfoDialog(position);

                return true;
            }
        });
    }

    private void showInfoDialog(int position)
    {
        if (mListAdapter != null && mListAdapter.getItem(position) != null)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            DialogFragment fragment = DeviceInfoDialog.newInstance(mListAdapter.getItem(position));
            fragment.show(transaction, "dialog");
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void addedDevice(IUpnpDevice device)
    {
        Log.v(TAG, "New device detected : " + device.getDisplayString());

        final DeviceDisplay d = new DeviceDisplay(device, mExtendedInformation);

        if (getActivity() != null) // Visible
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        int position = mListAdapter.getPosition(d);
                        if (position >= 0)
                        {
                            // Device already in the mListAdapter, re-set new value at same position
                            mListAdapter.remove(d);
                            mListAdapter.insert(d, position);
                        }
                        else
                        {
                            mListAdapter.add(d);
                        }
                        if (isSelected(d.getDevice()))
                        {
                            position = mListAdapter.getPosition(d);
                            getListView().setItemChecked(position, true);

                            Log.i(TAG, d.toString() + " is selected at position " + position);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void removedDevice(IUpnpDevice device)
    {
        Log.v(TAG, "Device removed : " + device.getFriendlyName());

        final DeviceDisplay d = new DeviceDisplay(device, mExtendedInformation);

        if (getActivity() != null) // Visible
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        // Remove device from mListAdapter
                        mListAdapter.remove(d);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public ArrayAdapter<DeviceDisplay> getListAdapter()
    {
        return mListAdapter;
    }

    /**
     * Filter to know if device is selected
     */
    protected abstract boolean isSelected(IUpnpDevice upnpDevice);

    /**
     * Select a device
     */
    protected abstract void select(IUpnpDevice upnpDevice);

    /**
     * Select a device
     */
    protected abstract void select(IUpnpDevice upnpDevice, boolean force);
}