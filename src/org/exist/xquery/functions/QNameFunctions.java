/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-04 The eXist Team
 *
 *  http://exist-db.org
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
 *  $Id$
 */
package org.exist.xquery.functions;

import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Function;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.AnyURIValue;
import org.exist.xquery.value.QNameValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;

/**
 * @author wolf
 *
 */
public class QNameFunctions extends BasicFunction {

	public final static FunctionSignature prefixFromQName =
		new FunctionSignature(
				new QName("prefix-from-QName", Function.BUILTIN_FUNCTION_NS),
				"Returns an xs:NCName representing the prefix of $a. If $a is the empty " +
				"sequence, returns the empty sequence.",
				new SequenceType[] {
					new SequenceType(Type.QNAME, Cardinality.ZERO_OR_ONE)
				},
				new SequenceType(Type.NCNAME, Cardinality.ZERO_OR_ONE));
	
	public final static FunctionSignature localNameFromQName =
		new FunctionSignature(
				new QName("local-name-from-QName", Function.BUILTIN_FUNCTION_NS),
				"Returns an xs:NCName representing the local part of $a. If $a is the empty " +
				"sequence, returns the empty sequence.",
				new SequenceType[] {
					new SequenceType(Type.QNAME, Cardinality.ZERO_OR_ONE)
				},
				new SequenceType(Type.NCNAME, Cardinality.ZERO_OR_ONE));
	
	public final static FunctionSignature namespaceURIFromQName =
		new FunctionSignature(
				new QName("namespace-uri-from-QName", Function.BUILTIN_FUNCTION_NS),
				"Returns the namespace URI for $a. If $a is the empty " +
				"sequence, returns the empty sequence.",
				new SequenceType[] {
					new SequenceType(Type.QNAME, Cardinality.ZERO_OR_ONE)
				},
				new SequenceType(Type.ANY_URI, Cardinality.ZERO_OR_ONE));
	
	/**
	 * @param context
	 * @param signature
	 */
	public QNameFunctions(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	/* (non-Javadoc)
	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)
	 */
	public Sequence eval(Sequence[] args, Sequence contextSequence)
			throws XPathException {
		if (args[0].getLength() == 0)
			return Sequence.EMPTY_SEQUENCE;
		QNameValue value = (QNameValue) args[0].itemAt(0);
		QName qname = value.getQName();
		if (isCalledAs("prefix-from-QName")) {
			String prefix = qname.getPrefix();
			if (prefix == null)
				return Sequence.EMPTY_SEQUENCE;
			else
				return new StringValue(prefix, Type.NCNAME);
		} else if (isCalledAs("local-name-from-QName"))
			return new StringValue(qname.getLocalName(), Type.NCNAME);
		else {
			String uri = qname.getNamespaceURI();
			if (uri == null)
				uri = "";
			return new AnyURIValue(uri);
		}
	}
}
