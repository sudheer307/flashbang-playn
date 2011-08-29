//
// Flashbang - a framework for creating PlayN games
// Copyright (C) 2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/flashbang-playn

package com.threerings.flashbang.rsrc;

import java.util.Map;

import playn.core.Image;
import playn.core.Json;
import playn.core.Sound;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.threerings.flashbang.util.Loadable;

public class ResourceManager
{
    public Resource getResource (String path)
    {
        return _resources.get(path);
    }

    public Sound getSound (String path)
    {
        Resource rsrc = getResource(path);
        if (rsrc == null) {
            return null;
        }
        Preconditions.checkState(rsrc instanceof SoundResource);
        return ((SoundResource) rsrc).get();
    }

    public Image getImage (String path)
    {
        Resource rsrc = getResource(path);
        if (rsrc == null) {
            return null;
        }
        Preconditions.checkState(rsrc instanceof ImageResource);
        return ((ImageResource) rsrc).get();
    }

    public Json.Object getJson (String path)
    {
        Resource rsrc = getResource(path);
        if (rsrc == null) {
            return null;
        }
        Preconditions.checkState(rsrc instanceof JsonResource);
        return ((JsonResource) rsrc).get();
    }

    public String getText (String path)
    {
        Resource rsrc = getResource(path);
        if (rsrc == null) {
            return null;
        }
        Preconditions.checkState(rsrc instanceof TextResource);
        return ((TextResource) rsrc).get();
    }

    public boolean isLoaded (String path)
    {
        return (getResource(path) != null);
    }

    public void add (Resource rsrc, String group)
    {
        Preconditions.checkState(rsrc.state() == Loadable.State.LOADED, "Resource must be loaded");
        rsrc._group = group;
        Resource old = _resources.put(rsrc.path, rsrc);
        Preconditions.checkState(old == null,
            "A resource with that name already exists [name=%s]", rsrc.path);
    }

    public void add (Iterable<Resource> rsrcs, String group)
    {
        for (Resource rsrc : rsrcs) {
            add(rsrc, group);
        }
    }

    /**
     * Unloads all Resources that belong to the specified group
     */
    public void unload (String group)
    {
        for (Resource rsrc : Lists.newArrayList(_resources.values())) {
            if (rsrc.group().equals(group)) {
                _resources.remove(rsrc.path);
            }
        }
    }

    /**
     * Unloads all Resources
     */
    public void unloadAll ()
    {
        _resources.clear();
    }

    protected Map<String, Resource> _resources = Maps.newHashMap();
}
