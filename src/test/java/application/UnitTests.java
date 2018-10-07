/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.APIQueriedWrongly;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void apiImportedProperly() throws Exception {

        MvcResult queryResult = this.mockMvc.perform(get("/queryAPI")).andReturn();
        String queryContent = queryResult.getResponse().getContentAsString();

        JsonElement jelement = new JsonParser().parse(queryContent);

        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("result");
        JsonArray jarray = jobject.getAsJsonArray("records");
        jobject = jarray.get(0).getAsJsonObject();
        String contentTested = jobject.get("location").getAsString();

        if (!contentTested.equals("Bloodbank@HSA"))
            throw new APIQueriedWrongly();

    }

}
