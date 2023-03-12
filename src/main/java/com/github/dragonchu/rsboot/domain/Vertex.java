package com.github.dragonchu.rsboot.domain;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private final String id;
    private final List<Tag> tags;

    private Vertex(String id) {
        this.id = id;
        this.tags = new ArrayList<>();
    }

    public static Vertex setId(String id) {
        return new Vertex(id);
    }

    public Vertex addTag(Tag tag) {
        tags.add(tag);
        return this;
    }

    public String getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
