package com.csrobot.course.mybatis;/*
package com.csrobot.course.common.mybatis;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.sql.Connection;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TenantFilterInterceptor implements Interceptor, InitializingBean {
  private final static String BOUND_SQL_NAME = "delegate.boundSql.sql";
  private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
  private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
  private final static ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();


  @Resource
  private TenantFilterInterceptorConfig config;

  private SqlConditionHelper conditionHelper;


  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

//    StatementHandler statementHandler = realTarget(invocation.getTarget());
    BoundSql boundSql = statementHandler.getBoundSql();
    String newSql = boundSql.getSql();
    // 设置数据状态过滤条件
    if (!DisableStateHolder.get()) {
      newSql = addDataStatusCondition(newSql, SQLBinaryOperator.LessThanOrGreater);
    }

    // 设置租户ID过滤条件
    if (!DisableTenantHolder.get()) {
      newSql = addTenantCondition(newSql, ContextUtils.currentTenantId(), SQLBinaryOperator.Equality);
    }
    //全局操作对象
    MetaObject metaObject = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
        DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
    //把新sql设置到boundSql
    metaObject.setValue(BOUND_SQL_NAME, newSql);
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
  }


  */
/**
   * 给sql语句where添加租户id过滤条件
   *
   * @param sql      要添加过滤条件的sql语句
   * @param tenantId 当前的租户id
   * @return 添加条件后的sql语句
   *//*

  private String addTenantCondition(String sql, Optional<Id> tenantId, SQLBinaryOperator operator) {
    if (StringUtils.isBlank(sql) || StringUtils.isBlank(config.getTenantIdField())) {
      return sql;
    }
    List<SQLStatement> statementList = SQLUtils.parseStatements(sql, config.getDialect());
    if (statementList == null || statementList.size() == 0) {
      return sql;
    }
    if (!tenantId.isPresent()) {
      return sql;
    }
    SQLStatement sqlStatement = statementList.get(0);
    conditionHelper.addStatementCondition(sqlStatement, config.getTenantIdField(), String.valueOf(tenantId.get().getValue()), operator);
    return SQLUtils.toSQLString(statementList, DbType.valueOf(config.getDialect()));
  }


  */
/**
   * 给sql语句where添加数据状态过滤条件
   *
   * @param sql      要添加过滤条件的sql语句
   * @param operator 条件的逻辑（AND，OR)
   * @return 添加条件后的sql语句
   *//*

  private String addDataStatusCondition(String sql, SQLBinaryOperator operator) {
    if (StringUtils.isBlank(sql) || StringUtils.isBlank("state")) {
      return sql;
    }
    List<SQLStatement> statementList = SQLUtils.parseStatements(sql, config.getDialect());
    if (statementList == null || statementList.size() == 0) {
      return sql;
    }
    SQLStatement sqlStatement = statementList.get(0);
    conditionHelper.addStatementCondition(sqlStatement, "state", String.valueOf(DataStatus.deleted.realVal()), operator);
    return SQLUtils.toSQLString(statementList, DbType.valueOf(config.getDialect()));
  }

  @Override
  public void afterPropertiesSet() {
    */
/**
     * 多租户条件字段决策器
     *//*

    ITableFieldConditionDecision conditionDecision = new ITableFieldConditionDecision() {
      @Override
      public boolean isAllowNullValue() {
        return false;
      }

      @Override
      public boolean adjudge(String tableName, String fieldName) {

        if (config.getTenantIdField().equals(fieldName)) {

          if (config.getFilterTenantTables().contains(tableName)) {
            BizException.assertTrue(BizErrors.TENANT_ID_EMPTY_ERR, "租户ID不能为空", ContextUtils.currentTenantId().isPresent());
            return true;
          }
          return false;
        }
        return config.getFilterStateTables().contains(tableName);
      }
    };
    this.conditionHelper = new SqlConditionHelper(conditionDecision);
  }
}
*/
