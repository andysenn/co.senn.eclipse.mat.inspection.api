package co.senn.eclipse.mat.ext.inspection;

import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.report.QuerySpec;
import org.eclipse.mat.report.SectionSpec;
import org.eclipse.mat.util.IProgressListener;

@CommandName("inspection-parent:suspects")
public class InspectionQuerySpec implements IQuery {

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
		SectionSpec parent = new SectionSpec("Inspection Report");
		
		
		
		QuerySpec query = new QuerySpec("Test", new TextResult("So far, so good"));
		parent.add(query);
		
		return parent;
	}

}
