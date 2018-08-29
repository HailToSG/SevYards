package ru.intertrust.custommodule.actions;

//TODO: Понять, откуда можно вытянуть ссылку на территорию, по которой мы перешли в child-collection

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.ComponentHandler;
import ru.intertrust.cm.core.gui.api.server.widget.FormDefaultValueSetter;
import ru.intertrust.cm.core.gui.model.form.FieldPath;
import ru.intertrust.cm.core.gui.model.form.FormObjects;
import ru.intertrust.cm.core.gui.model.form.FormState;

import java.util.*;

@ComponentName("inventory.form.default.value.setter")
public class InventoryFormDefaultValueSetter implements ComponentHandler, FormDefaultValueSetter {

    @Autowired
    CollectionsService collectionsService;

    private static final String TERRITORY_LINKED_FIELD = "territory_id";
    private static final String INVENTORY_STATUS_FIELD = "status";
    private static final String TERRITORY_NAME_FIELD = "name";
    private static final String QUERY_TERRITORY_BY_INVENTORY = "SELECT * FROM ter_territory WHERE id= {0}";
    private static final String INVENTORY_TERRITORY_NAME_FIELD = "territory_name";
    private static final String IS_BUILDINGS_FILLED_FIELD_CHECKBOX = "is_buildings_filled";
    private static final String IS_RIGHTHOLDERS_FILLED_FIELD_CHECKBOX = "is_rightholders_filled";
    private static final String IS_ELEMENTS_FILLED_CHECKBOX = "is_elements_filled";
    private static final String DATE_START_FIELD = "start_time";
    private static final Boolean CHECKBOX_FALSE_VALUE = false;
    private static Id terrReference = TerritoryIdKeeper.getTerrId();

    @Override
    public Value[] getDefaultValues(FormObjects formObjects, FieldPath fieldPath) {
        return new Value[0];
    }

    @Override
    public Value getDefaultValue(FormObjects formObjects, FieldPath fieldPath) {
        return null;
    }

    @Override
    public Value[] getDefaultValues(FormState formState, FieldPath fieldPath) {
        return new Value[0];
    }

    @Override
    public Value getDefaultValue(FormState formState, FieldPath fieldPath) {
        DomainObject domainObj = formState.getObjects().getRootDomainObject();
        String fieldPathValue = fieldPath.getPath();
        if (domainObj != null) {
            switch (fieldPathValue) {
                case INVENTORY_STATUS_FIELD:
                    return new ReferenceValue(null);
                case INVENTORY_TERRITORY_NAME_FIELD:
                    return new StringValue(getTerritoryById(terrReference));
                case TERRITORY_LINKED_FIELD:
                    if (terrReference != null)
                        domainObj.setReference(TERRITORY_LINKED_FIELD, terrReference);
                    return null;
                case IS_BUILDINGS_FILLED_FIELD_CHECKBOX:
                    return new BooleanValue(CHECKBOX_FALSE_VALUE);
                case IS_RIGHTHOLDERS_FILLED_FIELD_CHECKBOX:
                    return new BooleanValue(CHECKBOX_FALSE_VALUE);
                case IS_ELEMENTS_FILLED_CHECKBOX:
                    return new BooleanValue(CHECKBOX_FALSE_VALUE);
                case DATE_START_FIELD:
                    return new DateTimeValue(new Date());
                default:
                    return null;
            }
        }
        return null;
    }
    public String getTerritoryById(Id status){
        Value statusIdValue = new ReferenceValue(status);
        List<Value> params = new ArrayList<>();
        params.add(statusIdValue);
        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_TERRITORY_BY_INVENTORY, params);
        if (collection != null && collection.size() > 0) {
            for (IdentifiableObject O : collection) {
                return O.getString(TERRITORY_NAME_FIELD);
            }
        }
        return null;
    }
}



