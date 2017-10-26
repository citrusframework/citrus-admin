/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javadsl;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.testng.CitrusParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class DataProviderJavaTest extends TestNGCitrusTestDesigner {

    @Test(dataProvider = "messageDataProvider")
    @CitrusTest
    @CitrusParameters({ "message" })
    public void fooProviderTest(String message) {
        echo(message);
    }

    @Test(dataProvider = "messageDataProvider")
    @CitrusTest(name = "BarProviderTest")
    @CitrusParameters({ "message" })
    public void barProviderTest(String message) {
        echo(message);
    }

    @DataProvider
    public Object[][] messageDataProvider() {
        return new Object[][] {{ "Citrus rocks!" },
                { "Citrus really rocks!" },
                { "Citrus is awesome!" }};
    }
}