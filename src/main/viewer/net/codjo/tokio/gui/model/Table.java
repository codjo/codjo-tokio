package net.codjo.tokio.gui.model;
/**
 *
 */
public class Table {
    private String name;
    private boolean isInput;
    private boolean isOutput;
    private boolean displayInput = false;
    private boolean displayOutput = false;


    public Table(String name, boolean input, boolean output) {
        this.name = name;
        isInput = input;
        isOutput = output;
    }


    public String getName() {
        return name;
    }


    public boolean isInput() {
        return isInput;
    }


    public void setInput(boolean input) {
        isInput = input;
    }


    public boolean isOutput() {
        return isOutput;
    }


    public void setOutput(boolean output) {
        isOutput = output;
    }


    public boolean isDisplayInput() {
        return displayInput;
    }


    public void setDisplayInput(boolean displayInput) {
        this.displayInput = displayInput;
    }


    public boolean isDisplayOutput() {
        return displayOutput;
    }


    public void setDisplayOutput(boolean displayOutput) {
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

        Table table = (Table)o;

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
