/*
 * Copyright (C) 2022 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson;

/**
 * An enumeration that defines the kind of newline to use for serialization.
 *
 * See {@see https://en.wikipedia.org/wiki/Newline}
 *
 * @since 2.10.1
 */
public enum NewlineStyle {
  /**
   * Using this style will result in the same kind of new lines that the current environment uses.
   *
   * <p>So it will produce {@code "\r\n"} when running on Windows, and {@code "\n"} when running on macOS & Linux.</p>
   */
  CURRENT_OS,

  /**
   * Using this style will result in the same new line convention that Windows uses (and MS-DOS used).
   * This is {@code CR+LF} ({@code 0D 0A}, {@code "\r\n"})
   */
  WINDOWS,

  /**
   * Using this style will result in the same new line convention that macOS, Linux, and UNIX-like systems use.
   * This is {@code LF} ({@code 0A}, {@code "\n"})
   */
  MACOS_AND_LINUX,

  /**
   * Using this style will result in the same new line convention that classic Mac OS used. Rarely needed.
   * This is {@code CR} ({@code 0D}, {@code "\r"})
   */
  OLD_MACOS;
}
