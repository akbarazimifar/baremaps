/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.baremaps.database.calcite;

import java.util.Collection;
import org.apache.baremaps.database.collection.DataCollectionAdapter;
import org.apache.baremaps.database.schema.DataColumn;
import org.apache.baremaps.database.schema.DataTable;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;

/**
 * A simple table based on a list.
 */
class SqlDataTable extends AbstractTable implements ScannableTable {

  private final DataTable table;

  private RelDataType rowType;

  public SqlDataTable(DataTable table) {
    this.table = table;
  }

  @Override
  public Enumerable<Object[]> scan(final DataContext root) {
    Collection<Object[]> collection =
        new DataCollectionAdapter<>(table, row -> row.values().toArray());
    return Linq4j.asEnumerable(collection);
  }

  @Override
  public RelDataType getRowType(final RelDataTypeFactory typeFactory) {
    if (rowType == null) {
      rowType = createRowType(typeFactory);
    }
    return rowType;
  }

  private RelDataType createRowType(RelDataTypeFactory typeFactory) {
    var rowType = new RelDataTypeFactory.Builder(typeFactory);
    for (DataColumn column : table.rowType().columns()) {
      rowType.add(column.name(), SqlTypeConversion.types.get(column.type()));
    }
    return rowType.build();
  }
}
