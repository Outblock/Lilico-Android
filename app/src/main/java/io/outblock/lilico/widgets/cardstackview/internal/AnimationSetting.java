package io.outblock.lilico.widgets.cardstackview.internal;

import android.view.animation.Interpolator;

import io.outblock.lilico.widgets.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();

    int getDuration();

    Interpolator getInterpolator();
}
