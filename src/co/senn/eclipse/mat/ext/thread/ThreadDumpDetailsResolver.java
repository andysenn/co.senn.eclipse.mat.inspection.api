package co.senn.eclipse.mat.ext.thread;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.query.Column;
import org.eclipse.mat.snapshot.extension.IThreadDetailsResolver;
import org.eclipse.mat.snapshot.extension.IThreadInfo;
import org.eclipse.mat.util.IProgressListener;

public class ThreadDumpDetailsResolver implements IThreadDetailsResolver {

	@Override
	public void complementDeep(IThreadInfo thread, IProgressListener listener) throws SnapshotException {
		
	}

	@Override
	public void complementShallow(IThreadInfo thread, IProgressListener listener) throws SnapshotException {

	}

	@Override
	public Column[] getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

}
