package co.senn.eclipse.mat.ext.suspect.database.pool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.IProgressListener;

import co.senn.eclipse.mat.ext.AbstractQuery;

@CommandName("suspect:connection-pool-exhaustion-hikaricp")
public class ConnectionPoolExhaustionHikariCPQuery extends AbstractQuery {

	@Override
	public String getSubjectName() {
		return "HikariCP";
	}

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
		Map<IObject, Integer> waitersByPool = new HashMap<>();
		boolean found = forEachObjectOfType("com.zaxxer.hikari.pool.HikariPool", true, object -> {
			Object waiters = object.resolveValue("connectionBag.waiters.value");
			tryParseInt(waiters, i -> {
				if (i > 0) {
					waitersByPool.put(object, i);
				}
			});
		});

		if (!found) {
			return new TextResult(messageNotDetected());
		}
		if (waitersByPool.size() == 0) {
			return new TextResult(messageNoIssues());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Found ").append(waitersByPool.size()).append(" pool(s) with threads awaiting connections:\n");
		waitersByPool.forEach((object, waiters) -> {
			sb.append("\n").append(object).append(": ").append(waiters).append(" threads awaiting a connection");
		});

		return new TextResult(sb.toString());
	}

}
