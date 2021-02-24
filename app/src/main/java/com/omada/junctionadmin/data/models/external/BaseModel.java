package com.omada.junctionadmin.data.models.external;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.omada.junctionadmin.data.models.BaseModelCommon;

import java.io.Serializable;

public abstract class BaseModel extends BaseModelCommon implements Parcelable {

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
