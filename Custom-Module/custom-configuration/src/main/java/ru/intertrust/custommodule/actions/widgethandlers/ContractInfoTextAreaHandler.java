package ru.intertrust.custommodule.actions.widgethandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.widget.WidgetContext;
import ru.intertrust.cm.core.gui.impl.server.widget.TextAreaHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.widget.TextState;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

@ComponentName("contract.info.handler")
public class ContractInfoTextAreaHandler extends TextAreaHandler {

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Autowired
    private CrudService service;

    @Override
    public TextState getInitialState(WidgetContext context) {

        DomainObject terrObject = service.find
                (CustomModuleConstants.getTerrReferenceMap()
                        .get(currentUserAccessor.getCurrentUserId()));
        final TextState initialState = super.getInitialState(context);

        Long terrType = terrObject.getLong("territory_type");
        if (!(terrType == 10 || terrType == 15))
            initialState.setForceReadOnly(true);
        return initialState;
    }
}

