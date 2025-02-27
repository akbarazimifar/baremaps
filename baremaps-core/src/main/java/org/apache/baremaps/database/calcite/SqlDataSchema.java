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

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

public class SqlDataSchema extends AbstractSchema {

  private final Map<String, Table> tableMap;

  public SqlDataSchema(ImmutableMap<String, Table> tableMap) {
    this.tableMap = ImmutableMap.copyOf(tableMap);
  }

  @Override
  protected Map<String, Table> getTableMap() {
    return tableMap;
  }

}
