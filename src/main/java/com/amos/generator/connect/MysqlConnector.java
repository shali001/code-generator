package com.amos.generator.connect;

import com.amos.generator.util.PropertiesUtils;

import java.sql.*;
import java.util.*;

public class MysqlConnector implements Connector {


    public Map<String, String> mapColumnNameType(String tableName) {
        Map<String, String> colMap = new LinkedHashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData meta = getDatabaseMetaData(connection);
            ResultSet colRet = meta.getColumns(null, "%", tableName, "%");
            while (colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                int digits = colRet.getInt("DECIMAL_DIGITS");
                int dataType = colRet.getInt("DATA_TYPE");
                String columnType = getDataType(dataType, digits);
                colMap.put(columnName, columnType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return colMap;
    }

    public Map<String, String> mapColumnRemark(String tableName) {
        Map<String, String> colMap = new LinkedHashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData meta = getDatabaseMetaData(connection);
            ResultSet colRet = meta.getColumns(null, "%", tableName, "%");
            while (colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                String columnRemark = colRet.getString("REMARKS");
                String isNull=colRet.getString("IS_NULLABLE");
                colMap.put(columnName, columnRemark);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return colMap;
    }

    @Override
    public Map<String, String> mapNull(String tableName) {
        Map<String, String> colMap = new LinkedHashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData meta = getDatabaseMetaData(connection);
            ResultSet colRet = meta.getColumns(null, "%", tableName, "%");
            while (colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                String columnRemark = colRet.getString("IS_NULLABLE");
                colMap.put(columnName, columnRemark);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return colMap;
    }

    public List<String> listAllIndex(String tableName) {
        List<String> indexes = new ArrayList();
        Connection connection = null;
        try {
            connection = getConnection();
            ResultSet resultSet = getDatabaseMetaData(connection).getIndexInfo(null, null, tableName, false, true);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                indexes.add(indexName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return indexes;
    }

    public Map<String, String> getPrimaryKey(String tableName) {
        Map<String, String> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            ResultSet pkRSet = getDatabaseMetaData(connection).getPrimaryKeys(null, null, tableName);
            while (pkRSet.next()) {
                String primaryKey = pkRSet.getString("COLUMN_NAME");
                String primaryKeyType = mapColumnNameType(pkRSet.getString("TABLE_NAME")).get(primaryKey);
                map.put("primaryKey", primaryKey);
                map.put("primaryKeyType", primaryKeyType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return map;
    }

    @Override
    public List<String> listTablesByTablePrefix(String tablePrefix) {
        List<String> tableList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(connection);
            String[] tableType = {"TABLE"};
            ResultSet rs = databaseMetaData.getTables(null, null, "", tableType);
            while (rs.next()) {
                String tableName = rs.getString(3);
                if (tableName.toLowerCase().startsWith(tablePrefix.toLowerCase())) {
                    tableList.add(tableName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return tableList;
    }

    /**
     * translate database type into java type
     *
     * @param type
     * @return
     */
    private String getDataType(int type, int digits) {
        String dataType;
        switch (type) {
            case Types.VARCHAR:  //12
                dataType = "String";
                break;
            case Types.INTEGER:    //4
                dataType = "Integer";
                break;
            case Types.SMALLINT:    //4
                dataType = "Integer";
                break;
            case Types.TINYINT:    //4
                dataType = "Integer";
                break;
            case Types.BIT:    //-7
                dataType = "Integer";
                break;
            case Types.LONGVARCHAR: //-1
                dataType = "String";
                break;
            case Types.BIGINT: //-5
                dataType = "Long";
                break;
            case Types.DOUBLE: //8
                dataType = getPrecisionType();
                break;
            case Types.REAL: //7
                dataType = getPrecisionType();
                break;
            case Types.FLOAT: //6
                dataType = getPrecisionType();
                break;
            case Types.DECIMAL:    //3
                dataType = "BigDecimal";
                break;
            case Types.NUMERIC: //2
                switch (digits) {
                    case 0:
                        dataType = "Integer";
                        break;
                    default:
                        dataType = getPrecisionType();
                }
                break;
            case Types.TIME:  //91
                dataType = "Date";
                break;
            case Types.DATE:  //91
                dataType = "Date";
                break;
            case Types.TIMESTAMP: //93
                dataType = "Date";
                break;
            default:
                dataType = "String";
        }
        return dataType;
    }

    private String getPrecisionType() {
        String dataType;
        if ("high".equals(PropertiesUtils.getString("generator.precision"))) {
            dataType = "BigDecimal";
        } else {
            dataType = "Double";
        }
        return dataType;
    }

    private Connection getConnection() {
        Connection connection;
        try {
            String driverClassName = PropertiesUtils.getString("jdbc.driverClassName");
            String url = PropertiesUtils.getString("jdbc.url");
            String userName = PropertiesUtils.getString("jdbc.username");
            String password = PropertiesUtils.getString("jdbc.password");
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private DatabaseMetaData getDatabaseMetaData(Connection connection) {
        DatabaseMetaData databaseMetaData;
        try {
            databaseMetaData = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return databaseMetaData;
    }
}
