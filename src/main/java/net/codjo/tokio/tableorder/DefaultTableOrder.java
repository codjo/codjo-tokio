package net.codjo.tokio.tableorder;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.Relationship;
import net.codjo.tokio.model.DataSet;
import net.codjo.tokio.model.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class DefaultTableOrder implements TableOrder {
    private final DatabaseFactory databaseFactory = new DatabaseFactory();
    private final DatabaseHelper databaseHelper = databaseFactory.createDatabaseHelper();
    private final Connection connection;
    private final DataSet dataSet;
    private Relationship relationShip;


    public DefaultTableOrder(Connection connection, DataSet dataSet) {
        this.connection = connection;
        this.dataSet = dataSet;
    }


    public List<String> buildTablesDeleteOrder() {
        try {
            return databaseHelper
                  .buildTablesOrder(connection, getRelationShip().getSonToFather());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> buildTablesInsertOrder() {
        List<String> allTableName = new ArrayList<String>();
        for (Iterator i = dataSet.tables(); i.hasNext();) {
            Table table = (Table)i.next();
            allTableName.add(table.getName());
        }

        try {
            return databaseHelper
                  .buildTablesOrder(connection, getRelationShip().getFatherToSon(), allTableName);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    protected Relationship getRelationShip() {
        if (relationShip == null) {
            try {
                relationShip = databaseFactory.createRelationShip(connection);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return relationShip;
    }
}
