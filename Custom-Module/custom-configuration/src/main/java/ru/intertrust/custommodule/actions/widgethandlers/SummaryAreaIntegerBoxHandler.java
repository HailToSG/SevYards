package ru.intertrust.custommodule.actions.widgethandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.widget.WidgetContext;
import ru.intertrust.cm.core.gui.impl.server.widget.IntegerBoxHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.widget.IntegerBoxState;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;
import ru.intertrust.custommodule.actions.defaultvaluesetters.InventoryFormDefaultValueSetter;

@ComponentName("summary.area.handler")
public class SummaryAreaIntegerBoxHandler extends IntegerBoxHandler {

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Autowired
    private CrudService service;

    @Override
    public IntegerBoxState getInitialState(WidgetContext context) {
        Id terrId = CustomModuleConstants.getTerrReferenceMap()
                .get(currentUserAccessor.getCurrentUserId());
        DomainObject terrObject = service.find(terrId);
        final IntegerBoxState initialState = super.getInitialState(context);
        Long terrType = terrObject.getLong("territory_type");
        if (!(terrType == 0  || terrType == 5))
            initialState.setForceReadOnly(true);

        return initialState;
    }
}