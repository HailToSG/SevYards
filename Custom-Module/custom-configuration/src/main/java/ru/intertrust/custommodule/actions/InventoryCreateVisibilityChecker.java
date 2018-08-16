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

//TODO: упростить выборку незавершенных инвентаризаций
//TODO: Разобраться с кнопкой Создать на форме инвентаризации
//TODO: Сделать, чтобы делалась копия выделенной инвентаризации

@ComponentName("inventory.create.visibility.checker")
public class InventoryCreateVisibilityChecker implements ActionVisibilityChecker {
    private static final String STATUS_DRAFT = "Draft inventory";
    private static final String FIELD_NAME = "name";
    private static final String STATUS_FIELD = "status";
    private static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";

    @Autowired
    CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        Id terrReference = context.getDomainObject().getReference("territory_id");
        List<Value> ids = new ArrayList<>();
        ids.add(new ReferenceValue(terrReference));
        String queryAllInventoriesById = "select * from inventory where territory_id = {0}";
        IdentifiableObjectCollection identifiableObjects = collectionsService.findCollectionByQuery(queryAllInventoriesById, ids);

        if (identifiableObjects!= null && identifiableObjects.size() > 0) {
            for (IdentifiableObject idObj : identifiableObjects) {
                if (getQueryStatusById(crudService.find(idObj.getId()).getReference(STATUS_FIELD)).equals(STATUS_DRAFT)) {
                    return false;
                }
            }
        }
        return true;
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