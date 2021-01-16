package com.omada.junctionadmin.utils.taskhandler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class CountTriggeredLiveData <T> extends LiveData<T> {

    private final int FINAL_COUNT;
    private int count = 0;

    public CountTriggeredLiveData(int numberToCountTo){
        FINAL_COUNT = numberToCountTo;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void setValue(T value) {
        count++;
        if(count == FINAL_COUNT) {
            super.setValue(value);
        }
    }
}

