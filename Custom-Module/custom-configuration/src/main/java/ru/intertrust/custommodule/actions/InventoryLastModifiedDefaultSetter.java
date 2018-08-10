package ru.intertrust.custommodule.actions;

import ru.intertrust.cm.core.business.api.dto.DateTimeValue;
import ru.intertrust.cm.core.business.api.dto.Value;
import ru.intertrust.cm.core.gui.api.server.ComponentHandler;
import ru.intertrust.cm.core.gui.api.server.widget.FormDefaultValueSetter;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.form.FieldPath;
import ru.intertrust.cm.core.gui.model.form.FormObjects;
import ru.intertrust.cm.core.gui.model.form.FormState;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ComponentName("inventory.form.last.modified.default.value.setter")
public class InventoryLastModifiedDefaultSetter implements ComponentHandler, FormDefaultValueSetter {

    FileWriter fileWriter;

    private static final String  DATE_END_FIELD= "end_time";


    @Override
    public Value[] getDefaultValues(FormObjects formObjects, FieldPath fieldPath) {
        return new Value[0];
    }

    @Override
    public Value getDefaultValue(FormObjects formObjects, FieldPath fieldPath) {
        return null;
    }

    @Override
    public Value[] getDefaultValues(FormState formState, FieldPath fieldPath) {
        return new Value[0];
    }

    @Override
    public Value getDefaultValue(FormState formState, FieldPath fieldPath) {
        List<String> fieldList = new ArrayList<>();
        String fieldPathValue = fieldPath.getPath();


        if (fieldPathValue.equals(DATE_END_FIELD)){
             return new DateTimeValue(new Date());
        }
        else return null;

    }

}