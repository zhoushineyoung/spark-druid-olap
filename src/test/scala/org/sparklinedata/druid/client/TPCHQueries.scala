package org.sparklinedata.druid.client

import org.sparklinedata.druid._
import org.sparklinedata.druid.metadata.DruidDataSource

object TPCHQueries {

  val q1 = new GroupByQuerySpec("tpch",
    List(new DefaultDimensionSpec("l_returnflag"), new DefaultDimensionSpec("l_linestatus")),
    Some(new LimitSpec(10,
      List(new OrderByColumnSpec("l_returnflag"), new OrderByColumnSpec("l_linestatus")))
    ),
    None,
    Left("all"),
    None,
    List(
      FunctionAggregationSpec("count", "count", "count"),
      FunctionAggregationSpec("longSum", "sum_quantity", "sum_l_quantity"),
      FunctionAggregationSpec("doubleSum", "sum_base_price", "l_extendedprice"),
      FunctionAggregationSpec("doubleSum", "l_discount", "l_discount"),
      FunctionAggregationSpec("doubleSum", "l_tax", "l_tax"),
      new CardinalityAggregationSpec("count_order", List("o_orderkey"))
    ),
    Some(List(
      new ArithmeticPostAggregationSpec("sum_disc_price", "-",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("l_discount")), None),
      new ArithmeticPostAggregationSpec("sum_charge", "-",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("l_tax")), None),
      new ArithmeticPostAggregationSpec("avg_qty", "/",
        List(new FieldAccessPostAggregationSpec("sum_quantity"),
          new FieldAccessPostAggregationSpec("count")), None),
      new ArithmeticPostAggregationSpec("avg_price", "/",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("count")), None),
      new ArithmeticPostAggregationSpec("avg_disc", "/",
        List(new FieldAccessPostAggregationSpec("l_discount"),
          new FieldAccessPostAggregationSpec("count")), None)
    )),
    List("1993-01-01T00:00:00.000/1998-09-01T00:00:00.000")
  )

  val q1MonthGrain = new GroupByQuerySpec("tpch",
    List(
      new DefaultDimensionSpec("l_returnflag"),
      new DefaultDimensionSpec("l_linestatus"),
      new ExtractionDimensionSpec(DruidDataSource.TIME_COLUMN_NAME,
        "month",
        new TimeFormatExtractionFunctionSpec("yyyy-MMM")
      )
    ),
    Some(new LimitSpec(10,
      List(new OrderByColumnSpec("l_returnflag"), new OrderByColumnSpec("l_linestatus")))
    ),
    None,
    Right(new PeriodGranularitySpec("P1M")),
    None,
    List(
      FunctionAggregationSpec("longSum", "count", "count"),
      FunctionAggregationSpec("longSum", "sum_quantity", "sum_l_quantity"),
      FunctionAggregationSpec("doubleSum", "sum_base_price", "l_extendedprice"),
      FunctionAggregationSpec("doubleSum", "l_discount", "l_discount"),
      FunctionAggregationSpec("doubleSum", "l_tax", "l_tax"),
      new CardinalityAggregationSpec("count_order", List("o_orderkey"))
    ),
    Some(List(
      new ArithmeticPostAggregationSpec("sum_disc_price", "-",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("l_discount")), None),
      new ArithmeticPostAggregationSpec("sum_charge", "-",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("l_tax")), None),
      new ArithmeticPostAggregationSpec("avg_qty", "/",
        List(new FieldAccessPostAggregationSpec("sum_quantity"),
          new FieldAccessPostAggregationSpec("count")), None),
      new ArithmeticPostAggregationSpec("avg_price", "/",
        List(new FieldAccessPostAggregationSpec("sum_base_price"),
          new FieldAccessPostAggregationSpec("count")), None),
      new ArithmeticPostAggregationSpec("avg_disc", "/",
        List(new FieldAccessPostAggregationSpec("l_discount"),
          new FieldAccessPostAggregationSpec("count")), None)
    )),
    List("1993-01-01T00:00:00.000/1993-03-01T00:00:00.000")
  )

  val q3 = new GroupByQuerySpec("tpch",
    List(new DefaultDimensionSpec("o_orderkey"),
      new DefaultDimensionSpec("o_orderdate"),
      new DefaultDimensionSpec("o_shippriority")),
    Some(new LimitSpec(10,
      List(OrderByColumnSpec("revenue", "descending"), new OrderByColumnSpec("o_orderdate"))
    )),
    None,
    Left("all"),
    Some(LogicalFilterSpec("and",
      List(
        new SelectorFilterSpec("c_mktsegment", "BUILDING"),
        new JavascriptFilterSpec("o_orderdate",
          "function(x) { return(x < '1995-03-15') }"
        )
      )
    )
    ),
    List(
      FunctionAggregationSpec("doubleSum", "l_extendedprice", "l_extendedprice"),
      FunctionAggregationSpec("doubleSum", "l_discount", "l_discount")
    ),
    Some(List(
      new ArithmeticPostAggregationSpec("revenue", "-",
        List(new FieldAccessPostAggregationSpec("l_extendedprice"),
          new FieldAccessPostAggregationSpec("l_discount")), None)
    )),
    List("1995-03-15T00:00:00.000/1998-09-01T00:00:00.000")
  )


}