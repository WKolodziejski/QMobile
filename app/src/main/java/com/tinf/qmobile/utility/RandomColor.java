package com.tinf.qmobile.utility;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class RandomColor {
    private Stack<Integer> recycle, colors;

    public RandomColor() {
        colors = new Stack<>();
        recycle = new Stack<>();
        recycle.addAll(Arrays.asList(ArrayUtils.toObject(App.getContext().getResources().getIntArray(R.array.colors1))));
    }

    public int getColor() {
        if (colors.size() == 0) {
            while (!recycle.isEmpty())
                colors.push(recycle.pop());
            Collections.shuffle(colors);
        }
        Integer c = colors.pop();
        recycle.push(c);
        return c;
    }

}
