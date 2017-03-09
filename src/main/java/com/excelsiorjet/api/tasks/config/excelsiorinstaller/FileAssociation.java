/*
 * Copyright (c) 2017, Excelsior LLC.
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
package com.excelsiorjet.api.tasks.config.excelsiorinstaller;

import com.excelsiorjet.api.tasks.JetProject;
import com.excelsiorjet.api.tasks.JetTaskFailureException;
import com.excelsiorjet.api.tasks.config.packagefile.PackageFile;
import com.excelsiorjet.api.tasks.config.compiler.WindowsVersionInfoConfig;
import com.excelsiorjet.api.tasks.config.packagefile.PackageFileType;

import static com.excelsiorjet.api.util.Txt.s;

/**
 * (Windows) File association description.
 *
 * @author Nikita Lipsky
 */
public class FileAssociation {

    /**
     * File name extension <em>without</em> the leading dot, e.g. {@code myscript}.
     */
    public String extension;

    /**
     * File type description.
     * <p>
     * The operating system will display it in some dialogs, such as File Properties.
     * For example, the description of .mp3 files is "MP3 Format Sound".
     * </p>
     */
    public String description;

    /**
     * Location within the package of the executable program that has to be associated with this extension.
     */
    public String target;

    /**
     * String to be used in the prompt displayed by the Excelsior Installer wizard:
     * "Associate *.extension files with targetDescription".
     * {@link WindowsVersionInfoConfig#description} will be used by default.
     */
    public String targetDescription;

    /**
     * The location of an icon file that should be used for files with names ending in {@code extension}.
     * You may omit {@link PackageFile#path} parameter of the icon,
     * if {@link JetProject#packageFilesDir} already contains an icon at the specified {@link PackageFile#packagePath}
     * parameter, otherwise the icon will be added to the specified {@link PackageFile#packagePath} folder of the package.
     * If the icon is not set, the default icon of the target executable will be used.
     */
    public PackageFile icon;

    /**
     * Command-line arguments for the target.
     */
    public String[] arguments;

    /**
     * Initial state of the respective checkbox "Associate *.extension files with target-desc"
     * in the Excelsior Installer wizard.
     * Default value is {@code true}.
     */
    public boolean checked = true;

    void validate() throws JetTaskFailureException {
        if (extension == null) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.FileAssociationExtensionNull"));
        }

        if (target == null) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.FileAssociationTargetNull", extension));
        }

        icon.type = PackageFileType.FILE.toString();
        icon.validate("JetApi.ExcelsiorInstaller.FileAssociationIconDoesNotExist", extension);

        if (description == null) {
            description = "";
        }

        if (targetDescription == null) {
            targetDescription = "";
        }
    }

}
