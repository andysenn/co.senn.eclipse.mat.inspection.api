/*
 * Copyright 2018 Andy Senn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.senn.eclipse.mat.inspection.api;

public enum InspectionResultSeverity {

	/**
	 * Indicates that the result is for informational purposes only
	 */
	INFO("Info"),

	/**
	 * Indicates that the result is potentially indicative of an issue
	 */
	WARN("Warning"),

	/**
	 * Indicates that the result is very likely indicative of an issue
	 */
	SEVERE("Severe"),

	/**
	 * Indicates that the inspection failed to execute
	 */
	FAILURE("Failure");

	private final String name;

	InspectionResultSeverity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
