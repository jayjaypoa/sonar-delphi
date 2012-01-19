/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.pmd.profile;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.delphi.core.DelphiLanguage;
import org.sonar.plugins.delphi.pmd.DelphiPmdConstants;
import org.sonar.plugins.delphi.pmd.xml.DelphiRulesUtils;

/**
 * exports Delphi rules profile into Sonar
 */
public class DelphiPmdProfileExporter extends ProfileExporter {

  /**
   * ctor
   */
  public DelphiPmdProfileExporter() {
    super(DelphiPmdConstants.REPOSITORY_KEY, DelphiPmdConstants.REPOSITORY_NAME);
    setSupportedLanguages(DelphiLanguage.KEY);
    setMimeType("application/xml");
  }

  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {
    try {
      writer.write(DelphiRulesUtils.exportConfiguration(profile));
    } catch (IOException e) {
      throw new SonarException("Fail to export profile " + profile, e);
    }
  }

  /**
   * exports profile to string
   * 
   * @param profile
   *          profile
   * @return exported profile
   */
  public String exportProfileToString(RulesProfile profile) {
    StringWriter writer = new StringWriter();
    exportProfile(profile, writer);
    return writer.toString();
  }

}