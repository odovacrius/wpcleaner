/*
 *  WikipediaCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2008  Nicolas Vervelle
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wikipediacleaner.api.check;

import java.util.ArrayList;

import org.wikipediacleaner.api.data.Page;


/**
 * Algorithm for analyzing error 10 of check wikipedia project.
 * Error 10: Square brackets not correct end
 */
public class CheckErrorAlgorithm10 extends CheckErrorAlgorithmBase {

  public CheckErrorAlgorithm10() {
    super("Square brackets not correct end");
  }

  /* (non-Javadoc)
   * @see org.wikipediacleaner.api.check.CheckErrorAlgorithm#analyze(org.wikipediacleaner.api.data.Page, java.lang.String, java.util.ArrayList)
   */
  public boolean analyze(Page page, String contents, ArrayList<CheckErrorResult> errors) {
    if ((page == null) || (contents == null)) {
      return false;
    }

    // Analyze contents from the end by counting ]] and [[
    int startIndex = contents.length();
    boolean result = false;
    int beginIndex = contents.lastIndexOf("[[", startIndex);
    int endIndex = contents.lastIndexOf("]]", startIndex);
    int count = 0;
    while (startIndex > 0) {
      if ((beginIndex < 0) && (endIndex < 0)) {
        // No more ]] or [[
        startIndex = 0;
      } else if ((endIndex >= 0) && ((beginIndex < endIndex) || (beginIndex < 0))) {
        // Found a ]]
        count++;
        startIndex = endIndex;
        endIndex = contents.lastIndexOf("]]", startIndex - 1);
      } else {
        // Found a [[
        count--;
        if (count < 0) {
          // Found more [[ than ]]
          if (errors == null) {
            return true;
          }
          result = true;

          // Check if the situation is something like [[....] (replacement: [[....]])
          boolean errorReported = false;
          int nextEnd = contents.indexOf(']', beginIndex + 2);
          if (nextEnd > 0) {
            int nextCR = contents.indexOf('\n', beginIndex + 2);
            int nextBegin = contents.indexOf('[', beginIndex + 2);
            if (((nextCR < 0) || (nextCR > nextEnd)) &&
                ((nextBegin < 0) || (nextBegin > nextEnd))) {
              CheckErrorResult errorResult = new CheckErrorResult(
                  getShortDescription(), beginIndex, nextEnd + 1);
              errorResult.addReplacement(contents.substring(beginIndex, nextEnd + 1) + "]");

              // Check if the situation is something like [[http://....] (replacement: [http://....]) 
              if (contents.startsWith("http://", beginIndex + 2)) {
                errorResult.addReplacement(contents.substring(beginIndex + 1, nextEnd + 1));
              }

              errors.add(errorResult);
              errorReported = true;
            }
          }

          // Default
          if (!errorReported) {
            errors.add(new CheckErrorResult(getShortDescription(), beginIndex, beginIndex + 2));
          }
          count = 0;
        }
        startIndex = beginIndex;
        beginIndex = contents.lastIndexOf("[[", startIndex - 1);
      }
    }
    return result;
  }
}
