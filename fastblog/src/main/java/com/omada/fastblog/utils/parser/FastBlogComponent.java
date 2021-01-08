package com.omada.fastblog.utils.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface FastBlogComponent {

    enum ComponentType {

        FAST_BLOG_TITLE("title"),
        FAST_BLOG_AUTHOR("author"),
        FAST_BLOG_TEXT("text"),
        FAST_BLOG_IMAGE("image"),
        FAST_BLOG_IMAGE_LAYOUT("imageLayout"),
        FAST_BLOG_IMAGE_SLIDESHOW("imageSlideshow"),
        FAST_BLOG_QUOTE("quote");

        private final String name;

        ComponentType(String s) {
            name = s;
        }

        public boolean equalsComponent(ComponentType otherComponent) {

            if(otherComponent == null){
                return false;
            }
            return name.equals(otherComponent.toString());
        }

        @NotNull
        public String toString() {
            return this.name;
        }
    }

    ComponentType getComponentType();

    // convert all required contents to a map from the component
    Map<String, Object> onSerialize();

    // use serialized data to render
    void onParse(Map<String, Object> data);
}
