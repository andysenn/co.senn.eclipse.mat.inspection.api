# Eclipse MAT Inspections Support

An Eclipse Memory Analyzer plug-in that provides a framework for inspecting a JVM heap dump for common issues.

## Getting Started

Once the plug-in is installed, a new report named "Inspection Report" will appear on the "Overview" tab in Eclipse MAT. Clicking on this report will execute all of the inspections and display the resulting report in a new tab.

## Writing Inspections

Eclipse Memory Analyzer the Inspections Support plug-in and its implementations are built upon the Eclipse RCP (Rich Client Platform). For more information about writing Eclipse RCP plug-ins, please see the Eclipse RCP documentation.

### Extension Points

The plug-in exposes 2 extension points for new inspections:

- `co.senn.eclipse.mat.inspection.technology`
- `co.senn.eclipse.mat.inspection.inspection`

The "technology" extension point is used to define new technologies. The "inspection" extension point is used to define new inspections.

#### Technologies

Technologies are libraries, APIs, or other components of an application that may or may not be present. They are implemented using the provided `ITechnology` interface.

Technologies provide a container for inspections and are used to determine whether a given technology is present in a heap dump. If so, its inspections will be executed; otherwise, they will be skipped.

#### Inspections

Inspections perform a check for a specific issue or condition for a given technology and are implemented using the provided `IInspection` interface.

Instead of technologies specifying their inspections, inspections specify their technology. This is done so that new inspections can be added to existing technologies.