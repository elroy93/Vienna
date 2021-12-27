/*
 * Copyright 2008 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;

import com.onemuggle.vienna.common.ReNamed;

//@ReNamed("aasdf")
public class Enclosing {

    String x = "abc";

    public static class NestedSomeServiceProvider implements SomeService {

        public void init() {
            int y = 1234;
            @ReNamed("elroyRunnable1")
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                     // System.out.println("====");
                }
            };

            @ReNamed("elroyRunnable2")
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                     // System.out.println("====");
                }
            };

            runnable.run();
            runnable2.run();
             // System.out.println("xxxxxxxxxxx");
//
        }

//        Runnable runnable = null;


//        @ReNamed("elroyRunnable2")
//        Runnable runnable2 = null;
//        runnable2 = () -> System.out.print(1234);


    }
}
