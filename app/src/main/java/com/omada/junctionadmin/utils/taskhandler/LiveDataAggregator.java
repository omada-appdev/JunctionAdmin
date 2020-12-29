package com.omada.junctionadmin.utils.taskhandler;

import androidx.lifecycle.MediatorLiveData;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;


/*
Use this class when loading live data from multiple sources. Initialize the aggregator and call the
aggregator functions in the source observers when they are invoked

The template parameter is the type of data you are aggregating
 */

public abstract class LiveDataAggregator<D, S, T>{

    /*
    when data is loaded it goes into this map where the string denotes the type of data
    this is used later to check whether or not to aggregate and set value
    */
    protected final Map<D, S> dataOnHold = new HashMap<>();
    protected final MediatorLiveData<T> destinationLiveData;

    public LiveDataAggregator(MediatorLiveData<T> destination){
        destinationLiveData = destination;
    }

    @OverridingMethodsMustInvokeSuper
    public void holdData(D typeOfData, S data) {

        if(data == null){
            return;
        }

        if(dataOnHold.containsKey(typeOfData)){
            dataOnHold.put(typeOfData, mergeWithExistingData(typeOfData, dataOnHold.get(typeOfData), data));
        }
        else{
            dataOnHold.put(typeOfData, data);
        }

        if(checkDataForAggregability()){
            aggregateData();
        }
    }

    //this functions combines data coming from the same source (or type) with already existing data
    protected abstract S mergeWithExistingData(D typeofData, S oldData, S newData);

    //this function checks if it is safe to combine values from the sources and set value
    protected abstract boolean checkDataForAggregability();

    //this function combines the data stored on hold
    protected abstract void aggregateData();



}
