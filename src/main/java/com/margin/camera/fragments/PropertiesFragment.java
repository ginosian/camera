package com.margin.camera.fragments;

import com.margin.camera.R;
import com.margin.camera.models.Property;
import com.margin.components.fragments.ListFragment;

import java.util.List;

/**
 * Created on Feb 26, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PropertiesFragment extends ListFragment<Property> {

    private static final String TAG = PropertiesFragment.class.getSimpleName();

    /**
     * Create a new instance of PropertiesFragment, initialized to
     * show properties with 'properties'.
     */
    public static ListFragment<Property> create(List<Property> properties) {
        return newInstance(properties, R.layout.property_fragment_list_item);
    }
}
