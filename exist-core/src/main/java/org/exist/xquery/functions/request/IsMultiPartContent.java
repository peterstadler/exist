/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-09 Wolfgang M. Meier
 *  wolfgang@exist-db.org
 *  http://exist.sourceforge.net
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id: GetMethod.java 9749 2009-08-09 23:18:12Z ixitar $:
 */
package org.exist.xquery.functions.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.exist.dom.QName;
import org.exist.http.servlets.RequestWrapper;

import org.exist.xquery.*;
import org.exist.xquery.value.BooleanValue;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.JavaObjectValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.Type;

import javax.annotation.Nonnull;

/**
 * @author Dannes Wessels <dannes@exist-db.org>
 */
public class IsMultiPartContent extends StrictRequestFunction {

    protected static final Logger logger = LogManager.getLogger(IsMultiPartContent.class);
    public final static FunctionSignature signature =
            new FunctionSignature(
            new QName("is-multipart-content", RequestModule.NAMESPACE_URI, RequestModule.PREFIX),
            "Determine if the request contains multipart/form-data",
            null,
            new FunctionReturnSequenceType(Type.BOOLEAN, Cardinality.EXACTLY_ONE, 
                "true is the request is a multipart/form-data request else false."));

    public IsMultiPartContent(final XQueryContext context) {
        super(context, signature);
    }

    @Override
    public Sequence eval(final Sequence[] args, @Nonnull final RequestWrapper request)
            throws XPathException {
        return new BooleanValue(request.isMultipartContent());
    }
}
