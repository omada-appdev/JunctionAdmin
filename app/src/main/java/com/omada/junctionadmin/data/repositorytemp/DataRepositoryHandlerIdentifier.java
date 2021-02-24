package com.omada.junctionadmin.data.repositorytemp;

import androidx.annotation.Nullable;

/*
 Identifies each handler so that the data related to it can be stored in the data
 repository accessor data
*/
public final class DataRepositoryHandlerIdentifier {

    private final String id;

    DataRepositoryHandlerIdentifier(String id){
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DataRepositoryHandlerIdentifier other = (DataRepositoryHandlerIdentifier) obj;
        return this.id.equals(other.id);
    }

}
