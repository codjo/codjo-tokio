package net.codjo.tokio.viewer.model;

public class TableInfo {
    private String name;
    private Boolean isInput;
    private Boolean isOutput;
    private Boolean displayInput = false;
    private Boolean displayOutput = false;


    public TableInfo(String name, boolean input, boolean output) {
        this.name = name;
        isInput = input;
        isOutput = output;
    }


    public String getName() {
        return name;
    }


    public Boolean isInput() {
        return isInput;
    }


    public void setInput(Boolean input) {
        isInput = input;
    }


    public Boolean isOutput() {
        return isOutput;
    }


    public void setOutput(Boolean output) {
        isOutput = output;
    }


    public Boolean isDisplayInput() {
        return displayInput;
    }


    public void setDisplayInput(Boolean displayInput) {
        this.displayInput = displayInput;
    }


    public Boolean isDisplayOutput() {
        return displayOutput;
    }


    public void setDisplayOutput(Boolean displayOutput) {
        this.displayOutput = displayOutput;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableInfo table = (TableInfo)o;

        if (isInput != table.isInput) {
            return false;
        }
        if (isOutput != table.isOutput) {
            return false;
        }
        if (!name.equals(table.name)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isInput ? 1 : 0);
        result = 31 * result + (isOutput ? 1 : 0);
        return result;
    }
}
