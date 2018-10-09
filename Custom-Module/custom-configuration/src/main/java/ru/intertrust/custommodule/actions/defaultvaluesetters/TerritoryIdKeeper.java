package ru.intertrust.custommodule.actions.defaultvaluesetters;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.config.ConfigurationExplorer;
import ru.intertrust.cm.core.config.ConfigurationExplorerImpl;
import ru.intertrust.cm.core.config.gui.form.FormConfig;
import ru.intertrust.cm.core.config.gui.form.TabGroupConfig;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
