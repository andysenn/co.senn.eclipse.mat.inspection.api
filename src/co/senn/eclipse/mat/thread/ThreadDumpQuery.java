package co.senn.eclipse.mat.thread;

import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.Category;
import org.eclipse.mat.query.annotations.Help;
import org.eclipse.mat.query.annotations.Name;
import org.eclipse.mat.util.IProgressListener;

@Category("Java Basics")
@Name("Thread Dump")
@Help("Generates a thread dump of all available threads")
public class ThreadDumpQuery implements IQuery {

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
//		ThreadDetailResolverRegistry.instance().delegates().forEach(resolver -> {
//			
//		});
		
		

		return null;
	}

}
