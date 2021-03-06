//
// Flashbang - a framework for creating PlayN games
// Copyright (C) 2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/flashbang-playn

package flashbang.anim.rsrc;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import playn.core.GroupLayer;
import playn.core.Json;
import playn.core.Layer;
import playn.core.PlayN;

import react.RList;

public class EditableMovieGroupLayerConf extends EditableMovieLayerConf
    implements MovieGroupLayerConf
{
    public final RList<EditableMovieLayerConf> children = RList.create();

    public EditableMovieGroupLayerConf (Json.Object obj) {
        super(obj);
        addChildren(obj.getArray("children", Json.Object.class));
    }

    protected void addChildren(Json.TypedArray<Json.Object> kids) {
        for (Json.Object child : kids) {
            if (child.getString("type").equals("Group")) {
                children.add(new EditableMovieGroupLayerConf(child));
            } else {
                children.add(new EditableMovieImageLayerConf(child));
            }
        }
    }

    public EditableMovieGroupLayerConf (String name) {
        this.name.update(name);
    }

    public List<EditableMovieLayerConf> children () { return children; }

    @Override public Layer build (List<String> names, Map<String, GroupLayer> exports,
        Multimap<String, Animatable> animations) {
        GroupLayer layer = PlayN.graphics().createGroupLayer();
        exports.put(name.get(), layer);
        for (EditableMovieLayerConf child : children) {
            layer.add(child.build(names, exports, animations));
        }
        return add(layer, names, animations);
    }

    @Override protected void writeType (Json.Writer writer) {
        writer.value("type", "Group");
        writeChildren(writer);
    }

    protected void writeChildren (Json.Writer writer) {
        writer.array("children");
        for (EditableMovieLayerConf child : children) {
            child.write(writer);
        }
        writer.end();
    }
}
