package co.senn.eclipse.mat.ext.suspect.web.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.IProgressListener;

import co.senn.eclipse.mat.ext.AbstractQuery;

/**
 * Checks for non-started Tomcat web application contexts.
 * <p>
 * The presence of these can be indicative of a memory leak (eg: uninterruptible threads).
 * 
 * @author Andy Senn
 */
@CommandName("suspect:servlet-container-non-started-contexts")
public class ServletContainerNonStartedContextsQuery extends AbstractQuery {

	@Override
	public String getSubjectName() {
		return "Tomcat Class Loaders";
	}

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
		Collection<IObject> nonStartedClassLoaders = new ArrayList<>();
		boolean found = forEachObjectOfType("org.apache.catalina.loader.WebappClassLoader", true, object -> {

			// Older Tomcat Versions
			Object started = object.resolveValue("started");
			if (started != null && Objects.equals(started, false)) {
				nonStartedClassLoaders.add(object);
				return; // Instead of nesting if/else blocks
			}

			// Newer Tomcat Versions
			Object state = object.resolveValue("state.name");
			if (started != null && Objects.equals(state, "DESTROYED")) {
				nonStartedClassLoaders.add(object);
				return; // Instead of nesting if/else blocks
			}

			// Any other things to check here?
		});

		if (!found) {
			return new TextResult(messageNotDetected());
		}
		if (nonStartedClassLoaders.size() == 0) {
			return new TextResult(messageNoIssues());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Found ").append(nonStartedClassLoaders.size()).append(" non-started context(s), which may be indicitive of a memory leak:\n");
		nonStartedClassLoaders.forEach(object -> {
			sb.append("\n").append(object);
		});

		return new TextResult(sb.toString());
	}

}
