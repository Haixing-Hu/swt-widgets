/******************************************************************************
 *
 * Copyright (c) 2014  Haixing Hu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Haixing Hu (starfish.hu at gmail dot com) - Initial implementation and API.
 *
 ******************************************************************************/

package com.github.haixing_hu.swt.utils;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * A class used to provide localized messages for the widgets.
 *
 * @author Haixing Hu
 */
public final class Messages {

  /**
   * The default encoding of the messages properties files. The value of this
   * constant is {@value}.
   */
  public static final String DEFAULT_ENCODING = "UTF-8";

  private static volatile Messages instance = null;

  /**
   * Gets the singleton instance of the {@link Message} class.
   * <p>
   * <b>NOTE:</b> This implementation use the "double checked locking" trick,
   * and it is only worked for the JDK >= 5.0.
   *
   * @return the singleton instance of the {@link Message} class.
   */
  public static Messages getInstance() {
    if (instance == null) {
      synchronized (Messages.class) {
        if (instance == null) {
          instance = new Messages();
        }
      }
    }
    return instance;
  }

  private final Locale locale;
  private final ReloadableResourceBundleMessageSource source;

  private Messages() {
    locale = Locale.getDefault();
    source = new ReloadableResourceBundleMessageSource();
    source.setDefaultEncoding(DEFAULT_ENCODING);
    source.setBasename("classpath:messages");
  }

  /**
   * Gets the localized message for a specified key.
   *
   * @param key
   *          the key of the message.
   * @return the localized message for the specified key.
   */
  public String get(String key) {
    return source.getMessage(key, null, locale);
  }

  /**
   * Gets the localized message for a specified key formatted with specified
   * arguments.
   *
   * @param key
   *          the key of the message.
   * @param args
   *          the arguments used to format the message.
   * @return the localized message formatted with the specified arguments.
   */
  public String get(String key, Object... args) {
    return source.getMessage(key, args, locale);
  }
}
