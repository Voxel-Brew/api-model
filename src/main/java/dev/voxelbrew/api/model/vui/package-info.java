/**
 * Virtual UI (VUI) contracts shared by frontend-neutral workflows.
 *
 * <p>Layouts arrange immutable snapshots of component children. Inputs expose IDs used to map
 * frontend values to dialog validation and settings. Hosts render these trees or resolve their
 * values from command-line arguments; these contracts do not depend on a UI toolkit.
 * File sources and destinations describe chooser inputs without performing file I/O.
 */
package dev.voxelbrew.api.model.vui;
