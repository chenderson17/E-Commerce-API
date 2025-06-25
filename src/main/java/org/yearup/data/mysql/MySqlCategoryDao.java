package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try(Connection connection = getConnection()){
            //prepared statement
            PreparedStatement statement = connection.prepareStatement(sql);
            //results
            ResultSet row = statement.executeQuery();
            while(row.next()){
                Category category = mapRow(row);
                categories.add(category);
            }

        }
        catch (Exception error){
            throw new RuntimeException(error);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        Category category = null;
        String sql = "SELECT * FROM categories" + " WHERE category_id = ?";
        try(Connection connection = getConnection()){
            //prepared statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,categoryId);
            ResultSet row = statement.executeQuery();
            while(row.next()){
                return category = mapRow(row);
            }
        }
        catch (Exception error){
            throw new RuntimeException();
        }

        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String sql = "INSERT INTO categories(category_id,name,description)" + " VALUES(?,?,?)";
        try(Connection connection = getConnection()){
            //prepared statement
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1,category.getCategoryId());
            statement.setString(2,category.getName());
            statement.setString(3,category.getDescription());
            int row = statement.executeUpdate();
            if(row > 0){
               ResultSet genereatedKeys = statement.getGeneratedKeys();
               if(genereatedKeys.next()){
                   int catId = genereatedKeys.getInt(1);
                   return  getById(catId);
               }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?" + ", description = ?" + " WHERE category_id = ?";
        // update category
        try(Connection connection = getConnection()){
        PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3,categoryId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String sql = "DELETE FROM categories" + " WHERE category_id = ?";
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,categoryId);
            statement.executeUpdate();
        }
        catch (Exception error){
            throw new RuntimeException();

        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
