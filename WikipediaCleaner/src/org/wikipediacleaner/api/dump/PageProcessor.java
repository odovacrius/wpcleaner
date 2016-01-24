/*
 *  WPCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2016  Nicolas Vervelle
 *
 *  See README.txt file for licensing information.
 */


package org.wikipediacleaner.api.dump;

import org.wikipediacleaner.api.constants.EnumWikipedia;
import org.wikipediacleaner.api.data.Page;


/**
 * Interface to process pages from dumps.
 */
public interface PageProcessor {

  /**
   * @return Wiki.
   */
  public EnumWikipedia getWiki();

  /**
   * Process a page.
   * 
   * @param page Page to be processed.
   */
  public void processPage(Page page);
}
