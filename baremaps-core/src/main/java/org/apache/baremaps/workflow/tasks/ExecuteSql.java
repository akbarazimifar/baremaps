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

package org.apache.baremaps.workflow.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.baremaps.workflow.Task;
import org.apache.baremaps.workflow.WorkflowContext;
import org.apache.baremaps.workflow.WorkflowException;

public record ExecuteSql(String database, Path file, boolean parallel) implements Task {

  @Override
  public void execute(WorkflowContext context) throws Exception {
    var script = clean(Files.readString(file));
    var queries = split(script);
    if (parallel) {
      queries = queries.parallel();
    }
    queries.forEach(
        query -> {
          var dataSource = context.getDataSource(database);
          try (var connection = dataSource.getConnection();
              var statement = connection.createStatement()) {
            statement.execute(query);
          } catch (SQLException e) {
            throw new WorkflowException(e);
          }
        });
  }

  /**
   * Split a SQL string into multiple SQL statements.
   *
   * @param sql The SQL string.
   * @return The SQL statements.
   */
  public static Stream<String> split(String sql) {
    return Arrays.stream(sql.split("\\s*;\\s*(?=(?:[^']*'[^']*')*[^']*$)"));
  }

  /**
   * Remove comments from a SQL string.
   *
   * @param sql The SQL string.
   * @return The SQL string without comments.
   */
  public static String clean(String sql) {
    var result = sql;

    // remove single line comments
    var singleLineCommentPattern = Pattern.compile("--.*", Pattern.MULTILINE);
    var singleLineCommentMatcher = singleLineCommentPattern.matcher(result);
    result = singleLineCommentMatcher.replaceAll("");

    // remove multi line comments
    var multiLineCommentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    var multiLineMatcher = multiLineCommentPattern.matcher(result);
    result = multiLineMatcher.replaceAll("");

    return result;
  }
}
