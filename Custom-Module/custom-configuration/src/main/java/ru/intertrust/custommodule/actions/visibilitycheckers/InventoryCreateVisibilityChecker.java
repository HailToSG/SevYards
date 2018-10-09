package ru.intertrust.custommodule.actions.visibilitycheckers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.custommodule.actions.actionhandlers.StatusSetter;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.create.visibility.checker")
public class InventoryCreateVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        StatusSetter statusSetter = new StatusSetter();
        DomainObject domainObject = context.getDomainObject();
        Id terrReference = domainObject.getReference(CustomModuleConstants.TERRITORY_LINKED_FIELD);
        String statusName;
        Id status;
        if (!domainObject.isNew()) {
            List<Value> ids = new ArrayList<>();
            ids.add(new ReferenceValue(terrReference));
            IdentifiableObjectCollection identifiableObjects = collectionsService
                    .findCollectionByQuery(CustomModuleConstants.QUERY_INVENTORIES_BY_TERRITORY_ID, ids);

            if (identifiableObjects != null && identifiableObjects.size() > 0) {
                for (IdentifiableObject identifiableObject : identifiableObjects) {
                    DomainObject dObj = crudService.find(identifiableObject.getId());
                    status = dObj.getReference(CustomModuleConstants.STATUS_FIELD);
                    statusName = statusSetter.getQueryStatusById(status, collectionsService);
                    if (statusName == null || statusName.equals(CustomModuleConstants.STATUS_DRAFT)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }
}