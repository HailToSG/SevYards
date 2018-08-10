package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.util.PlaceholderResolver;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.status.finish.visible.checker")
public class InventoryStatusFinishVisibilityChecker implements ActionVisibilityChecker {
    private static final String  STATUS_DRAFT = "Draft inventory";
    private static final String  STATUS_FIELD_NAME = "name";
    private static final String  IS_BUILDINGS_FILLED_FIELD = "is_buildings_filled";
    private static final String  IS_RIGHTHOLDERS_FILLED_FIELD = "is_rightholders_filled";
    private static final String  IS_ELEMENTS_FILLED_FIELD = "is_elements_filled";
    private static final String  QUERY_STATUS_BY_ID = "select * from status where id = {0}";

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        PlaceholderResolver placeholderResolver = new PlaceholderResolver();
        if          (context.getDomainObject()!=null && context.getDomainObject().getStatus() !=null
                    &&(getQueryStatusById(context.getDomainObject().getStatus()).equals(STATUS_DRAFT)
                    &&context.getDomainObject().getBoolean(IS_BUILDINGS_FILLED_FIELD)
                    &&context.getDomainObject().getBoolean(IS_RIGHTHOLDERS_FILLED_FIELD)
                    &&context.getDomainObject().getBoolean(IS_ELEMENTS_FILLED_FIELD))){
        return true;
        }
        else return false;
    }

    public String getQueryStatusById (Id status){
        List<Value> params = new ArrayList<>();
        Value statusIdValue = new ReferenceValue(status);
        params.add(statusIdValue);
        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_STATUS_BY_ID,params);
        if (collection!=null && collection.size()>0){
            for (IdentifiableObject O : collection){
                return O.getString(STATUS_FIELD_NAME);
            }
        }
        return null;
    }
}
