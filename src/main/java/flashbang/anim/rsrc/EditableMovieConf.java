//
// Flashbang - a framework for creating PlayN games
// Copyright (C) 2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/flashbang-playn

package flashbang.anim.rsrc;

import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import playn.core.GroupLayer;
import playn.core.Json;
import playn.core.PlayN;

import react.RList;
import react.UnitSignal;
import react.Value;

import flashbang.anim.Movie;

public class EditableMovieConf implements MovieConf
{
    public final UnitSignal treeChanged = new UnitSignal();

    public final EditableMovieGroupLayerConf root = new EditableMovieGroupLayerConf("root");

    // TODO - listen for add and remove to update children
    public final RList<String> animations = RList.create(Lists.newArrayList(DEFAULT_ANIMATION));

    public final Value<String> animation = Value.create(DEFAULT_ANIMATION);

    public EditableMovieConf () {
        root.children.connect(_treeListener);
    }

    public EditableMovieConf (Json.TypedArray<Json.Object> children) {
        this();
        root.addChildren(children);
        // Initialize our list of animations
        if (!root.children.isEmpty()) {
            animations.clear();
            animations.addAll(root.children.get(0).animations.keySet());
        }
    }

    public void add (EditableMovieGroupLayerConf parent, EditableMovieLayerConf child) {
        add(parent, child, -1);
    }

    public void add (EditableMovieGroupLayerConf parent, EditableMovieLayerConf child, int idx) {
        for (String name : animations) {
            if (child.animations.containsKey(name)) continue;
            child.animations.put(name, new EditableAnimConf());
        }
        if (idx == -1) idx = parent.children.size();
        parent.children.add(idx, child);
    }

    public EditableMovieGroupLayerConf findParent (EditableMovieLayerConf layer) {
        return findParent(layer, root);
    }

    protected EditableMovieGroupLayerConf findParent (EditableMovieLayerConf layer,
        EditableMovieGroupLayerConf position) {
        if (position.children.contains(layer)) return position;
        for (EditableMovieLayerConf child : position.children) {
            if (!(child instanceof EditableMovieGroupLayerConf)) continue;
            EditableMovieGroupLayerConf found =
                findParent(layer, ((EditableMovieGroupLayerConf)child));
            if (found != null) return found;
        }
        return null;
    }

    @Override public Movie build () {
        GroupLayer root = PlayN.graphics().createGroupLayer();
        Multimap<String, Animatable> animations = ArrayListMultimap.create();
        Map<String, GroupLayer> exports = Maps.newHashMap();
        for (MovieLayerConf child : this.root.children) {
            root.add(child.build(this.animations, exports, animations));
        }
        return new Movie(root, exports, animations);
    }

    public String write () {
        Json.Writer writer = PlayN.json().newWriter().object();
        write(writer);
        return writer.end().write();
    }

    public void write (Json.Writer writer) {
       root.writeChildren(writer);
    }

    protected final RList.Listener<EditableMovieLayerConf> _treeListener =
        new RList.Listener<EditableMovieLayerConf> () {
            @Override public void onAdd (EditableMovieLayerConf added) {
                if (added instanceof EditableMovieGroupLayerConf) {
                    ((EditableMovieGroupLayerConf)added).children.connect(this);
                }
                treeChanged.emit();
            }

            @Override public void onRemove (EditableMovieLayerConf removed) {
                if (removed instanceof EditableMovieGroupLayerConf) {
                    ((EditableMovieGroupLayerConf)removed).children.disconnect(this);
                }
                treeChanged.emit();
            }
        };
}
