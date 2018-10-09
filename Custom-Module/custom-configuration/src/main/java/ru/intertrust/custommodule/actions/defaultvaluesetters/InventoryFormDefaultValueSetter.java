package ru.intertrust.custommodule.actions.defaultvaluesetters;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.ComponentHandler;
import ru.intertrust.cm.core.gui.api.server.widget.FormDefaultValueSetter;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.FieldPath;
import ru.intertrust.cm.core.gui.model.form.FormObjects;
import ru.intertrust.cm.core.gui.model.form.FormState;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.Date;

@ComponentName("inventory.form.default.value.setter")
public class InventoryFormDefaultValueSetter implements ComponentHandler, FormDefaultValueSetter {

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Autowired
    private CrudService service;

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
        Id currentUserId = currentUserAccessor.getCurrentUserId();
        Id terrRef = CustomModuleConstants.getTerrReferenceMap().get(currentUserId);
        DomainObject terrObj = service.find(CustomModuleConstants.getTerrReferenceMap().get(currentUserId));
        DomainObject domainObj = formState.getObjects().getRootDomainObject();

        String fieldPathValue = fieldPath.getPath();
        if (domainObj != null) {
            switch (fieldPathValue) {
                case CustomModuleConstants.INVENTORY_TERRITORY_NAME_FIELD:
                    return new StringValue(terrObj.getString(CustomModuleConstants.TERRITORY_NAME_FIELD));
                case CustomModuleConstants.TERRITORY_LINKED_FIELD:
                    if (terrRef != null)
                        domainObj.setReference(CustomModuleConstants.TERRITORY_LINKED_FIELD,
                                terrRef);
                    return null;
                case CustomModuleConstants.IS_BUILDINGS_FILLED_FIELD:
                    return new BooleanValue(false);
                case CustomModuleConstants.IS_RIGHTHOLDERS_FILLED_FIELD:
                    return new BooleanValue(false);
                case CustomModuleConstants.IS_ELEMENTS_FILLED_FIELD:
                    return new BooleanValue(false);
                case CustomModuleConstants.DATE_START_FIELD:
                    return new DateTimeValue(new Date());
                default:
                    return null;
            }
        }
        return null;
    }
}



