/*
 * Copyright (c) 2015,2016 Excelsior LLC.
 *
 *  This file is part of Excelsior JET API.
 *
 *  Excelsior JET API is free software:
 *  you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Excelsior JET API is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Excelsior JET API.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package com.excelsiorjet.api.tasks.config;

import com.excelsiorjet.api.tasks.JetProject;
import com.excelsiorjet.api.tasks.JetTaskFailureException;
import com.excelsiorjet.api.util.Utils;

import java.io.File;


/**
 * Configuration parameters of OS X App Bundle.
 * Used to create OS X App Bundle and Mac Installer Package (.pkg file).
 *
 * <p>
 * See
 * <a href=
 * "https://developer.apple.com/library/mac/documentation/CoreFoundation/Conceptual/CFBundles/BundleTypes/BundleTypes.html">
 * Bundle Programming Guide</a>
 * to learn more about OS X App Bundles.
 * </p>
 * @author Nikita Lipsky
 */
public class OSXAppBundleConfig {

    /**
     * OS X app bundle file name.
     * Default is {@link JetProject#outputName}.
     */
    public String fileName;

    /**
     * Value for the {@code CFBundleName} key in the resulting {@code Info.plist} file.
     * Default is {@link JetProject#product}.
     */
    public String bundleName;

    /**
     * Value for the {@code CFBundleIdentifier} key in the resulting {@code Info.plist} file.
     * Default is {@code ${project.groupId}.${project.build.finalName}}.
     */
    public String identifier;

    /**
     * Value for the {@code CFBundleShortVersionString} key in the resulting {@code Info.plist} file.
     * By default, derived from {@link JetProject#version}.
     */
    public String shortVersion;

    /**
     * Value for the {@code CFBundleVersionString} key in the resulting {@code Info.plist} file.
     * By default, derived from {@code ${project.version}}.
     */
    public String version;

    /**
     * Value for the {@code CFBundleIconFile} key in the resulting {@code Info.plist} file.
     * Default is {@code icon.icns} in {@link JetProject#jetResourcesDir} folder.
     */
    public File icon;

    /**
     * Value for the {@code NSHighResolutionCapable} key in the resulting {@code Info.plist} file.
     */
    public boolean highResolutionCapable = true;

    /**
     * "Developer ID Application" or "Mac App Distribution" certificate name for signing the resulting OS X app bundle.
     *  You may also set the parameter via the {@code osx.developer.id} system property.
     * <p>
     * Refer to the official
     * <a href=
     * "https://developer.apple.com/library/ios/documentation/IDEs/Conceptual/AppDistributionGuide/MaintainingCertificates/MaintainingCertificates.html">
     * App Distribution Guide</A>
     * for details.
     * </p>
     */
    public String developerId;

    /**
     * "Developer ID Installer" or "Mac Installer Distribution" certificate name for signing the resulting
     *  OS X Installer Package (.pkg file).
     *  You may also set the parameter via {@code osx.publisher.id"} system property.
     *
     * <p>
     * Refer to the official
     * <a href=
     * "https://developer.apple.com/library/ios/documentation/IDEs/Conceptual/AppDistributionGuide/MaintainingCertificates/MaintainingCertificates.html">
     * App Distribution Guide</A>
     * for details.
     * </p>
     *
     */
    public String publisherId;

    /**
     * The default installation path on the target system, used during the creation of the .pkg installer file.
     * Default value is "/Applications".
     */
    public String installPath = "/Applications";

    public void fillDefaults(JetProject project, String fileName, String bundleName, String version, String shortVersion) throws JetTaskFailureException {
        if (this.fileName == null) {
            this.fileName = fileName;
        }
        if (this.bundleName == null) {
            this.bundleName = bundleName;
        }
        if (this.identifier == null) {
            this.identifier = project.groupId() + "." + project.artifactName();
        }
        this.icon = Utils.checkFileWithDefault(this.icon, new File(project.jetResourcesDir(), "icon.icns"),
                "JetApi.OSXBundle.FileDoesNotExist", "icon");
        if (this.version == null) {
            this.version = version;
        }
        if (this.shortVersion == null) {
            this.shortVersion = shortVersion;
        }
        if (this.developerId == null) {
            this.developerId = System.getProperty("osx.developer.id");
        }
        if (this.publisherId == null) {
            this.publisherId = System.getProperty("osx.publisher.id");
        }
    }
}
