package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalSynchronizationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NationalSynchronizationFailureServiceImpl extends BaseOpenmrsService
        implements NationalSynchronizationFailureService {

    @Autowired
    private M2SysNationalSynchronizationFailureDao dao;

    @Override
    public NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure) {
        return dao.save(nationalSynchronizationFailure);
    }
}
