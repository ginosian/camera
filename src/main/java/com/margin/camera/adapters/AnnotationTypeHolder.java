package com.margin.camera.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.margin.camera.R;
import com.margin.components.views.SeekBarWithHint;

/**
 * Created on Mar 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public abstract class AnnotationTypeHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, SeekBarWithHint.OnSeekBarHintChangeListener {

    protected CompoundButton title;
    protected SeekBarWithHint severity;

    public AnnotationTypeHolder(final View itemView) {
        super(itemView);
        title = (CompoundButton) itemView.findViewById(R.id.type_name);
        severity = (SeekBarWithHint) itemView.findViewById(R.id.type_severity);
        severity.setOnHintChangeListener(this);
        severity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress < 33) {
                    setSeekBarColor(seekBar, getColor(seekBar.getContext(),
                            R.color.lightGreen500));
                } else if (progress >= 33 && progress < 66) {
                    setSeekBarColor(seekBar, getColor(seekBar.getContext(), R.color.orange500));
                } else if (progress >= 66) {
                    setSeekBarColor(seekBar, getColor(seekBar.getContext(), R.color.red500));
                }
                onSeekBarProgressChanged(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        setSeekBarColor(severity, getColor(title.getContext(), R.color.lightGreen500));
        itemView.findViewById(R.id.type_layout).setOnClickListener(this);
    }

    /**
     * Do something on seekbar progress changed
     * TODO: The integers 0, 33, 66 need to be saved in an xml so we can easily configure
     */
    protected abstract void onSeekBarProgressChanged(int progress);

    /**
     * Change the 'seekBar' to the specified 'color'.
     */
    private void setSeekBarColor(SeekBar seekBar, int color) {
        LayerDrawable layerDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layerDrawable = (LayerDrawable) seekBar.getProgressDrawable().getCurrent();
        } else {
            layerDrawable = (LayerDrawable) seekBar.getProgressDrawable();
        }
        Drawable drawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private int getColor(Context context, int colorResourceId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(colorResourceId, null);
        } else {
            return context.getResources().getColor(colorResourceId);
        }
    }

    @Override
    public SeekBarWithHint.HintType onHintChanged(SeekBarWithHint seekBarHint, int progress) {
        if (progress >= 0 && progress < 33) {
            return SeekBarWithHint.HintType.Mild;
        } else if (progress >= 33 && progress < 66) {
            return SeekBarWithHint.HintType.Medium;
        } else if (progress >= 66) {
            return SeekBarWithHint.HintType.Severe;
        }
        return SeekBarWithHint.HintType.None;
    }
}
