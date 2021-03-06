/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author pedago
 */
public class DAO implements IDAO
{
    
    protected final DataSource myDataSource;

    public DAO(DataSource dataSource)
    {
        this.myDataSource = dataSource;
    }
        
    @Override
    public List<PurchaseOrder> getPurchaseOrders(Customer customer)
    {
        List<PurchaseOrder> result = new LinkedList<>();

        String sql = "SELECT * FROM PURCHASE_ORDER WHERE CUSTOMER_ID = ?";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, customer.getId());
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    PurchaseOrder purchase = new PurchaseOrder(rs.getInt("ORDER_NUM"), rs.getInt("CUSTOMER_ID"), rs.getInt("PRODUCT_ID"),
                                                    rs.getInt("QUANTITY"), rs.getDouble("SHIPPING_COST"), rs.getDate("SALES_DATE"),
                                                    rs.getDate("SHIPPING_DATE"), rs.getString("FREIGHT_COMPANY"));
                    result.add(purchase);
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    public List<PurchaseOrder> getAllPurchaseOrders()
    {
        List<PurchaseOrder> result = new LinkedList<>();

        String sql = "SELECT * FROM PURCHASE_ORDER";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    PurchaseOrder purchase = new PurchaseOrder(rs.getInt("ORDER_NUM"), rs.getInt("CUSTOMER_ID"), rs.getInt("PRODUCT_ID"),
                                                    rs.getInt("QUANTITY"), rs.getDouble("SHIPPING_COST"), rs.getDate("SALES_DATE"),
                                                    rs.getDate("SHIPPING_DATE"), rs.getString("FREIGHT_COMPANY"));
                    result.add(purchase);
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }
    
    @Override
    public boolean addPurchaseOrder(PurchaseOrder order)
    {
        String insertPurchaseOrder = "INSERT INTO PURCHASE_ORDER VALUES((SELECT MAX(ORDER_NUM) + 1 FROM PURCHASE_ORDER), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection myConnection = myDataSource.getConnection();
            PreparedStatement purchaseStatement = myConnection.prepareStatement(insertPurchaseOrder))
        {
            purchaseStatement.setInt(1, order.getCustomerId());
            purchaseStatement.setInt(2, order.getProductId());
            purchaseStatement.setInt(3, order.getQuantity());
            purchaseStatement.setDouble(4, order.getShippingCost());
            purchaseStatement.setDate(5, order.getSalesDate());
            purchaseStatement.setDate(6, order.getShippingDate());
            purchaseStatement.setString(7, order.getFreightCompany());
            
            if (purchaseStatement.executeUpdate() == 1);
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override //login = email, psw = id
    public Customer login(String login, String password)
    {
        Customer result = null;
        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql))
        {

                stmt.setInt(1, Integer.parseInt(password));
                try (ResultSet rs = stmt.executeQuery())
                {
                    if (rs.next())
                    {
                        result = new Customer(rs.getInt("CUSTOMER_ID"),
                                                rs.getString("DISCOUNT_CODE"),
                                                rs.getString("ZIP"),
                                                rs.getString("NAME"),
                                                rs.getString("ADDRESSLINE1"),
                                                rs.getString("ADDRESSLINE2"),
                                                rs.getString("CITY"),
                                                rs.getString("STATE"),
                                                rs.getString("PHONE"),
                                                rs.getString("FAX"),
                                                rs.getString("EMAIL"),
                                                rs.getInt("CREDIT_LIMIT"));
                    }
                }
        }  catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
	}
        if (result != null && !login.equals(result.getEmail()))
            return null;
        return result;
    }

    @Override
    public boolean updateCustomer(Customer newCustomerData)
    {
        String sql = "UPDATE CUSTOMER SET DISCOUNT_CODE = ?, ZIP = ?, NAME = ?, ADDRESSLINE1 = ?, ADDRESSLINE2 = ?, CITY = ?, STATE = ?, PHONE = ?, FAX = ?, EMAIL = ?, CREDIT_LIMIT = ? WHERE CUSTOMER_ID = ?";
        try (	Connection myConnection = myDataSource.getConnection();
		PreparedStatement statement = myConnection.prepareStatement(sql))
        {
            statement.setString(1, newCustomerData.getDiscountCode());
            statement.setString(2, newCustomerData.getZip());
            statement.setString(3, newCustomerData.getName());
            statement.setString(4, newCustomerData.getAddr1());
            statement.setString(5, newCustomerData.getAddr2());
            statement.setString(6, newCustomerData.getCity());
            statement.setString(7, newCustomerData.getState());
            statement.setString(8, newCustomerData.getPhone());
            statement.setString(9, newCustomerData.getFax());
            statement.setString(10, newCustomerData.getEmail());
            statement.setInt(11, newCustomerData.getCreditLimit());
            statement.setInt(12, newCustomerData.getId());
            
            if (statement.executeUpdate() == 1)
                return true;
	} catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public boolean deletePurchaseOrder(int orderNum)
    {
        String sql = "DELETE FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
	try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, orderNum);

            if (stmt.executeUpdate() == 1)
                return true;
	} catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
	}
        
        return true;
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrder order)
    {
        String sql = "UPDATE PURCHASE_ORDER SET CUSTOMER_ID = ?, PRODUCT_ID = ?, QUANTITY = ?, SHIPPING_COST = ?, SALES_DATE = ?, SHIPPING_DATE = ?, FREIGHT_COMPANY = ? WHERE ORDER_NUM = ?";
        try (	Connection myConnection = myDataSource.getConnection();
		PreparedStatement statement = myConnection.prepareStatement(sql))
        {
            statement.setInt(1, order.getCustomerId());
            statement.setInt(2, order.getProductId());
            statement.setInt(3, order.getQuantity());
            statement.setDouble(4, order.getShippingCost());
            statement.setDate(5, order.getSalesDate());
            statement.setDate(6, order.getShippingDate());
            statement.setString(7, order.getFreightCompany());
            statement.setInt(8, order.getOrderNum());
            
            if (statement.executeUpdate() == 1)
                return true;
	} catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public List<Product> getAllProducts()
    {
        List<Product> result = new LinkedList<>();

        String sql = "SELECT * FROM PRODUCT";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    Product product = new Product(rs.getInt("PRODUCT_ID"), rs.getInt("MANUFACTURER_ID"), rs.getString("PRODUCT_CODE"),
                                                    rs.getDouble("PURCHASE_COST"), rs.getInt("QUANTITY_ON_HAND"), rs.getDouble("MARKUP"),
                                                    rs.getString("AVAILABLE"), rs.getString("DESCRIPTION"));
                    result.add(product);
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    @Override
    public boolean addProduct(Product product)
    {
        String insertProduct = "INSERT INTO PRODUCT VALUES((SELECT MAX(PRODUCT_ID) + 1 FROM PRODUCT), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection myConnection = myDataSource.getConnection();
            PreparedStatement productStatement = myConnection.prepareStatement(insertProduct))
        {
            productStatement.setInt(1, product.getManufacturerId());
            productStatement.setString(2, product.getProductCode());
            productStatement.setDouble(3, product.getPurchaseCost());
            productStatement.setInt(4, product.getQuantityOnHand());
            productStatement.setDouble(5, product.getMarkup());
            productStatement.setString(6, product.isAvailable() ? "TRUE" : "FALSE");
            productStatement.setString(7, product.getDescription());
            
            if (productStatement.executeUpdate() == 1);
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public boolean deleteProduct(int productId)
    {
        String sql = "DELETE FROM PRODUCT WHERE PRODUCT_ID = ?";
	try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, productId);
            
            if (stmt.executeUpdate() == 1)
                return true;
	} catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
	}
        
        return false;
    }

    @Override
    public boolean updateProduct(Product product)
    {
        String sql = "UPDATE PRODUCT SET MANUFACTURER_ID = ?, PRODUCT_CODE = ?, PURCHASE_COST = ?, QUANTITY_ON_HAND = ?, MARKUP = ?, AVAILABLE = ?, DESCRIPTION = ? WHERE PRODUCT_ID = ?";
        try (	Connection myConnection = myDataSource.getConnection();
		PreparedStatement statement = myConnection.prepareStatement(sql))
        {
            statement.setInt(1, product.getManufacturerId());
            statement.setString(2, product.getProductCode());
            statement.setDouble(3, product.getPurchaseCost());
            statement.setInt(4, product.getQuantityOnHand());
            statement.setDouble(5, product.getMarkup());
            statement.setString(6, product.isAvailable() ? "TRUE" : "FALSE");
            statement.setString(7, product.getDescription());
            statement.setInt(8, product.getId());
            
            if (statement.executeUpdate() == 1)
                    return true;
	} catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public List<MicroMarket> getAllMicroMarkets()
    {
        List<MicroMarket> result = new LinkedList<>();

        String sql = "SELECT * FROM MICRO_MARKET";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    result.add(new MicroMarket(rs.getString("ZIP_CODE"), rs.getDouble("RADIUS"),
                                                rs.getDouble("AREA_LENGTH"), rs.getDouble("AREA_WIDTH")));
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    @Override
    public List<ProductCodeRevenue> getProductCodesRevenues(Date startDate, Date endDate)
    {
        List<ProductCodeRevenue> result = new LinkedList<>();

        String sql = "SELECT PRODUCT_CODE.DESCRIPTION, SUM(PURCHASE_ORDER.QUANTITY * PRODUCT.PURCHASE_COST) AS REVENU FROM"
                  + " PURCHASE_ORDER INNER JOIN PRODUCT USING (PRODUCT_ID)"
                  + " INNER JOIN PRODUCT_CODE ON PRODUCT.PRODUCT_CODE = PRODUCT_CODE.PROD_CODE" +
"            WHERE PURCHASE_ORDER.SALES_DATE BETWEEN ? AND ? GROUP BY (PRODUCT_CODE.DESCRIPTION)";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    result.add(new ProductCodeRevenue(rs.getString("DESCRIPTION"), rs.getDouble("REVENU")));
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    @Override
    public List<MicroMarketRevenue> getMicroMarketsRevenues(Date startDate, Date endDate)
    {
        List<MicroMarketRevenue> result = new LinkedList<>();

        String sql = "SELECT MICRO_MARKET.ZIP_CODE, SUM(PURCHASE_ORDER.QUANTITY * PRODUCT.PURCHASE_COST) AS REVENU FROM\n" +
"                            PURCHASE_ORDER INNER JOIN PRODUCT USING (PRODUCT_ID)\n" +
"                                    INNER JOIN CUSTOMER USING (CUSTOMER_ID)\n" +
"                                    INNER JOIN MICRO_MARKET ON CUSTOMER.ZIP = MICRO_MARKET.ZIP_CODE\n" +
"            WHERE PURCHASE_ORDER.SALES_DATE BETWEEN ? AND ?" +
"            GROUP BY (MICRO_MARKET.ZIP_CODE)";

        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    result.add(new MicroMarketRevenue(rs.getString("ZIP_CODE"), rs.getDouble("REVENU")));
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    @Override
    public List<CustomerRevenue> getCustomersRevenues(Date startDate, Date endDate)
    {
        List<CustomerRevenue> result = new LinkedList<>();

        String sql = "SELECT CUSTOMER.NAME, SUM(PURCHASE_ORDER.QUANTITY * PRODUCT.PURCHASE_COST) AS REVENU FROM\n" +
"                            PURCHASE_ORDER INNER JOIN PRODUCT USING (PRODUCT_ID)\n" +
"                                    INNER JOIN CUSTOMER USING (CUSTOMER_ID)\n" +
"            WHERE PURCHASE_ORDER.SALES_DATE BETWEEN ? AND ?" +
"            GROUP BY (CUSTOMER.NAME)";

        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    result.add(new CustomerRevenue(rs.getString("NAME"), rs.getDouble("REVENU")));
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;    
    }        

    @Override
    public String getProductName(int productId) {
        String result = null;
        
        String sql = "SELECT DESCRIPTION FROM PRODUCT WHERE PRODUCT_ID = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql))
        {
                stmt.setInt(1, productId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        result = rs.getString("DESCRIPTION");
                }
        }  catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
	}

        return result;
    }

    @Override
    public PurchaseOrder getPurchaseOrder(int orderNum) {
        PurchaseOrder result = null;
        
        String sql = "SELECT * FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql))
        {
                stmt.setInt(1, orderNum);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result = new PurchaseOrder(rs.getInt("ORDER_NUM"), rs.getInt("CUSTOMER_ID"), rs.getInt("PRODUCT_ID"),
                                                    rs.getInt("QUANTITY"), rs.getDouble("SHIPPING_COST"), rs.getDate("SALES_DATE"),
                                                    rs.getDate("SHIPPING_DATE"), rs.getString("FREIGHT_COMPANY"));
                    }
                }
        }  catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
	}

        return result;
    }

    @Override
    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> result = new ArrayList<>();

        String sql = "SELECT MANUFACTURER_ID, NAME FROM MANUFACTURER ORDER BY (NAME)";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    Manufacturer manufacturer = new Manufacturer(
                        rs.getInt("MANUFACTURER_ID"),
                        rs.getString("NAME")
                    );
                    
                    result.add(manufacturer);
                }
            }
        } catch (SQLException ex)
        {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    @Override
    public List<ProductCode> getAllProductCodes() {
        List<ProductCode> result = new ArrayList<>();

        String sql = "SELECT * FROM PRODUCT_CODE";
        try (Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    ProductCode productCode = new ProductCode(
                            rs.getString("PROD_CODE"),
                            rs.getString("DISCOUNT_CODE").charAt(0),
                            rs.getString("DESCRIPTION")
                    );
                    
                    result.add(productCode);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
            return null;
        }
        
        return result;
    }

    @Override
    public Product getProduct(int productId) {
        Product result = null;
        
        String sql = "SELECT * FROM PRODUCT WHERE PRODUCT_ID = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql))
        {
                stmt.setInt(1, productId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result = new Product(
                                rs.getInt("PRODUCT_ID"),
                                rs.getInt("MANUFACTURER_ID"),
                                rs.getString("PRODUCT_CODE"),
                                rs.getDouble("PURCHASE_COST"),
                                rs.getInt("QUANTITY_ON_HAND"),
                                rs.getDouble("MARKUP"),
                                rs.getString("AVAILABLE"),
                                rs.getString("DESCRIPTION")
                        );
                    }
                }
        }  catch (SQLException ex) {
            Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
	}

        return result;
    }
}
