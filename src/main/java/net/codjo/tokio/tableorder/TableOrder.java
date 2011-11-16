package net.codjo.tokio.tableorder;
import java.util.List;
public interface TableOrder {

    List<String> buildTablesDeleteOrder();


    List<String> buildTablesInsertOrder();
}
