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
        StatusSetter statusSetter = new StatusSetter();
        List<Value> params = new ArrayList<>();
        params.add(new ReferenceValue
                (statusSetter.getStatusIdByName(CustomModuleConstants.STATUS_DRAFT, collectionsService)));
        IdentifiableObjectCollection draftInventoriesCollection = collectionsService
                .findCollectionByQuery(CustomModuleConstants.QUERY_INVENTORIES_BY_STATUS, params);

        return (draftInventoriesCollection != null & draftInventoriesCollection.size()>0);

    }
}
