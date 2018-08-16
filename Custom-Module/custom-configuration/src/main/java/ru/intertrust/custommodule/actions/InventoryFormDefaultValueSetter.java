package ru.intertrust.custommodule.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.cm.core.gui.api.server.ComponentHandler;
import ru.intertrust.cm.core.gui.api.server.widget.FormDefaultValueSetter;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.FieldPath;
import ru.intertrust.cm.core.gui.model.form.FormObjects;
import ru.intertrust.cm.core.gui.model.form.FormState;

import java.util.*;


@ComponentName("inventory.form.default.value.setter")
public class InventoryFormDefaultValueSetter implements ComponentHandler, FormDefaultValueSetter {

    @Autowired
    private DomainObjectDao domainObjectService;

    @Autowired
    CrudService crudService;

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    private AccessControlService accessControlService;

    private static final String QUERY_INVENTORY_BY_MAX_ID = "SELECT * FROM inventory WHERE id=(SELECT max(id) FROM inventory)";
    private static final String TERRITORY_LINKED_FIELD = "territory_id";
    private static final String IS_BUILDINGS_FILLED_FIELD_CHECKBOX = "is_buildings_filled";
    private static final String IS_RIGHTHOLDERS_FILLED_FIELD_CHECKBOX = "is_rightholders_filled";
    private static final String IS_ELEMENTS_FILLED_CHECKBOX = "is_elements_filled";
    private static final String DATE_START_FIELD = "start_time";
    private static final Boolean CHECKBOX_FALSE_VALUE = false;

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
        String fieldPathValue = fieldPath.getPath();

        IdentifiableObjectCollection collection = collectionsService.findCollectionByQuery(QUERY_INVENTORY_BY_MAX_ID);
        IdentifiableObject lastInventory = collection.get(0);
        ArrayList list = lastInventory.getFields();
        List<Id> temporaryBuildingsList = crudService.findLinkedDomainObjectsIds(lastInventory.getId(), "build_temporary", "inventory_id");
        List<DomainObject> temporaryBuildingsObjects = crudService.find(temporaryBuildingsList);
        crudService.save(temporaryBuildingsObjects);

        switch (fieldPathValue) {
            case TERRITORY_LINKED_FIELD:
                return new ReferenceValue(lastInventory.getId());
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
}
