package com.wallpo.android.videoTrimmer.interfaces;


import com.wallpo.android.videoTrimmer.views.RangeSeekBarView;

public interface OnRangeSeekBarListener {
    void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value);


    void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value);

}
