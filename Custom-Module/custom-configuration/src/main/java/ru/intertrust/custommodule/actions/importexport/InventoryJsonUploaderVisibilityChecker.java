package ru.intertrust.custommodule.actions.importexport;

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

@ComponentName("inventory.json.uploader.visibility.checker")
public class InventoryJsonUploaderVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    CrudService crudService;

    @Autowired
    CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        Id contextTerritoryId = context.getDomainObject().getId();
        DomainObject dObj = context.getDomainObject();
        StatusSetter statusSetter = new StatusSetter();
        List<DomainObject> allInventoriesList = crudService.findAll("inventory");
        List<DomainObject> inventoriesByTerritory = new ArrayList<>();
        if(dObj!=null && !dObj.isNew()) {
            for (DomainObject object : allInventoriesList) {
                if (object.getReference(CustomModuleConstants.TERRITORY_LINKED_FIELD).equals(contextTerritoryId)) {
                    inventoriesByTerritory.add(object);
                }
            }

            for (DomainObject object : inventoriesByTerritory) {
                if (object.getReference("status").equals(statusSetter.getStatusIdByName(
                        CustomModuleConstants.STATUS_DRAFT, collectionsService))) {
                    return true;
                }
            }
        }
        return false;

    }
}
