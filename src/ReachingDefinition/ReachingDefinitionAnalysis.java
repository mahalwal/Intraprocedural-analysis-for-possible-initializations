//Manish Mahalwal
//2016054

package ReachingDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;

class Pair {
	public String l, r;
	Pair(String l, String r) {
		this.l = l;
		this.r = r;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+l+","+r+")";
	}
}
public class ReachingDefinitionAnalysis extends ForwardFlowAnalysis {

	Body b;
	FlowSet inval, outval;
	int firstAnalysis = 0;
	public ReachingDefinitionAnalysis(UnitGraph g)
	{
		super(g);
		firstAnalysis = 1;
		b = g.getBody();
		doAnalysis();
	}
	
	@Override
	protected void flowThrough(Object in, Object unit, Object out) {
		
		inval = (FlowSet)in;
		outval = (FlowSet)out;
		
		if(firstAnalysis == 1) {
			Chain<Local> local = b.getLocals();
			ArrayList<String> list = new ArrayList<String>();
			for(Local l: local) {
				if(!l.toString().equals("this"))
					list.add(l.toString());
			}
			
			Set<String> unique = new HashSet<String>(list);
			for(String str: unique) {
				System.out.println("Adding: " + str + "?");
				inval.add(new Pair(str, "?"));
			}
			firstAnalysis = 0;
		}
//		System.out.println(b.getLocals().toString());

		Stmt u = (Stmt)unit;
		String currStmt = u.toString();
		inval.copy(outval);
		

		// Kill operation
		Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
		Iterator<ValueBox> useIt = u.getUseBoxes().iterator();

		while(defIt.hasNext())
		{
			ValueBox defBox = (ValueBox)defIt.next();
			ValueBox useBox = (ValueBox)useIt.next();
			String currLeft = defBox.getValue().toString();
			String currRight = useBox.getValue().toString();
			
			if(currLeft.equals("this"))
				continue;
			if ((defBox.getValue() instanceof Local)) {
				Iterator inIt = outval.iterator();
				System.out.println("Current Statment: " + currStmt);
//				System.out.println(currStmt);
				while (inIt.hasNext()) {
					System.out.println("Current element in inval");
					Pair s = (Pair) inIt.next();
					System.out.println(s);
					System.out.println(currLeft + "," +currRight);
					
					String sleft = s.l;
					String sright = s.r;
					if(currLeft.compareTo(sleft)==0) {
						if(sright.compareTo("?")==0) {
							outval.remove(s);
							outval.add(new Pair(currLeft, currRight));
							System.out.println("removed and added from outval");
							break;
						}
					}

				}
			}
		}
		
		//Gen operation
//		if (u instanceof AssignStmt)
//			outval.add(u);
		
		if (u instanceof AssignStmt)
		{
			System.out.println("In: " + inval.toString());
			System.out.println("Unit: " + u.toString());
			System.out.println("Out :" + outval.toString());
			System.out.println();
		}
		
	}
	@Override
	protected void copy(Object source, Object dest) {
		FlowSet srcSet = (FlowSet)source;
		FlowSet	destSet = (FlowSet)dest;
		srcSet.copy(destSet);
		
	}
	@Override
	protected Object entryInitialFlow() {
		return new ArraySparseSet();
	}
	@Override
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inval1=(FlowSet)in1;
		FlowSet inval2=(FlowSet)in2;
		FlowSet outSet=(FlowSet)out;
		// May analysis
		inval1.union(inval2, outSet);
		System.out.println("In merge");
		System.out.println("inval1:"+inval1);
		System.out.println("inval2:"+inval2);
		System.out.println("Outval:"+outSet);
		System.out.println();
	}
	
	
	@Override
	protected Object newInitialFlow() {
		return new ArraySparseSet();
	}
}
