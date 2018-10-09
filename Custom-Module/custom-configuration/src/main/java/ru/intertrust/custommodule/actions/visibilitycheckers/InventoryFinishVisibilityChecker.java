package ru.intertrust.custommodule.actions.visibilitycheckers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.custommodule.actions.actionhandlers.StatusSetter;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.finish.visibility.checker")
public class InventoryFinishVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        StatusSetter statusSetter = new StatusSetter();
        DomainObject domainObject = context.getDomainObject();
        Id actualStatus = domainObject.getStatus();

        if (!domainObject.isNew() && actualStatus != null) {
            String actualStatusName = statusSetter.getQueryStatusById(actualStatus, collectionsService);
            if (actualStatusName.equals(CustomModuleConstants.STATUS_DRAFT)
                    && domainObject.getBoolean(CustomModuleConstants.IS_BUILDINGS_FILLED_FIELD)
                    && domainObject.getBoolean(CustomModuleConstants.IS_RIGHTHOLDERS_FILLED_FIELD)
                    && domainObject.getBoolean(CustomModuleConstants.IS_ELEMENTS_FILLED_FIELD)
                    && !domainObject.getValue("end_time").isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
