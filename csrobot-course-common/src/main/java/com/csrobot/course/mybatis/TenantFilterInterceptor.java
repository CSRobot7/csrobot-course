package com.csrobot.course.mybatis;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.csrobot.course.DisableTenantHolder;
import com.csrobot.course.context.ContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TenantFilterInterceptor implements Interceptor, InitializingBean {
  private final static String BOUND_SQL_NAME = "delegate.boundSql.sql";
  private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
  private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
  private final static ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

  private SqlConditionHelper conditionHelper;

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

//    StatementHandler statementHandler = realTarget(invocation.getTarget());
    BoundSql boundSql = statementHandler.getBoundSql();
    String newSql = boundSql.getSql();

    if (!DisableTenantHolder.get()) {

    }

    newSql = addTenantCondition(newSql, SQLBinaryOperator.Equality);
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


  /**
   * 给sql语句where添加租户id过滤条件
   *
   * @param sql      要添加过滤条件的sql语句
   * @return 添加条件后的sql语句
   */
  private String addTenantCondition(String sql, SQLBinaryOperator operator) {
    if (StringUtils.isBlank(sql)) {
      return sql;
    }
    List<SQLStatement> statementList = SQLUtils.parseStatements(sql, "mysql");
    if (CollectionUtils.isEmpty(statementList)) {
      return sql;
    }

    SQLStatement sqlStatement = statementList.get(0);
    conditionHelper.addStatementCondition(sqlStatement, "tenant_id", String.valueOf(ContextUtils.currentTenantId()), operator);
    return SQLUtils.toSQLString(statementList, DbType.valueOf("mysql"));
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    ITableFieldConditionDecision conditionDecision = new ITableFieldConditionDecision() {
      @Override
      public Boolean isAllowNullValue() {
        return false;
      }

      @Override
      public Boolean adjudge(String tableName, String fieldName) {
        if ("tenant_id".equals(fieldName)) {
          return true;
        }
        return false;
        //还可以配置增加对表的过滤
      }
    };
    this.conditionHelper = new SqlConditionHelper(conditionDecision);
  }
}
