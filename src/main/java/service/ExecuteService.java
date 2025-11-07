package service;

import connections.CassandraPool;
import connections.MongoPool;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Iterator;

public class ExecuteService {

    /**
     * Executes the given query text against the chosen database.
     * dbType: "MongoDB" or "Cassandra"
     * For MongoDB:
     *  - "find <collection> <filterJson?>" -> returns up to 100 matching documents
     *  - otherwise treats the whole text as a JSON command and calls runCommand
     * For Cassandra:
     *  - treats the text as a CQL statement and executes it
     */
    public String execute(String dbType, String queryText) {
        if (dbType == null || queryText == null) {
            return "Invalid input";
        }
        String q = queryText.trim();
        try {
            if ("MongoDB".equalsIgnoreCase(dbType)) {
                MongoDatabase db = MongoPool.getInstance().getConnection();

                    String[] parts = q.split("\\s+", 3);
                    if (parts.length < 2) {
                        return "Modo de uso: find <collection> <filter>";
                    }
                    String collectionName = parts[1];
                    String filterJson = parts.length >= 3 ? parts[2] : "{}";
                    Document filter = Document.parse(filterJson);
                    MongoCollection<Document> col = db.getCollection(collectionName);

                    FindIterable<Document> it = col.find(filter).limit(100);
                    StringBuilder sb = new StringBuilder();
                    int count = 0;
                    for (Document doc : it) {
                        sb.append(doc.toJson()).append("\n");
                        count++;
                    }
                    if (count == 0) {
                        return "(no documents)";
                    }

                    return sb.toString();

            } else if ("Cassandra".equalsIgnoreCase(dbType)) {
                CqlSession session = CassandraPool.getInstance().getSession();
                ResultSet rs = session.execute(q);
                StringBuilder sb = new StringBuilder();
                ColumnDefinitions cols = rs.getColumnDefinitions();
                // header
                for (int i = 0; i < cols.size(); i++) {
                    if (i > 0) sb.append(" | ");
                    sb.append(cols.get(i).getName().asInternal());
                }
                sb.append("\n");
                // rows
                for (Row row : rs) {
                    for (int i = 0; i < cols.size(); i++) {
                        if (i > 0) sb.append(" | ");
                        Object val = null;
                        try {
                            val = row.getObject(i);
                        } catch (Exception ex) {
                            val = "(unreadable)";
                        }
                        sb.append(val != null ? val.toString() : "null");
                    }
                    sb.append("\n");
                }
                return sb.toString();
            } else {
                return "Unknown database type: " + dbType;
            }
        } catch (Exception e) {
            return "Error executing query: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}
