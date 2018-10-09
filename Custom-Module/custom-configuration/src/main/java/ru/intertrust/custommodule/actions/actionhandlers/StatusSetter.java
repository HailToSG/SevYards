package ru.intertrust.custommodule.actions.actionhandlers;

import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.access.AccessToken;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.List;

public class StatusSetter {


    public boolean changeStatusForDo(Id doId, String status,
                                     CollectionsService collectionsService, AccessControlService accessControlService,
                                     DomainObjectDao domainObjectService) {
        Id statusDomainObject;
        statusDomainObject = getStatusIdByName(status, collectionsService);
        DomainObject saveDomainObject;
        AccessToken accessToken;

        if (accessControlService != null && domainObjectService != null) {
            if (statusDomainObject != null) {
                accessToken = accessControlService.createSystemAccessToken(getClass().getName());
                saveDomainObject = domainObjectService.setStatus(doId, statusDomainObject, accessToken);
                return (saveDomainObject != null);
            }
        }
        return false;
    }

    public Id getStatusIdByName(String name, CollectionsService collectionsService) {
        List<Value> params = new ArrayList<>();
        params.add(new StringValue(name));
        IdentifiableObjectCollection collection = collectionsService
                .findCollectionByQuery(CustomModuleConstants.QUERY_STATUS_BY_NAME, params);
        if (collection != null && collection.size() > 0)
            return collection.get(0).getId();
        else return null;
    }

    public String getQueryStatusById(Id status, CollectionsService collectionsService) {
        List<Value> parameters = new ArrayList<>();
        Value statusIdValue = new ReferenceValue(status);
        parameters.add(statusIdValue);

        if (status != null) {
            IdentifiableObjectCollection collection = collectionsService
                    .findCollectionByQuery(CustomModuleConstants.QUERY_STATUS_BY_ID, parameters);
            if (collection != null && collection.size() > 0) {
                return collection.get(0).getString(CustomModuleConstants.STATUS_FIELD_NAME);
            }
        }
        return null;
    }


}
