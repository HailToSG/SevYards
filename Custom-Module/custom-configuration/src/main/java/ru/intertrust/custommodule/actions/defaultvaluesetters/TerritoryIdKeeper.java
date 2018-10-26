package ru.intertrust.custommodule.actions.defaultvaluesetters;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

@ComponentName("territory.id.keeper")
public class TerritoryIdKeeper implements ActionVisibilityChecker {

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Override
    public boolean isVisible(ActionVisibilityContext context) {

        Id territoryObjId;
        DomainObject domainObject = context.getDomainObject();
        Id currentUserId = currentUserAccessor.getCurrentUserId();

        if (domainObject != null) {
            territoryObjId = domainObject.getId();

            CustomModuleConstants.setTerrReference(currentUserId, territoryObjId);
        }
        return true;
    }

}