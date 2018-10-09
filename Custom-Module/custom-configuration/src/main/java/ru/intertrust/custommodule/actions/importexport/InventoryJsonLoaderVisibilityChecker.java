package ru.intertrust.custommodule.actions.importexport;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;

import javax.inject.Inject;
import java.util.List;

@ComponentName("inventory.json.loader.visibility.checker")
public class InventoryJsonLoaderVisibilityChecker implements ActionVisibilityChecker {


    @Autowired
    private CrudService crudService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        List<DomainObject> attachList = null;
        Id inventoryId = context.getDomainObject().getId();

        if (inventoryId != null)
            attachList = crudService.findLinkedDomainObjects(inventoryId, "territory_import", "inventory");

        return attachList != null && !attachList.isEmpty();
    }
}
