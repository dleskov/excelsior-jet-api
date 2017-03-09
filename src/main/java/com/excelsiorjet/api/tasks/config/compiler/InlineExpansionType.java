/*
 * Copyright (c) 2016-2017, Excelsior LLC.
 *
 *  This file is part of Excelsior JET API.
 *
 *  Excelsior JET API is free software:
 *  you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Excelsior JET API is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Excelsior JET API.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package com.excelsiorjet.api.tasks.config.compiler;

import com.excelsiorjet.api.tasks.JetTaskFailureException;
import com.excelsiorjet.api.util.Utils;

import static com.excelsiorjet.api.util.Txt.s;

/**
 * Inline expansion types enumeration.
 */
public enum InlineExpansionType {
    VERY_AGGRESSIVE,
    AGGRESSIVE, //default
    MEDIUM,
    LOW,
    TINY_METHODS_ONLY;

    public String toString() {
        return Utils.enumConstantNameToParameter(name());
    }

    public static InlineExpansionType validate(String inlineExpansion) throws JetTaskFailureException {
        try {
            return InlineExpansionType.valueOf(Utils.parameterToEnumConstantName(inlineExpansion));
        } catch (Exception e) {
            throw new JetTaskFailureException(s("JetApi.UnknownInlineExpansionValue.Failure", inlineExpansion));
        }
    }

    public static InlineExpansionType fromString(String inlineExpansion) {
        try {
            return validate(inlineExpansion);
        } catch (JetTaskFailureException e) {
            throw new AssertionError("inlineExpansion should be valid here", e);
        }
    }
}
