package net.codjo.tokio.tableorder;
import net.codjo.tokio.model.DataSet;
import java.sql.Connection;
public class DefaultTableOrderBuilder implements TableOrderBuilder {

    public TableOrder get(Connection connection, DataSet dataSet) {
        return new DefaultTableOrder(connection, dataSet);
    }
}
