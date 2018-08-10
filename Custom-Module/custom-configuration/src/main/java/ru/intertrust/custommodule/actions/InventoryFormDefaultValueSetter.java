package ru.intertrust.custommodule.actions;

import ru.intertrust.cm.core.business.api.dto.BooleanValue;
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


@ComponentName("inventory.form.default.value.setter")
public class InventoryFormDefaultValueSetter implements ComponentHandler, FormDefaultValueSetter {

    FileWriter fileWriter;

    private static final String IS_BUILDINGS_FILLED_FIELD_CHECKBOX= "is_buildings_filled";
    private static final String IS_RIGHTHOLDERS_FILLED_FIELD_CHECKBOX= "is_rightholders_filled";
    private static final String IS_ELEMENTS_FILLED_CHECKBOX = "is_elements_filled";
    private static final String  DATE_START_FIELD= "start_time";
    private static final String  DATE_END_FIELD= "end_time";
    private static final Boolean CHECKBOX_TRUE_VALUE = true;

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

//        {
//            try {
//                fileWriter = new FileWriter("SSSSAAAAA.txt", false);
//                fileWriter.flush();
//                fileWriter.write("fieldPathValue: " + fieldPathValue);
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        switch (fieldPathValue){
            case IS_BUILDINGS_FILLED_FIELD_CHECKBOX: return new BooleanValue(CHECKBOX_TRUE_VALUE);
            case IS_RIGHTHOLDERS_FILLED_FIELD_CHECKBOX: return new BooleanValue(CHECKBOX_TRUE_VALUE);
            case IS_ELEMENTS_FILLED_CHECKBOX: return new BooleanValue(CHECKBOX_TRUE_VALUE);
            case DATE_START_FIELD: return new DateTimeValue(new Date());
            case DATE_END_FIELD: return new DateTimeValue(new Date());
            default: return null;
        }

    }

}
