package ru.intertrust.custommodule.actions.visibilitycheckers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.custommodule.actions.actionhandlers.StatusSetter;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

@ComponentName("object.save.remove.visibility.checker")
public class ObjectSafeRemoveVisibilityChecker implements ActionVisibilityChecker {

    @Autowired
    private CollectionsService collectionsService;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        StatusSetter statusSetter = new StatusSetter();
        DomainObject dObj = context.getDomainObject();

        if (dObj != null) {
            String actualStatus;
            Id status = dObj.getStatus();
            if (status != null) {
                actualStatus = statusSetter.getQueryStatusById(status, collectionsService);
                if (!actualStatus.equals(CustomModuleConstants.STATUS_DELETED))
                    return true;
            }
        }

        return false;
    }
}
