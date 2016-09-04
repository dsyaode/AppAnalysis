package com.china.ds.appanalysis.db;



import android.annotation.TargetApi;
import android.os.Build;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/8/5.
 */
public final class DBAccessor {

    @SuppressWarnings("unchecked")
    public static <T> Dao.CreateOrUpdateStatus createOrUpdate(T t) {
        Dao.CreateOrUpdateStatus result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(t.getClass());
            result = dao.createOrUpdate(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return 大于零 保存成功
     */
    @SuppressWarnings("unchecked")
    public static <T> int saveObject(T t) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(t.getClass());
            result = dao.create(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return 大于零 保存成功
     */
    @SuppressWarnings("unchecked")
    public static <T> int saveListObject(List<T> t) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(t.get(0).getClass());
            for(T sub : t) {
                result += dao.create(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return 大于零 更新成功
     */
    @SuppressWarnings("unchecked")
    public static <T> int updateObject(T t) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(t.getClass());
            result = dao.update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return 大于零 更新成功
     */
    @SuppressWarnings("unchecked")
    public static <T> int updateListObject(List<T> t) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(t.getClass());
            for(T sub : t) {
                result = dao.update(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据特定条件更新特定字段
     * @param <T>
     *
     * @param updateValue
     * @param updateValue
     * @param columnName where字段
     * @param value where值
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <T> int update(Class<T> clazz, String updateKey, Object updateValue, String columnName, Object value) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.where().eq(columnName, value);
            updateBuilder.updateColumnValue(updateKey, updateValue);
            return updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据一个或多个条件更新特定字段
     * @param <T>
     *
     * @param conditionMap
     * @param updateValue where字段
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <T> int update(Class<T> clazz, Map<String, Object> conditionMap, String updateKey, Object updateValue) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            Set<String> keySet = conditionMap.keySet();
            int i = 0;
            Where<T, Integer> where = updateBuilder.where();
            for(String key : keySet){
                ++i;
                where.eq(key, conditionMap.get(key));
                if(i < keySet.size()){
                    where.and();
                }
            }
            updateBuilder.updateColumnValue(updateKey, updateValue);
            return updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据特定条件更新一个或多个字段值
     * @param clazz
     * @param columnName
     * @param value
     * @param map 需要更新的字段键值
     * @return
     */
    public static <T> int update(Class<T> clazz, String columnName, Object value, Map<String, Object> map) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.where().eq(columnName, value);

            Set<String> keySet = map.keySet();
            for(String key : keySet){
                updateBuilder.updateColumnValue(key, map.get(key));
            }

            return updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据一个或多个条件更新一个或多个字段值
     * @param <T>
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <T> int update(Class<T> clazz, Map<String, Object> conditionMap, Map<String, Object> updateMap) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            Set<String> keySet = conditionMap.keySet();
            Where<T, Integer> where = updateBuilder.where();
            int i = 0;
            for(String key : keySet){
                ++i;
                where.eq(key, conditionMap.get(key));
                if(i < keySet.size()){
                    where.and();
                }
            }
            Set<String> keySet2 = updateMap.keySet();
            for(String key : keySet2){
                updateBuilder.updateColumnValue(key, updateMap.get(key));
            }

            return updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static <T> int update(Class<T> clazz, String statement, String... arguments) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            return dao.updateRaw(statement, arguments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static <T> int delete(Class<T> clazz, String columnName, Object value) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(columnName, value);
            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return 大于零 删除成功
     */
    @SuppressWarnings("unchecked")
    public static <T> int deleteObject(T t) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance()
                    .createDao(t.getClass());
            result = dao.delete(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 处理逻辑：当数据量小于等于500时，直接全部删除；当大于500时，分批次删除，每次删除500条直至全部删除。<br/>
     * 当数据过多时(测试是999条及小于999时可删除，超过1000条时出错)，会出现错误。<br/>
     * bug:http://sourceforge.net/p/ormlite/bugs/157/
     * @param <T>
     * @param datas
     * @param clazz
     * @return 大于零 删除成功
     *
     */
    public static <T> int delete(Collection<T> datas, Class<T> clazz)
    {
        int result = 0;
        if(datas == null || datas.size() == 0) {
            return result;
        }
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            if(datas.size() <= 500){
                result = dao.delete(datas);
            }else{
                Collection<T> list = new ArrayList<T>();
                for(T obj : datas){
                    list.add(obj);
                    if(list.size()%500 == 0){
                        result += dao.delete(list);
                        list.clear();
                    }
                }
                if(!list.isEmpty()){
                    result += dao.delete(list);
                    list.clear();
                    list = null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> int deleteALL(Class<T> clazz, Where<T, Integer> where) {
        int result = 0;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.setWhere(where);
            result = deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 取得where语句 between
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> Where<T, Integer> getWhere(Class<T> clazz, String columnName, long low, long high)
    {
        Where<T, Integer> where = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
//			DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
            QueryBuilder<T, Integer> deleteBuilder = dao.queryBuilder();
            where = deleteBuilder.where().between(columnName, low, high);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return where;
    }

    /**
     * 取得where语句
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> Where<T, Integer> getWhere(Class<T> clazz)
    {
        Where<T, Integer> where = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
//			DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
            QueryBuilder<T, Integer> deleteBuilder = dao.queryBuilder();
            where = deleteBuilder.where();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return where;
    }

    /**
     * 取得query where语句
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> Where<T, Integer> getQueryWhere(Class<T> clazz){
        Where<T, Integer> where = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            where = queryBuilder.where();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return where;
    }

    /**
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> List<T> queryAll(Class<T> clazz) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance()
                    .createDao(clazz);
            result = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param clazz
     * @param orderColumnName 排序字段
     * @param ascending 升序/降序    true-升序；false-降序
     * @return
     */
    public static <T> List<T> queryAll(Class<T> clazz, String[] orderColumnName, boolean[] ascending) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance()
                    .createDao(clazz);

            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            if(orderColumnName != null && ascending != null && orderColumnName.length > 0 && orderColumnName.length == ascending.length){
                int i = 0;
                for(String columnName : orderColumnName){
                    queryBuilder.orderBy(columnName, ascending[i]);
                    i++;
                }
            }

            result = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     *  Query for the items in the object table that match a simple where with a single field = value type of WHERE clause.
     * @param clazz
     * @param fieldName
     * @param value
     * @return
     */
    public static <T> List<T> queryForEq(Class<T> clazz, String fieldName, Object value) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance()
                    .createDao(clazz);
            result = dao.queryForEq(fieldName, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> queryForNotEq(Class<T> clazz, List< Map.Entry<String, Object> > conditionMap) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);

            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            if(conditionMap != null){
//				Set<String> keySet = conditionMap.keySet();
                Where<T, Integer> where = queryBuilder.where();
                int i = 0;
                for(Map.Entry<String, Object> entry : conditionMap){
                    ++i;
                    where.ne(entry.getKey(), entry.getValue());
                    if( i < conditionMap.size()){
                        where.and();
                    }
                }
            }

            result = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  Query for the rows in the database that matches all of the field to value entries from the map passed in. If you are worried about SQL quote escaping, you should use queryForFieldValuesArgs(Map).
     * @param clazz
     * @param fieldValues
     * @return
     */
    public static <T> List<T> queryForFieldValues(Class<T> clazz, Map<String, Object> fieldValues) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance()
                    .createDao(clazz);
            result = dao.queryForFieldValues(fieldValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static <T> List<T> queryForFieldValuesArgs(Class<T> clazz, Map<String, Object> fieldValues) {
        List<T> result = null;
        try {
            Dao<T, Integer> dao = DBHelper.getInstance()
                    .createDao(clazz);
            result = dao.queryForFieldValuesArgs(fieldValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param clazz
     * @param fieldValues
     * @return
     */
    public static <T> List<T> queryAll(Class<T> clazz,
                                       Map<String, Object> fieldValues) {

        return queryForFieldValuesArgs(clazz, fieldValues);
		/*
		List<T> result = null;
		try {
			Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
			result = dao.queryForFieldValuesArgs(fieldValues);
		} catch (SQLException e) {
		}
		return result;
		*/
    }

    @SuppressWarnings("unchecked")
    public <T> boolean executeSql(Class<T> c, String sql) {
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(c.getClass());
            dao.executeRaw(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param clazz
     * @param id
     * @return The object that has the ID field which equals id or null if no matches.
     */
    public static <T> T queryForId(Class<T> clazz, int id){
        T result = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);
            result = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param clazz
     * @param rawRowMapper
     * @param query
     * @param arguments
     * @return
     */
    public static <UO> List<UO> queryRaw(Class clazz, RawRowMapper<UO> rawRowMapper, String query, String... arguments) {
        List<UO> result = null;
        try {
            Dao dao = DBHelper.getInstance().createDao(clazz);
            GenericRawResults<UO> gr = dao.queryRaw(query, rawRowMapper, arguments);
            result = gr.getResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    public static <T> List<T> queryForFieldLike(Class<T> clazz, String field, String value){
        List<T> list = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);

            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().like(field, value);

            list = dao.query(queryBuilder.prepare());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @param clazz
     * @param orderField
     * @return
     */
    public static <T> T queryForFirst(Class<T> clazz, String orderField){
        return queryForFirst(clazz, null, orderField);
    }




    /**
     *
     * @param clazz
     * @param conditionMap
     * @param orderField
     * @return
     */
    public static <T> T queryForFirst(Class<T> clazz, Map<String, Object> conditionMap, String orderField){
        T obj = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);

            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            if(conditionMap != null){
                Set<String> keySet = conditionMap.keySet();
                Where<T, Integer> where = queryBuilder.where();
                int i = 0;
                for(String key : keySet){
                    ++i;
                    where.eq(key, conditionMap.get(key));
                    if(i < keySet.size()){
                        where.and();
                    }
                }
            }
            //降序
            queryBuilder.orderBy(orderField, false);

            obj = dao.queryForFirst(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     *
     * @param clazz
     * @param conditionMap
     * @param orderField
     * @param ascending true-升序，false-降序
     * @return
     */
    public static <T> T queryForFirst(Class<T> clazz, Map<String, Object> conditionMap, String orderField, boolean ascending){
        T obj = null;
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DBHelper.getInstance().createDao(clazz);

            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            if(conditionMap != null){
                Set<String> keySet = conditionMap.keySet();
                Where<T, Integer> where = queryBuilder.where();
                int i = 0;
                for(String key : keySet){
                    ++i;
                    where.eq(key, conditionMap.get(key));
                    if(i < keySet.size()){
                        where.and();
                    }
                }
            }
            queryBuilder.orderBy(orderField, ascending);

            obj = dao.queryForFirst(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
