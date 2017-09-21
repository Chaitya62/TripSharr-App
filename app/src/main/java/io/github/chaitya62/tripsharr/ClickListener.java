package io.github.chaitya62.tripsharr;

import android.view.View;

/**
 * Created by mikasa on 1/9/17.
 */


public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view,int position);

}