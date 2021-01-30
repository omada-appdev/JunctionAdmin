package com.omada.junctionadmin.data.models.internal;

import com.google.firebase.firestore.Exclude;
import com.omada.junctionadmin.data.models.BaseModelCommon;
import com.omada.junctionadmin.data.models.external.BaseModel;

public class BaseModelInternal extends BaseModelCommon {

    public BaseModelInternal(){
    }

    public BaseModelInternal(String id) {
        super(id);
    }

    @Exclude
    @Override
    public String getId(){
        return super.getId();
    }

    @Exclude
    @Override
    public void setId(String id){
        super.setId(id);
    }
}
