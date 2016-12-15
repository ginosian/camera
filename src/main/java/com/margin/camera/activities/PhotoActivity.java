package com.margin.camera.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.margin.camera.R;
import com.margin.camera.fragments.PhotoCaptureFragment;
import com.margin.camera.models.Photo;
import com.margin.camera.models.Property;
import com.margin.camera.utils.Constants;
import com.margin.components.activities.BaseActivity;
import com.margin.components.fragments.BackHandledFragment;
import com.margin.components.utils.AndroidVersionUtils;
import com.margin.components.utils.GATrackerUtils;
import com.margin.components.utils.PermissionUtils;
import com.margin.components.utils.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoActivity extends BaseActivity implements PhotoCaptureFragment
        .OnImageCaptureListener, PhotoCaptureFragment.OnClosePressedListener,
        BackHandledFragment.IBackPressedHandler {

    public static final String PHOTO = "photo";

    private static final int PERMISSIONS_CAMERA = 111;
    private static final int PERMISSIONS_STORAGE = 222;

    private BackHandledFragment mSelectedFragment;
    /**
     * If we start requesting permissions, this variable will be True (used when going to the
     * app settings from the SnackBar).
     */
    private boolean isRequestingPermission;

    private int mEntityId;
    private String mImageDirPath;
    private List<Property> mProperties;

    private View.OnClickListener mOpenSystemSettings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    };
    private Snackbar mSnackbar;

    private long mCreatingTimestamp;

    /**
     * Start PhotoActivity from an {@link Activity}.
     *
     * @param activity     The {@link Activity} that is opening the photo widget.
     * @param requestCode  If >= 0, this code will be returned in onActivityResult() when
     *                     the activity exits.
     * @param entityId     The shipment's unique entity ID.
     * @param imageDirPath The file path where the image will be saved.
     */
    public static void startActivityForResult(Activity activity, int requestCode,
                                              int entityId, String imageDirPath) {
        startActivityForResult(activity, requestCode, entityId, imageDirPath, null);
    }

    /**
     * Start PhotoActivity from a {@link Fragment}.
     *
     * @param fragment     The {@link Fragment} that is opening the photo widget.
     * @param requestCode  The integer request code originally supplied to
     *                     startActivityForResult(), allowing you to identify who this
     *                     result came from.
     * @param entityId     The shipment's unique entity ID.
     * @param imageDirPath The file path where the image will be saved.
     */
    public static void startActivityForResult(Fragment fragment, int requestCode,
                                              int entityId, String imageDirPath) {
        startActivityForResult(fragment, requestCode, entityId, imageDirPath, null);
    }

    /**
     * Start PhotoActivity from an {@link Activity}.
     *
     * @param activity     The {@link Activity} that is opening the photo widget.
     * @param requestCode  If >= 0, this code will be returned in onActivityResult() when
     *                     the activity exits.
     * @param entityId     The shipment's unique entity ID.
     * @param imageDirPath The file path where the image will be saved.
     * @param properties   Image/shipment properties that will be shown with the taken image.
     */
    public static void startActivityForResult(Activity activity, int requestCode,
                                              int entityId, String imageDirPath,
                                              Map<String, String> properties) {
        Intent intent = createPhotoIntent(activity, PhotoActivity.class, entityId,
                imageDirPath, properties);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Start PhotoActivity from a {@link Fragment}.
     *
     * @param fragment     The {@link Fragment} that is opening the photo widget.
     * @param requestCode  The integer request code originally supplied to
     *                     startActivityForResult(), allowing you to identify who this
     *                     result came from.
     * @param entityId     The shipment's unique entity ID.
     * @param imageDirPath The file path where the image will be saved.
     * @param properties   Image/shipment properties that will be shown with the taken image.
     */
    public static void startActivityForResult(Fragment fragment, int requestCode,
                                              int entityId, String imageDirPath,
                                              Map<String, String> properties) {
        Intent intent = createPhotoIntent(fragment.getContext(), PhotoActivity.class, entityId,
                imageDirPath, properties);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Create a photo intent with extra parameters.
     */
    protected static Intent createPhotoIntent(Context context, Class<?> activityClass,
                                              int entityId, String imageDirPath,
                                              Map<String, String> properties) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(PhotoCaptureFragment.ENTITY_ID, entityId);
        intent.putExtra(PhotoCaptureFragment.IMAGE_DIR_PATH, imageDirPath);
        if (properties != null) {
            ArrayList<Property> listProperties = new ArrayList<>(properties.size());
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                listProperties.add(new Property(entry.getKey(), entry.getValue()));
            }
            intent.putParcelableArrayListExtra(PhotoCaptureFragment.PROPERTIES, listProperties);
        }
        return intent;
    }

    @Override
    protected void onLocationPermissionDeclined() {
        checkPermissionsAndShowCamera();
    }

    @Override
    protected void onLocationPermissionGranted() {
        checkPermissionsAndShowCamera();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreatingTimestamp = System.currentTimeMillis();
        setContentView(R.layout.activity_photo);
        mImageDirPath = getIntent().getStringExtra(PhotoCaptureFragment.IMAGE_DIR_PATH);
        if (null == mImageDirPath) {
            Toast.makeText(this, "Directory path could not be null", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
        mEntityId = getIntent().getIntExtra(PhotoCaptureFragment.ENTITY_ID, -1);
        if (getIntent().hasExtra(PhotoCaptureFragment.PROPERTIES)) {
            mProperties = getIntent().getParcelableArrayListExtra(PhotoCaptureFragment.PROPERTIES);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequestingPermission) {
            // if we returned from requesting permission then just do nothing,
            // because the permission dialog has been dismissed, and
            // we will make all the stuff in onRequestPermissionsResult()
            isRequestingPermission = false;
            if (AndroidVersionUtils.isHigherEqualToMarshmallow()) {
                if (!PermissionUtils.isPermissionGranted(this,
                        Manifest.permission.CAMERA) ||
                        !PermissionUtils.isPermissionGranted(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionSnackbar();
                }
            }
        } else {
            if (AndroidVersionUtils.isHigherEqualToMarshmallow()) {
                if (!PermissionUtils.isPermissionGranted(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ||
                        !PermissionUtils.isPermissionGranted(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // we haven't received location permissions yet
                    // so now we should see the location permission dialog,
                    // and we will do camera checking in onRequestPermissionsResult()
                    return;
                }
            }
            // we can proceed to camera checking
            checkPermissionsAndShowCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissPermissionSnackbar();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (null != fragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Send statistics about image cancelling on Google Analytics
     */
    protected void trackImageCancelling(String category) {
        GATrackerUtils.trackEvent(this, category, Constants.EventAction.CANCEL, null, 0);
    }

    /**
     * Send statistics about how long it takes from opening the activity
     * until saving the image on Google Analytics
     */
    protected void trackImageCreating(String category) {
        GATrackerUtils.trackTimedEvent(this, category,
                System.currentTimeMillis() - mCreatingTimestamp,
                Constants.EventName.CREATING_PHOTO, null);
    }

    @Override
    public void onClosePressed() {
        trackImageCancelling(Constants.EventCategory.CAMERA_WIDGET);
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onImageCaptured(Photo photo) {
        photo.setLocation(getLastKnownLocation());
        //pass photo object back to the caller
        Intent returnIntent = new Intent();
        returnIntent.putExtra(PHOTO, photo);
        setResult(RESULT_OK, returnIntent);
        trackImageCreating(Constants.EventCategory.CAMERA_WIDGET);
        finish();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        mSelectedFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        if (mSelectedFragment == null || !mSelectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    /**
     * Show {@link PhotoCaptureFragment} with the given arguments.
     *
     * @param entityId     photo entity ID
     * @param imageDirPath the path where the image will be stored
     * @param properties   image properties, see {@link Property} (optional)
     */
    protected void showPhotoFragment(final int entityId, final String imageDirPath,
                                     @Nullable final List<Property> properties) {
        // this transaction can be invoked during the onRequestPermissionsResult()
        // method work. In that case we can catch "java.lang.IllegalStateException:
        // Can not perform this action after onSaveInstanceState". To fix that we use
        // handler post() method instead of commitAllowingStateLoss() for better
        // user experience
        if (getSupportFragmentManager()
                .findFragmentByTag(PhotoCaptureFragment.TAG) == null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            PhotoCaptureFragment.newInstance(entityId, imageDirPath, properties),
                            PhotoCaptureFragment.TAG).addToBackStack(PhotoCaptureFragment.TAG).commit();
                }
            });
        }
    }

    /**
     * Hide photo fragment with TAG.
     * Covers corner case: if user manually revoked permissions from the settings, when
     * {@link PhotoCaptureFragment} was showing. We will just remove the {@link Fragment} and ask
     * for the permissions again.
     */
    protected void hidePhotoFragment(final String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager
                            .POP_BACK_STACK_INCLUSIVE);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            // we need both of this permissions to show camera
            case PERMISSIONS_CAMERA:
            case PERMISSIONS_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionsAndShowCamera();
                }
                // it doesn't matter we checked for permissions or
                // showed snackBar, we set it true
                isRequestingPermission = true;
                break;
            default:
                // let BaseActivity check location permissions result
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * It's necessary to get two permissions for Android 6.0+: using camera and
     * writing on an external storage (to save pictures).
     */
    private void checkPermissionsAndShowCamera() {
        if (AndroidVersionUtils.isHigherEqualToMarshmallow()) {
            if (!PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)) {
                hidePhotoFragment(PhotoCaptureFragment.TAG);
                dismissPermissionSnackbar();
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CAMERA);
                isRequestingPermission = true;
                return;
            }
            if (!PermissionUtils.isPermissionGranted(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hidePhotoFragment(PhotoCaptureFragment.TAG);
                dismissPermissionSnackbar();
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_STORAGE);
                isRequestingPermission = true;
                return;
            }
        }
        showPhotoFragment(mEntityId, mImageDirPath, mProperties);
    }

    /**
     * In case that the user denied permission, a {@link Snackbar} will be shown with asking
     * to go to the settings and allow app permissions.
     */
    private void showPermissionSnackbar() {
        mSnackbar = SnackbarUtils.showSnackbar(this, R.string.message_no_permissions_snackbar,
                R.string.settings, Snackbar.LENGTH_INDEFINITE, mOpenSystemSettings);
    }

    /**
     * Dismiss the permission {@link Snackbar} if it is currently being showed.
     */
    private void dismissPermissionSnackbar() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }
}
