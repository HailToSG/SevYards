package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.status.draft.visible.checker")
public class InventoryStatusDraftVisibilityChecker implements ActionVisibilityChecker {


    private static final String STATUS_FINISHED = "Finished inventory";
    private static final String FIELD_NAME = "name";
    private static final String QUERY_STATUS_BY_ID = "select * from status where id = {0}";

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        if (context.getDomainObject() != null && context.getDomainObject().getStatus() != null) {
            if (getQueryStatusById(context.getDomainObject().getStatus()).equals(STATUS_FINISHED)) {

                return true;
            } else return false;
        }

        return false;
    }

    public String getQueryStatusById(Id status) {
        List<Value> parameters = new ArrayList<>();
        Value statusIdValue = new ReferenceValue(status);
        parameters.add(statusIdValue);

        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_ID, parameters);
        if (collection != null && collection.size() > 0) {
            for (IdentifiableObject Obj : collection) {
                return Obj.getString(FIELD_NAME);
            }
        }
        return null;
    }
}