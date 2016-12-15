package com.margin.camera.utils;

/**
 * Created on Apr 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Constants {

    /**
     * Provide event categories for google analytics
     */
    public class EventCategory {
        public static final String CAMERA_WIDGET = "Camera Widget";
        public static final String ANNOTATION_CAMERA_WIDGET = "Annotation Camera Widget";
    }

    /**
     * Provide event actions for google analytics
     */
    public class EventAction {
        public static final String CANCEL = "Cancel";
        public static final String DISCARD = "Discard";
        public static final String IMAGE_PROPERTIES = "Image Properties";
        public static final String COMMENT_EDITED = "Comment Edited";
        public static final String VISIBILITY_TOGGLED = "Visibility Toggled";
        public static final String ANNOTATION_ADDING_SHORTCUT = "Annotation Adding Shortcut";
        public static final String ANNOTATION_DRAG_DELETE = "Annotation Drag Delete";
        public static final String ANNOTATION_MANUAL_DELETE = "Annotation Manual Delete";
        public static final String ANNOTATION_LOCATION_ADDED = "Annotation Location Added";
        public static final String ANNOTATION_LOCATION_REMOVED = "Annotation Location Removed";
        public static final String ANNOTATION_SAVED = "Annotations Saved";
    }

    /**
     * Provide event values for google analytics
     */
    public class EventValue {
        public static final long ANNOTATION_HIDDEN = 0;
        public static final long ANNOTATION_SHOWN = 1;
    }

    /**
     * Provide event names for google analytics
     */
    public class EventName {
        public static final String CREATING_PHOTO = "Creating Photo";
    }
}
