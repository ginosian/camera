package com.margin.camera.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.margin.camera.R;
import com.margin.camera.fragments.AnnotationPhotoCaptureFragment;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Property;
import com.margin.camera.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created on Mar 21, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoActivity extends PhotoActivity {

    private List<AnnotationType> mAnnotationTypes;

    /**
     * Start AnnotationPhotoActivity from another activity with given arguments
     */
    public static void startActivityForResult(Activity activity, int requestCode,
                                              int entityId, String imageDirPath,
                                              Map<String, String> properties,
                                              Collection<AnnotationType> types) {
        Intent intent = createPhotoIntent(activity, AnnotationPhotoActivity.class,
                entityId, imageDirPath, properties);
        intent.putParcelableArrayListExtra(AnnotationPhotoCaptureFragment.LIST_OF_ANNOTATION_TYPES,
                new ArrayList<>(types));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Start AnnotationPhotoActivity from fragment with given arguments
     */
    public static void startActivityForResult(Fragment fragment, int requestCode,
                                              int entityId, String imageDirPath,
                                              Map<String, String> properties,
                                              Collection<AnnotationType> types) {
        Intent intent = createPhotoIntent(fragment.getContext(), AnnotationPhotoActivity.class,
                entityId, imageDirPath, properties);
        intent.putParcelableArrayListExtra(AnnotationPhotoCaptureFragment.LIST_OF_ANNOTATION_TYPES,
                new ArrayList<>(types));
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnnotationTypes = getIntent().getParcelableArrayListExtra(
                AnnotationPhotoCaptureFragment.LIST_OF_ANNOTATION_TYPES);
    }

    @Override
    protected void showPhotoFragment(final int entityId, final String imageDirPath,
                                     final List<Property> properties) {
        if (getSupportFragmentManager()
                .findFragmentByTag(AnnotationPhotoCaptureFragment.TAG) == null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            AnnotationPhotoCaptureFragment.newInstance(entityId, imageDirPath,
                                    properties, mAnnotationTypes), AnnotationPhotoCaptureFragment.TAG)
                            .addToBackStack(AnnotationPhotoCaptureFragment.TAG).commit();
                }
            });
        }
    }

    @Override
    protected void hidePhotoFragment(String /*ignored*/tag) {
        super.hidePhotoFragment(AnnotationPhotoCaptureFragment.TAG);
    }

    @Override
    protected void trackImageCancelling(String /*ignored*/category) {
        super.trackImageCancelling(Constants.EventCategory.ANNOTATION_CAMERA_WIDGET);
    }

    @Override
    protected void trackImageCreating(String /*ignored*/category) {
        super.trackImageCreating(Constants.EventCategory.ANNOTATION_CAMERA_WIDGET);
    }
}
