package co.senn.eclipse.mat.ext.thread;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.ResultMetaData;
import org.eclipse.mat.report.IOutputter;

public class ThreadDumpRenderer implements IOutputter {

	@Override
	public void embedd(Context context, IResult result, Writer writer) throws IOException {
		ResultMetaData metadata = result.getResultMetaData();
		metadata.getDetailResultProviders().forEach(op -> {
			
		});
	}

	@Override
	public void process(Context context, IResult result, Writer writer) throws IOException {
		// TODO Auto-generated method stub

	}

}
