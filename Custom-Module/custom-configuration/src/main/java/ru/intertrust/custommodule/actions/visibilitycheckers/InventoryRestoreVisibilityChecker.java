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

@ComponentName("inventory.restore.visibility.checker")
public class InventoryRestoreVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        StatusSetter statusSetter = new StatusSetter();
        Id statusID = context.getDomainObject().getStatus();
        if (context.getDomainObject() != null && statusID != null) {
            return statusSetter.getQueryStatusById(statusID, collectionsService)
                    .equals(CustomModuleConstants.STATUS_FINISHED);
        }
        return false;

    }
}
