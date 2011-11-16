package net.codjo.tokio.model;
public class ValueSplitter {
    private static final String SPECIAL_CHAR = "@";


    private ValueSplitter() {
    }


    public static ObjectValueList split(String toSplit) {
        int nextSpecialCharIndex;
        if (toSplit == null) {
            return new ObjectValueList(new NullValue());
        }
        else if ("".equals(toSplit)) {
            return new ObjectValueList();
        }
        else {
            ObjectValue objectValue;

            if (toSplit.startsWith(SPECIAL_CHAR)) {
                nextSpecialCharIndex = toSplit.indexOf(SPECIAL_CHAR, 1);
                if (nextSpecialCharIndex == -1) {
                    return new ObjectValueList(new StringValue(toSplit));
                }
                else {
                    objectValue = new VariableValue(toSplit.substring(1, nextSpecialCharIndex));
                    nextSpecialCharIndex++;
                }
            }
            else {
                nextSpecialCharIndex = toSplit.indexOf(SPECIAL_CHAR, 0);
                if (nextSpecialCharIndex == -1
                    || toSplit.indexOf(SPECIAL_CHAR, nextSpecialCharIndex + 1) == -1) {
                    return new ObjectValueList(new StringValue(toSplit));
                }
                else {
                    objectValue = new StringValue(toSplit.substring(0, nextSpecialCharIndex));
                }
            }

            return concatenate(objectValue, split(toSplit.substring(nextSpecialCharIndex)));
        }
    }


    private static ObjectValueList concatenate(ObjectValue prefix, ObjectValueList splittedValue) {
        if (splittedValue == null) {
            return new ObjectValueList(prefix);
        }
        else {
            return new ObjectValueList(prefix, splittedValue);
        }
    }
}
