package co.senn.eclipse.mat.inspection.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to {@link ITechnology} and {@link IInspection} implementations,
 * indicates that they should be skipped during the inspection.
 * 
 * @author Andy Senn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ignore {
}
