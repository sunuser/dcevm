package com.github.dcevm.installer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * 功能：命令行方式安装DCEVM<br>
 * <br>
 * <p>
 * 修订历史：<br>
 * 日期　　       版本    修订人    描述<br>
 * ----------------------------------------------------<br>
 * 2017/5/8    1.0    SUN      初始版本<br>
 */
public class MainCommandLine {
    private final ConfigurationInfo config;
    private String jdkPath;

    public MainCommandLine(String jdkPath) {
        this.config = ConfigurationInfo.current();
        Installer installer = new Installer(config);

        this.jdkPath = jdkPath;
    }

    public void setJdkPath(String jdkPath) {
        this.jdkPath = jdkPath;
    }

    public void run() {
        Path dir = Paths.get(jdkPath);

        Preferences p = Preferences.userNodeForPackage(Installer.class);
        final String prefID = "defaultDirectory";
        p.put(prefID, dir.getParent().toString());
        try {
            Installation installation = new Installation(config, dir);

            String inputKeyword = genMenu(installation);
            String answer = System.console().readLine();
            while (inputKeyword.indexOf(answer)<0) {
                answer = System.console().readLine();
            }

            if ("U".equals(answer)){
                installation.uninstallDCE();
            } else if ("R".equals(answer)){
                installation.installDCE(false);
            } else if ("I".equals(answer)) {
                installation.installDCE(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String genMenu(Installation installation) {
        StringBuilder licenseText = new StringBuilder();
        licenseText.append("Dynamic Code Evolution VM\n");
        licenseText.append("=========================\n");
        licenseText.append("A modification of the Java HotSpot(TM) VM that allows unlimited class redefinition at runtime.\n\n\n");
        licenseText.append("Enhance current Java (JRE/JDK) installations with DCEVM (http://github.com/dcevm/dcevm).\n");
        licenseText.append("You can either replace current Java VM or install DCEVM as alternative JVM (run with -XXaltjvm=dcevm command-line option).\n");
        licenseText.append("This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License version 2 only, as published by the Free Software Foundation.\n\n");
        licenseText.append("This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. \n\n");
        licenseText.append("See the GNU General Public License version 2 for more details (a copy is included in the LICENSE file that accompanied this code).\n");
        licenseText.append("You should have received a copy of the GNU General Public License version 2 along with this work; \n");
        licenseText.append("if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.\n");
        licenseText.append("ASM LICENSE TEXT:\n");
        licenseText.append("Copyright (c) 2000-2005 INRIA, France Telecom\n");
        licenseText.append("All rights reserved.\n\n");
        licenseText.append("Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n");
        licenseText.append("1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n");
        licenseText.append("2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n");
        licenseText.append("3. Neither the name of the copyright holders nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\n");
        licenseText.append("THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. \n");
        licenseText.append("IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; \n");
        licenseText.append("OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n\n\n");

        licenseText.append("----------------------------------\n");
        licenseText.append(String.format("Directory:          %s\n",installation.getPath()));
        licenseText.append(String.format("Type:               %s\n",installation.getVersion()));
        licenseText.append(String.format("Java Version:       %s(%s)\n",installation.isJDK()?"JDK":"JRE", installation.is64Bit()?"64 Bit":""));
        licenseText.append(String.format("Replaced by DCEVM?: %s\n",installation.isDCEInstalled()?"Yes("+installation.getVersionDcevm()+")":"No"));
        licenseText.append(String.format("Installed altjvm?:  %s\n",installation.isDCEInstalledAltjvm()?"Yes("+installation.getVersionDcevmAltjvm()+")":"No"));
        licenseText.append("----------------------------------\n");

        licenseText.append("Your choice: "+"\n");
        String buttonLabels = "";
        String keyword = "C";
        if (installation.isDCEInstalled() || installation.isDCEInstalledAltjvm()) {
            buttonLabels += "(U)ninstall;";
            keyword += "U";
        }
        if (!installation.isDCEInstalled()) {
            buttonLabels = "(R)eplace by DDCEVM;";
            keyword += "R";
        }
        if (!installation.isDCEInstalledAltjvm()) {
            buttonLabels += "(I)nstall DCEVM as altjvm;";
            keyword += "I";
        }

        buttonLabels += "(C)ancel";
        licenseText.append(textButton(buttonLabels));
        System.out.println(licenseText.toString());

        return keyword;
    }

    private String textButton(String buttonLabels) {
        StringBuffer sb = new StringBuffer();
        String[] labels = buttonLabels.split(";");
        for (int i = 0; i < labels.length ; i++) {
            String label = labels[i];
            sb.append("+");
            for (int j = 0; j < label.length() + 4; j++) {
                sb.append("-");
            }
            sb.append("+  ");
        }
        sb.append("\n");
        for (int i = 0; i < labels.length ; i++) {
            String label = labels[i];
            sb.append("|  ");
            sb.append(label);
            sb.append("  |  ");
        }
        sb.append("\n");

        String[] lines = sb.toString().split("\\n");
        sb.append(lines[0]);

        return sb.toString();
    }
}
