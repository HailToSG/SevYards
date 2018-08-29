package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.copy.visibility.checker")
public class InventoryCopyVisibilityChecker implements ActionVisibilityChecker {
    private static final String STATUS_FINISHED = "Draft finished";
    private static final String FIELD_NAME = "name";
    private static final String TERRITORY_REFERENCE_FIELD = "territory_id";
    private static final String STATUS_FIELD = "status";
    private static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";
    private static final String QUERY_INVENTORIES_BY_TERRITORY_ID = "select * from inventory where territory_id = {0}";

    @Autowired
    CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        DomainObject domainObject = context.getDomainObject();
        Id terReference = domainObject.getReference(TERRITORY_REFERENCE_FIELD);

        if (terReference != null) {
            List<Value> ids = new ArrayList<>();
            ids.add(new ReferenceValue(terReference));
            IdentifiableObjectCollection identifiableObjects = collectionsService.findCollectionByQuery(QUERY_INVENTORIES_BY_TERRITORY_ID, ids);

            if (identifiableObjects != null && identifiableObjects.size() > 0) {
                for (IdentifiableObject idObject : identifiableObjects) {
                    if (!getQueryStatusById(crudService.find(idObject.getId()).getReference(STATUS_FIELD)).equals(STATUS_FINISHED)||
                            getQueryStatusById(crudService.find(idObject.getId()).getReference(STATUS_FIELD))==null) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    private String getQueryStatusById(Id status) {
        List<Value> param = new ArrayList<>();
        Value statusIdValue = new ReferenceValue(status);
        param.add(statusIdValue);
        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_ID, param);
        if (collection != null && collection.size() > 0) {
            for (IdentifiableObject Ob : collection) {
                return Ob.getString(FIELD_NAME);
            }
        }
        return null;
    }
}