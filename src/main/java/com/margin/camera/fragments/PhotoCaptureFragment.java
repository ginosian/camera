package com.margin.camera.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.margin.camera.R;
import com.margin.camera.listeners.OnImageDeletedListener;
import com.margin.camera.listeners.OnImageSavedListener;
import com.margin.camera.managers.PhotoCaptureManager;
import com.margin.camera.misc.LatestImageAsyncTask;
import com.margin.camera.models.Photo;
import com.margin.camera.models.Property;
import com.margin.camera.presenters.IPhotoCapturePresenter;
import com.margin.camera.utils.Constants;
import com.margin.camera.utils.DialogUtils;
import com.margin.camera.views.IPhotoCaptureView;
import com.margin.components.fragments.BackHandledFragment;
import com.margin.components.fragments.ListFragment;
import com.margin.components.models.IListItem;
import com.margin.components.utils.AnimationUtils;
import com.margin.components.utils.CameraUtils;
import com.margin.components.utils.GATrackerUtils;
import com.margin.components.utils.IntentUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureFragment extends BackHandledFragment implements IPhotoCaptureView,
        IPhotoCapturePresenter, OnImageSavedListener, OnImageDeletedListener,
        ListFragment.OnItemClickListener<IListItem>, TextureView.SurfaceTextureListener,
        Camera.PictureCallback {

    public static final String TAG = PhotoCaptureFragment.class.getSimpleName();

    public static final String ENTITY_ID = "entity_id";
    public static final String IMAGE_DIR_PATH = "image_dir_path";
    public static final String PROPERTIES = "properties";
    public static final int REQUEST_CODE_GALLERY = 8574;
    protected static final float FULL_ALPHA = 1.0f;
    protected static final float TRANSPARENT_40 = 0.4f;
    protected static final float TRANSPARENT_60 = 0.6f;
    protected static final float FULL_TRANSPARENT = 0.0f;
    private static final int FADE_DURATION = 300;
    protected Photo mPhoto = new Photo();
    protected View mInfoButton;
    protected ViewGroup mCameraLayout;
    protected ViewGroup mButtonsLayout;
    protected View mGalleryIconLayout;
    protected RoundedImageView mGalleryIconImageView;
    private OnImageCaptureListener mImageCaptureListener;
    private OnClosePressedListener mClosePressedListener;
    private int mEntityId;
    private String mImageDirPath;
    private PhotoCaptureManager mPhotoCaptureManager;
    private ImageView mCloseButton;
    private View mCaptureButton;
    private View mDoneButton;
    private View mDarkOverlay;
    private ImageView mPhotoPreview;
    private Camera mCamera;
    private TextureView mCameraPreview;

    private boolean isFullscreenBefore;

    private Dialog mCameraErrorDialog;
    private int mGalleryButtonSize;
    private byte[] mGalleryImageBytes;
    private boolean mIsOptimalPreviewSizeSet;

    /**
     * Create a new instance of PhotoCaptureFragment, initialized to
     * show the photo capturing for shipment with 'entityId'. Photo will
     * be saved into the 'imageDirPath' directory.
     */
    public static PhotoCaptureFragment newInstance(int entityId, String imageDirPath) {
        return newInstance(entityId, imageDirPath, null);
    }

    /**
     * Create a new instance of PhotoCaptureFragment, initialized to
     * show the photo capturing for shipment with 'entityId'. Photo will
     * be saved into the 'imageDirPath' directory. Info can be provided
     * with 'properties' (can be null)
     */
    public static PhotoCaptureFragment newInstance(int entityId, String imageDirPath,
                                                   Collection<Property> properties) {
        PhotoCaptureFragment fragment = new PhotoCaptureFragment();
        fragment.setArguments(createArguments(entityId, imageDirPath, properties));
        return fragment;
    }

    /**
     * Create common arguments for all photo fragments. There are thee arguments:
     * 'entityId', 'imageDirPath' and 'properties'.
     */
    protected static Bundle createArguments(int entityId, String imageDirPath,
                                            Collection<Property> properties) {
        Bundle arguments = new Bundle();
        arguments.putInt(ENTITY_ID, entityId);
        arguments.putString(IMAGE_DIR_PATH, imageDirPath);
        if (properties != null) {
            arguments.putParcelableArrayList(PROPERTIES, new ArrayList<>(properties));
        }
        return arguments;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mImageCaptureListener = (OnImageCaptureListener) context;
            mClosePressedListener = (OnClosePressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnImageCaptureListener & OnClosePressedListener!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEntityId = getArguments().getInt(ENTITY_ID);
            mImageDirPath = getArguments().getString(IMAGE_DIR_PATH);
            if (getArguments().containsKey(PROPERTIES)) {
                ArrayList<Parcelable> properties = getArguments()
                        .getParcelableArrayList(PROPERTIES);
                if (properties != null) {
                    for (Parcelable property : properties) {
                        mPhoto.addProperty((Property) property);
                    }
                }
            }
        }
        mPhotoCaptureManager = new PhotoCaptureManager();
        int flags = getActivity().getWindow().getAttributes().flags;
        if (!(isFullscreenBefore = ((flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0))) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_capturing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCloseButton = (ImageView) view.findViewById(R.id.close_button);
        mCaptureButton = view.findViewById(R.id.capture_button);
        mDoneButton = view.findViewById(R.id.done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButtonPressed();
            }
        });
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonPressed();
            }
        });
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCaptureButtonPressed();
            }
        });
        mCameraLayout = (ViewGroup) view.findViewById(R.id.camera_preview);
        mPhotoPreview = (ImageView) view.findViewById(R.id.photo_preview);
        mPhotoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoPreviewPressed();
            }
        });
        mInfoButton = view.findViewById(R.id.info_button);
        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInfoButtonPressed();
            }
        });
        mDarkOverlay = view.findViewById(R.id.dark_overlay);
        mButtonsLayout = (ViewGroup) view.findViewById(R.id.buttons_container);
        mGalleryIconLayout = view.findViewById(R.id.icon_gallery_layout);
        mGalleryButtonSize = getResources().getDimensionPixelSize(R.dimen.button_height_tall);
        mGalleryIconImageView = (RoundedImageView) view.findViewById(R.id.icon_gallery);
        mGalleryIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoCaptureFragment.this.getActivity()
                        .startActivityForResult(IntentUtils.galleryIntent(), REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (null != resultData) {
                    Uri selectedImage = resultData.getData();
                    if (null != selectedImage) {
                        try {
                            InputStream imageStream = getActivity()
                                    .getContentResolver()
                                    .openInputStream(selectedImage);

                            if (null != imageStream) {
                                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                int nRead;
                                byte[] data = new byte[16384];

                                while ((nRead = imageStream.read(data, 0, data.length)) != -1) {
                                    buffer.write(data, 0, nRead);
                                }

                                buffer.flush();
                                mGalleryImageBytes = buffer.toByteArray();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, resultData);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initCamera();
        if (null != mGalleryImageBytes) {
            onPictureCapturedFromGallery(mGalleryImageBytes);
            mGalleryImageBytes = null;
        }
        new LatestImageAsyncTask(mGalleryIconImageView, mGalleryButtonSize,
                new LatestImageAsyncTask.OnImageLoaded() {
                    @Override
                    public void imageLoaded(Bitmap bitmap) {
                        setVisibility(mGalleryIconLayout, true);
                        mGalleryIconImageView.setImageBitmap(bitmap);
                    }
                }).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isFullscreenBefore) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mGalleryImageBytes = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // We should set all listeners in null to avoid memory leaks
        mImageCaptureListener = null;
        mClosePressedListener = null;
    }

    private void initCamera() {
        Camera camera;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace(); // Camera is not available (in use or does not exist)
            mCameraErrorDialog = DialogUtils.showCameraErrorDialog(getActivity());
            GATrackerUtils.trackException(getContext(), e);
            return;
        }
        setCamera(camera);
        setPreview(new TextureView(getContext()));
        mCameraLayout.addView(mCameraPreview, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        mCameraLayout.removeView(mCameraPreview);
        setPreview(null);
        if (mCameraErrorDialog != null) {
            mCameraErrorDialog.dismiss();
        }
    }

    /**
     * Set visibility for all views in photo fragments
     */
    protected void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Send statistics about image discarding on Google Analytics
     */
    protected void trackImageDiscarding(String category) {
        GATrackerUtils.trackEvent(getContext(), category, Constants.EventAction.DISCARD, null, 0);
    }

    @Override
    public void showCaptureButton(boolean show) {
        setVisibility(mCaptureButton, show);
    }

    @Override
    public void showGalleryButton(boolean show) {
        setVisibility(mGalleryIconImageView, show);
    }

    @Override
    public void showCloseButton(boolean show) {
        setVisibility(mCloseButton, show);
    }

    @Override
    public void showDoneButton(boolean show) {
        setVisibility(mDoneButton, show);
    }

    @Override
    public void showInfoButton(boolean show) {
        setVisibility(mInfoButton, show);
    }

    @Override
    public void showDarkOverlay(boolean show) {
        if (show) {
            AnimationUtils.fadeInAnimation(mDarkOverlay, FADE_DURATION, TRANSPARENT_60);
        } else {
            AnimationUtils.fadeOutAnimation(mDarkOverlay, FADE_DURATION);
        }
    }

    @Override
    public void showCameraView(boolean show) {
        setVisibility(mCameraPreview, show);
    }

    @Override
    public void showPhotoPreview(boolean show) {
        setVisibility(mPhotoPreview, show);
    }

    @Override
    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, this);
        }
    }

    @Override
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void setPreview(TextureView textureView) {
        mCameraPreview = textureView;
        if (mCameraPreview != null) {
            mCameraPreview.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void setCameraPreviewTexture(SurfaceTexture surface) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewTexture(surface);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(); // Something bad happened
            GATrackerUtils.trackException(getContext(), ioe);
        }
    }

    @Override
    public void setCloseButtonAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        mCloseButton.setAlpha(alpha);
    }

    @Override
    public void setDoneButtonAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        mDoneButton.setAlpha(alpha);
    }

    @Override
    public void setCloseButtonImage(int resourceDrawableId) {
        mCloseButton.setImageResource(resourceDrawableId);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        CameraUtils.setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.
                CAMERA_FACING_BACK, mCamera);
        setCameraPreviewTexture(surface);
        if (mCamera != null) {
            if (!mIsOptimalPreviewSizeSet) {
                mIsOptimalPreviewSizeSet = true;
                CameraUtils.setOptimalPreviewSize(mCamera, width, height);
            }
            CameraUtils.setAutoFocus(mCamera);
            mCamera.startPreview();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        onPictureCaptured(data);
    }

    @Override
    public void onImageDeleted() {
        mPhoto.setImagePath(null);
        mPhoto.setPhotoId(null);
        showCameraView(true);
        showCaptureButton(true);
        showGalleryButton(true);
        showDoneButton(false);
        showPhotoPreview(false);
        showCloseButton(true);
        showInfoButton(false);
        if (mCamera != null) {
            mCamera.startPreview();
        }
        trackImageDiscarding(Constants.EventCategory.CAMERA_WIDGET);
    }

    @Override
    public void onImageSaved(String path) {
        mPhoto.setImagePath(path);
        mPhoto.setPhotoId(UUID.randomUUID().toString());
        mPhotoPreview.setImageBitmap(BitmapFactory.decodeFile(path, new BitmapFactory.Options()));
        showCameraView(false);
        showCaptureButton(false);
        showGalleryButton(false);
        showDoneButton(true);
        showPhotoPreview(true);
        showCloseButton(true);
        showDarkOverlay(true);
        showInfoButton(!mPhoto.getProperties().isEmpty());
    }

    @Override
    public void onImageDeleteFailed(Exception e) {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onImageSaveFailed(Exception e) {
        Log.e(TAG, e.getMessage());
    }

    /**
     * Return the captured/saved Photo to the calling Activity.
     */
    private void imageCaptured(Photo photo) {
        if (mImageCaptureListener != null) {
            mImageCaptureListener.onImageCaptured(photo);
        }
    }

    /**
     * Tell the calling Activity that the close button has been evoked.
     */
    private void closePressed() {
        if (mClosePressedListener != null) {
            mClosePressedListener.onClosePressed();
        }
    }

    @Override
    public void onCaptureButtonPressed() {
        showCaptureButton(false);
        showGalleryButton(false);
        showCloseButton(false);
        takePicture();
    }

    @Override
    public void onPictureCaptured(byte[] imageData) {
        mPhotoCaptureManager.saveTakenImage(getContext(), mImageDirPath, imageData,
                mEntityId, true, this);
    }

    @Override
    public void onPictureCapturedFromGallery(byte[] imageData) {
        // Never rotate images taken from gallery
        mPhotoCaptureManager.saveTakenImage(getContext(), mImageDirPath, imageData,
                mEntityId, false, this);
    }

    @Override
    public void onCloseButtonPressed() {
        if (mDarkOverlay.getVisibility() == View.VISIBLE) {
            onInfoButtonPressed();
            return;
        }
        if (mCaptureButton.getVisibility() == View.VISIBLE) {
            closePressed();
        } else {
            mPhotoCaptureManager.deleteTakenImage(mPhoto.image_path(), this);
        }
    }

    @Override
    public void onDoneButtonPressed() {
        imageCaptured(mPhoto);
    }

    @Override
    public void onPhotoPreviewPressed() {
        // Do nothing
    }

    @Override
    public void onInfoButtonPressed() {
//        showDarkOverlay(!isListFragmentShowing());
        if (!isListFragmentShowing()) {
            showPropertiesFragment();
        } else {
            getChildFragmentManager().popBackStack();
        }
        mCloseButton.setAlpha(!isListFragmentShowing() ? TRANSPARENT_40 : FULL_ALPHA);
        mCloseButton.setImageResource(!isListFragmentShowing() ? R.drawable.back_button_icon :
                R.drawable.close_button_icon);
        showDoneButton(isListFragmentShowing());
    }

    @Override
    public boolean onBackPressed() {
        onCloseButtonPressed();
        return true;
    }

    @Override
    public void onItemClick(IListItem item) {
        Toast.makeText(getContext(), "Item clicked!\nTitle: " + item.getTitle() + "\nSubtext: "
                + item.getSubtext(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show {@link PropertiesFragment}, initialized to show the photo
     * properties with 'items'.
     */
    protected void showPropertiesFragment() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_out, R.anim.fade_in,
                R.anim.fade_out, R.anim.fade_in);
        ft.replace(R.id.properties_container, PropertiesFragment.create(
                mPhoto.getProperties()));
        ft.addToBackStack(null);
        ft.commit();
        GATrackerUtils.trackEvent(getContext(), Constants.EventCategory.CAMERA_WIDGET,
                Constants.EventAction.IMAGE_PROPERTIES, null, 0);
    }

    /**
     * Make preparations for showing/hiding ListFragment
     *
     * @return list fragment is showing or not
     */
    private boolean isListFragmentShowing() {
        return mDoneButton.getVisibility() != View.VISIBLE;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to add action on image captured event.
     */
    public interface OnImageCaptureListener {
        void onImageCaptured(Photo photo);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to add action on close button pressed event.
     */
    public interface OnClosePressedListener {
        void onClosePressed();
    }
}
