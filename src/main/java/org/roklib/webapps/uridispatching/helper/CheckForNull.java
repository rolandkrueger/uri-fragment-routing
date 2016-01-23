/*
 * Copyright (C) 2007 Roland Krueger
 * Created on 17.10.2009
 *
 * Author: Roland Krueger (www.rolandkrueger.info)
 *
 * This file is part of RoKlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.roklib.webapps.uridispatching.helper;

public class CheckForNull {
    public static void check(Object... objects) {
        StringBuilder buf = null;
        int count = 0;
        for (Object o : objects) {
            count++;
            if (o == null) {
                if (buf == null) {
                    buf = new StringBuilder("Argument at position ");
                }
                buf.append(count).append(", ");
            }
        }
        if (buf != null) {
            buf.setLength(buf.length() - 2); // remove last comma
            StringBuilder buf2 = new StringBuilder();
            for (Object o : objects) {
                buf2.append(o == null ? "null, " : "set, ");
            }
            buf2.setLength(buf2.length() - 2);
            buf.append(" is null (").append(buf2.toString()).append(").");
            throw new IllegalArgumentException(buf.toString());
        }
    }
}
