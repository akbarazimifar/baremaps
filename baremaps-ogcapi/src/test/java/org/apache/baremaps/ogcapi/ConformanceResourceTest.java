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

package org.apache.baremaps.ogcapi;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

public class ConformanceResourceTest extends OgcApiTest {

  @Test
  @Tag("integration")
  public void getConformance() {
    var response = target().path("/conformance").request().get();
    assertEquals(200, response.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
  }

}
