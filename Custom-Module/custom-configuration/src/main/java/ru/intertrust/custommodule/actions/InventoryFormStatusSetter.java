package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.access.AccessToken;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.ActionContext;
import ru.intertrust.cm.core.gui.model.action.ActionData;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.action")
public class InventoryFormStatusSetter extends ActionHandler<ActionContext, ActionData> {

    private static final String STATUS_DRAFT = "Draft inventory";
    private static final String STATUS_FINISHED = "Finished inventory";
    private static final String FIELD_NAME = "name";
    private static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";
    private static final String QUERY_STATUS_BY_NAME = "select * from status where name = {0}";

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    private CrudService crudService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private DomainObjectDao domainObjectService;

    @Override
    public ActionData executeAction(ActionContext context) {
        Id rootObjectId = context.getRootObjectId();
        SimpleActionData actionData = new SimpleActionData();
        if (rootObjectId != null) {
            DomainObject rootDomainObject = crudService.find(rootObjectId);

            if (getStatusById(rootDomainObject.getStatus()).equals(STATUS_DRAFT)) {
                changeStatusForDo(rootDomainObject.getId(), STATUS_FINISHED);
            }
            if (getStatusById(rootDomainObject.getStatus()).equals(STATUS_FINISHED)) {
                changeStatusForDo(rootDomainObject.getId(), STATUS_DRAFT);
            }
        }

        return actionData;
    }

    public String getStatusById(Id status) {
        Value statusIdValue = new ReferenceValue(status);
        List<Value> params = new ArrayList<>();
        params.add(statusIdValue);
        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_ID, params);
        if (collection != null && collection.size() > 0) {
            for (IdentifiableObject O : collection) {
                return O.getString(FIELD_NAME);
            }
        }

        return null;
    }

    public boolean changeStatusForDo(Id doId, String status) {
        Id statusDomainObject;
        statusDomainObject = getStatusByName(status);
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

    public Id getStatusByName(String name) {
        List<Value> params = new ArrayList<>();
        params.add(new StringValue(name));
        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_NAME, params);
        if (collection != null && collection.size() > 0)
            return collection.get(0).getId();
        else return null;
    }
}
