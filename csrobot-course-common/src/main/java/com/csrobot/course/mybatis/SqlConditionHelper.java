package com.csrobot.course.mybatis;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


public class SqlConditionHelper {
  private ITableFieldConditionDecision conditionDecision;

  public SqlConditionHelper(ITableFieldConditionDecision conditionDecision) {
    this.conditionDecision = conditionDecision;
  }


  /**
   * 为sql语句添加指定where条件
   * @param sqlStatement
   * @param fieldName
   * @param fieldValue
   * @param operator
   */
  public void addStatementCondition(SQLStatement sqlStatement, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (sqlStatement instanceof SQLSelectStatement) {
      SQLSelectQueryBlock queryObject = (SQLSelectQueryBlock) ((SQLSelectStatement) sqlStatement).getSelect().getQuery();
      addSelectStatementCondition(queryObject, queryObject.getFrom(), fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLUpdateStatement) {
      SQLUpdateStatement updateStatement = (SQLUpdateStatement) sqlStatement;
      addUpdateStatementCondition(updateStatement, fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLDeleteStatement) {
      SQLDeleteStatement deleteStatement = (SQLDeleteStatement) sqlStatement;
      addDeleteStatementCondition(deleteStatement, fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLInsertStatement) {
      SQLInsertStatement insertStatement = (SQLInsertStatement) sqlStatement;
      addInsertStatementCondition(insertStatement, fieldName, fieldValue, operator);
    }
  }


  /**
   * 为sql语句添加指定where条件
   *
   * @param sqlStatement
   * @param tableName
   * @param fieldName
   * @param fieldValue
   */
  public void addStatementCondition(SQLStatement sqlStatement, String tableName, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (sqlStatement instanceof SQLSelectStatement) {
      SQLSelectQueryBlock queryObject = (SQLSelectQueryBlock) ((SQLSelectStatement) sqlStatement).getSelect().getQuery();
      addSelectStatementCondition(queryObject, queryObject.getFrom(), tableName, fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLUpdateStatement) {
      SQLUpdateStatement updateStatement = (SQLUpdateStatement) sqlStatement;
      addUpdateStatementCondition(updateStatement, fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLDeleteStatement) {
      SQLDeleteStatement deleteStatement = (SQLDeleteStatement) sqlStatement;
      addDeleteStatementCondition(deleteStatement, fieldName, fieldValue, operator);
      return;
    }
    if (sqlStatement instanceof SQLInsertStatement) {
      SQLInsertStatement insertStatement = (SQLInsertStatement) sqlStatement;
      addInsertStatementCondition(insertStatement, fieldName, fieldValue, operator);
      return;
    }
  }


  /**
   * 为insert语句添加where条件
   *
   * @param insertStatement
   * @param fieldName
   * @param fieldValue
   */
  private void addInsertStatementCondition(SQLInsertStatement insertStatement, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (insertStatement != null) {
      SQLInsertInto sqlInsertInto = insertStatement;
      SQLSelect sqlSelect = sqlInsertInto.getQuery();
      if (sqlSelect != null) {
        SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) sqlSelect.getQuery();
        addSelectStatementCondition(selectQueryBlock, selectQueryBlock.getFrom(), fieldName, fieldValue, operator);
      }
    }
  }

  /**
   * 为delete语句添加where条件
   *
   * @param deleteStatement
   * @param fieldName
   * @param fieldValue
   */
  private void addDeleteStatementCondition(SQLDeleteStatement deleteStatement, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    SQLExpr where = deleteStatement.getWhere();
    //添加子查询中的where条件
    addSQLExprCondition(where, fieldName, fieldValue, operator);
    SQLExpr newCondition = newEqualityCondition(deleteStatement.getTableName().getSimpleName(),
    deleteStatement.getTableSource().getAlias(), fieldName, fieldValue, where, operator);
    deleteStatement.setWhere(newCondition);
  }


  /**
   * where中添加指定筛选条件
   *
   * @param where      源where条件
   * @param fieldName
   * @param fieldValue
   */
  private void addSQLExprCondition(SQLExpr where, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (where instanceof SQLInSubQueryExpr) {
      SQLInSubQueryExpr inWhere = (SQLInSubQueryExpr) where;
      SQLSelect subSelectObject = inWhere.getSubQuery();
      SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock) subSelectObject.getQuery();
      addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), fieldName, fieldValue, operator);
    } else if (where instanceof SQLBinaryOpExpr) {
      SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) where;
      SQLExpr left = opExpr.getLeft();
      SQLExpr right = opExpr.getRight();
      addSQLExprCondition(left, fieldName, fieldValue, operator);
      addSQLExprCondition(right, fieldName, fieldValue, operator);
    } else if (where instanceof SQLQueryExpr) {
      SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) (((SQLQueryExpr) where).getSubQuery()).getQuery();
      addSelectStatementCondition(selectQueryBlock, selectQueryBlock.getFrom(), fieldName, fieldValue, operator);
    }
  }


  /**
   * 为update语句添加where条件
   *
   * @param updateStatement
   * @param fieldName
   * @param fieldValue
   */
  private void addUpdateStatementCondition(SQLUpdateStatement updateStatement, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    SQLExpr where = updateStatement.getWhere();
    //添加子查询中的where条件
    addSQLExprCondition(where, fieldName, fieldValue, operator);
    SQLExpr newCondition = newEqualityCondition(updateStatement.getTableName().getSimpleName(),
    updateStatement.getTableSource().getAlias(), fieldName, fieldValue, where, operator);
    updateStatement.setWhere(newCondition);
  }


  /**
   * 给一个查询对象添加一个where条件
   *
   * @param queryObject
   * @param fieldName
   * @param fieldValue
   */
  private void addSelectStatementCondition(SQLSelectQueryBlock queryObject, SQLTableSource from
      , String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (StringUtils.isBlank(fieldName) || from == null || queryObject == null) {
      return;
    }
    SQLExpr originCondition = queryObject.getWhere();
    if (from instanceof SQLExprTableSource) {
      String tableName = ((SQLIdentifierExpr) ((SQLExprTableSource) from).getExpr()).getName();
      String alias = from.getAlias();
      SQLExpr newCondition = newEqualityCondition(tableName, alias, fieldName, fieldValue, originCondition, operator);
      queryObject.setWhere(newCondition);
      return;
    }
    if (from instanceof SQLJoinTableSource) {
      SQLJoinTableSource joinObject = (SQLJoinTableSource) from;
      SQLTableSource left = joinObject.getLeft();
      SQLTableSource right = joinObject.getRight();
      addSelectStatementCondition(queryObject, left, fieldName, fieldValue, operator);
      addSelectStatementCondition(queryObject, right, fieldName, fieldValue, operator);
      return;
    }
    if (from instanceof SQLSubqueryTableSource) {
      SQLSelect subSelectObject = ((SQLSubqueryTableSource) from).getSelect();
      SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock) subSelectObject.getQuery();
      addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), fieldName, fieldValue, operator);
      return;
    }
    throw new NotImplementedException("未处理的异常");
  }


  private void addSelectStatementCondition(SQLSelectQueryBlock queryObject, SQLTableSource from
      , String dataRightTableName, String fieldName, String fieldValue, SQLBinaryOperator operator) {
    if (StringUtils.isBlank(fieldName) || from == null || queryObject == null) {
      return;
    }
    SQLExpr originCondition = queryObject.getWhere();
    if (from instanceof SQLExprTableSource) {
      String tableName = ((SQLIdentifierExpr) ((SQLExprTableSource) from).getExpr()).getName();
      if (!Objects.equals(tableName, dataRightTableName)) {
        return;
      }

      String alias = from.getAlias();
      SQLExpr newCondition = newEqualityCondition(tableName, alias, fieldName, fieldValue, originCondition, operator);
      queryObject.setWhere(newCondition);
      return;
    }

    if (from instanceof SQLJoinTableSource) {
      SQLJoinTableSource joinObject = (SQLJoinTableSource) from;
      SQLTableSource left = joinObject.getLeft();
      SQLTableSource right = joinObject.getRight();
      addSelectStatementCondition(queryObject, left, dataRightTableName, fieldName, fieldValue, operator);
      addSelectStatementCondition(queryObject, right, dataRightTableName, fieldName, fieldValue, operator);
      return;
    }

    if (from instanceof SQLSubqueryTableSource) {
      SQLSelect subSelectObject = ((SQLSubqueryTableSource) from).getSelect();
      SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock) subSelectObject.getQuery();
      addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), dataRightTableName, fieldName, fieldValue, operator);
      return;
    }
    throw new NotImplementedException("未处理的异常");
  }


  /**
   * 根据原来的condition创建一个新的condition
   *
   * @param tableName       表名称
   * @param tableAlias      表别名
   * @param fieldName
   * @param fieldValue
   * @param originCondition
   * @return
   */
  private SQLExpr newEqualityCondition(String tableName, String tableAlias, String fieldName, String fieldValue
      , SQLExpr originCondition, SQLBinaryOperator operator) {

    if (!conditionDecision.adjudge(tableName, fieldName)) {
      return originCondition;
    }
    //如果条件字段不允许为空
    if (fieldValue == null && !conditionDecision.isAllowNullValue()) {
      return originCondition;
    }
    SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr(fieldName), new SQLNumberExpr(Long.valueOf(fieldValue)), operator);
    return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, condition, false, originCondition);

  }


}