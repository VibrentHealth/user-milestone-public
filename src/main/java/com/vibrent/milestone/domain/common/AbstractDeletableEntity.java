package com.vibrent.milestone.domain.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * class that maps with common audit fields
 */
@MappedSuperclass
public abstract class AbstractDeletableEntity extends AbstractEntity {


    @Column(name = "is_deleted")
    protected boolean isDeleted;


    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        if (isDeleted == null)
            this.isDeleted = false;
        else {
            this.isDeleted = isDeleted;
        }
    }
}