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

@ComponentName("inventory.copy.visibility.checker")
public class InventoryCopyVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {

        DomainObject inventoryObject = context.getDomainObject();
        Id terReference = inventoryObject.getReference(CustomModuleConstants.TERRITORY_LINKED_FIELD);

        if (inventoryObject.isNew() || terReference == null) {
            return false;
        }
        IdentifiableObjectCollection identifiableObjects = getIdentifiableObjects(terReference);
        return isAllInventoriesClosed(identifiableObjects);
    }

    private boolean isAllInventoriesClosed(IdentifiableObjectCollection identifiableObjects) {
        Id inventoryStatus;
        String actualStatusName;
        StatusSetter statusSetter = new StatusSetter();
        if (identifiableObjects != null && identifiableObjects.size() > 0) {
            for (IdentifiableObject idObject : identifiableObjects) {
                inventoryStatus = crudService.find(idObject.getId()).getReference(CustomModuleConstants.STATUS_FIELD);
                actualStatusName = statusSetter.getQueryStatusById(inventoryStatus, collectionsService);
                if (actualStatusName.equals(CustomModuleConstants.STATUS_DRAFT)) {
                    return false;
                }
            }
        }
        return true;
    }

    private IdentifiableObjectCollection getIdentifiableObjects(Id terReference) {
        List<Value> ids = new ArrayList<>();
        ids.add(new ReferenceValue(terReference));
        return collectionsService.findCollectionByQuery(CustomModuleConstants.QUERY_INVENTORIES_BY_TERRITORY_ID, ids);
    }
}