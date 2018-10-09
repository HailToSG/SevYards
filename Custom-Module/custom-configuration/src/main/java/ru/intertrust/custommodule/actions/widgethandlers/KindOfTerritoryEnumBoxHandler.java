package ru.intertrust.custommodule.actions.widgethandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.config.ConfigurationExplorerImpl;
import ru.intertrust.cm.core.config.gui.form.*;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.widget.WidgetContext;
import ru.intertrust.cm.core.gui.impl.server.widget.EnumBoxHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.widget.EnumBoxState;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.List;

@ComponentName("kind.of.territory.handler")
public class KindOfTerritoryEnumBoxHandler extends EnumBoxHandler {

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Autowired
    private CrudService service;

    @Override
    public EnumBoxState getInitialState(WidgetContext context) {
        Id terrId = CustomModuleConstants.getTerrReferenceMap().get(currentUserAccessor.getCurrentUserId());
        DomainObject terrObject = service.find(terrId);
        final EnumBoxState initialState = super.getInitialState(context);

        Long terrType = terrObject.getLong("territory_type");
        if (!(terrType == 0))
            initialState.setForceReadOnly(true);

        return initialState;
    }
}

