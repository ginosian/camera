package com.margin.camera.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.margin.camera.R;
import com.margin.camera.models.Note;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NotePinView extends SubsamplingScaleImageView {

    private Map<PointF, Bitmap> mNotePins;
    private Paint mPaint;
    private int mNoteLeftOffset;
    private boolean mIsShowAnnotations = true;

    public NotePinView(Context context) {
        this(context, null);
    }

    public NotePinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void addNotes(Collection<Note> notes) {
        mNotePins = new HashMap<>(notes.size());
        for (Note note : notes) {
            if (!note.hasEmptyCoordinates()) {
                mNotePins.put(new PointF(note.x(), note.y()),
                        loadBitmapFromView(NoteView.createNoteView(getContext(), note)));
            }
        }
        invalidate();
    }

    public void showNotes(boolean show) {
        mIsShowAnnotations = show;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Don't draw pins before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }
        if (mIsShowAnnotations && mNotePins != null && !mNotePins.isEmpty()) {
            for (Map.Entry<PointF, Bitmap> entry : mNotePins.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    Bitmap noteBitmap = entry.getValue();
                    if (noteBitmap != null && !noteBitmap.isRecycled()) {
                        PointF viewPoint = sourceToViewCoord(entry.getKey());
                        float x = viewPoint.x - mNoteLeftOffset;
                        float y = viewPoint.y - noteBitmap.getHeight();
                        canvas.drawBitmap(noteBitmap, x, y, mPaint);
                        Log.d("Note view", entry.toString());
                    }
                }
            }
        }
    }

    private void initialise() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // the position (x,y) of any view is a left top corner of the view, but in our case
        // the POI is where the triangle is pointing to. So we should calculate actual
        // view position like:
        // leftViewPosition = noteX - triangleLeftOffset;
        // topViewPosition = noteY - noteViewHeight;
        if (mNoteLeftOffset == 0) {
            // offset is used in dp, so it should properly affect all screen densities.
            mNoteLeftOffset = (int) getResources().getDimension(R.dimen.note_triangle_left_offset);
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }
}
