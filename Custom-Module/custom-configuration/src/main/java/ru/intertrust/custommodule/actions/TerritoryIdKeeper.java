package ru.intertrust.custommodule.actions;

import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityChecker;
import ru.intertrust.cm.core.gui.api.server.action.ActionVisibilityContext;
import ru.intertrust.cm.core.gui.model.ComponentName;

@ComponentName("territory.id.keeper")
public class TerritoryIdKeeper implements ActionVisibilityChecker {
    private static Id terrId;

    public static Id getTerrId() {
        return terrId;
    }

    public static void setTerrId(Id terrId) {
        TerritoryIdKeeper.terrId = terrId;
    }

    @Override
    public boolean isVisible(ActionVisibilityContext context) {
        DomainObject territoryObj = context.getDomainObject();
        if (territoryObj != null)
            setTerrId(territoryObj.getId());
        return true;
    }
}
