/*
 * Copyright (c) 2015-2017, Excelsior LLC.
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

import com.excelsiorjet.api.ExcelsiorJet;
import com.excelsiorjet.api.tasks.JetProject;
import com.excelsiorjet.api.tasks.JetTaskFailureException;
import com.excelsiorjet.api.tasks.config.packagefile.PackageFile;
import com.excelsiorjet.api.tasks.config.packagefile.PackageFileType;
import com.excelsiorjet.api.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.excelsiorjet.api.util.EncodingDetector.detectEncoding;
import static com.excelsiorjet.api.util.Txt.s;

/**
 * Configuration parameters for Excelsior Installer packaging type.
 *
 * @author Nikita Lipsky
 */
public class ExcelsiorInstallerConfig {

    private static final String AUTO_DETECT_EULA_ENCODING = "autodetect";
    private static final String UNICODE_EULA_FLAG = "-unicode-eula";
    private static final String EULA_FLAG = "-eula";

    private static final Set<String> VALID_EULA_ENCODING_VALUES = new LinkedHashSet<String>() {{
        add(StandardCharsets.US_ASCII.name());
        add(StandardCharsets.UTF_16LE.name());
        add(AUTO_DETECT_EULA_ENCODING);
    }};

    /**
     * The license agreement file. Used for Excelsior Installer.
     * File containing the end-user license agreement, for Excelsior Installer to display during installation.
     * The file must be a plain text file either in US-ASCII or UTF-16LE encoding.
     * If not set, and the file {@code eula.txt} in {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     *
     * @see #eulaEncoding eulaEncoding
     */
    public File eula;

    /**
     * Encoding of the EULA file. Permitted values:
     * <ul>
     *     <li>{@code US-ASCII}</li>
     *     <li>{@code UTF-16LE}</li>
     *     <li>{@code autodetect} (Default value)</li>
     * </ul>
     * If set to {@code autodetect}, the plugin looks for a byte order mark (BOM) in the file specified by {@link #eula}, and:
     * <ul>
     * <li>assumes US-ASCII encoding if no BOM is present,</li>
     * <li>assumes UTF-16LE encoding if the respective BOM ({@code 0xFF 0xFE}) is present, or </li>
     * <li>halts execution with error if some other BOM is present.</li>
     * </ul>
     * @see <a href="https://en.wikipedia.org/wiki/Byte_order_mark">Byte order mark</a>
     * @see #eula eula
     */
    protected String eulaEncoding = AUTO_DETECT_EULA_ENCODING;

    /**
     * (Windows) Excelsior Installer splash screen image in BMP format.
     * If not set, and the file {@code installerSplash.bmp} in {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     */
    public File installerSplash;

    /**
     * Forced setup language.
     * <p>
     * Excelsior Installer can display its screens in multiple languages.
     * By default, it selects the most appropriate language based on the locale settings of the target system.
     * Use this parameter to force a specific language instead. Permitted values:
     *  {@code autodetect} (default), {@code english}, {@code french}, {@code german}, {@code japanese}, {@code russian},
     *  {@code polish}, {@code spanish}, {@code italian}, {@code brazilian}.
     * </p>
     * This functionality is available in Excelsior JET 11.3 and above.
     */
    public String language;

    /**
     * Remove all files from the installation folder on uninstall.
     * <p>
     * By default, the uninstaller only removes those files and directories from the installation directory
     * that the original applicaion installer, and possibly update installers, had created.
     * If any files and directories were created by a post-install runnnable, callback DLL,
     * installed and/or third-party applications, the uninstaller will leave them in place and report to the user that
     * it was unable to remove the installation directory due to their presence.
     * </p>
     * This functionality is available in Excelsior JET 11.3 and above.
     */
    public boolean cleanupAfterUninstall;

    /**
     * Excelsior Installer can optionally run one of the executable files included in the package
     * upon successful installation. Use this parameter to specify the executable and its arguments.
     *
     * @see AfterInstallRunnable#target
     * @see AfterInstallRunnable#arguments
     */
    public AfterInstallRunnable afterInstallRunnable = new AfterInstallRunnable();

    /**
     * Packaged files compression level.
     * <p>
     * Excelsior Installer supports three compression levels: {@code fast}, {@code medium} (default),
     * and {@code high} (slow).
     * </p>
     * This functionality is available in Excelsior JET 11.3 and above.
     */
    public String compressionLevel;

    /**
     * Installation directory configuration.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     *
     * @see InstallationDirectory#type
     * @see InstallationDirectory#path
     * @see InstallationDirectory#fixed
     */
    public InstallationDirectory installationDirectory = new InstallationDirectory();

    /**
     * (Windows) Registry key for installation.
     * <p>
     * During installation, Excelsior Installer creates a registry key to store information
     * required for the installation of update packages.
     * The key is located either in the {@code HKEY_LOCAL_MACHINE/SOFTWARE/}
     * or the {@code HKEY_CURRENT_USER/SOFTWARE/} subtree,
     * depending on whether Common or Personal installation type gets selected during installation.
     * By default, the rest of the full name of the key is derived from the values of {@link JetProject#vendor},
     * {@link JetProject#product} and {@link JetProject#version} parameters:
     * {@code company-name/product-name/product-version}. Use this parameter to override that default.
     * </p>
     * This functionality is available in Excelsior JET 11.3 and above.
     */
    public String registryKey;

    /**
     * (Windows) Descriptions of shortcuts that the resulting installer will create.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     * @see Shortcut
     */
    public List<Shortcut> shortcuts = Collections.emptyList();

    /**
     * Default post-install action suppress flag.
     * <p>
     * Upon successful installation, Excelsior Installer can optionally display to the user a list of checkboxes
     * enabling various post-install actions, such as launching the installed application,
     * viewing the readme file, restarting the system, and so on.
     * The default is to add a launch action for each JET-compiled executable in the package with the text
     * "Start executable-name".
     * If you do not want to add the default action, set this parameter to {@code true}.
     * </p>
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     * @see #postInstallCheckboxes
     */
    public boolean noDefaultPostInstallActions;

    /**
     * Post-install actions descriptions.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     * @see #noDefaultPostInstallActions
     */
    public List<PostInstallCheckbox> postInstallCheckboxes = Collections.emptyList();

    /**
     * File associations descriptions.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     * @see FileAssociation
     */
    public List<FileAssociation> fileAssociations = Collections.emptyList();

    /**
     * Install callback dynamic library.
     * If not set, and the file {@code install.dll/libinstall.so} in the {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     */
    public File installCallback;

    /**
     * Uninstall callback dynamic library.
     * <p>
     * An uninstall callback dynamic library has to be present on the end user system at the time of uninstall,
     * so you need to specify its location in the project.
     * You may omit {@link PackageFile#path} parameter of the uninstallCallback,
     * if {@link JetProject#packageFilesDir} already contains a library at the specified {@link PackageFile#packagePath}
     * parameter, otherwise the library will be added to the package to the specified {@link PackageFile#packagePath} folder.
     * If the file {@code uninstall.dll/libuninstall.so} in the {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     * </p>
     * This functionality is available in Excelsior JET 11.3 and above.
     */
    public PackageFile uninstallCallback = new PackageFile(PackageFileType.FILE);

    /**
     * (Windows) Image to display on the first screen of the installation wizard. Recommended size: 177*314px.
     * If not set, and the file {@code welcomeImage.bmp} in the {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     */
    public File welcomeImage;

    /**
     * (Windows) Image to display in the upper-right corner on the second and subsequent screens of the
     * installation wizard. Recommended size: 109*59px.
     * If not set, and the file {@code installerImage.bmp} in the {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     */
    public File installerImage;

    /**
     * (Windows) Image to display on the first screen of the uninstall wizard. Recommended size: 177*314px.
     * If not set, and the file {@code uninstallerImage.bmp} in the {@link JetProject#jetResourcesDir} folder exists,
     * that file is used by convention.
     * <p>
     * This functionality is available in Excelsior JET 11.3 and above.
     * </p>
     */
    public File uninstallerImage;

    public void fillDefaults(JetProject project, ExcelsiorJet excelsiorJet) throws JetTaskFailureException {
        //check eula settings
        if (!VALID_EULA_ENCODING_VALUES.contains(eulaEncoding)) {
            throw new JetTaskFailureException(s("JetApi.Package.Eula.UnsupportedEncoding", eulaEncoding));
        }

        eula = checkFileWithDefault(eula, new File(project.jetResourcesDir(), "eula.txt"), "eula");

        installerSplash = checkFileWithDefault(installerSplash,
                new File(project.jetResourcesDir(), "installerSplash.bmp"), "installerSplash");

        if (!excelsiorJet.since11_3()) {
            check11_3ParametersNotSet();
        }

        if (language != null) {
            SetupLanguage.validate(language);
        }

        if (afterInstallRunnable.isDefined()) {
            afterInstallRunnable.validate();
        }

        if (compressionLevel != null) {
            SetupCompressionLevel compression = SetupCompressionLevel.validate(compressionLevel);
            if ((compression != SetupCompressionLevel.FAST) && !excelsiorJet.isAdvancedExcelsiorInstallerFeaturesSupported()) {
                throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.UnsupportedCompressionLevel", compressionLevel));
            }
        }

        if (installationDirectory.isDefined()) {
            installationDirectory.validate(excelsiorJet);
        }

        for (Shortcut shortcut: shortcuts) {
            shortcut.validate(excelsiorJet);
        }

        if (!postInstallCheckboxes.isEmpty() && !excelsiorJet.isAdvancedExcelsiorInstallerFeaturesSupported()) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.UnsupportedParameter", "postInstallCheckboxes"));
        } else {
            for (PostInstallCheckbox postInstallCheckbox: postInstallCheckboxes) {
                postInstallCheckbox.validate();
            }
        }

        if (!fileAssociations.isEmpty() && !excelsiorJet.isAdvancedExcelsiorInstallerFeaturesSupported()) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.UnsupportedParameter", "fileAssociations"));
        } else {
            for (FileAssociation fileAssociation: fileAssociations) {
                fileAssociation.validate();
            }
        }

        installCallback = checkFileWithDefault(installCallback,
                new File(project.jetResourcesDir(), excelsiorJet.getTargetOS().mangleDllName("install")), "installCallback");

        if (!uninstallCallback.isDefined()) {
            File uninstall = new File(project.jetResourcesDir(), excelsiorJet.getTargetOS().mangleDllName("uninstall"));
            if (uninstall.exists()) {
                uninstallCallback.path = uninstall;
            }
        }
        uninstallCallback.type = PackageFileType.FILE.toString();
        uninstallCallback.validate("JetApi.ExcelsiorInstaller.FileDoesNotExist", "uninstallCallback");

        welcomeImage = checkBrandingParameter(excelsiorJet, welcomeImage, "welcomeImage",
                new File(project.jetResourcesDir(), "welcomeImage.bmp"));
        installerImage = checkBrandingParameter(excelsiorJet, installerImage, "installerImage",
            new File(project.jetResourcesDir(), "installerImage.bmp"));
        uninstallerImage = checkBrandingParameter(excelsiorJet, uninstallerImage, "uninstallerImage",
            new File(project.jetResourcesDir(), "uninstallerImage.bmp"));
    }

    private void check11_3ParametersNotSet() throws JetTaskFailureException {
        ArrayList<String> parameters = new ArrayList<>();
        if (language != null) {
            parameters.add("language");
        }
        if (afterInstallRunnable.isDefined()) {
           parameters.add("afterInstallRunnable");
        }
        if (compressionLevel != null) {
            parameters.add("compressionLevel");
        }
        if (installationDirectory.isDefined()) {
            parameters.add("installationDirectory");
        }
        if (!shortcuts.isEmpty()) {
            parameters.add("shortcuts");
        }
        if (cleanupAfterUninstall) {
            parameters.add("cleanupAfterUninstall");
        }
        if (registryKey != null) {
            parameters.add("registryKey");
        }
        if (noDefaultPostInstallActions) {
            parameters.add("noDefaultPostInstallActions");
        }
        if (!postInstallCheckboxes.isEmpty()) {
            parameters.add("postInstallCheckboxes");
        }
        if (!fileAssociations.isEmpty()) {
            parameters.add("fileAssociations");
        }
        if (installCallback != null) {
            parameters.add("installCallback");
        }
        if (uninstallCallback.isDefined()) {
            parameters.add("uninstallCallback");
        }
        if (welcomeImage != null) {
            parameters.add("welcomeImage");
        }
        if (installerImage != null) {
            parameters.add("installerImage");
        }
        if (uninstallerImage != null) {
            parameters.add("uninstallerImage");
        }
        if (parameters.size() == 1) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.Since11_3Parameter", parameters.get(0)));
        } else if (parameters.size() > 1) {
            throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.Since11_3Parameters", String.join(",", parameters)));
        }
    }

    private static File checkFileWithDefault(File file, File defaultFile, String notExistParam) throws JetTaskFailureException {
        return Utils.checkFileWithDefault(file, defaultFile, "JetApi.ExcelsiorInstaller.FileDoesNotExist", notExistParam);
    }

    private File checkBrandingParameter(ExcelsiorJet excelsiorJet, File parValue, String parName, File defaultValue) throws JetTaskFailureException {
        if (!excelsiorJet.isAdvancedExcelsiorInstallerFeaturesSupported()) {
            if (parValue != null) {
                throw new JetTaskFailureException(s("JetApi.ExcelsiorInstaller.UnsupportedParameter", parName));
            } else {
                return null;
            }
        }
        return checkFileWithDefault(parValue, defaultValue, parName);
    }

    public String eulaFlag() throws JetTaskFailureException {
        String actualEncoding;
        try {
            actualEncoding = detectEncoding(eula);
        } catch (IOException e) {
            throw new JetTaskFailureException(e.getMessage(), e);
        }

        if (!AUTO_DETECT_EULA_ENCODING.equals(eulaEncoding)) {
            if (!actualEncoding.equals(eulaEncoding)) {
                throw new JetTaskFailureException(s("JetApi.Package.Eula.EncodingDoesNotMatchActual", actualEncoding, eulaEncoding));
            }
        }

        if (StandardCharsets.UTF_16LE.name().equals(actualEncoding)) {
            return UNICODE_EULA_FLAG;
        } else if (StandardCharsets.US_ASCII.name().equals(actualEncoding)) {
            return EULA_FLAG;
        } else {
            throw new JetTaskFailureException(s("JetApi.Package.Eula.UnsupportedEncoding", eulaEncoding));
        }
    }
}
