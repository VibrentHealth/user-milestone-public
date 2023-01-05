package com.vibrent.milestone.domain.common;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

public class UseIdOrGenerate extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        if (obj == null) {
            throw new HibernateException(new NullPointerException());
        } else {
            return (((IdGeneratorAbstract)obj).getId() == null ? super.generate(session, obj) : ((IdGeneratorAbstract)obj).getId());
        }
    }
}