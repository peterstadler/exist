package org.exist.xquery.functions;

import org.exist.dom.QName;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Function;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.exist.xquery.value.ValueSequence;

/**
 * Implements the fn:reverse function.
 *
 * @author <a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>
 */
public class FunReverse extends Function {

	public final static FunctionSignature signature =
		new FunctionSignature(
			new QName("reverse", Function.BUILTIN_FUNCTION_NS),
			"Reverses the order of items in a sequence.  If the argument is an empty" +
			"sequence, the empty sequence is returned.",
			new SequenceType[] {new SequenceType(Type.ITEM, Cardinality.ZERO_OR_MORE)},
			new SequenceType(Type.ITEM, Cardinality.ZERO_OR_MORE));
			
	public FunReverse(XQueryContext context) {
		super(context, signature);
	}

	public Sequence eval(Sequence contextSequence, Item contextItem) throws XPathException {
		Sequence seq = getArguments(contextSequence, contextItem)[0];
		if(seq.getLength() == 0) return Sequence.EMPTY_SEQUENCE;
		Sequence result = new ValueSequence();
		for (int i = seq.getLength()-1; i>=0; i--) {
			result.add(seq.itemAt(i));
		}
		return result;
	}

}
