package net.codjo.tokio.tableorder;
import net.codjo.tokio.model.DataSet;
import java.sql.Connection;
public interface TableOrderBuilder {

    TableOrder get(Connection connection, DataSet dataSet);
}
