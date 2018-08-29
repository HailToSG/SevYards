package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.edit.visible.checker")
public class InventoryEditVisibilityChecker implements ActionVisibilityChecker {

    private static final String STATUS_FINISHED = "Finished inventory";
    private static final String FIELD_NAME = "name";
    private static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        Id statusId = context.getDomainObject().getStatus();

        if (context.getDomainObject() == null || statusId == null) {
           return false;
        }else if (getQueryStatusById(statusId).equals(STATUS_FINISHED)) {
            return false;
        }
        return true;
    }

    public String getQueryStatusById(Id status) {
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
