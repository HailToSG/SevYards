package ru.intertrust.custommodule.actions.widgethandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.business.api.dto.Value;
import ru.intertrust.cm.core.business.api.dto.LongValue;
import ru.intertrust.cm.core.dao.api.CurrentUserAccessor;
import ru.intertrust.cm.core.gui.api.server.widget.WidgetContext;
import ru.intertrust.cm.core.gui.impl.server.widget.IntegerBoxHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.widget.IntegerBoxState;
import ru.intertrust.cm.core.gui.model.form.widget.WidgetState;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.List;

@ComponentName("objects.area.handler")
public class ObjectsAreaIntegerBoxHandler extends IntegerBoxHandler {
    private static Id invId;

    @Autowired
    private CurrentUserAccessor currentUserAccessor;

    @Autowired
    private CrudService service;

    @Override
    public IntegerBoxState getInitialState(WidgetContext context) {
        invId = context.getFormObjects().getRootDomainObject().getId();
        final IntegerBoxState initialState = super.getInitialState(context);
        LongValue summaryValue = (LongValue) getValue(initialState);

        if (summaryValue != null)
            initialState.setValue(summaryValue.get());

        return initialState;
    }

    @Override
    public Value getValue(WidgetState state) {
        state.setEditable(true);
        int summa;
        List<DomainObject> linkedResidential, linkedPermanent, linkedTemporary;

        if (invId != null) {
            Id terrId = CustomModuleConstants.getTerrReferenceMap()
                    .get(currentUserAccessor.getCurrentUserId());
            Long terrType = service.find(terrId).getLong("territory_type");

            if (terrType == 0 || terrType == 5) {
                linkedResidential = service.findLinkedDomainObjects
                        (invId, "build_residential", "inventory_id");
                linkedPermanent = service.findLinkedDomainObjects
                        (invId, "build_temporary", "inventory_id");
                linkedTemporary = service.findLinkedDomainObjects
                        (invId, "build_permanent", "inventory_id");

                summa = getSummaryArea(linkedResidential)
                        + getSummaryArea(linkedPermanent)
                        + getSummaryArea(linkedTemporary);

                return new LongValue(summa);
            }
        }
        return new LongValue(((IntegerBoxState) state).getNumber());
    }

    private int getSummaryArea(List<DomainObject> buildingsList) {
        int summaryBuildingsArea = 0;

        if (!buildingsList.isEmpty())
            for (DomainObject obj : buildingsList)
                summaryBuildingsArea += obj.getLong("area");

        return summaryBuildingsArea;
    }
}

